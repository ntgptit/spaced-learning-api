package com.spacedlearning.service.impl;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spacedlearning.dto.progress.ModuleProgressCreateRequest;
import com.spacedlearning.dto.progress.ModuleProgressDetailResponse;
import com.spacedlearning.dto.progress.ModuleProgressSummaryResponse;
import com.spacedlearning.dto.progress.ModuleProgressUpdateRequest;
import com.spacedlearning.exception.SpacedLearningException;
import com.spacedlearning.mapper.ModuleProgressMapper;
import com.spacedlearning.repository.ModuleProgressRepository;
import com.spacedlearning.repository.ModuleRepository;
import com.spacedlearning.service.ModuleProgressService;
import com.spacedlearning.service.RepetitionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ModuleProgressServiceImpl implements ModuleProgressService {

    private static final String MODULE = "Module";
    private static final String MODULE_PROGRESS = "ModuleProgress";

    private final ModuleProgressRepository progressRepository;
    private final ModuleRepository moduleRepository;
    private final ModuleProgressMapper progressMapper;
    private final RepetitionService repetitionService;

    @Override
    @Transactional
    public ModuleProgressDetailResponse create(ModuleProgressCreateRequest request) {
        log.debug("Creating new module progress: {}", request);
        Objects.requireNonNull(request, "ModuleProgressCreateRequest must not be null");

        final var module = this.moduleRepository.findById(request.getModuleId())
                .orElseThrow(() -> SpacedLearningException.resourceNotFound(MODULE, request.getModuleId()));

        if (this.progressRepository.existsByModuleId(request.getModuleId())) {
            throw SpacedLearningException.resourceAlreadyExists(
                    MODULE_PROGRESS, "module_id", request.getModuleId().toString());
        }

        final var progress = this.progressMapper.toEntity(request, module);
        final var savedProgress = this.progressRepository.save(progress);

        log.info("Module progress created with ID: {}", savedProgress.getId());

        this.repetitionService.createDefaultSchedule(savedProgress.getId());

        return this.progressMapper.toDto(savedProgress);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        log.debug("Deleting module progress with ID: {}", id);

        final var progress = this.progressRepository.findById(id)
                .orElseThrow(() -> SpacedLearningException.resourceNotFound(MODULE_PROGRESS, id));

        progress.softDelete();
        this.progressRepository.save(progress);

        log.info("Module progress soft deleted with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ModuleProgressSummaryResponse> findAll(Pageable pageable) {
        log.debug("Fetching all module progress with pagination: {}", pageable);
        return this.progressRepository.findAll(pageable)
                .map(this.progressMapper::toSummaryDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ModuleProgressSummaryResponse> findByBookId(UUID bookId, Pageable pageable) {
        log.debug("Fetching module progress by book ID: {}, pageable: {}", bookId, pageable);
        return this.progressRepository.findByBookId(bookId, pageable)
                .map(this.progressMapper::toSummaryDto);
    }

    @Override
    @Transactional(readOnly = true)
    public ModuleProgressDetailResponse findById(UUID id) {
        log.debug("Fetching module progress by ID: {}", id);
        final var progress = this.progressRepository.findWithRepetitionsById(id)
                .orElseThrow(() -> SpacedLearningException.resourceNotFound(MODULE_PROGRESS, id));
        return this.progressMapper.toDto(progress);
    }

    @Override
    @Transactional(readOnly = true)
    public ModuleProgressDetailResponse findByModuleId(UUID moduleId) {
        log.debug("Fetching module progress by module ID: {}", moduleId);
        final var progress = this.progressRepository.findByModuleId(moduleId)
                .orElseThrow(() -> SpacedLearningException.resourceNotFound(
                        "ModuleProgress for Module " + moduleId, null));
        return this.progressMapper.toDto(progress);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ModuleProgressSummaryResponse> findByModuleId(UUID moduleId, Pageable pageable) {
        log.debug("Fetching module progress by module ID: {}, pageable: {}", moduleId, pageable);
        if (!this.moduleRepository.existsById(moduleId)) {
            throw SpacedLearningException.resourceNotFound(MODULE, moduleId);
        }
        return this.progressRepository.findByModuleId(moduleId, pageable)
                .map(this.progressMapper::toSummaryDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ModuleProgressSummaryResponse> findDueForStudy(LocalDate studyDate, Pageable pageable) {
        final var date = studyDate != null ? studyDate : LocalDate.now();
        log.debug("Fetching module progress due for study on or before: {}, pageable: {}", date, pageable);
        return this.progressRepository.findByNextStudyDateLessThanEqual(date, pageable)
                .map(this.progressMapper::toSummaryDto);
    }

    @Override
    @Transactional(readOnly = true)
    public ModuleProgressDetailResponse findOrCreateProgressForModule(UUID moduleId) {
        log.debug("Finding or creating module progress for module ID: {}", moduleId);

        final var existing = this.progressRepository.findByModuleId(moduleId);
        if (existing.isPresent()) {
            return this.progressMapper.toDto(existing.get());
        }

        this.moduleRepository.findById(moduleId)
                .orElseThrow(() -> SpacedLearningException.resourceNotFound(MODULE, moduleId));

        final var request = ModuleProgressCreateRequest.builder()
                .moduleId(moduleId)
                .build();

        // Calling repository directly avoids self-injection or transactional loss
        final var module = this.moduleRepository.findById(request.getModuleId())
                .orElseThrow(() -> SpacedLearningException.resourceNotFound(MODULE, request.getModuleId()));

        if (this.progressRepository.existsByModuleId(request.getModuleId())) {
            throw SpacedLearningException.resourceAlreadyExists(
                    MODULE_PROGRESS, "module_id", request.getModuleId().toString());
        }

        final var progress = this.progressMapper.toEntity(request, module);
        final var saved = this.progressRepository.save(progress);

        this.repetitionService.createDefaultSchedule(saved.getId());
        return this.progressMapper.toDto(saved);
    }

    @Override
    @Transactional
    public ModuleProgressDetailResponse update(UUID id, ModuleProgressUpdateRequest request) {
        log.debug("Updating module progress with ID: {}, request: {}", id, request);
        Objects.requireNonNull(request, "ModuleProgressUpdateRequest must not be null");

        final var progress = this.progressRepository.findById(id)
                .orElseThrow(() -> SpacedLearningException.resourceNotFound(MODULE_PROGRESS, id));

        this.progressMapper.updateFromDto(request, progress);
        final var updatedProgress = this.progressRepository.save(progress);

        log.info("Module progress updated with ID: {}", updatedProgress.getId());
        return this.progressMapper.toDto(updatedProgress);
    }
}
