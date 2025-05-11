package com.spacedlearning.service.impl.repetition;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import com.spacedlearning.entity.ModuleProgress;
import com.spacedlearning.entity.Repetition;
import com.spacedlearning.entity.enums.CycleStudied;
import com.spacedlearning.entity.enums.RepetitionStatus;
import com.spacedlearning.exception.SpacedLearningException;
import com.spacedlearning.repository.ModuleProgressRepository;
import com.spacedlearning.repository.RepetitionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class LearningCycleManager {

    private final RepetitionDateOptimizer optimizer;
    private final ModuleProgressRepository progressRepository;
    private final RepetitionFactory repetitionFactory;
    private final RepetitionRepository repetitionRepository;
    private final RepetitionScheduleManager scheduleManager;
    private final MessageSource messageSource;

    public void checkAndAdvanceCycle(ModuleProgress progress) {
        final var progressId = progress.getId();

        final var total = this.repetitionRepository.countByModuleProgressId(progressId);
        if (total == 0) {
            throw SpacedLearningException.validationError(
                    this.messageSource, "error.progress.noRepetitions",
                    progressId);
        }

        final var completed = this.repetitionRepository.countByModuleProgressIdAndStatus(
                progressId, RepetitionStatus.COMPLETED);

        if (completed < total) {
            log.info("Not all repetitions completed yet for progress ID: {} ({} of {})", progressId, completed, total);
            return;
        }

        final var current = Optional.ofNullable(progress.getCyclesStudied()).orElse(CycleStudied.FIRST_TIME);
        final var next = switch (current) {
        case FIRST_TIME -> CycleStudied.FIRST_REVIEW;
        case FIRST_REVIEW -> CycleStudied.SECOND_REVIEW;
        case SECOND_REVIEW -> CycleStudied.THIRD_REVIEW;
        case THIRD_REVIEW -> CycleStudied.MORE_THAN_THREE_REVIEWS;
        case MORE_THAN_THREE_REVIEWS -> CycleStudied.MORE_THAN_THREE_REVIEWS;
        };

        final var now = LocalDate.now();

        if ((current == next) && !CycleStudied.MORE_THAN_THREE_REVIEWS.equals(current)) {
            throw SpacedLearningException.validationError(
                    this.messageSource, "error.progress.finalCycleReached",
                    current, progressId);
        }

        if (CycleStudied.MORE_THAN_THREE_REVIEWS.equals(current)) {
            final int currentCount = Optional.ofNullable(progress.getExtendedReviewCount()).orElse(0);
            progress.setExtendedReviewCount(currentCount + 1);
            this.progressRepository.save(progress);
            log.info("Extended review cycle #{} for progress ID: {}", progress.getExtendedReviewCount(), progressId);
        }

        if (current != next) {
            progress.addCycleStart(next, now);
            progress.setCyclesStudied(next);
            this.progressRepository.save(progress);
            log.info("Advanced cycle from {} to {} for progress ID: {}, starting on {}", current, next, progressId,
                    now);
        }

        final var existing = this.repetitionRepository.findByModuleProgressIdOrderByRepetitionOrder(progressId);
        var updated = false;
        for (final Repetition rep : existing) {
            if (rep.getStatus() != RepetitionStatus.COMPLETED) {
                rep.setStatus(RepetitionStatus.COMPLETED);
                updated = true;
            }
        }

        if (updated) {
            this.repetitionRepository.saveAll(existing);
            log.debug("Marked all unfinished repetitions as COMPLETED for progress ID: {}", progressId);
        }

        createNextCycle(progress);
    }

    private void createNextCycle(ModuleProgress progress) {
        final var lastCompletedDate = this.repetitionRepository.findLastCompletedRepetitionDate(progress
                .getId());
        final var fallback = Optional.ofNullable(progress.getFirstLearningDate()).orElse(LocalDate.now());
        final var baseStart = lastCompletedDate.map(d -> d.plusDays(7)).orElse(fallback);

        final var dateCounts = this.scheduleManager.loadReviewDateCounts(baseStart);
        final var optimalStart = this.optimizer.findOptimalDate(baseStart, dateCounts);

        progress.addCycleStart(progress.getCyclesStudied(), optimalStart);
        this.progressRepository.save(progress);

        final var newReps = this.repetitionFactory.generateSchedule(progress, optimalStart, dateCounts);
        if (newReps.isEmpty()) {
            log.warn("Failed to create new repetitions for next cycle. Progress ID: {}", progress.getId());
            return;
        }

        this.repetitionRepository.saveAll(newReps);
        this.scheduleManager.updateNextStudyDate(progress);
        log.info("Created {} new repetitions for next cycle. Progress ID: {}", newReps.size(), progress.getId());
    }
}
