package com.spacedlearning.service.impl.repetition;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

//New class to handle scheduling logic
@Component
@RequiredArgsConstructor
@Slf4j
public class RepetitionScheduleManager {

    // Base parameters for spaced repetition algorithm
    private static final double BASE_DAILY_WORDS = 41.7;
    private static final int[] REVIEW_MULTIPLIERS = { 2, 4, 8, 13, 19, 26 };
    private static final int MAX_WORD_FACTOR = 3;
    private static final int MAX_INTERVAL_DAYS = 31;
    private static final int MIN_DAILY_WORDS = 20;

    private final RepetitionRepository repetitionRepository;
    private final ModuleProgressRepository progressRepository;

    public void initializeFirstLearningDate(ModuleProgress progress) {
        if (progress.getFirstLearningDate() == null) {
            progress.setFirstLearningDate(LocalDate.now());
            progressRepository.save(progress);
        }
    }

    public List<Repetition> createRepetitionsForProgress(ModuleProgress progress) {
        final List<Repetition> repetitions = new ArrayList<>();
        final RepetitionOrder[] orders = RepetitionOrder.values();

        for (int i = 0; i < orders.length; i++) {
            final Repetition repetition = createRepetition(progress, orders[i], i);
            repetitions.add(repetition);
        }
        return repetitions;
    }

    /**
     * Reschedule future repetitions based on a new start date for a specific repetition
     *
     * @param progress     Progress record
     * @param currentOrder Current repetition order
     * @param newStartDate New start date for the current repetition
     */
    public void rescheduleFutureRepetitions(
            ModuleProgress progress,
            RepetitionOrder currentOrder,
            LocalDate newStartDate) {
        final int currentIndex = getOrderIndex(currentOrder);
        if (currentIndex == -1 || currentIndex >= RepetitionOrder.values().length - 1) {
            return;
        }

        final List<Repetition> futureRepetitions = getFutureRepetitions(progress, currentIndex);

        if (futureRepetitions.isEmpty()) {
            log.debug("No future repetitions to reschedule for progress ID: {}", progress.getId());
            return;
        }

        log.info("Rescheduling {} future repetitions based on new date: {}", Optional.of(futureRepetitions.size()), newStartDate);

        // Calculate intervals relative to the current repetition
        RepetitionOrder.values();

        for (final Repetition repetition : futureRepetitions) {
            final int repIndex = getOrderIndex(repetition.getRepetitionOrder());
            Math.max(1, repIndex - currentIndex);

            // Calculate new review date based on spaced repetition intervals
            // Use the review multipliers to maintain proper spacing
            final int dayOffset = REVIEW_MULTIPLIERS[repIndex] - REVIEW_MULTIPLIERS[currentIndex];
            final LocalDate newReviewDate = newStartDate.plusDays(dayOffset);

            repetition.setReviewDate(newReviewDate);
            log.debug("Rescheduled repetition {} to {}", repetition.getRepetitionOrder(), newReviewDate);
        }

        repetitionRepository.saveAll(futureRepetitions);
        updateNextStudyDate(progress);
    }

    private Repetition createRepetition(ModuleProgress progress, RepetitionOrder order, int reviewIndex) {
        final LocalDate reviewDate = calculateReviewDate(progress, reviewIndex);
        final Repetition repetition = new Repetition();
        repetition.setModuleProgress(progress);
        repetition.setRepetitionOrder(order);
        repetition.setStatus(RepetitionStatus.NOT_STARTED);
        repetition.setReviewDate(reviewDate);
        return repetition;
    }

