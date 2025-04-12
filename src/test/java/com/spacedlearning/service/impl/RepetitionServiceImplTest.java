package com.spacedlearning.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import com.spacedlearning.dto.repetition.RepetitionCreateRequest;
import com.spacedlearning.dto.repetition.RepetitionResponse;
import com.spacedlearning.dto.repetition.RepetitionUpdateRequest;
import com.spacedlearning.entity.ModuleProgress;
import com.spacedlearning.entity.Repetition;
import com.spacedlearning.entity.enums.RepetitionOrder;
import com.spacedlearning.entity.enums.RepetitionStatus;
import com.spacedlearning.mapper.RepetitionMapper;
import com.spacedlearning.repository.RepetitionRepository;
import com.spacedlearning.service.impl.repetition.RepetitionScheduleManager;
import com.spacedlearning.service.impl.repetition.RepetitionValidator;



class RepetitionServiceImplTest {

    @InjectMocks
    private RepetitionServiceImpl repetitionService;

    @Mock
    private RepetitionRepository repetitionRepository;

    @Mock
    private RepetitionMapper repetitionMapper;

    @Mock
    private RepetitionScheduleManager scheduleManager;

    @Mock
    private RepetitionValidator validator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreate() {
        RepetitionCreateRequest request = new RepetitionCreateRequest();
        request.setModuleProgressId(UUID.randomUUID());
        request.setRepetitionOrder(RepetitionOrder.FIRST_REPETITION);

        ModuleProgress progress = new ModuleProgress();
        Repetition repetition = new Repetition();
        Repetition savedRepetition = new Repetition();
        RepetitionResponse response = new RepetitionResponse();

        when(validator.findModuleProgress(request.getModuleProgressId())).thenReturn(progress);
        doNothing().when(validator).validateRepetitionDoesNotExist(request.getModuleProgressId(),
                request.getRepetitionOrder());
        when(repetitionMapper.toEntity(request, progress)).thenReturn(repetition);
        when(repetitionRepository.save(repetition)).thenReturn(savedRepetition);
        when(repetitionMapper.toDto(savedRepetition)).thenReturn(response);

        RepetitionResponse result = repetitionService.create(request);

        assertEquals(response, result);
        verify(scheduleManager).updateNextStudyDate(progress);
    }

    @Test
    void testCreateDefaultSchedule() {
        UUID moduleProgressId = UUID.randomUUID();
        ModuleProgress progress = new ModuleProgress();
        List<Repetition> repetitions = Collections.emptyList();

        when(validator.findModuleProgress(moduleProgressId)).thenReturn(progress);
        when(repetitionRepository.findByModuleProgressIdOrderByRepetitionOrder(moduleProgressId))
                .thenReturn(repetitions);
        when(scheduleManager.createRepetitionsForProgress(progress)).thenReturn(repetitions);
        when(repetitionRepository.saveAll(repetitions)).thenReturn(repetitions);
        when(repetitionMapper.toDtoList(repetitions)).thenReturn(Collections.emptyList());

        List<RepetitionResponse> result = repetitionService.createDefaultSchedule(moduleProgressId);

        assertTrue(result.isEmpty());
        verify(scheduleManager).initializeFirstLearningDate(progress);
        verify(scheduleManager).updateNextStudyDate(progress);
    }

    @Test
    void testDelete() {
        UUID id = UUID.randomUUID();
        Repetition repetition = new Repetition();
        ModuleProgress progress = new ModuleProgress();

        when(validator.findRepetition(id)).thenReturn(repetition);
        when(repetition.getModuleProgress()).thenReturn(progress);

        repetitionService.delete(id);

        verify(repetition).softDelete();
        verify(repetitionRepository).save(repetition);
        verify(scheduleManager).updateNextStudyDate(progress);
    }

    @Test
    void testFindAll() {
        PageRequest pageable = PageRequest.of(0, 10);
        Page<Repetition> page = new PageImpl<>(Collections.emptyList());

        when(repetitionRepository.findAll(pageable)).thenReturn(page);
        when(repetitionMapper.toDto(any())).thenReturn(new RepetitionResponse());

        Page<RepetitionResponse> result = repetitionService.findAll(pageable);

        assertTrue(result.isEmpty());
    }

    @Test
    void testFindById() {
        UUID id = UUID.randomUUID();
        Repetition repetition = new Repetition();
        RepetitionResponse response = new RepetitionResponse();

        when(validator.findRepetition(id)).thenReturn(repetition);
        when(repetitionMapper.toDto(repetition)).thenReturn(response);

        RepetitionResponse result = repetitionService.findById(id);

        assertEquals(response, result);
    }

    @Test
    void testFindByModuleProgressId() {
        UUID moduleProgressId = UUID.randomUUID();
        List<Repetition> repetitions = Collections.emptyList();

        when(repetitionRepository.findByModuleProgressIdOrderByRepetitionOrder(moduleProgressId))
                .thenReturn(repetitions);
        when(repetitionMapper.toDtoList(repetitions)).thenReturn(Collections.emptyList());

        List<RepetitionResponse> result =
                repetitionService.findByModuleProgressId(moduleProgressId);

        assertTrue(result.isEmpty());
    }

    @Test
    void testFindByModuleProgressIdAndOrder() {
        UUID moduleProgressId = UUID.randomUUID();
        RepetitionOrder order = RepetitionOrder.FIRST_REPETITION;
        Repetition repetition = new Repetition();
        RepetitionResponse response = new RepetitionResponse();

        when(repetitionRepository.findByModuleProgressIdAndRepetitionOrder(moduleProgressId, order))
                .thenReturn(Optional.of(repetition));
        when(repetitionMapper.toDto(repetition)).thenReturn(response);

        RepetitionResponse result =
                repetitionService.findByModuleProgressIdAndOrder(moduleProgressId, order);

        assertEquals(response, result);
    }

    @Test
    void testFindDueRepetitions() {
        UUID userId = UUID.randomUUID();
        LocalDate reviewDate = LocalDate.now();
        RepetitionStatus status = RepetitionStatus.NOT_STARTED;
        PageRequest pageable = PageRequest.of(0, 10);
        Page<Repetition> page = new PageImpl<>(Collections.emptyList());

        when(repetitionRepository.findDueRepetitions(reviewDate, status, pageable))
                .thenReturn(page);
        when(repetitionMapper.toDto(any())).thenReturn(new RepetitionResponse());

        Page<RepetitionResponse> result =
                repetitionService.findDueRepetitions(userId, reviewDate, status, pageable);

        assertTrue(result.isEmpty());
    }

    @Test
    void testUpdate() {
        UUID id = UUID.randomUUID();
        RepetitionUpdateRequest request = new RepetitionUpdateRequest();
        Repetition repetition = new Repetition();
        ModuleProgress progress = new ModuleProgress();
        RepetitionResponse response = new RepetitionResponse();

        when(validator.findRepetition(id)).thenReturn(repetition);
        when(repetition.getModuleProgress()).thenReturn(progress);
        when(repetitionMapper.toDto(repetition)).thenReturn(response);

        RepetitionResponse result = repetitionService.update(id, request);

        assertEquals(response, result);
        verify(scheduleManager).updateNextStudyDate(progress);
    }
}
