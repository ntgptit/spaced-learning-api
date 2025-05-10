package com.spacedlearning.service.impl.repetition;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.spacedlearning.entity.ModuleProgress;
import com.spacedlearning.entity.Repetition;
import com.spacedlearning.entity.enums.RepetitionOrder;
import com.spacedlearning.entity.enums.RepetitionStatus;
import com.spacedlearning.repository.RepetitionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class RepetitionRescheduler {

    private final RepetitionDateCalculator calculator;
    private final RepetitionDateOptimizer optimizer;
    private final RepetitionRepository repetitionRepository;
    private final RepetitionScheduleManager scheduleManager;

    private List<Repetition> getFutureRepetitions(ModuleProgress progress, int currentIndex) {
        return this.repetitionRepository.findByModuleProgressIdAndStatusOrderByRepetitionOrder(
                progress.getId(), RepetitionStatus.NOT_STARTED)
                .stream()
                .filter(rep -> this.calculator.getOrderIndex(rep.getRepetitionOrder()) > currentIndex)
                .sorted(Comparator.comparingInt(rep -> this.calculator.getOrderIndex(rep.getRepetitionOrder())))
                .toList();
    }

    public void rescheduleFutureRepetitions(@NonNull ModuleProgress progress,
            @NonNull RepetitionOrder currentOrder,
            @NonNull LocalDate newStartDate) {
        final var currentIndex = this.calculator.getOrderIndex(currentOrder);
        if ((currentIndex < 0) || this.calculator.isFinalRepetition(currentIndex)) {
            log.debug("No future repetitions to reschedule for progress ID: {}", progress.getId());
            return;
        }

        final var futureReps = getFutureRepetitions(progress, currentIndex);
        if (futureReps.isEmpty()) {
            return;
        }

        final var dateCounts = this.scheduleManager.loadReviewDateCounts(newStartDate);
        var changed = false;

        var prevDate = newStartDate;
        var prevIndex = currentIndex;

        for (final Repetition rep : futureReps) {
            final var repIndex = this.calculator.getOrderIndex(rep.getRepetitionOrder());
            if (repIndex < 0) {
                continue;
            }

            final var calculated = this.calculator.calculateAdjustedDate(progress, repIndex, newStartDate,
                    dateCounts);
            final var minAllowed = prevDate.plusDays(this.calculator.getMinRequiredGap(prevIndex, repIndex));
            final var adjusted = calculated.isBefore(minAllowed) ? minAllowed : calculated;
            final var optimal = this.optimizer.findOptimalDate(adjusted, dateCounts);

            if (!optimal.equals(rep.getReviewDate())) {
                rep.setReviewDate(optimal);
                changed = true;
                log.debug("Rescheduled repetition {} to {} for progress ID: {}",
                        rep.getRepetitionOrder(), optimal, progress.getId());
            }

            prevDate = optimal;
            prevIndex = repIndex;
        }

        if (changed) {
            this.repetitionRepository.saveAll(futureReps);
            this.scheduleManager.updateNextStudyDate(progress);
            log.info("Future repetitions rescheduled and saved for progress ID: {}", progress
                    .getId());
        }
    }

    public void updateFollowingAfterCompletion(@NonNull ModuleProgress progress, @NonNull Repetition completed) {
        final var currentIndex = this.calculator.getOrderIndex(completed.getRepetitionOrder());
        if ((currentIndex < 0) || this.calculator.isFinalRepetition(currentIndex)) {
            return;
        }

        final var future = getFutureRepetitions(progress, currentIndex);
        if (future.isEmpty()) {
            return;
        }

        log.info("Updating future repetitions after completion for progress ID: {}", progress
                .getId());
        var changed = false;

        final var baseDate = completed.getReviewDate().isAfter(LocalDate.now())
                ? completed.getReviewDate()
                : LocalDate.now();
        final var dateCounts = this.scheduleManager.loadReviewDateCounts(baseDate);

        var prevDate = baseDate;
        var prevIndex = currentIndex;

        for (final Repetition rep : future) {
            final var index = this.calculator.getOrderIndex(rep.getRepetitionOrder());
            if (index < 0) {
                continue;
            }

            final var calculated = this.calculator.calculateAdjustedDate(progress, index, baseDate, dateCounts);
            final var minAllowed = prevDate.plusDays(this.calculator.getMinRequiredGap(prevIndex, index));
            final var newDate = calculated.isBefore(minAllowed) ? minAllowed : calculated;

            if (!newDate.equals(rep.getReviewDate())) {
                rep.setReviewDate(newDate);
                changed = true;
                log.debug("Updated repetition {} to new review date: {} (previous: {})",
                        rep.getRepetitionOrder(), newDate, rep.getReviewDate());
            }

            prevDate = newDate;
            prevIndex = index;
        }

        if (changed) {
            this.repetitionRepository.saveAll(future);
            this.scheduleManager.updateNextStudyDate(progress);
            log.info("Updated and saved future repetitions for progress ID: {}", progress.getId());
        }
    }
}
