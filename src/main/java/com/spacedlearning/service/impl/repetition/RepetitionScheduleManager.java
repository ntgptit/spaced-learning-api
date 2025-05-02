package com.spacedlearning.service.impl.repetition;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.spacedlearning.entity.ModuleProgress;
import com.spacedlearning.entity.Repetition;
import com.spacedlearning.entity.enums.RepetitionOrder;
import com.spacedlearning.entity.enums.RepetitionStatus;
import com.spacedlearning.repository.ModuleProgressRepository;
import com.spacedlearning.repository.RepetitionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Main class responsible for managing repetition schedules.
 * Coordinates between calculator and cycle management.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RepetitionScheduleManager {
    private final RepetitionRepository repetitionRepository;
    private final ModuleProgressRepository progressRepository;
    private final RepetitionDateCalculator dateCalculator;
    private final CycleManager cycleManager;

    /**
     * Initializes the first learning date for a module progress if not set.
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
     */
    @NonNull
    public List<Repetition> createRepetitionsForProgress(@NonNull ModuleProgress progress) {
        // Ensure first cycle is initialized
        cycleManager.initializeFirstCycle(progress);

        final List<Repetition> repetitions = new ArrayList<>();
        final RepetitionOrder[] orders = RepetitionOrder.values();

        if (orders.length < 5) {
            log.warn("Not enough RepetitionOrder values to create 5 repetitions. Progress ID: {}", progress.getId());
            return repetitions;
        }

        LocalDate previousDate = null;
        int previousIndex = -1;

        for (int i = 0; i < 5; i++) {
            // Calculate date based on progress and index
            LocalDate calculatedDate = dateCalculator.calculateStandardReviewDate(progress, i,
                    progress.getEffectiveStartDate());

            // Ensure minimum gap between consecutive repetitions
            if (previousDate != null && previousIndex >= 0) {
                calculatedDate = enforceMinimumGap(calculatedDate, previousDate, i, previousIndex);
            }

            // Create the repetition
            final Repetition repetition = createRepetition(progress, orders[i], calculatedDate);
            repetitions.add(repetition);

            previousDate = repetition.getReviewDate();
            previousIndex = i;
        }

        return repetitions;
    }

    /**
     * Updates the next study date for a module progress based on pending repetitions.
     */
    public void updateNextStudyDate(@NonNull ModuleProgress progress) {
        final List<Repetition> pendingRepetitions = repetitionRepository
                .findByModuleProgressIdAndStatusOrderByReviewDate(progress.getId(), RepetitionStatus.NOT_STARTED);

        // Early return if no pending repetitions
        if (pendingRepetitions.isEmpty()) {
            clearNextStudyDateIfNeeded(progress);
            return;
        }

        final LocalDate newNextStudyDate = pendingRepetitions.get(0).getReviewDate();

        // Early return if next study date hasn't changed
        if (progress.getNextStudyDate() != null && newNextStudyDate.equals(progress.getNextStudyDate())) {
            log.trace("Next study date {} already up-to-date for progress ID: {}",
                    newNextStudyDate, progress.getId());
            return;
        }

        // Update next study date
        progress.setNextStudyDate(newNextStudyDate);
        progressRepository.save(progress);
        log.debug("Updated next study date to {} for progress ID: {}", newNextStudyDate, progress.getId());
    }

    /**
     * Reschedules future repetitions based on a new start date.
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

        if (!isValidRepetitionOrder(progress, currentOrder, currentIndex)) {
            return;
        }

        final List<Repetition> futureRepetitions = getFutureRepetitions(progress, currentIndex);
        if (futureRepetitions.isEmpty()) {
            log.debug("No future repetitions found to reschedule for progress ID: {}", progress.getId());
            return;
        }

        log.info("Rescheduling {} future repetitions starting from {} for progress ID: {}",
                futureRepetitions.size(), newStartDate, progress.getId());

        final boolean changed = rescheduleRepetitions(progress, futureRepetitions, currentIndex, newStartDate);

        if (changed) {
            repetitionRepository.saveAll(futureRepetitions);
            updateNextStudyDate(progress);
            log.info("Future repetitions rescheduled and saved for progress ID: {}", progress.getId());
        } else {
            log.debug("No changes applied during rescheduling for progress ID: {}", progress.getId());
        }
    }

    /**
     * Updates the review dates of future repetitions after a repetition is completed.
     */
    public void markCompletedRepetitions(@NonNull ModuleProgress progress, @NonNull Repetition currentRepetition) {
        final RepetitionOrder currentOrder = currentRepetition.getRepetitionOrder();
        final int currentIndex = getOrderIndex(currentOrder);

        if (currentIndex == -1 || !isValidRepetitionOrder(progress, currentOrder, currentIndex)) {
            return;
        }

        final List<Repetition> futureRepetitions = getFutureRepetitions(progress, currentIndex);
        if (futureRepetitions.isEmpty()) {
            return;
        }

        log.info("Updating future repetitions after completion for progress ID: {}", progress.getId());

        final LocalDate baseDate = currentRepetition.getReviewDate().isAfter(LocalDate.now())
                ? currentRepetition.getReviewDate()
                : LocalDate.now();

        final boolean changed = updateFutureRepetitionDates(progress, futureRepetitions, currentIndex, baseDate);

        if (changed) {
            repetitionRepository.saveAll(futureRepetitions);
            log.info("Saved updated review dates for progress ID: {}", progress.getId());
        }
    }

    /**
     * Checks and updates the study cycle for a module progress based on completed repetitions.
     */
    public void checkAndUpdateCycleStudied(@NonNull ModuleProgress progress) {
        cycleManager.checkAndUpdateCycleStudied(progress, this::createRepetitionsForProgress);
    }

    // ---- PRIVATE HELPER METHODS ----

    /**
     * Clears the next study date if it's set.
     */
    private void clearNextStudyDateIfNeeded(ModuleProgress progress) {
        if (progress.getNextStudyDate() == null) {
            return;
        }

        progress.setNextStudyDate(null);
        progressRepository.save(progress);
        log.debug("Cleared next study date as no pending repetitions found for progress ID: {}", progress.getId());
    }

    /**
     * Creates a repetition with the specified order and date.
     */
    private Repetition createRepetition(ModuleProgress progress, RepetitionOrder order, LocalDate reviewDate) {
        final Repetition repetition = new Repetition();
        repetition.setModuleProgress(progress);
        repetition.setRepetitionOrder(order);
        repetition.setStatus(RepetitionStatus.NOT_STARTED);
        repetition.setReviewDate(reviewDate);
        return repetition;
    }

    /**
     * Enforces a minimum gap between repetition dates.
     */
    private LocalDate enforceMinimumGap(LocalDate calculatedDate, LocalDate previousDate,
            int currentIndex, int previousIndex) {
        final int[] reviewMultipliers = { 2, 4, 8, 13, 19, 26 };
        final int minRequiredGapDays = reviewMultipliers[currentIndex] - reviewMultipliers[previousIndex];
        final LocalDate minAllowedDate = previousDate.plusDays(minRequiredGapDays);

        if (calculatedDate.isBefore(minAllowedDate)) {
            return minAllowedDate;
        }
        return calculatedDate;
    }

    /**
     * Reschedules a list of repetitions based on a new start date.
     * Returns true if any changes were made.
     */
    private boolean rescheduleRepetitions(ModuleProgress progress, List<Repetition> repetitions,
            int currentIndex, LocalDate baseDate) {
        boolean changed = false;
        LocalDate previousDate = baseDate;
        int previousIndex = currentIndex;

        for (final Repetition repetition : repetitions) {
            final int repIndex = getOrderIndex(repetition.getRepetitionOrder());
            if (repIndex < 0) {
                log.warn("Cannot determine index for repetition ID: {}", repetition.getId());
                continue;
            }

            // Calculate new date with reschedule strategy
            final LocalDate calculatedDate = dateCalculator.calculateRescheduleReviewDate(
                    progress, repIndex, baseDate);

            // Enforce minimum gap between repetitions
            final LocalDate adjustedDate = enforceMinimumGap(
                    calculatedDate, previousDate, repIndex, previousIndex);

            // Update only if the date has changed
            if (!adjustedDate.equals(repetition.getReviewDate())) {
                repetition.setReviewDate(adjustedDate);
                log.debug("Rescheduled repetition {} to {} for progress ID: {}",
                        repetition.getRepetitionOrder(), adjustedDate, progress.getId());
                changed = true;
            }

            previousDate = adjustedDate;
            previousIndex = repIndex;
        }

        return changed;
    }

    /**
     * Updates dates for future repetitions after completion.
     * Returns true if any changes were made.
     */
    private boolean updateFutureRepetitionDates(ModuleProgress progress, List<Repetition> repetitions,
            int currentIndex, LocalDate baseDate) {
        boolean changed = false;
        LocalDate previousDate = baseDate;
        int previousIndex = currentIndex;

        for (final Repetition repetition : repetitions) {
            final int repIndex = getOrderIndex(repetition.getRepetitionOrder());
            if (repIndex < 0) {
                continue;
            }

            // Calculate new date based on completion
            final LocalDate calculatedDate = dateCalculator.calculateCompletionReviewDate(
                    progress, repIndex, baseDate);

            // Enforce minimum gap
            final LocalDate newReviewDate = enforceMinimumGap(
                    calculatedDate, previousDate, repIndex, previousIndex);

            // Update only if changed
            if (!newReviewDate.equals(repetition.getReviewDate())) {
                repetition.setReviewDate(newReviewDate);
                changed = true;
                log.debug("Updated repetition {} to new review date: {}",
                        repetition.getRepetitionOrder(), newReviewDate);
            }

            previousDate = newReviewDate;
            previousIndex = repIndex;
        }

        return changed;
    }

    /**
     * Gets the index of a repetition order in the enum.
     */
    private int getOrderIndex(RepetitionOrder order) {
        if (order == null) {
            return -1;
        }
        return Arrays.asList(RepetitionOrder.values()).indexOf(order);
    }

    /**
     * Validates if there are more orders after the current one.
     */
    private boolean isValidRepetitionOrder(ModuleProgress progress, RepetitionOrder currentOrder, int currentIndex) {
        final RepetitionOrder[] allOrders = RepetitionOrder.values();
        if (currentIndex >= allOrders.length - 1) {
            log.debug("No future orders after {} for progress ID: {}", currentOrder, progress.getId());
            return false;
        }
        return true;
    }

    /**
     * Retrieves future repetitions that are not yet started, sorted by repetition order.
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
}