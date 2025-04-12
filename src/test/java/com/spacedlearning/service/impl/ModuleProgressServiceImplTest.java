package com.spacedlearning.service.impl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.time.LocalDate;
import java.util.Collections;
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
import com.spacedlearning.dto.progress.ModuleProgressCreateRequest;
import com.spacedlearning.dto.progress.ModuleProgressDetailResponse;
import com.spacedlearning.dto.progress.ModuleProgressSummaryResponse;
import com.spacedlearning.dto.progress.ModuleProgressUpdateRequest;
import com.spacedlearning.entity.Module;
import com.spacedlearning.entity.ModuleProgress;
import com.spacedlearning.mapper.ModuleProgressMapper;
import com.spacedlearning.repository.ModuleProgressRepository;
import com.spacedlearning.repository.ModuleRepository;
import com.spacedlearning.service.RepetitionService;



class ModuleProgressServiceImplTest {

    @Mock
    private ModuleProgressRepository progressRepository;

    @Mock
    private ModuleRepository moduleRepository;

    @Mock
    private ModuleProgressMapper progressMapper;

    @Mock
    private RepetitionService repetitionService;

    @InjectMocks
    private ModuleProgressServiceImpl moduleProgressService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreate() {
        UUID moduleId = UUID.randomUUID();
        Module module = new Module();
        module.setId(moduleId);

        ModuleProgressCreateRequest request =
                ModuleProgressCreateRequest.builder().moduleId(moduleId).build();

        ModuleProgress progress = new ModuleProgress();
        ModuleProgress savedProgress = new ModuleProgress();
        savedProgress.setId(UUID.randomUUID());

        when(moduleRepository.findById(moduleId)).thenReturn(Optional.of(module));
        when(progressRepository.existsByModuleId(moduleId)).thenReturn(false);
        when(progressMapper.toEntity(request, module)).thenReturn(progress);
        when(progressRepository.save(progress)).thenReturn(savedProgress);
        when(progressMapper.toDto(savedProgress)).thenReturn(new ModuleProgressDetailResponse());

        ModuleProgressDetailResponse response = moduleProgressService.create(request);

        assertNotNull(response);
        verify(repetitionService).createDefaultSchedule(savedProgress.getId());
    }

    @Test
    void testDelete() {
        UUID progressId = UUID.randomUUID();
        ModuleProgress progress = new ModuleProgress();

        when(progressRepository.findById(progressId)).thenReturn(Optional.of(progress));

        moduleProgressService.delete(progressId);

        verify(progressRepository).save(progress);
    }

    @Test
    void testFindAll() {
        PageRequest pageable = PageRequest.of(0, 10);
        Page<ModuleProgress> progressPage = new PageImpl<>(Collections.emptyList());

        when(progressRepository.findAll(pageable)).thenReturn(progressPage);

        Page<ModuleProgressSummaryResponse> response = moduleProgressService.findAll(pageable);

        assertNotNull(response);
    }

    @Test
    void testFindById() {
        UUID progressId = UUID.randomUUID();
        ModuleProgress progress = new ModuleProgress();

        when(progressRepository.findWithRepetitionsById(progressId))
                .thenReturn(Optional.of(progress));
        when(progressMapper.toDto(progress)).thenReturn(new ModuleProgressDetailResponse());

        ModuleProgressDetailResponse response = moduleProgressService.findById(progressId);

        assertNotNull(response);
    }

    @Test
    void testFindByModuleId() {
        UUID moduleId = UUID.randomUUID();
        PageRequest pageable = PageRequest.of(0, 10);
        Page<ModuleProgress> progressPage = new PageImpl<>(Collections.emptyList());

        when(moduleRepository.existsById(moduleId)).thenReturn(true);
        when(progressRepository.findByModuleId(moduleId, pageable)).thenReturn(progressPage);

        Page<ModuleProgressSummaryResponse> response =
                moduleProgressService.findByModuleId(moduleId, pageable);

        assertNotNull(response);
    }

    @Test
    void testFindDueForStudy() {
        LocalDate studyDate = LocalDate.now();
        PageRequest pageable = PageRequest.of(0, 10);
        Page<ModuleProgress> progressPage = new PageImpl<>(Collections.emptyList());

        when(progressRepository.findByNextStudyDateLessThanEqual(studyDate, pageable))
                .thenReturn(progressPage);

        Page<ModuleProgressSummaryResponse> response =
                moduleProgressService.findDueForStudy(studyDate, pageable);

        assertNotNull(response);
    }

    @Test
    void testUpdate() {
        UUID progressId = UUID.randomUUID();
        ModuleProgressUpdateRequest request = new ModuleProgressUpdateRequest();
        ModuleProgress progress = new ModuleProgress();
        ModuleProgress updatedProgress = new ModuleProgress();

        when(progressRepository.findById(progressId)).thenReturn(Optional.of(progress));
        when(progressRepository.save(progress)).thenReturn(updatedProgress);
        when(progressMapper.toDto(updatedProgress)).thenReturn(new ModuleProgressDetailResponse());

        ModuleProgressDetailResponse response = moduleProgressService.update(progressId, request);

        assertNotNull(response);
    }

    @Test
    void testFindOrCreateProgressForModule() {
        UUID moduleId = UUID.randomUUID();
        Module module = new Module();
        module.setId(moduleId);
        ModuleProgress progress = new ModuleProgress();
        ModuleProgressCreateRequest createRequest =
                ModuleProgressCreateRequest.builder().moduleId(moduleId).build();

        when(progressRepository.findByModuleId(moduleId)).thenReturn(Optional.empty());
        when(moduleRepository.findById(moduleId)).thenReturn(Optional.of(module));
        when(progressMapper.toEntity(createRequest, module)).thenReturn(progress);
        when(progressRepository.save(progress)).thenReturn(progress);
        when(progressMapper.toDto(progress)).thenReturn(new ModuleProgressDetailResponse());

        ModuleProgressDetailResponse response =
                moduleProgressService.findOrCreateProgressForModule(moduleId);

        assertNotNull(response);
    }
}
