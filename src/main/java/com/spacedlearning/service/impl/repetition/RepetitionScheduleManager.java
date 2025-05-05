package com.spacedlearning.service.impl.repetition;

import com.spacedlearning.entity.Module;
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
 * Responsibilities are separated into scheduling, completion handling, and general management.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RepetitionScheduleManager {

    // --- Constants ---
    private static final double BASE_DAILY_WORDS = 41.7;
    private static final int[] REVIEW_MULTIPLIERS = {2, 4, 8, 13, 19, 26};
    private static final int MIN_DAILY_WORDS = 20;
    private static final long OPTIMAL_DATE_MAX_COUNT = 3L;
    private static final int OPTIMAL_DATE_SEARCH_WINDOW_DAYS = 7;
    private static final double COMPLETION_ADJUSTMENT_FACTOR = 0.5;
    private static final double STUDY_CYCLE_ADJUSTMENT_FACTOR = 0.2;
    private static final double WORD_FACTOR_ADJUSTMENT = 0.3;
    private static final int MAX_DATE_CONSTRAINT_DAYS = 60;
    private static final EnumMap<RepetitionOrder, Integer> ORDER_INDEX_MAP = new EnumMap<>(RepetitionOrder.class);

    static {
        final RepetitionOrder[] values = RepetitionOrder.values();
        for (int i = 0; i < values.length; i++) {
            ORDER_INDEX_MAP.put(values[i], i);
        }
    }

    private final RepetitionRepository repetitionRepository;
    private final ModuleProgressRepository progressRepository;

    // ----- SCHEDULE MANAGEMENT METHODS -----

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
     * Initializes the first cycle for a module progress if not already recorded.
     *
     * @param progress The module progress to initialize.
     */
    public void initializeFirstCycle(@NonNull ModuleProgress progress) {
        if (progress.findLatestCycleStart(CycleStudied.FIRST_TIME) != null) {
            return;
        }

        LocalDate startDate = progress.getFirstLearningDate();
        if (startDate == null) {
            startDate = LocalDate.now();
            progress.setFirstLearningDate(startDate);
        }

        progress.addCycleStart(CycleStudied.FIRST_TIME, startDate);
        progressRepository.save(progress);
        log.info("Initialized first cycle record for progress ID: {} starting on {}",
                progress.getId(), startDate);
    }

    /**
     * Creates the initial set of repetitions for a module progress.
     *
     * @param progress The module progress to create repetitions for.
     * @return A list of created repetitions.
     */
    public List<Repetition> createRepetitionsForProgress(@NonNull ModuleProgress progress) {
        initializeFirstCycle(progress);
        final LocalDate baseDate = getEffectiveStartDate(progress);
        final Map<LocalDate, Long> dateCounts = loadReviewDateCounts(baseDate);
        return buildRepetitions(progress, baseDate, dateCounts);
    }

    @NonNull
    private List<Repetition> buildRepetitions(@NonNull ModuleProgress progress,
                                              @NonNull LocalDate baseDate,
                                              @NonNull Map<LocalDate, Long> dateCounts) {
        final List<Repetition> repetitions = new ArrayList<>();
        final RepetitionOrder[] orders = RepetitionOrder.values();
        if (orders.length < 5) {
            log.warn("Not enough RepetitionOrder values to create 5 repetitions. Progress ID: {}", progress.getId());
            return repetitions;
        }

        LocalDate previousDate = null;
        int previousIndex = -1;

        for (int i = 0; i < 5; i++) {
            LocalDate calculatedDate = calculateAdjustedReviewDate(progress, i, baseDate, dateCounts);

            if (previousDate != null) {
                final int minGap = getMinRequiredGap(i, previousIndex);
                final LocalDate minAllowedDate = previousDate.plusDays(minGap);
                if (calculatedDate.isBefore(minAllowedDate)) {
                    calculatedDate = minAllowedDate;
                }
            }

            final LocalDate optimalDate = findOptimalDate(calculatedDate, dateCounts);

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
     * Creates repetitions for a module progress with a specific start date.
     *
     * @param progress   The module progress.
     * @param startDate  The start date.
     * @param dateCounts Map of date counts.
     * @return A list of created repetitions.
     */
    @NonNull
    private List<Repetition> createRepetitionsForProgress(
            @NonNull ModuleProgress progress,
            @NonNull LocalDate startDate,
            @NonNull Map<LocalDate, Long> dateCounts) {

        final List<Repetition> repetitions = new ArrayList<>();
        final RepetitionOrder[] orders = RepetitionOrder.values();
        if (orders.length < 5) {
            log.warn("Not enough RepetitionOrder values to create 5 repetitions. Progress ID: {}", progress.getId());
            return repetitions;
        }

        LocalDate previousDate = null;
        int previousIndex = -1;

        for (int i = 0; i < 5; i++) {
            LocalDate calculatedDate = calculateAdjustedReviewDate(progress, i, startDate, dateCounts);

            if (previousDate != null) {
                final int minGap = getMinRequiredGap(previousIndex, i);
                final LocalDate minAllowedDate = previousDate.plusDays(minGap);
                if (calculatedDate.isBefore(minAllowedDate)) {
                    calculatedDate = minAllowedDate;
                }
            }

            final LocalDate optimalDate = findOptimalDate(calculatedDate, dateCounts);

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
        if (progress.getNextStudyDate() == null || !newNextStudyDate.equals(progress.getNextStudyDate())) {
            progress.setNextStudyDate(newNextStudyDate);
            progressRepository.save(progress);
            log.debug("Updated next study date to {} for progress ID: {}", newNextStudyDate, progress.getId());
            return;
        }

        log.trace("Next study date {} already up-to-date for progress ID: {}", newNextStudyDate, progress.getId());
    }

    /**
     * Gets the effective start date for schedule calculations.
     *
     * @param progress The module progress.
     * @return The effective start date.
     */
    @NonNull
    private LocalDate getEffectiveStartDate(@NonNull ModuleProgress progress) {
        // First check LearningCycle for current cycle
        final LocalDate cycleStart = progress.findLatestCycleStart(progress.getCyclesStudied());
        if (cycleStart != null) {
            log.debug("Using cycle start date {} from LearningCycle for cycle {} and progress ID: {}",
                    cycleStart, progress.getCyclesStudied(), progress.getId());
            return cycleStart;
        }

        // Then use firstLearningDate
        if (progress.getFirstLearningDate() != null) {
            log.debug("Using firstLearningDate {} for progress ID: {}",
                    progress.getFirstLearningDate(), progress.getId());
            return progress.getFirstLearningDate();
        }

        // Fallback to current date
        log.warn("Missing start date for cycle {} and progress ID: {}. Falling back to current date.",
                progress.getCyclesStudied(), progress.getId());
        return LocalDate.now();
    }

    private int getCycleStudiedCount(final CycleStudied cyclesStudied) {
        if (cyclesStudied == null) {
            return 0;
        }

        return switch (cyclesStudied) {
            case FIRST_REVIEW -> 1;
            case SECOND_REVIEW -> 2;
            case THIRD_REVIEW, MORE_THAN_THREE_REVIEWS -> 3;
            default -> 0;
        };
    }

    /**
     * Finds the least busy date within a search window for scheduling a repetition.
     *
     * @param initialDate The initial date to start the search from.
     * @param dateCounts  Map of date counts.
     * @return The optimal date for scheduling.
     * @throws IllegalArgumentException if the initial date is null.
     */
    @NonNull
    private LocalDate findOptimalDate(@NonNull LocalDate initialDate, @NonNull Map<LocalDate, Long> dateCounts) {
        if (initialDate.isBefore(LocalDate.now())) {
            log.debug("Initial date {} is in the past. Defaulting to today.", initialDate);
            return LocalDate.now();
        }

        final long initialCount = dateCounts.getOrDefault(initialDate, 0L);
        if (initialCount <= OPTIMAL_DATE_MAX_COUNT) {
            return initialDate;
        }

        for (int i = 1; i <= OPTIMAL_DATE_SEARCH_WINDOW_DAYS; i++) {
            final LocalDate candidate = initialDate.plusDays(i);
            if (candidate.isBefore(LocalDate.now())) {
                continue;
            }
            if (dateCounts.getOrDefault(candidate, 0L) <= OPTIMAL_DATE_MAX_COUNT) {
                return candidate;
            }
        }

        // fallback: just one day ahead or today
        final LocalDate fallback = initialDate.plusDays(1);
        return fallback.isBefore(LocalDate.now()) ? LocalDate.now() : fallback;
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

        final Map<LocalDate, Long> dateCounts = loadReviewDateCounts(newStartDate);

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

            final LocalDate calculatedDate = calculateAdjustedReviewDate(progress, repIndex, newStartDate, dateCounts);
            final int minRequiredGapDays = getMinRequiredGap(previousIndex, repIndex);
            final LocalDate minAllowedDate = previousDate.plusDays(minRequiredGapDays);

            final LocalDate adjustedDate = calculatedDate.isBefore(minAllowedDate) ? minAllowedDate : calculatedDate;
            final LocalDate optimalDate = findOptimalDate(adjustedDate, dateCounts);

            if (optimalDate.equals(repetition.getReviewDate())) {
                previousDate = optimalDate;
                previousIndex = repIndex;
                continue;
            }

            repetition.setReviewDate(optimalDate);
            log.debug("Rescheduled repetition {} to {} (calculated: {}, minGap: {}) for progress ID: {}",
                    repetition.getRepetitionOrder(), optimalDate, calculatedDate, minRequiredGapDays, progress.getId());

            changed = true;
            previousDate = optimalDate;
            previousIndex = repIndex;
        }

        if (!changed) {
            log.debug("No changes applied during rescheduling for progress ID: {}", progress.getId());
            return;
        }

        persistRepetitionsAndUpdateStudyDate(progress, futureRepetitions);
        log.info("Future repetitions rescheduled and saved for progress ID: {}", progress.getId());
    }

    private int getMinRequiredGap(int currentIndex, int previousIndex) {
        if (currentIndex >= REVIEW_MULTIPLIERS.length || previousIndex >= REVIEW_MULTIPLIERS.length) {
            return 0;
        }
        return REVIEW_MULTIPLIERS[currentIndex] - REVIEW_MULTIPLIERS[previousIndex];
    }

    /**
     * Calculates an adjusted review date based on module progress and learning factors.
     *
     * @param progress        The module progress.
     * @param repetitionIndex The repetition index.
     * @param baseDate        The base date for calculations.
     * @param dateCounts      Map of date counts for optimization.
     * @return The calculated adjusted review date.
     */
    @NonNull
    private LocalDate calculateAdjustedReviewDate(
            @NonNull ModuleProgress progress,
            int repetitionIndex,
            @NonNull LocalDate baseDate,
            @NonNull Map<LocalDate, Long> dateCounts) {

        final int wordCount = Optional.ofNullable(progress.getModule())
                .map(Module::getWordCount)
                .orElse(0);

        final double wordFactor = Math.max(MIN_DAILY_WORDS, wordCount > 0 ? wordCount : BASE_DAILY_WORDS)
                / BASE_DAILY_WORDS;
        final int studyCycleCount = getCycleStudiedCount(progress.getCyclesStudied());

        final double completedPercent = Optional.ofNullable(progress.getPercentComplete())
                .map(BigDecimal::doubleValue)
                .orElse(0.0);

        final double factor = 1.0
                + (studyCycleCount - 1) * STUDY_CYCLE_ADJUSTMENT_FACTOR
                + completedPercent / 100.0 * COMPLETION_ADJUSTMENT_FACTOR
                + (wordFactor - 1.0) * WORD_FACTOR_ADJUSTMENT;

        final int baseMultiplier = repetitionIndex < REVIEW_MULTIPLIERS.length
                ? REVIEW_MULTIPLIERS[repetitionIndex]
                : REVIEW_MULTIPLIERS[REVIEW_MULTIPLIERS.length - 1];

        final long dayOffset = Math.round(baseMultiplier * factor);
        final LocalDate calculatedDate = baseDate.plusDays(dayOffset);

        return findOptimalDate(calculatedDate, dateCounts);
    }

    // ----- COMPLETION HANDLING METHODS -----

    /**
     * Updates the review dates of future repetitions after a repetition is completed.
     *
     * @param progress          The module progress containing the repetitions.
     * @param currentRepetition The current repetition.
     */
    public void markCompletedRepetitions(@NonNull ModuleProgress progress, @NonNull Repetition currentRepetition) {
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

        // Create dateCounts map for the calculateAdjustedReviewDate method
        final Map<LocalDate, Long> dateCounts = loadReviewDateCounts(baseDate);

        LocalDate previousDate = baseDate;
        int previousIndex = currentIndex;

        for (final Repetition repetition : futureRepetitions) {
            final int repetitionIndex = getOrderIndex(repetition.getRepetitionOrder());
            if (repetitionIndex == -1) {
                continue;
            }

            // Tính khoảng cách tối thiểu giữa lần trước và lần hiện tại theo REVIEW_MULTIPLIERS
            final int minRequiredGapDays = getMinRequiredGap(previousIndex, repetitionIndex);

            // Tính ngày đề xuất
            final LocalDate calculatedDate = calculateAdjustedReviewDate(progress, repetitionIndex, baseDate,
                    dateCounts);

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
            persistRepetitionsAndUpdateStudyDate(progress, futureRepetitions);
            log.info("Saved updated review dates for progress ID: {}", progress.getId());
        }
    }

    private void persistRepetitionsAndUpdateStudyDate(ModuleProgress progress,
                                                      final List<Repetition> futureRepetitions) {
        repetitionRepository.saveAll(futureRepetitions);
        updateNextStudyDate(progress);
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

        // Add new cycle record to LearningCycle
        final LocalDate now = LocalDate.now();
        progress.addCycleStart(nextCycle, now);

        progress.setCyclesStudied(nextCycle);
        progressRepository.save(progress);
        log.info("Updated cycle studied from {} to {} for progress ID: {} starting on {}",
                currentCycle, nextCycle, progress.getId(), now);
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

        LocalDate fallbackDate = progress.getFirstLearningDate();
        if (fallbackDate == null) {
            fallbackDate = LocalDate.now();
            progress.setFirstLearningDate(fallbackDate);
        }

        final LocalDate proposedStartDate = lastCompletedDateOpt
                .map(date -> date.plusDays(7))
                .orElse(fallbackDate);

        final Map<LocalDate, Long> dateCounts = loadReviewDateCounts(proposedStartDate);
        final LocalDate optimizedStartDate = findOptimalDate(proposedStartDate, dateCounts);

        progress.addCycleStart(progress.getCyclesStudied(), optimizedStartDate);
        progressRepository.save(progress);

        log.debug("Set new reference date for cycle {} to {} for progress ID: {}",
                progress.getCyclesStudied(), optimizedStartDate, progress.getId());

        return createRepetitionsForProgress(progress, optimizedStartDate, dateCounts);
    }

    // ----- UTILITY METHODS -----

    /**
     * Retrieves future repetitions that are not yet started, sorted by repetition order.
     *
     * @param progress     The module progress to query.
     * @param currentIndex The index of the current repetition order.
     * @return A list of future repetitions sorted by repetition order.
     */
    @NonNull
    private List<Repetition> getFutureRepetitions(@NonNull ModuleProgress progress, int currentIndex) {
        final List<Repetition> pendingRepetitions = repetitionRepository
                .findByModuleProgressIdAndStatusOrderByRepetitionOrder(progress.getId(), RepetitionStatus.NOT_STARTED);

        return pendingRepetitions.stream()
                .filter(rep -> getOrderIndex(rep.getRepetitionOrder()) > currentIndex)
                .sorted((r1, r2) -> {
                    final int order1 = getOrderIndex(r1.getRepetitionOrder());
                    final int order2 = getOrderIndex(r2.getRepetitionOrder());
                    return Integer.compare(order1, order2);
                })
                .toList();
    }

    /**
     * Gets the index of a repetition order in the enum.
     *
     * @param order The repetition order.
     * @return The index of the order, or -1 if the order is null.
     */
    private int getOrderIndex(RepetitionOrder order) {
        return ORDER_INDEX_MAP.getOrDefault(order, -1);
    }

    private boolean validateRepetitionOrder(ModuleProgress progress, RepetitionOrder currentOrder, int currentIndex) {
        final RepetitionOrder[] allOrders = RepetitionOrder.values();
        if (currentIndex >= allOrders.length - 1) {
            log.debug("No future orders after {} for progress ID: {}", currentOrder, progress.getId());
            return true;
        }
        return false;
    }

    @NonNull
    private Map<LocalDate, Long> loadReviewDateCounts(@NonNull LocalDate startDate) {
        final LocalDate endDate = startDate.plusDays(RepetitionScheduleManager.MAX_DATE_CONSTRAINT_DAYS);
        final List<Object[]> rawCounts = repetitionRepository.countReviewDatesBetween(startDate, endDate);
        if (rawCounts == null || rawCounts.isEmpty()) {
            return Map.of();
        }

        return rawCounts.stream().collect(Collectors.toMap(
                arr -> {
                    final Object dateObj = arr[0];
                    if (dateObj instanceof final LocalDate localDate) {
                        return localDate;
                    }
                    if (dateObj instanceof final java.sql.Date sqlDate) {
                        return sqlDate.toLocalDate();
                    }
                    throw new IllegalStateException("Unexpected reviewDate type: " + dateObj.getClass());
                },
                arr -> ((Number) arr[1]).longValue(),
                (v1, v2) -> v1));
    }
}