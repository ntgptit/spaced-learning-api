package com.spacedlearning.service.impl.repetition;

import com.spacedlearning.entity.ModuleProgress;
import com.spacedlearning.entity.Repetition;
import com.spacedlearning.entity.enums.CycleStudied;
import com.spacedlearning.entity.enums.RepetitionOrder;
import com.spacedlearning.entity.enums.RepetitionStatus;
import com.spacedlearning.repository.ModuleProgressRepository;
import com.spacedlearning.repository.RepetitionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Manages the scheduling and updating of repetition cycles for spaced learning modules.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RepetitionScheduleManager {

    // --- Constants ---
    private static final double BASE_DAILY_WORDS = 41.7;
    private static final int[] REVIEW_MULTIPLIERS = {2, 4, 8, 13, 19, 26};
    private static final int MAX_WORD_FACTOR = 3;
    private static final int MAX_INTERVAL_DAYS = 31;
    private static final int MIN_DAILY_WORDS = 20;
    private static final int INITIAL_REPETITIONS_COUNT = 4;
    private static final int FIFTH_REPETITION_OFFSET_DAYS = 15;
    private static final long OPTIMAL_DATE_MAX_COUNT = 3L;
    private static final int OPTIMAL_DATE_SEARCH_WINDOW_DAYS = 7;
    private static final int MAX_DATE_CONSTRAINT_DAYS = 60;

    private final RepetitionRepository repetitionRepository;
    private final ModuleProgressRepository progressRepository;

    private static double getBaseInterval(int reviewIndex, int studyCycleCount, double dailyWordCount) {
        int adjustedCycleCount = Math.max(1, studyCycleCount);
        double wordFactor = Math.min(MAX_WORD_FACTOR, Math.max(dailyWordCount, BASE_DAILY_WORDS) / BASE_DAILY_WORDS);

        int baseMultiplier = reviewIndex < REVIEW_MULTIPLIERS.length ? REVIEW_MULTIPLIERS[reviewIndex]
                : REVIEW_MULTIPLIERS[REVIEW_MULTIPLIERS.length - 1];
        double baseInterval = wordFactor * Math.min(MAX_INTERVAL_DAYS, (double) adjustedCycleCount * baseMultiplier);
        if (adjustedCycleCount >= 3 && reviewIndex >= 2) {
            baseInterval = Math.sqrt((double) adjustedCycleCount * baseMultiplier) * wordFactor * 5.0;
        }
        return baseInterval;
    }

    private boolean validateRepetitionOrder(ModuleProgress progress, RepetitionOrder currentOrder, int currentIndex) {
        RepetitionOrder[] allOrders = RepetitionOrder.values();
        if (currentIndex >= allOrders.length - 1) {
            log.debug("No future orders after {} for progress ID: {}", currentOrder, progress.getId());
            return true;
        }
        return false;
    }

    /**
     * Initializes the first learning date for a module progress if not set.
     *
     * @param progress The module progress to initialize.
     */
    public void initializeFirstLearningDate(@NonNull ModuleProgress progress) {
        if (progress.getFirstLearningDate() != null) {
            return;
        }

        LocalDate currentDate = LocalDate.now();
        progress.setFirstLearningDate(currentDate);
        progressRepository.save(progress);
        log.debug("Initialized first learning date to {} for progress ID: {}", currentDate, progress.getId());
    }

    /**
     * Creates the initial set of repetitions for a module progress.
     *
     * @param progress The module progress to create repetitions for.
     * @return A list of created repetitions.
     */
    @NonNull
    public List<Repetition> createRepetitionsForProgress(@NonNull ModuleProgress progress) {
        List<Repetition> repetitions = new ArrayList<>();
        RepetitionOrder[] orders = RepetitionOrder.values();

        for (int i = 0; i < INITIAL_REPETITIONS_COUNT; i++) {
            if (i >= orders.length) {
                log.warn("Insufficient RepetitionOrder values for repetition at index {}. Stopping.", i);
                return repetitions;
            }

            LocalDate reviewDate = calculateReviewDate(progress, i);
            Repetition repetition = new Repetition();
            repetition.setModuleProgress(progress);
            repetition.setRepetitionOrder(orders[i]);
            repetition.setStatus(RepetitionStatus.NOT_STARTED);
            repetition.setReviewDate(reviewDate);
            repetitions.add(repetition);
        }

        int fifthRepetitionIndex = INITIAL_REPETITIONS_COUNT;
        if (orders.length <= fifthRepetitionIndex) {
            log.warn("Insufficient RepetitionOrder values for 5th repetition for progress ID: {}", progress.getId());
            return repetitions;
        }

        LocalDate reviewDate5 = calculateReviewDate(progress, fifthRepetitionIndex);
        LocalDate adjustedReviewDate5 = reviewDate5;
        if (repetitions.size() >= INITIAL_REPETITIONS_COUNT) {
            LocalDate rep4Date = repetitions.get(INITIAL_REPETITIONS_COUNT - 1).getReviewDate();
            if (reviewDate5.equals(rep4Date)) {
                log.debug("Adjusting 5th repetition date due to clash with 4th for progress ID: {}", progress.getId());
                adjustedReviewDate5 = rep4Date.plusDays(FIFTH_REPETITION_OFFSET_DAYS);
            }
        }

        Repetition repetition5 = new Repetition();
        repetition5.setModuleProgress(progress);
        repetition5.setRepetitionOrder(orders[fifthRepetitionIndex]);
        repetition5.setStatus(RepetitionStatus.NOT_STARTED);
        repetition5.setReviewDate(findOptimalDate(adjustedReviewDate5));
        repetitions.add(repetition5);

        return repetitions;
    }

    /**
     * Reschedules future repetitions based on a new start date.
     *
     * @param progress     The module progress containing the repetitions.
     * @param currentOrder The current repetition order.
     * @param newStartDate The new start date for rescheduling.
     */
    public void rescheduleFutureRepetitions(
            @NonNull ModuleProgress progress,
            @NonNull RepetitionOrder currentOrder,
            @NonNull LocalDate newStartDate) {

        int currentIndex = getOrderIndex(currentOrder);
        if (currentIndex == -1) {
            log.debug("Invalid order {} for rescheduling for progress ID: {}", currentOrder, progress.getId());
            return;
        }

        if (validateRepetitionOrder(progress, currentOrder, currentIndex)) return;

        List<Repetition> futureRepetitions = getFutureRepetitions(progress, currentIndex);
        if (futureRepetitions.isEmpty()) {
            log.debug("No future repetitions found to reschedule for progress ID: {}", progress.getId());
            return;
        }

        log.info("Rescheduling {} future repetitions based on new date: {} for progress ID: {}",
                futureRepetitions.size(), newStartDate, progress.getId());

        boolean changed = false;
        for (Repetition repetition : futureRepetitions) {
            int repIndex = getOrderIndex(repetition.getRepetitionOrder());
            if (repIndex < 0 || repIndex >= REVIEW_MULTIPLIERS.length || currentIndex >= REVIEW_MULTIPLIERS.length) {
                log.error("Invalid index detected during rescheduling. RepIndex: {}, CurrentIndex: {}. Skipping repetition {}.",
                        repIndex, currentIndex, repetition.getId());
                continue;
            }

            int dayOffset = REVIEW_MULTIPLIERS[repIndex] - REVIEW_MULTIPLIERS[currentIndex];
            LocalDate newReviewDate = newStartDate.plusDays(dayOffset);
            if (newReviewDate.equals(repetition.getReviewDate())) {
                continue;
            }

            repetition.setReviewDate(newReviewDate);
            log.debug("Rescheduled repetition {} to {} for progress ID: {}", repetition.getRepetitionOrder(), newReviewDate, progress.getId());
            changed = true;
        }

        if (!changed) {
            log.debug("No dates changed during reschedule for progress ID: {}", progress.getId());
            return;
        }

        repetitionRepository.saveAll(futureRepetitions);
        updateNextStudyDate(progress);
    }

    /**
     * Calculates the planned review date for a repetition.
     *
     * @param progress    The module progress to calculate the date for.
     * @param reviewIndex The index of the review multiplier.
     * @return The calculated review date.
     */
    @NonNull
    public LocalDate calculateReviewDate(@NonNull ModuleProgress progress, int reviewIndex) {
        int wordCount = progress.getModule() != null ? progress.getModule().getWordCount() : 0;
        double dailyWordCount = Math.max(MIN_DAILY_WORDS, wordCount > 0 ? (double) wordCount : BASE_DAILY_WORDS);

        CycleStudied cyclesStudied = progress.getCyclesStudied();
        int studyCycleCount = 0;
        if (cyclesStudied != null) {
            studyCycleCount = switch (cyclesStudied) {
                case FIRST_REVIEW -> 1;
                case SECOND_REVIEW -> 2;
                case THIRD_REVIEW, MORE_THAN_THREE_REVIEWS -> 3;
                default -> 0;
            };
        }

        BigDecimal percentComplete = progress.getPercentComplete();
        double baseInterval = getBaseInterval(reviewIndex, studyCycleCount, dailyWordCount);

        LocalDate referenceDate = progress.getFirstLearningDate() != null ? progress.getFirstLearningDate() : LocalDate.now();
        double daysToAdd = baseInterval;
        if (percentComplete != null && percentComplete.doubleValue() > 0.0) {
            daysToAdd = baseInterval * (percentComplete.doubleValue() / 100.0);
        }

        LocalDate calculatedDate = referenceDate.plusDays(Math.round(daysToAdd));
        if (progress.getFirstLearningDate() == null) {
            return findOptimalDate(calculatedDate);
        }
        if (calculatedDate.isAfter(progress.getFirstLearningDate().plusDays(MAX_DATE_CONSTRAINT_DAYS))) {
            log.debug("Constraining date {} to {} days after first learning date for progress ID: {}",
                    calculatedDate, MAX_DATE_CONSTRAINT_DAYS, progress.getId());
            calculatedDate = progress.getFirstLearningDate().plusDays(MAX_DATE_CONSTRAINT_DAYS);
        }

        return findOptimalDate(calculatedDate);
    }

    /**
     * Finds the least busy date within a search window for scheduling a repetition.
     *
     * @param initialDate The initial date to start the search from.
     * @return The optimal date for scheduling.
     * @throws IllegalArgumentException if the initial date is null.
     */
    @NonNull
    private LocalDate findOptimalDate(LocalDate initialDate) {
        if (initialDate == null) {
            throw new IllegalArgumentException("Initial date cannot be null for findOptimalDate");
        }

        LocalDate endDate = initialDate.plusDays(OPTIMAL_DATE_SEARCH_WINDOW_DAYS);
        log.debug("Finding optimal date starting from {} within window ending {}", initialDate, endDate);

        List<Object[]> dateCountsList = repetitionRepository.countReviewDatesBetween(initialDate, endDate);
        if (dateCountsList == null || dateCountsList.isEmpty()) {
            log.debug("No repetition counts found in window for {}. Using initial date or today.", initialDate);
            return initialDate.isBefore(LocalDate.now()) ? LocalDate.now() : initialDate;
        }

        Map<LocalDate, Long> dateCounts = dateCountsList.stream()
                .collect(Collectors.toMap(
                        arr -> {
                            Object dateObj = arr[0];
                            if (dateObj instanceof LocalDate) {
                                return (LocalDate) dateObj;
                            }
                            if (dateObj instanceof java.sql.Date) {
                                return ((java.sql.Date) dateObj).toLocalDate();
                            }
                            log.error("Unexpected reviewDate type from repository: {}. Related initialDate: {}", dateObj.getClass(), initialDate);
                            throw new IllegalStateException("Unexpected reviewDate type: " + dateObj.getClass());
                        },
                        arr -> ((Number) arr[1]).longValue(),
                        (v1, v2) -> v1
                ));

        Long initialCount = dateCounts.getOrDefault(initialDate, 0L);
        if (initialCount <= OPTIMAL_DATE_MAX_COUNT && !initialDate.isBefore(LocalDate.now())) {
            log.debug("Initial date {} is optimal (count={}).", initialDate, initialCount);
            return initialDate;
        }

        if (initialCount > OPTIMAL_DATE_MAX_COUNT) {
            log.warn("Initial date {} is overloaded (count={}). Searching for alternatives.", initialDate, initialCount);
        }

        for (int i = 1; i <= OPTIMAL_DATE_SEARCH_WINDOW_DAYS; i++) {
            LocalDate candidateDate = initialDate.plusDays(i);
            if (candidateDate.isBefore(LocalDate.now())) {
                continue;
            }

            Long count = dateCounts.getOrDefault(candidateDate, 0L);
            if (count <= OPTIMAL_DATE_MAX_COUNT) {
                log.info("Found suitable alternative date: {} (count={}) for initial date {}", candidateDate, count, initialDate);
                return candidateDate;
            }
        }

        LocalDate fallbackDate = initialDate.plusDays(1);
        if (fallbackDate.isBefore(LocalDate.now())) {
            fallbackDate = LocalDate.now();
        }
        log.warn("No optimal date found within {} days. Falling back to: {}",
                OPTIMAL_DATE_SEARCH_WINDOW_DAYS, fallbackDate);
        return fallbackDate;
    }

    /**
     * Updates the next study date for a module progress based on pending repetitions.
     *
     * @param progress The module progress to update.
     */
    public void updateNextStudyDate(@NonNull ModuleProgress progress) {
        List<Repetition> pendingRepetitions = repetitionRepository
                .findByModuleProgressIdAndStatusOrderByReviewDate(progress.getId(), RepetitionStatus.NOT_STARTED);

        if (pendingRepetitions.isEmpty()) {
            if (progress.getNextStudyDate() != null) {
                progress.setNextStudyDate(null);
                progressRepository.save(progress);
                log.debug("Cleared next study date as no pending repetitions found for progress ID: {}", progress.getId());
            }
            return;
        }

        LocalDate newNextStudyDate = pendingRepetitions.get(0).getReviewDate();
        if (newNextStudyDate.equals(progress.getNextStudyDate())) {
            log.trace("Next study date {} already up-to-date for progress ID: {}", newNextStudyDate, progress.getId());
            return;
        }

        progress.setNextStudyDate(newNextStudyDate);
        progressRepository.save(progress);
        log.debug("Updated next study date to {} for progress ID: {}", newNextStudyDate, progress.getId());
    }

    /**
     * Updates the review dates of future repetitions after a repetition is completed.
     *
     * @param progress     The module progress containing the repetitions.
     * @param currentOrder The current repetition order.
     */
    public void updateFutureRepetitions(@NonNull ModuleProgress progress, @NonNull RepetitionOrder currentOrder) {
        int currentIndex = getOrderIndex(currentOrder);
        if (currentIndex == -1) {
            log.debug("Invalid order {} for updating future repetitions for progress ID: {}", currentOrder, progress.getId());
            return;
        }

        if (validateRepetitionOrder(progress, currentOrder, currentIndex)) return;

        List<Repetition> futureRepetitions = getFutureRepetitions(progress, currentIndex);
        if (futureRepetitions.isEmpty()) {
            log.debug("No future repetitions found to update for progress ID: {}", progress.getId());
            return;
        }

        log.info("Updating dates for {} future repetitions based on current progress for progress ID: {}",
                futureRepetitions.size(), progress.getId());

        boolean changed = false;
        for (Repetition repetition : futureRepetitions) {
            int repetitionIndex = getOrderIndex(repetition.getRepetitionOrder());
            if (repetitionIndex == -1) {
                log.error("Could not find index for repetition order {} during update. Skipping repetition ID: {}",
                        repetition.getRepetitionOrder(), repetition.getId());
                continue;
            }

            LocalDate newReviewDate = calculateReviewDate(progress, repetitionIndex);
            if (newReviewDate.equals(repetition.getReviewDate())) {
                continue;
            }

            repetition.setReviewDate(newReviewDate);
            log.debug("Updated repetition {} review date to {} for progress ID: {}",
                    repetition.getRepetitionOrder(), newReviewDate, progress.getId());
            changed = true;
        }

        if (!changed) {
            log.debug("No dates needed changing for future repetitions for progress ID: {}", progress.getId());
            return;
        }

        repetitionRepository.saveAll(futureRepetitions);
        log.info("Saved updated dates for future repetitions for progress ID: {}", progress.getId());
    }

    /**
     * Retrieves future repetitions that are not yet started.
     *
     * @param progress     The module progress to query.
     * @param currentIndex The index of the current repetition order.
     * @return A list of future repetitions.
     */
    @NonNull
    private List<Repetition> getFutureRepetitions(@NonNull ModuleProgress progress, int currentIndex) {
        List<Repetition> pendingRepetitions = repetitionRepository
                .findByModuleProgressIdAndStatusOrderByRepetitionOrder(progress.getId(), RepetitionStatus.NOT_STARTED);

        return pendingRepetitions.stream()
                .filter(rep -> getOrderIndex(rep.getRepetitionOrder()) > currentIndex)
                .toList();
    }

    /**
     * Gets the index of a repetition order in the enum.
     *
     * @param order The repetition order.
     * @return The index of the order, or -1 if the order is null.
     */
    private int getOrderIndex(RepetitionOrder order) {
        if (order == null) {
            return -1;
        }
        return Arrays.asList(RepetitionOrder.values()).indexOf(order);
    }

    /**
     * Checks and updates the study cycle for a module progress based on completed repetitions.
     *
     * @param progress The module progress to update.
     */
    public void checkAndUpdateCycleStudied(@NonNull ModuleProgress progress) {
        long totalRepetitionCount = repetitionRepository.countByModuleProgressId(progress.getId());
        if (totalRepetitionCount <= 0) {
            log.debug("No repetitions found for progress ID: {}. Cannot check cycle.", progress.getId());
            return;
        }

        long completedCount = repetitionRepository.countByModuleProgressIdAndStatus(progress.getId(), RepetitionStatus.COMPLETED);
        if (completedCount < totalRepetitionCount) {
            log.debug("Not all repetitions completed ({} / {}) for progress ID: {}. No cycle update.",
                    completedCount, totalRepetitionCount, progress.getId());
            return;
        }

        log.info("All {} repetitions completed for progress ID: {}. Checking for cycle update.", completedCount, progress.getId());

        CycleStudied currentCycle = progress.getCyclesStudied() != null ? progress.getCyclesStudied() : CycleStudied.FIRST_TIME;
        CycleStudied nextCycle = switch (currentCycle) {
            case FIRST_TIME -> CycleStudied.FIRST_REVIEW;
            case FIRST_REVIEW -> CycleStudied.SECOND_REVIEW;
            case SECOND_REVIEW -> CycleStudied.THIRD_REVIEW;
            case THIRD_REVIEW -> CycleStudied.MORE_THAN_THREE_REVIEWS;
            default -> currentCycle;
        };

        if (nextCycle == currentCycle) {
            log.info("Current cycle {} is the final cycle or no update needed for progress ID: {}", currentCycle, progress.getId());
            return;
        }

        progress.setCyclesStudied(nextCycle);
        progressRepository.save(progress);
        log.info("Updated cycle studied from {} to {} for progress ID: {}", currentCycle, nextCycle, progress.getId());
        createNewRepetitionCycle(progress);
    }

    /**
     * Creates a new repetition cycle for a module progress after completing the previous cycle.
     *
     * @param progress The module progress to create a new cycle for.
     */
    private void createNewRepetitionCycle(@NonNull ModuleProgress progress) {
        log.info("Creating new repetition cycle for progress ID: {}", progress.getId());

        List<Repetition> existingRepetitions = repetitionRepository.findByModuleProgressIdOrderByRepetitionOrder(progress.getId());
        boolean requiresSave = false;
        for (Repetition rep : existingRepetitions) {
            if (rep.getStatus() == RepetitionStatus.COMPLETED) {
                continue;
            }
            rep.setStatus(RepetitionStatus.COMPLETED);
            requiresSave = true;
        }

        if (requiresSave) {
            repetitionRepository.saveAll(existingRepetitions);
            log.debug("Marked previously non-completed repetitions as COMPLETED for progress ID: {}", progress.getId());
        }

        if (!requiresSave) {
            log.debug("All existing repetitions were already COMPLETED for progress ID: {}", progress.getId());
        }

        List<Repetition> newRepetitions = createRepetitionsWithCurrentDate(progress);
        if (newRepetitions.isEmpty()) {
            log.warn("Failed to create new repetitions for the next cycle for progress ID: {}", progress.getId());
            return;
        }

        repetitionRepository.saveAll(newRepetitions);
        log.info("Created and saved {} new repetitions for the next cycle for progress ID: {}", newRepetitions.size(), progress.getId());
        updateNextStudyDate(progress);
    }

    /**
     * Prepares the module progress and creates repetitions for a new cycle.
     *
     * @param progress The module progress to prepare.
     * @return A list of new repetitions for the cycle.
     */
    @NonNull
    private List<Repetition> createRepetitionsWithCurrentDate(@NonNull ModuleProgress progress) {
        Optional<LocalDate> lastCompletedDateOpt = repetitionRepository.findLastCompletedRepetitionDate(progress.getId());
        if (lastCompletedDateOpt.isEmpty()) {
            log.warn("Could not find last completed repetition date for progress ID: {}. Using existing/fallback firstLearningDate.", progress.getId());
            if (progress.getFirstLearningDate() == null) {
                progress.setFirstLearningDate(LocalDate.now());
                progressRepository.save(progress);
            }
            return createRepetitionsForProgress(progress);
        }

        LocalDate lastCompletedDate = lastCompletedDateOpt.get();
        LocalDate newFirstLearningDate = lastCompletedDate.plusDays(1);
        progress.setFirstLearningDate(newFirstLearningDate);
        progressRepository.save(progress);
        log.debug("Set new reference date (firstLearningDate) to {} for new cycle for progress ID: {}",
                newFirstLearningDate, progress.getId());

        return createRepetitionsForProgress(progress);
    }
}