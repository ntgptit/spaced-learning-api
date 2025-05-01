package com.spacedlearning.service.impl.repetition;

import com.spacedlearning.entity.Module;
import com.spacedlearning.entity.ModuleProgress;
import com.spacedlearning.entity.Repetition;
import com.spacedlearning.entity.enums.CycleStudied;
import com.spacedlearning.entity.enums.RepetitionOrder;
import com.spacedlearning.entity.enums.RepetitionStatus;
import com.spacedlearning.repository.ModuleProgressRepository;
import com.spacedlearning.repository.RepetitionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class RepetitionScheduleManagerTest {

    @Mock
    private RepetitionRepository repetitionRepository;

    @Mock
    private ModuleProgressRepository progressRepository;

    @InjectMocks
    private RepetitionScheduleManager scheduleManager;

    private ModuleProgress progress;
    private LocalDate today;

    @BeforeEach
    void setUp() {
        today = LocalDate.now();
        Module module = new Module();
        module.setWordCount(Integer.valueOf(100));

        progress = new ModuleProgress();
        progress.setId(java.util.UUID.randomUUID());
        progress.setModule(module);
        progress.setFirstLearningDate(today);
        progress.setCyclesStudied(CycleStudied.FIRST_TIME);
        progress.setPercentComplete(BigDecimal.valueOf(50.0));
    }

    @Test
    void initializeFirstLearningDate_ShouldSetDateWhenNull() {
        progress.setFirstLearningDate(null);
        scheduleManager.initializeFirstLearningDate(progress);
        verify(progressRepository).save(progress);
        assertEquals(today, progress.getFirstLearningDate());
    }

    @Test
    void createRepetitionsForProgress_ShouldCreateCorrectRepetitions() {
        List<Repetition> repetitions = scheduleManager.createRepetitionsForProgress(progress);

        assertEquals(RepetitionOrder.values().length, repetitions.size());
        for (int i = 0; i < repetitions.size(); i++) {
            assertEquals(RepetitionOrder.values()[i], repetitions.get(i).getRepetitionOrder());
            assertEquals(RepetitionStatus.NOT_STARTED, repetitions.get(i).getStatus());
            assertEquals(progress, repetitions.get(i).getModuleProgress());
            assertNotNull(repetitions.get(i).getReviewDate());
        }
    }

    @Test
    void rescheduleFutureRepetitions_ShouldUpdateDates() {
        LocalDate newStartDate = today.plusDays(5);
        RepetitionOrder currentOrder = RepetitionOrder.FIRST_REPETITION;

        Repetition futureRepetition = new Repetition();
        futureRepetition.setRepetitionOrder(RepetitionOrder.SECOND_REPETITION);
        futureRepetition.setStatus(RepetitionStatus.NOT_STARTED);

        when(repetitionRepository.findByModuleProgressIdAndStatusOrderByRepetitionOrder(
                any(java.util.UUID.class), any())).thenReturn(List.of(futureRepetition));

        scheduleManager.rescheduleFutureRepetitions(progress, currentOrder, newStartDate);

        verify(repetitionRepository).saveAll(any());
        verify(progressRepository).save(any());
    }

    @Test
    void checkAndUpdateCycleStudied_ShouldUpdateCycleWhenAllCompleted() {
        when(repetitionRepository.countByModuleProgressId(any(java.util.UUID.class)))
                .thenReturn(Long.valueOf(6L));
        when(repetitionRepository.countByModuleProgressIdAndStatus(any(java.util.UUID.class),
                any())).thenReturn(Long.valueOf(6L));
        when(repetitionRepository
                .findByModuleProgressIdOrderByRepetitionOrder(any(java.util.UUID.class)))
                .thenReturn(List.of());

        scheduleManager.checkAndUpdateCycleStudied(progress);

        verify(progressRepository, times(1)).save(progress);
        assertEquals(CycleStudied.FIRST_REVIEW, progress.getCyclesStudied());
    }

    @Test
    void updateNextStudyDate_ShouldUpdateWhenPendingRepetitionsExist() {
        Repetition pendingRepetition = new Repetition();
        pendingRepetition.setReviewDate(today.plusDays(1));

        when(repetitionRepository
                .findByModuleProgressIdAndStatusOrderByReviewDate(any(java.util.UUID.class), any()))
                .thenReturn(List.of(pendingRepetition));

        scheduleManager.updateNextStudyDate(progress);

        verify(progressRepository).save(progress);
        assertEquals(today.plusDays(1), progress.getNextStudyDate());
    }
}