    public LocalDate calculateReviewDate(ModuleProgress progress, int reviewIndex) {
        final Integer wordCount = progress.getModule() != null ? progress.getModule().getWordCount() : (Integer) 0;
        final double dailyWordCount = Math.max(MIN_DAILY_WORDS, wordCount > 0 ? wordCount : BASE_DAILY_WORDS);
        final int studyCycleCount = getStudyCycleCount(progress.getCyclesStudied());
        final BigDecimal percentComplete = progress.getPercentComplete();

        final int adjustedCycleCount = Math.max(1, studyCycleCount);
        final double wordFactor = calculateWordFactor(dailyWordCount);
        final double baseInterval = calculateBaseInterval(reviewIndex, adjustedCycleCount, wordFactor);
        final LocalDate referenceDate = progress.getFirstLearningDate() != null ? progress.getFirstLearningDate()
                : LocalDate.now();

        final double daysToAdd = percentComplete == null || percentComplete.doubleValue() == 0.0
                ? baseInterval
                : baseInterval * percentComplete.doubleValue() / 100.0;

        final LocalDate initialDate = referenceDate.plusDays(Math.round(daysToAdd));
        final LocalDate constrainedDate = constrainDate(progress, initialDate);
        return findOptimalDate(constrainedDate);
    }

    private LocalDate constrainDate(ModuleProgress progress, LocalDate initialDate) {
        if (progress.getFirstLearningDate() != null && initialDate.minusDays(60).isAfter(progress
                .getFirstLearningDate())) {
            return progress.getFirstLearningDate().plusDays(60);
        }
        return initialDate;
    }

    private LocalDate findOptimalDate(LocalDate initialDate) {
        final int repetitionCount = getRepetitionCount(initialDate);
        if (repetitionCount <= 3) {
            return initialDate;
        }

        log.warn("Initial date {} is overloaded (count={}). Searching alternatives...", Optional.of(Optional.of(Optional.ofNullable(initialDate))), Optional.of(repetitionCount));
        for (int i = 1; i <= 7; i++) {
            assert initialDate != null;
            final LocalDate candidateDate = initialDate.plusDays(i);
            if (getRepetitionCount(candidateDate) <= 3) {
                log.info("Found suitable alternative date: {}", candidateDate);
                return candidateDate;
            }
        }
        final LocalDate fallbackDate = initialDate.plusDays(1);
        log.warn("No suitable alternative found. Falling back to: {}", fallbackDate);
        return fallbackDate;
    }

    private int getRepetitionCount(LocalDate date) {
        try {
            return repetitionRepository.countReviewDateExisted(date);
        } catch (final Exception e) {
            log.error("Error counting repetitions for date {}: {}", date, e.getMessage(), e);
            return Integer.MAX_VALUE;
        }
    }

    private double calculateWordFactor(double dailyWordCount) {
        return Math.min(MAX_WORD_FACTOR, Math.max(dailyWordCount, BASE_DAILY_WORDS) / BASE_DAILY_WORDS);
    }

    private double calculateBaseInterval(int reviewIndex, int adjustedCycleCount, double wordFactor) {
        final int baseMultiplier = reviewIndex < REVIEW_MULTIPLIERS.length ? REVIEW_MULTIPLIERS[reviewIndex]
                : REVIEW_MULTIPLIERS[REVIEW_MULTIPLIERS.length - 1];
        return wordFactor * Math.min(MAX_INTERVAL_DAYS, adjustedCycleCount * baseMultiplier);
    }

    public void updateNextStudyDate(ModuleProgress progress) {
        final List<Repetition> pendingRepetitions = repetitionRepository
                .findByModuleProgressIdAndStatusOrderByReviewDate(progress.getId(), RepetitionStatus.NOT_STARTED);
        if (!pendingRepetitions.isEmpty()) {
            progress.setNextStudyDate(pendingRepetitions.get(0).getReviewDate());
            progressRepository.save(progress);
            log.debug("Updated next study date to {} for progress ID: {}", progress.getNextStudyDate(), progress
                    .getId());
        }
    }

    public void updateFutureRepetitions(ModuleProgress progress, RepetitionOrder currentOrder) {
        final int currentIndex = getOrderIndex(currentOrder);
        if (currentIndex == -1 || currentIndex >= RepetitionOrder.values().length - 1) {
            return;
        }

        final List<Repetition> futureRepetitions = getFutureRepetitions(progress, currentIndex);
        updateRepetitionDates(progress, futureRepetitions);
    }

    private List<Repetition> getFutureRepetitions(ModuleProgress progress, int currentIndex) {
        final List<Repetition> futureRepetitions = repetitionRepository
                .findByModuleProgressIdAndStatusOrderByRepetitionOrder(progress.getId(), RepetitionStatus.NOT_STARTED);
        return futureRepetitions.stream()
                .filter(rep -> getOrderIndex(rep.getRepetitionOrder()) > currentIndex)
                .toList();
    }

