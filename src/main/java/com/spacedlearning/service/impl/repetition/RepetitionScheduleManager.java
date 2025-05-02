package com.spacedlearning.service.impl.repetition;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.spacedlearning.entity.ModuleProgress;
import com.spacedlearning.entity.Repetition;
import com.spacedlearning.entity.enums.CycleStudied;
import com.spacedlearning.entity.enums.RepetitionOrder;
import com.spacedlearning.entity.enums.RepetitionStatus;
import com.spacedlearning.repository.ModuleProgressRepository;
import com.spacedlearning.repository.RepetitionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Manages the scheduling and updating of repetition cycles for spaced learning modules.
 * Responsibilities are separated into scheduling, completion handling, and general management.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RepetitionScheduleManager {

    // --- Constants ---
    private static final double BASE_DAILY_WORDS = 41.7;
    private static final int[] REVIEW_MULTIPLIERS = { 2, 4, 8, 13, 19, 26 };
    private static final int MAX_WORD_FACTOR = 3;
    private static final int MAX_INTERVAL_DAYS = 31;
    private static final int MIN_DAILY_WORDS = 20;
    private static final long OPTIMAL_DATE_MAX_COUNT = 2L;
    private static final int OPTIMAL_DATE_SEARCH_WINDOW_DAYS = 7;
    private static final int MAX_DATE_CONSTRAINT_DAYS = 60;

    private final RepetitionRepository repetitionRepository;
    private final ModuleProgressRepository progressRepository;

    // ----- SCHEDULE MANAGEMENT METHODS -----

    private double getBaseInterval(int reviewIndex, int studyCycleCount, double dailyWordCount) {
        final int adjustedCycleCount = Math.max(1, studyCycleCount);
        final double wordFactor = Math.min(MAX_WORD_FACTOR, Math.max(dailyWordCount, BASE_DAILY_WORDS)
                / BASE_DAILY_WORDS);

        final int baseMultiplier = reviewIndex < REVIEW_MULTIPLIERS.length ? REVIEW_MULTIPLIERS[reviewIndex]
                : REVIEW_MULTIPLIERS[REVIEW_MULTIPLIERS.length - 1];
        double baseInterval = wordFactor * Math.min(MAX_INTERVAL_DAYS, (double) adjustedCycleCount * baseMultiplier);
        if (adjustedCycleCount >= 3 && reviewIndex >= 2) {
            baseInterval = Math.sqrt((double) adjustedCycleCount * baseMultiplier) * wordFactor * 5.0;
        }
        return baseInterval;
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

        final LocalDate currentDate = LocalDate.now();
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
        final List<Repetition> repetitions = new ArrayList<>();
        final RepetitionOrder[] orders = RepetitionOrder.values();

        if (orders.length < 5) {
            log.warn("Not enough RepetitionOrder values to create 5 repetitions. Progress ID: {}", progress.getId());
            return repetitions;
        }

        LocalDate previousDate = null;
        int previousIndex = -1;

        for (int i = 0; i < 5; i++) {
            LocalDate calculatedDate = calculateReviewDate(progress, i);

            if (previousDate != null && previousIndex >= 0) {
                final int minRequiredGapDays = REVIEW_MULTIPLIERS[i] - REVIEW_MULTIPLIERS[previousIndex];
                final LocalDate minAllowedDate = previousDate.plusDays(minRequiredGapDays);
                if (calculatedDate.isBefore(minAllowedDate)) {
                    calculatedDate = minAllowedDate;
                }
            }

            final LocalDate optimalDate = findOptimalDate(calculatedDate);

            final Repetition repetition = new Repetition();
            repetition.setModuleProgress(progress);
            repetition.setRepetitionOrder(orders[i]);
            repetition.setStatus(RepetitionStatus.NOT_STARTED);
            repetition.setReviewDate(optimalDate);
            repetitions.add(repetition);

            previousDate = optimalDate;
            previousIndex = i;
        }

        return repetitions;
    }

    /**
     * Updates the next study date for a module progress based on pending repetitions.
     *
     * @param progress The module progress to update.
     */
    public void updateNextStudyDate(@NonNull ModuleProgress progress) {
        final List<Repetition> pendingRepetitions = repetitionRepository
                .findByModuleProgressIdAndStatusOrderByReviewDate(progress.getId(), RepetitionStatus.NOT_STARTED);

        if (pendingRepetitions.isEmpty()) {
            if (progress.getNextStudyDate() != null) {
                progress.setNextStudyDate(null);
                progressRepository.save(progress);
                log.debug("Cleared next study date as no pending repetitions found for progress ID: {}", progress
                        .getId());
            }
            return;
        }

        final LocalDate newNextStudyDate = pendingRepetitions.get(0).getReviewDate();
        if (newNextStudyDate.equals(progress.getNextStudyDate())) {
            log.trace("Next study date {} already up-to-date for progress ID: {}", newNextStudyDate, progress.getId());
            return;
        }

        progress.setNextStudyDate(newNextStudyDate);
        progressRepository.save(progress);
        log.debug("Updated next study date to {} for progress ID: {}", newNextStudyDate, progress.getId());
    }

    /**
     * Calculates the planned review date for a repetition.
     *
     * @param progress    The module progress to calculate the date for.
     * @param reviewIndex The index of the review multiplier.
     * @return The calculated review date.
     */
    @NonNull
    private LocalDate calculateReviewDate(@NonNull ModuleProgress progress, int reviewIndex) {
        final int wordCount = progress.getModule() != null ? progress.getModule().getWordCount() : 0;
        final double dailyWordCount = Math.max(MIN_DAILY_WORDS, wordCount > 0 ? (double) wordCount : BASE_DAILY_WORDS);

        final CycleStudied cyclesStudied = progress.getCyclesStudied();
        final int studyCycleCount = getCycleStudiedCount(cyclesStudied);

        final BigDecimal percentComplete = progress.getPercentComplete();
        final double baseInterval = getBaseInterval(reviewIndex, studyCycleCount, dailyWordCount);

        final LocalDate referenceDate = progress.getEffectiveStartDate(); // NEW LINE
        double daysToAdd = baseInterval;

        if (percentComplete != null && percentComplete.doubleValue() > 0.0) {
            daysToAdd = baseInterval * (percentComplete.doubleValue() / 100.0);
        }

        LocalDate calculatedDate = referenceDate.plusDays(Math.round(daysToAdd));
        if (calculatedDate.isAfter(referenceDate.plusDays(MAX_DATE_CONSTRAINT_DAYS))) {
            log.debug("Constraining date {} to {} days after reference date for progress ID: {}",
                    calculatedDate, MAX_DATE_CONSTRAINT_DAYS, progress.getId());
            calculatedDate = referenceDate.plusDays(MAX_DATE_CONSTRAINT_DAYS);
        }

        return findOptimalDate(calculatedDate);
    }

    private int getCycleStudiedCount(final CycleStudied cyclesStudied) {
        int studyCycleCount = 0;
        if (cyclesStudied != null) {
            studyCycleCount = switch (cyclesStudied) {
            case FIRST_REVIEW -> 1;
            case SECOND_REVIEW -> 2;
            case THIRD_REVIEW, MORE_THAN_THREE_REVIEWS -> 3;
            default -> 0;
            };
        }
        return studyCycleCount;
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

        final LocalDate endDate = initialDate.plusDays(OPTIMAL_DATE_SEARCH_WINDOW_DAYS);
        log.debug("Finding optimal date starting from {} within window ending {}", initialDate, endDate);

        final List<Object[]> dateCountsList = repetitionRepository.countReviewDatesBetween(initialDate, endDate);
        if (dateCountsList == null || dateCountsList.isEmpty()) {
            log.debug("No repetition counts found in window for {}. Using initial date or today.", initialDate);
            return initialDate.isBefore(LocalDate.now()) ? LocalDate.now() : initialDate;
        }

        final Map<LocalDate, Long> dateCounts = dateCountsList.stream()
                .collect(Collectors.toMap(
                        arr -> {
                            final Object dateObj = arr[0];
                            if (dateObj instanceof LocalDate) {
                                return (LocalDate) dateObj;
                            }
                            if (dateObj instanceof java.sql.Date) {
                                return ((java.sql.Date) dateObj).toLocalDate();
                            }
                            log.error("Unexpected reviewDate type from repository: {}. Related initialDate: {}", dateObj
                                    .getClass(), initialDate);
                            throw new IllegalStateException("Unexpected reviewDate type: " + dateObj.getClass());
                        },
                        arr -> ((Number) arr[1]).longValue(),
                        (v1, v2) -> v1));

        final Long initialCount = dateCounts.getOrDefault(initialDate, 0L);
        if (initialCount <= OPTIMAL_DATE_MAX_COUNT && !initialDate.isBefore(LocalDate.now())) {
            log.debug("Initial date {} is optimal (count={}).", initialDate, initialCount);
            return initialDate;
        }

        if (initialCount > OPTIMAL_DATE_MAX_COUNT) {
            log.warn("Initial date {} is overloaded (count={}). Searching for alternatives.", initialDate,
                    initialCount);
        }

        for (int i = 1; i <= OPTIMAL_DATE_SEARCH_WINDOW_DAYS; i++) {
            final LocalDate candidateDate = initialDate.plusDays(i);
            if (candidateDate.isBefore(LocalDate.now())) {
                continue;
            }

            final Long count = dateCounts.getOrDefault(candidateDate, 0L);
            if (count <= OPTIMAL_DATE_MAX_COUNT) {
                log.info("Found suitable alternative date: {} (count={}) for initial date {}", candidateDate, count,
                        initialDate);
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

    // ----- RESCHEDULE METHODS -----

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

        final int currentIndex = getOrderIndex(currentOrder);
        if (currentIndex == -1) {
            log.debug("Invalid order {} for rescheduling for progress ID: {}", currentOrder, progress.getId());
            return;
        }

        if (validateRepetitionOrder(progress, currentOrder, currentIndex)) {
            return;
        }

        final List<Repetition> futureRepetitions = getFutureRepetitions(progress, currentIndex);
        if (futureRepetitions.isEmpty()) {
            log.debug("No future repetitions found to reschedule for progress ID: {}", progress.getId());
            return;
        }

        log.info("Rescheduling {} future repetitions starting from {} for progress ID: {}",
                futureRepetitions.size(), newStartDate, progress.getId());

        boolean changed = false;
        LocalDate previousDate = newStartDate;
        int previousIndex = currentIndex;

        for (final Repetition repetition : futureRepetitions) {
            final int repIndex = getOrderIndex(repetition.getRepetitionOrder());
            if (repIndex < 0) {
                log.warn("Cannot determine index for repetition ID: {}", repetition.getId());
                continue;
            }

            final LocalDate calculatedDate = calculateAdjustedReviewDate(progress, repIndex, newStartDate);
            final int minRequiredGapDays = REVIEW_MULTIPLIERS[repIndex] - REVIEW_MULTIPLIERS[previousIndex];

            final LocalDate minAllowedDate = previousDate.plusDays(minRequiredGapDays);
            final LocalDate adjustedDate = calculatedDate.isAfter(minAllowedDate) ? calculatedDate : minAllowedDate;

            if (adjustedDate.equals(repetition.getReviewDate())) {
                previousDate = adjustedDate;
                previousIndex = repIndex;
                continue;
            }

            repetition.setReviewDate(adjustedDate);
            log.debug("Rescheduled repetition {} to {} (calculated: {}, minGap: {}) for progress ID: {}",
                    repetition.getRepetitionOrder(), adjustedDate, calculatedDate, minRequiredGapDays, progress
                            .getId());

            changed = true;
            previousDate = adjustedDate;
            previousIndex = repIndex;
        }

        if (!changed) {
            log.debug("No changes applied during rescheduling for progress ID: {}", progress.getId());
            return;
        }

        repetitionRepository.saveAll(futureRepetitions);
        updateNextStudyDate(progress);
        log.info("Future repetitions rescheduled and saved for progress ID: {}", progress.getId());
    }

    @NonNull
    private LocalDate calculateAdjustedReviewDate(@NonNull ModuleProgress progress, int repetitionIndex,
            @NonNull LocalDate baseDate) {
        final int wordCount = progress.getModule() != null ? progress.getModule().getWordCount() : 0;
        final double wordFactor = Math.max(MIN_DAILY_WORDS, wordCount > 0 ? wordCount : BASE_DAILY_WORDS)
                / BASE_DAILY_WORDS;

        final CycleStudied cyclesStudied = progress.getCyclesStudied();
        final int studyCycleCount = getCycleStudiedCount(cyclesStudied);

        final BigDecimal percent = progress.getPercentComplete();
        final double completedPercent = percent != null ? percent.doubleValue() : 0.0;

        final double factor = 1.0
                + (studyCycleCount - 1) * 0.2
                + completedPercent / 100.0 * 0.5
                + (wordFactor - 1.0) * 0.3;

        final int baseMultiplier = repetitionIndex < REVIEW_MULTIPLIERS.length
                ? REVIEW_MULTIPLIERS[repetitionIndex]
                : REVIEW_MULTIPLIERS[REVIEW_MULTIPLIERS.length - 1];

        final double dayOffset = baseMultiplier * factor;

        final LocalDate calculatedDate = baseDate.plusDays(Math.round(dayOffset));
        return findOptimalDate(calculatedDate);
    }

    // ----- COMPLETION HANDLING METHODS -----

    /**
     * Updates the review dates of future repetitions after a repetition is completed.
     *
     * @param progress     The module progress containing the repetitions.
     * @param currentOrder The current repetition order.
     */
    public void updateFutureRepetitions(@NonNull ModuleProgress progress, @NonNull Repetition currentRepetition) {
        final RepetitionOrder currentOrder = currentRepetition.getRepetitionOrder();
        final int currentIndex = getOrderIndex(currentOrder);
        if (currentIndex == -1 || validateRepetitionOrder(progress, currentOrder, currentIndex)) {
            return;
        }

        final List<Repetition> futureRepetitions = getFutureRepetitions(progress, currentIndex);
        if (futureRepetitions.isEmpty()) {
            return;
        }

        log.info("Updating future repetitions after completion for progress ID: {}", progress.getId());

        boolean changed = false;
        final LocalDate baseDate = currentRepetition.getReviewDate().isAfter(LocalDate.now())
                ? currentRepetition.getReviewDate()
                : LocalDate.now();

        LocalDate previousDate = baseDate;
        int previousIndex = currentIndex;

        for (final Repetition repetition : futureRepetitions) {
            final int repetitionIndex = getOrderIndex(repetition.getRepetitionOrder());
            if (repetitionIndex == -1) {
                continue;
            }

            // Tính khoảng cách tối thiểu giữa lần trước và lần hiện tại theo REVIEW_MULTIPLIERS
            final int minRequiredGapDays = REVIEW_MULTIPLIERS[repetitionIndex] - REVIEW_MULTIPLIERS[previousIndex];

            // Tính ngày đề xuất
            final LocalDate calculatedDate = calculateReviewDateAfterCompletion(progress, repetitionIndex, baseDate);

            // Nếu chưa đạt khoảng cách yêu cầu, buộc đẩy lùi ngày
            final LocalDate newReviewDate = calculatedDate.isAfter(previousDate.plusDays(minRequiredGapDays))
                    ? calculatedDate
                    : previousDate.plusDays(minRequiredGapDays);

            if (!newReviewDate.equals(repetition.getReviewDate())) {
                repetition.setReviewDate(newReviewDate);
                changed = true;
                log.debug("Updated repetition {} to new review date: {} (prev: {})",
                        repetition.getRepetitionOrder(), newReviewDate, repetition.getReviewDate());
            }

            previousDate = newReviewDate;
            previousIndex = repetitionIndex;
        }

        if (changed) {
            repetitionRepository.saveAll(futureRepetitions);
            log.info("Saved updated review dates for progress ID: {}", progress.getId());
        }
    }

    @NonNull
    private LocalDate calculateReviewDateAfterCompletion(
            @NonNull ModuleProgress progress,
            int repetitionIndex,
            @NonNull LocalDate baseDate) {

        final int wordCount = progress.getModule() != null ? progress.getModule().getWordCount() : 0;
        final double wordFactor = Math.max(MIN_DAILY_WORDS, wordCount > 0 ? wordCount : BASE_DAILY_WORDS)
                / BASE_DAILY_WORDS;

        final BigDecimal percent = progress.getPercentComplete();
        final double score = percent != null ? percent.doubleValue() : 0.0; // từ 0 đến 100

        final int baseMultiplier = repetitionIndex < REVIEW_MULTIPLIERS.length
                ? REVIEW_MULTIPLIERS[repetitionIndex]
                : REVIEW_MULTIPLIERS[REVIEW_MULTIPLIERS.length - 1];

        final double adjustedMultiplier = baseMultiplier * (score / 100.0);

        final double dayOffset = adjustedMultiplier * wordFactor;

        final LocalDate calculatedDate = baseDate.plusDays(Math.round(dayOffset));
        return findOptimalDate(calculatedDate);
    }

    /**
     * Checks and updates the study cycle for a module progress based on completed repetitions.
     *
     * @param progress The module progress to update.
     */
    public void checkAndUpdateCycleStudied(@NonNull ModuleProgress progress) {
        final long totalRepetitionCount = repetitionRepository.countByModuleProgressId(progress.getId());
        if (totalRepetitionCount <= 0) {
            log.debug("No repetitions found for progress ID: {}. Cannot check cycle.", progress.getId());
            return;
        }

        final long completedCount = repetitionRepository.countByModuleProgressIdAndStatus(progress.getId(),
                RepetitionStatus.COMPLETED);
        if (completedCount < totalRepetitionCount) {
            log.debug("Not all repetitions completed ({} / {}) for progress ID: {}. No cycle update.",
                    completedCount, totalRepetitionCount, progress.getId());
            return;
        }

        log.info("All {} repetitions completed for progress ID: {}. Checking for cycle update.", completedCount,
                progress.getId());

        final CycleStudied currentCycle = progress.getCyclesStudied() != null ? progress.getCyclesStudied()
                : CycleStudied.FIRST_TIME;
        final CycleStudied nextCycle = switch (currentCycle) {
        case FIRST_TIME -> CycleStudied.FIRST_REVIEW;
        case FIRST_REVIEW -> CycleStudied.SECOND_REVIEW;
        case SECOND_REVIEW -> CycleStudied.THIRD_REVIEW;
        case THIRD_REVIEW -> CycleStudied.MORE_THAN_THREE_REVIEWS;
        default -> currentCycle;
        };

        if (nextCycle == currentCycle) {
            log.info("Current cycle {} is the final cycle or no update needed for progress ID: {}", currentCycle,
                    progress.getId());
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

        final List<Repetition> existingRepetitions = repetitionRepository.findByModuleProgressIdOrderByRepetitionOrder(
                progress.getId());
        boolean requiresSave = false;
        for (final Repetition rep : existingRepetitions) {
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

        final List<Repetition> newRepetitions = createRepetitionsWithCurrentDate(progress);
        if (newRepetitions.isEmpty()) {
            log.warn("Failed to create new repetitions for the next cycle for progress ID: {}", progress.getId());
            return;
        }

        repetitionRepository.saveAll(newRepetitions);
        log.info("Created and saved {} new repetitions for the next cycle for progress ID: {}", newRepetitions.size(),
                progress.getId());
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
        final Optional<LocalDate> lastCompletedDateOpt = repetitionRepository.findLastCompletedRepetitionDate(progress
                .getId());
        if (lastCompletedDateOpt.isEmpty()) {
            log.warn(
                    "Could not find last completed repetition date for progress ID: {}. Using existing/fallback firstLearningDate.",
                    progress.getId());
            if (progress.getFirstLearningDate() == null) {
                progress.setFirstLearningDate(LocalDate.now());
                progressRepository.save(progress);
            }
            return createRepetitionsForProgress(progress);
        }

        final LocalDate lastCompletedDate = lastCompletedDateOpt.get();
        final LocalDate newFirstLearningDate = lastCompletedDate.plusDays(1);
        progress.setFirstLearningDate(newFirstLearningDate);
        progressRepository.save(progress);
        log.debug("Set new reference date (firstLearningDate) to {} for new cycle for progress ID: {}",
                newFirstLearningDate, progress.getId());

        return createRepetitionsForProgress(progress);
    }

    // ----- UTILITY METHODS -----

    /**
     * Retrieves future repetitions that are not yet started.
     *
     * @param progress     The module progress to query.
     * @param currentIndex The index of the current repetition order.
     * @return A list of future repetitions.
     */
    @NonNull
    private List<Repetition> getFutureRepetitions(@NonNull ModuleProgress progress, int currentIndex) {
        final List<Repetition> pendingRepetitions = repetitionRepository
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

    private boolean validateRepetitionOrder(ModuleProgress progress, RepetitionOrder currentOrder, int currentIndex) {
        final RepetitionOrder[] allOrders = RepetitionOrder.values();
        if (currentIndex >= allOrders.length - 1) {
            log.debug("No future orders after {} for progress ID: {}", currentOrder, progress.getId());
            return true;
        }
        return false;
    }
}