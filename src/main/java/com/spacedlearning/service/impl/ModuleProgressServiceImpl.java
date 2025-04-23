package com.spacedlearning.service.impl;

import com.spacedlearning.dto.progress.ModuleProgressCreateRequest;
import com.spacedlearning.dto.progress.ModuleProgressDetailResponse;
import com.spacedlearning.dto.progress.ModuleProgressSummaryResponse;
import com.spacedlearning.dto.progress.ModuleProgressUpdateRequest;
import com.spacedlearning.entity.Module;
import com.spacedlearning.entity.ModuleProgress;
import com.spacedlearning.exception.SpacedLearningException;
import com.spacedlearning.mapper.ModuleProgressMapper;
import com.spacedlearning.repository.ModuleProgressRepository;
import com.spacedlearning.repository.ModuleRepository;
import com.spacedlearning.service.ModuleProgressService;
import com.spacedlearning.service.RepetitionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of ModuleProgressService
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ModuleProgressServiceImpl implements ModuleProgressService {

    public static final String MODULE = "Module";
    public static final String MODULE_PROGRESS = "ModuleProgress";
    private final ModuleProgressRepository progressRepository;
    private final ModuleRepository moduleRepository;
    private final ModuleProgressMapper progressMapper;
    private final RepetitionService repetitionService;

    @Override
    @Transactional
    public ModuleProgressDetailResponse create(ModuleProgressCreateRequest request) {
        log.debug("Creating new module progress: {}", request);

        // Validate module existence
        final Module module = moduleRepository.findById(request.getModuleId())
                .orElseThrow(() -> SpacedLearningException.resourceNotFound(MODULE, request.getModuleId()));

        // Check if progress already exists for module
        if (progressRepository.existsByModuleId(request.getModuleId())) {
            throw SpacedLearningException.resourceAlreadyExists(MODULE_PROGRESS, "module_id", request.getModuleId()
                    .toString());
        }

        // Create and save progress
        final ModuleProgress progress = progressMapper.toEntity(request, module);
        final ModuleProgress savedProgress = progressRepository.save(progress);

        log.info("Module progress created successfully with ID: {}", savedProgress.getId());

        // Create default repetition schedule
        repetitionService.createDefaultSchedule(savedProgress.getId());

        return progressMapper.toDto(savedProgress);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        log.debug("Deleting module progress with ID: {}", id);

        final ModuleProgress progress = progressRepository.findById(id)
                .orElseThrow(() -> SpacedLearningException.resourceNotFound(MODULE_PROGRESS, id));

        progress.softDelete(); // Use soft delete
        progressRepository.save(progress);

        log.info("Module progress soft deleted successfully with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ModuleProgressSummaryResponse> findAll(Pageable pageable) {
        log.debug("Finding all module progress with pagination: {}", pageable);
        return progressRepository.findAll(pageable)
                .map(progressMapper::toSummaryDto);
    }

    @Override
    @Transactional(readOnly = true)
    public ModuleProgressDetailResponse findById(UUID id) {
        log.debug("Finding module progress by ID: {}", id);
        final ModuleProgress progress = progressRepository.findWithRepetitionsById(id)
                .orElseThrow(() -> SpacedLearningException.resourceNotFound(MODULE_PROGRESS, id));
        return progressMapper.toDto(progress);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ModuleProgressSummaryResponse> findByModuleId(UUID moduleId, Pageable pageable) {
        log.debug("Finding module progress by module ID: {}, pageable: {}", moduleId, pageable);
        // Verify module exists
        if (!moduleRepository.existsById(moduleId)) {
            throw SpacedLearningException.resourceNotFound(MODULE, moduleId);
        }

        return progressRepository.findByModuleId(moduleId, pageable)
                .map(progressMapper::toSummaryDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ModuleProgressSummaryResponse> findByBookId(UUID bookId, Pageable pageable) {
        log.debug("Finding module progress by book ID: {}, pageable: {}", bookId, pageable);

        return progressRepository.findByBookId(bookId, pageable)
                .map(progressMapper::toSummaryDto);
    }

    @Override
    @Transactional(readOnly = true)
    public ModuleProgressDetailResponse findByModuleId(UUID moduleId) {
        log.debug("Finding module progress by module ID: {}", moduleId);

        final ModuleProgress progress = progressRepository.findByModuleId(moduleId)
                .orElseThrow(() -> SpacedLearningException
                        .resourceNotFound("ModuleProgress for Module " + moduleId, null));

        return progressMapper.toDto(progress);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ModuleProgressSummaryResponse> findDueForStudy(LocalDate studyDate, Pageable pageable) {
        log.debug("Finding module progress due for study on or before date: {}, pageable: {}",
                studyDate, pageable);

        final LocalDate dateToCheck = studyDate != null ? studyDate : LocalDate.now();

        return progressRepository.findByNextStudyDateLessThanEqual(dateToCheck, pageable)
                .map(progressMapper::toSummaryDto);
    }

    @Override
    @Transactional
    public ModuleProgressDetailResponse update(UUID id, ModuleProgressUpdateRequest request) {
        log.debug("Updating module progress with ID: {}, request: {}", id, request);

        final ModuleProgress progress = progressRepository.findById(id)
                .orElseThrow(() -> SpacedLearningException.resourceNotFound(MODULE_PROGRESS, id));

        progressMapper.updateFromDto(request, progress);
        final ModuleProgress updatedProgress = progressRepository.save(progress);

        log.info("Module progress updated successfully with ID: {}", updatedProgress.getId());
        return progressMapper.toDto(updatedProgress);
    }

    @Override
    @Transactional(readOnly = true)
    public ModuleProgressDetailResponse findOrCreateProgressForModule(UUID moduleId) {
        log.debug("Finding or creating module progress for module ID: {}", moduleId);

        // Try to find existing progress
        final Optional<ModuleProgress> existingProgress = progressRepository.findByModuleId(moduleId);

        if (existingProgress.isPresent()) {
            return progressMapper.toDto(existingProgress.get());
        }

        moduleRepository.findById(moduleId)
                .orElseThrow(() -> SpacedLearningException.resourceNotFound(MODULE, moduleId));

        final ModuleProgressCreateRequest createRequest = ModuleProgressCreateRequest.builder()
                .moduleId(moduleId)
                .build();

        return create(createRequest);
    }
}