    private void updateRepetitionDates(ModuleProgress progress, List<Repetition> repetitions) {
        if (repetitions.isEmpty()) {
            log.debug("No future repetitions to update for progress ID: {}", progress.getId());
            return;
        }

        for (final Repetition repetition : repetitions) {
            final int repetitionIndex = getOrderIndex(repetition.getRepetitionOrder());
            final LocalDate newReviewDate = calculateReviewDate(progress, repetitionIndex);
            repetition.setReviewDate(newReviewDate);
        }
        repetitionRepository.saveAll(repetitions);
    }

    private int getOrderIndex(RepetitionOrder order) {
        return Arrays.asList(RepetitionOrder.values()).indexOf(order);
    }

    public void checkAndUpdateCycleStudied(ModuleProgress progress) {
        final long totalRepetitionCount = repetitionRepository.countByModuleProgressId(progress.getId());
        final long completedCount = repetitionRepository.countByModuleProgressIdAndStatus(progress.getId(),
                RepetitionStatus.COMPLETED);

        if (completedCount >= totalRepetitionCount) {
            updateToNextCycleLevel(progress);
            createNewRepetitionCycle(progress);
        }
    }

    private void updateToNextCycleLevel(ModuleProgress progress) {
        final CycleStudied currentCycle = progress.getCyclesStudied();
        final CycleStudied nextCycle = getNextCycle(currentCycle);
        if (nextCycle != currentCycle) {
            progress.setCyclesStudied(nextCycle);
            progressRepository.save(progress);
            log.info("Updated cycle studied to {} for progress ID: {}", nextCycle, progress.getId());
        }
    }

    private CycleStudied getNextCycle(CycleStudied current) {
        switch (current) {
        case FIRST_TIME:
            return CycleStudied.FIRST_REVIEW;
        case FIRST_REVIEW:
            return CycleStudied.SECOND_REVIEW;
        case SECOND_REVIEW:
            return CycleStudied.THIRD_REVIEW;
        case THIRD_REVIEW:
            return CycleStudied.MORE_THAN_THREE_REVIEWS;
        default:
            return current;
        }
    }

    private void createNewRepetitionCycle(ModuleProgress progress) {
        markExistingRepetitionsAsCompleted(progress);
        final List<Repetition> newRepetitions = createRepetitionsWithCurrentDate(progress);
        repetitionRepository.saveAll(newRepetitions);
        updateNextStudyDate(progress);
    }

    private void markExistingRepetitionsAsCompleted(ModuleProgress progress) {
        final List<Repetition> existingRepetitions = repetitionRepository.findByModuleProgressIdOrderByRepetitionOrder(
                progress.getId());
        existingRepetitions.stream()
                .filter(rep -> rep.getStatus() != RepetitionStatus.COMPLETED)
                .forEach(rep -> rep.setStatus(RepetitionStatus.COMPLETED));
        repetitionRepository.saveAll(existingRepetitions);
    }

    private List<Repetition> createRepetitionsWithCurrentDate(ModuleProgress progress) {
        final Optional<LocalDate> lastCompletedDate = repetitionRepository.findLastCompletedRepetitionDate(progress
                .getId());
        if (lastCompletedDate.isPresent()) {
            progress.setFirstLearningDate(lastCompletedDate.get().plusDays(1));
            progress.setNextStudyDate(calculateReviewDate(progress, 0));
            progressRepository.save(progress);
        }
        return createRepetitionsForProgress(progress);
    }

    private int getStudyCycleCount(CycleStudied cyclesStudied) {
        if (cyclesStudied == null) {
            return 0;
        }
        switch (cyclesStudied) {
        case FIRST_TIME:
            return 0;
        case FIRST_REVIEW:
            return 1;
        case SECOND_REVIEW:
            return 2;
        case THIRD_REVIEW, MORE_THAN_THREE_REVIEWS:
            return 3;
        default:
            return 0;
        }
    }
}