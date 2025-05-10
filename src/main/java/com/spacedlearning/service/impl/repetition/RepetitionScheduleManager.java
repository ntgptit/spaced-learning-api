package com.spacedlearning.service.impl.repetition;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.lang.NonNull;
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
public class RepetitionScheduleManager {
    private final ModuleProgressRepository progressRepository;
    private final RepetitionFactory repetitionFactory;
    private final RepetitionRepository repetitionRepository;

    public List<Repetition> createRepetitionsForProgress(@NonNull ModuleProgress progress) {
        initializeFirstCycle(progress);
        final var baseDate = getEffectiveStartDate(progress);
        final var dateCounts = loadReviewDateCounts(baseDate);
        return this.repetitionFactory.generateSchedule(progress, baseDate, dateCounts);
    }

    @NonNull
    private LocalDate getEffectiveStartDate(@NonNull ModuleProgress progress) {
        final var cycleStart = progress.findLatestCycleStart(progress.getCyclesStudied());
        if (cycleStart != null) {
            return cycleStart;
        }

        if (progress.getFirstLearningDate() != null) {
            return progress.getFirstLearningDate();
        }

        return LocalDate.now();
    }

    private void initializeFirstCycle(@NonNull ModuleProgress progress) {
        if (progress.findLatestCycleStart(CycleStudied.FIRST_TIME) != null) {
            return;
        }

        final var start = Optional.ofNullable(progress.getFirstLearningDate()).orElse(LocalDate.now());
        progress.setFirstLearningDate(start);
        progress.addCycleStart(CycleStudied.FIRST_TIME, start);
        this.progressRepository.save(progress);
    }

    public void initializeFirstLearningDate(@NonNull ModuleProgress progress) {
        if (progress.getFirstLearningDate() != null) {
            return;
        }

        final var today = LocalDate.now();
        progress.setFirstLearningDate(today);
        this.progressRepository.save(progress);
        RepetitionScheduleManager.log.debug("Initialized first learning date to {} for progress ID: {}", today, progress
                .getId());
    }

    @NonNull
    public Map<LocalDate, Long> loadReviewDateCounts(@NonNull LocalDate startDate) {
        final var MAX_DAYS = 60;
        final var end = startDate.plusDays(MAX_DAYS);
        final var rawCounts = this.repetitionRepository.countReviewDatesBetween(startDate, end);
        if ((rawCounts == null) || rawCounts.isEmpty()) {
            return Map.of();
        }

        return rawCounts.stream().collect(Collectors.toMap(
                arr -> {
                    final var dateObj = arr[0];
                    if (dateObj instanceof final LocalDate ld) {
                        return ld;
                    }
                    if (dateObj instanceof final java.sql.Date sql) {
                        return sql.toLocalDate();
                    }
                    throw new IllegalStateException("Unexpected type: " + dateObj.getClass());
                },
                arr -> ((Number) arr[1]).longValue(),
                (a, b) -> a));
    }

    public void updateNextStudyDate(ModuleProgress progress) {
        final var pending = this.repetitionRepository
                .findByModuleProgressIdAndStatusOrderByReviewDate(progress.getId(), RepetitionStatus.NOT_STARTED);

        if (pending.isEmpty()) {
            if (progress.getNextStudyDate() != null) {
                progress.setNextStudyDate(null);
                this.progressRepository.save(progress);
                RepetitionScheduleManager.log.debug("Cleared next study date for progress ID: {}", progress.getId());
            }
            return;
        }

        final var nextDate = pending.get(0).getReviewDate();
        if (!nextDate.equals(progress.getNextStudyDate())) {
            progress.setNextStudyDate(nextDate);
            this.progressRepository.save(progress);
            RepetitionScheduleManager.log.debug("Updated next study date to {} for progress ID: {}", nextDate, progress
                    .getId());
        }
    }
}
