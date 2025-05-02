package com.spacedlearning.service.impl.repetition;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.spacedlearning.entity.ModuleProgress;
import com.spacedlearning.entity.enums.CycleStudied;
import com.spacedlearning.repository.RepetitionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Utility class for calculating repetition dates based on various strategies.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RepetitionDateCalculator {
    // Constants for date calculation
    private static final double BASE_DAILY_WORDS = 41.7;
    private static final int[] REVIEW_MULTIPLIERS = { 2, 4, 8, 13, 19, 26 };
    private static final int MAX_WORD_FACTOR = 3;
    private static final int MIN_DAILY_WORDS = 20;
    private static final int MAX_INTERVAL_DAYS = 31;
    private static final int MAX_DATE_CONSTRAINT_DAYS = 60;
    private static final long OPTIMAL_DATE_MAX_COUNT = 3L;
    private static final int OPTIMAL_DATE_SEARCH_WINDOW_DAYS = 7;

    // Adjustment factors
    private static final double DEFAULT_ADJUSTMENT_FACTOR = 1.0;
    private static final double COMPLETION_ADJUSTMENT_FACTOR = 0.5;
    private static final double STUDY_CYCLE_ADJUSTMENT_FACTOR = 0.2;
    private static final double WORD_FACTOR_ADJUSTMENT = 0.3;

    private final RepetitionRepository repetitionRepository;

    /**
     * Calculates a standard review date for initial scheduling.
     *
     * @param progress    The module progress
     * @param reviewIndex The review index
     * @param baseDate    The base date to calculate from
     * @return The calculated review date
     */
    public LocalDate calculateStandardReviewDate(@NonNull ModuleProgress progress, int reviewIndex,
            @NonNull LocalDate baseDate) {
        return calculateReviewDate(progress, reviewIndex, baseDate, DEFAULT_ADJUSTMENT_FACTOR);
    }

    /**
     * Calculates a review date after a repetition is completed.
     *
     * @param progress    The module progress
     * @param reviewIndex The review index
     * @param baseDate    The base date to calculate from
     * @return The calculated review date
     */
    public LocalDate calculateCompletionReviewDate(@NonNull ModuleProgress progress, int reviewIndex,
            @NonNull LocalDate baseDate) {
        final BigDecimal percent = progress.getPercentComplete();
        final double score = percent != null ? percent.doubleValue() / 100.0 : 0.0;
        return calculateReviewDate(progress, reviewIndex, baseDate, score);
    }

    /**
     * Calculates a review date for rescheduling.
     *
     * @param progress    The module progress
     * @param reviewIndex The review index
     * @param baseDate    The base date to calculate from
     * @return The calculated review date
     */
    public LocalDate calculateRescheduleReviewDate(@NonNull ModuleProgress progress, int reviewIndex,
            @NonNull LocalDate baseDate) {
        // Apply combined factors for reschedule calculation
        final int studyCycleCount = getCycleStudiedCount(progress.getCyclesStudied());
        final BigDecimal percent = progress.getPercentComplete();
        final double completedPercent = percent != null ? percent.doubleValue() : 0.0;
        final double wordFactor = calculateWordFactor(getWordCount(progress));

        final double factor = 1.0
                + (studyCycleCount - 1) * STUDY_CYCLE_ADJUSTMENT_FACTOR
                + completedPercent / 100.0 * COMPLETION_ADJUSTMENT_FACTOR
                + (wordFactor - 1.0) * WORD_FACTOR_ADJUSTMENT;

        return calculateReviewDate(progress, reviewIndex, baseDate, factor);
    }

    /**
     * Main method for calculating review dates with different adjustment factors.
     */
    private LocalDate calculateReviewDate(@NonNull ModuleProgress progress, int reviewIndex,
            @NonNull LocalDate baseDate, double adjustmentFactor) {
        // Get basic parameters
        final int wordCount = getWordCount(progress);
        final double wordFactor = calculateWordFactor(wordCount);
        final int studyCycleCount = getCycleStudiedCount(progress.getCyclesStudied());
        final BigDecimal percentComplete = progress.getPercentComplete();

        getBaseMultiplier(reviewIndex);
        final double baseInterval = getBaseInterval(reviewIndex, studyCycleCount, wordFactor);

        // Apply adjustment factor
        double daysToAdd = baseInterval * adjustmentFactor;

        // Adjust for completion percentage if available
        if (percentComplete != null && percentComplete.doubleValue() > 0.0) {
            daysToAdd = daysToAdd * (percentComplete.doubleValue() / 100.0);
        }

        // Calculate the date and constrain if needed
        LocalDate calculatedDate = baseDate.plusDays(Math.round(daysToAdd));
        if (calculatedDate.isAfter(baseDate.plusDays(MAX_DATE_CONSTRAINT_DAYS))) {
            log.debug("Constraining date {} to {} days after reference date for progress ID: {}",
                    calculatedDate, MAX_DATE_CONSTRAINT_DAYS, progress.getId());
            calculatedDate = baseDate.plusDays(MAX_DATE_CONSTRAINT_DAYS);
        }

        // Find an optimal date with less load
        return findOptimalDate(calculatedDate);
    }

    /**
     * Calculates the base interval for a review.
     */
    private double getBaseInterval(int reviewIndex, int studyCycleCount, double wordFactor) {
        final int adjustedCycleCount = Math.max(1, studyCycleCount);

        final int baseMultiplier = getBaseMultiplier(reviewIndex);
        double baseInterval = wordFactor * Math.min(MAX_INTERVAL_DAYS, (double) adjustedCycleCount * baseMultiplier);

        if (adjustedCycleCount >= 3 && reviewIndex >= 2) {
            baseInterval = Math.sqrt((double) adjustedCycleCount * baseMultiplier) * wordFactor * 5.0;
        }
        return baseInterval;
    }

    /**
     * Gets the multiplier for the review index.
     */
    private int getBaseMultiplier(int reviewIndex) {
        return reviewIndex < REVIEW_MULTIPLIERS.length
                ? REVIEW_MULTIPLIERS[reviewIndex]
                : REVIEW_MULTIPLIERS[REVIEW_MULTIPLIERS.length - 1];
    }

    /**
     * Calculates the word factor based on word count.
     */
    private double calculateWordFactor(int wordCount) {
        return Math.min(MAX_WORD_FACTOR,
                Math.max(wordCount > 0 ? wordCount : BASE_DAILY_WORDS, MIN_DAILY_WORDS) / BASE_DAILY_WORDS);
    }

    /**
     * Gets the word count from a module progress.
     */
    private int getWordCount(ModuleProgress progress) {
        return progress.getModule() != null ? progress.getModule().getWordCount() : 0;
    }

    /**
     * Maps cycle studied enum to a count.
     */
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
     * Finds the optimal date for scheduling to balance load.
     */
    @NonNull
    public LocalDate findOptimalDate(LocalDate initialDate) {
        if (initialDate == null) {
            throw new IllegalArgumentException("Initial date cannot be null for findOptimalDate");
        }

        // Don't schedule in the past
        if (initialDate.isBefore(LocalDate.now())) {
            return LocalDate.now();
        }

        final LocalDate endDate = initialDate.plusDays(OPTIMAL_DATE_SEARCH_WINDOW_DAYS);
        log.debug("Finding optimal date starting from {} within window ending {}", initialDate, endDate);

        final List<Object[]> dateCountsList = repetitionRepository.countReviewDatesBetween(initialDate, endDate);
        if (dateCountsList == null || dateCountsList.isEmpty()) {
            return initialDate;
        }

        final Map<LocalDate, Long> dateCounts = dateCountsList.stream()
                .collect(Collectors.toMap(
                        arr -> convertToLocalDate(arr[0]),
                        arr -> ((Number) arr[1]).longValue(),
                        (v1, v2) -> v1));

        // Check if the initial date is already optimal
        final Long initialCount = dateCounts.getOrDefault(initialDate, 0L);
        if (initialCount <= OPTIMAL_DATE_MAX_COUNT) {
            log.debug("Initial date {} is optimal (count={}).", initialDate, initialCount);
            return initialDate;
        }

        // Find an alternative date with less load
        for (int i = 1; i <= OPTIMAL_DATE_SEARCH_WINDOW_DAYS; i++) {
            final LocalDate candidateDate = initialDate.plusDays(i);
            final Long count = dateCounts.getOrDefault(candidateDate, 0L);
            if (count <= OPTIMAL_DATE_MAX_COUNT) {
                log.info("Found suitable alternative date: {} (count={}) for initial date {}",
                        candidateDate, count, initialDate);
                return candidateDate;
            }
        }

        // Fallback to the day after if no optimal date found
        log.warn("No optimal date found within {} days. Falling back to: {}",
                OPTIMAL_DATE_SEARCH_WINDOW_DAYS, initialDate.plusDays(1));
        return initialDate.plusDays(1);
    }

    /**
     * Safely converts database object to LocalDate.
     */
    private LocalDate convertToLocalDate(Object dateObj) {
        if (dateObj instanceof LocalDate) {
            return (LocalDate) dateObj;
        }
        if (dateObj instanceof java.sql.Date) {
            return ((java.sql.Date) dateObj).toLocalDate();
        }
        log.error("Unexpected reviewDate type from repository: {}", dateObj.getClass());
        throw new IllegalStateException("Unexpected reviewDate type: " + dateObj.getClass());
    }
}