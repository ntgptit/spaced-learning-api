package com.spacedlearning.service.impl.repetition;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.spacedlearning.entity.ModuleProgress;
import com.spacedlearning.entity.Repetition;
import com.spacedlearning.entity.enums.CycleStudied;
import com.spacedlearning.entity.enums.RepetitionStatus;
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

    public void checkAndAdvanceCycle(ModuleProgress progress) {
        final var total = this.repetitionRepository.countByModuleProgressId(progress.getId());
        if (total == 0) {
            log.debug("No repetitions found for progress ID: {}. Cannot check cycle.", progress.getId());
            return;
        }

        final var completed = this.repetitionRepository.countByModuleProgressIdAndStatus(
                progress.getId(), RepetitionStatus.COMPLETED);

        if (completed < total) {
            log.debug("Not all repetitions completed ({} / {}) for progress ID: {}", completed, total, progress
                    .getId());
            return;
        }

        final var current = Optional.ofNullable(progress.getCyclesStudied()).orElse(CycleStudied.FIRST_TIME);
        final var next = switch (current) {
        case FIRST_TIME -> CycleStudied.FIRST_REVIEW;
        case FIRST_REVIEW -> CycleStudied.SECOND_REVIEW;
        case SECOND_REVIEW -> CycleStudied.THIRD_REVIEW;
        case THIRD_REVIEW -> CycleStudied.MORE_THAN_THREE_REVIEWS;
        default -> current;
        };

        if (current == next) {
            log.info("Current cycle {} is final or unchanged for progress ID: {}", current, progress.getId());
            return;
        }

        final var now = LocalDate.now();
        progress.addCycleStart(next, now);
        progress.setCyclesStudied(next);
        this.progressRepository.save(progress);

        log.info("Advanced cycle from {} to {} for progress ID: {}, starting on {}", current, next, progress.getId(),
                now);

        // Mark incomplete reps as completed
        final var existing = this.repetitionRepository.findByModuleProgressIdOrderByRepetitionOrder(
                progress.getId());
        var updated = false;
        for (final Repetition rep : existing) {
            if (rep.getStatus() != RepetitionStatus.COMPLETED) {
                rep.setStatus(RepetitionStatus.COMPLETED);
                updated = true;
            }
        }

        if (updated) {
            this.repetitionRepository.saveAll(existing);
            log.debug("Marked all unfinished repetitions as COMPLETED for progress ID: {}", progress.getId());
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
