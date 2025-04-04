package com.spacedlearning.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spacedlearning.dto.repetition.RepetitionCreateRequest;
import com.spacedlearning.dto.repetition.RepetitionResponse;
import com.spacedlearning.dto.repetition.RepetitionUpdateRequest;
import com.spacedlearning.entity.ModuleProgress;
import com.spacedlearning.entity.Repetition;
import com.spacedlearning.entity.enums.RepetitionOrder;
import com.spacedlearning.entity.enums.RepetitionStatus;
import com.spacedlearning.exception.SpacedLearningException;
import com.spacedlearning.mapper.RepetitionMapper;
import com.spacedlearning.repository.RepetitionRepository;
import com.spacedlearning.service.RepetitionService;
import com.spacedlearning.service.impl.repetition.RepetitionScheduleManager;
import com.spacedlearning.service.impl.repetition.RepetitionValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RepetitionServiceImpl implements RepetitionService {

    private final RepetitionRepository repetitionRepository;
    private final RepetitionMapper repetitionMapper;
    private final RepetitionScheduleManager scheduleManager;
    private final RepetitionValidator validator;

    @Override
    @Transactional
    public RepetitionResponse create(RepetitionCreateRequest request) {
        Objects.requireNonNull(request, "Repetition create request must not be null");
        log.debug("Creating new repetition: {}", request);

        final ModuleProgress progress = validator.findModuleProgress(request.getModuleProgressId());
        validator.validateRepetitionDoesNotExist(request.getModuleProgressId(), request.getRepetitionOrder());

        final Repetition repetition = repetitionMapper.toEntity(request, progress);
        final Repetition savedRepetition = repetitionRepository.save(repetition);
        scheduleManager.updateNextStudyDate(progress);

        log.info("Repetition created successfully with ID: {}", savedRepetition.getId());
        return repetitionMapper.toDto(savedRepetition);
    }

    @Override
    @Transactional
    public List<RepetitionResponse> createDefaultSchedule(UUID moduleProgressId) {
        Objects.requireNonNull(moduleProgressId, "Module progress ID must not be null");
        log.debug("Creating default repetition schedule for module progress ID: {}", moduleProgressId);

        final ModuleProgress progress = validator.findModuleProgress(moduleProgressId);
        final List<Repetition> existingRepetitions = repetitionRepository.findByModuleProgressIdOrderByRepetitionOrder(
                moduleProgressId);

        if (!existingRepetitions.isEmpty()) {
            log.info("Repetition schedule already exists for module progress ID: {}", moduleProgressId);
            return repetitionMapper.toDtoList(existingRepetitions);
        }

        scheduleManager.initializeFirstLearningDate(progress);
        final List<Repetition> repetitions = scheduleManager.createRepetitionsForProgress(progress);
        final List<Repetition> savedRepetitions = repetitionRepository.saveAll(repetitions);
        scheduleManager.updateNextStudyDate(progress);

        log.info("Created default repetition schedule with {} repetitions for module progress ID: {}",
                savedRepetitions.size(), moduleProgressId);
        return repetitionMapper.toDtoList(savedRepetitions);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Objects.requireNonNull(id, "Repetition ID must not be null");
        log.debug("Deleting repetition with ID: {}", id);

        final Repetition repetition = validator.findRepetition(id);
        final ModuleProgress progress = repetition.getModuleProgress();
        repetition.softDelete();
        repetitionRepository.save(repetition);
        scheduleManager.updateNextStudyDate(progress);

        log.info("Repetition soft deleted successfully with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RepetitionResponse> findAll(Pageable pageable) {
        Objects.requireNonNull(pageable, "Pageable must not be null");
        log.debug("Finding all repetitions with pagination: {}", pageable);
        return repetitionRepository.findAll(pageable).map(repetitionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public RepetitionResponse findById(UUID id) {
        Objects.requireNonNull(id, "Repetition ID must not be null");
        log.debug("Finding repetition by ID: {}", id);
        final Repetition repetition = validator.findRepetition(id);
        return repetitionMapper.toDto(repetition);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RepetitionResponse> findByModuleProgressId(UUID moduleProgressId) {
        Objects.requireNonNull(moduleProgressId, "Module progress ID must not be null");
        log.debug("Finding repetitions by module progress ID: {}", moduleProgressId);
        validator.validateModuleProgressExists(moduleProgressId);
        final List<Repetition> repetitions = repetitionRepository.findByModuleProgressIdOrderByRepetitionOrder(
                moduleProgressId);
        return repetitionMapper.toDtoList(repetitions);
    }

    @Override
    @Transactional(readOnly = true)
    public RepetitionResponse findByModuleProgressIdAndOrder(UUID moduleProgressId, RepetitionOrder repetitionOrder) {
        Objects.requireNonNull(moduleProgressId, "Module progress ID must not be null");
        Objects.requireNonNull(repetitionOrder, "Repetition order must not be null");
        log.debug("Finding repetition by module progress ID: {} and order: {}", moduleProgressId, repetitionOrder);

        final Repetition repetition = repetitionRepository.findByModuleProgressIdAndRepetitionOrder(moduleProgressId,
                repetitionOrder)
                .orElseThrow(() -> SpacedLearningException.resourceNotFound(
                        "Repetition for ModuleProgress " + moduleProgressId + " and Order " + repetitionOrder, null));
        return repetitionMapper.toDto(repetition);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RepetitionResponse> findDueRepetitions(UUID userId, LocalDate reviewDate, RepetitionStatus status,
            Pageable pageable) {
        Objects.requireNonNull(userId, "User ID must not be null");
        Objects.requireNonNull(pageable, "Pageable must not be null");
        log.debug("Finding due repetitions for user ID: {} on or before date: {}, status: {}, pageable: {}",
                userId, reviewDate, status, pageable);

        final LocalDate dateToCheck = reviewDate != null ? reviewDate : LocalDate.now();
        final RepetitionStatus statusToCheck = status != null ? status : RepetitionStatus.NOT_STARTED;
        return repetitionRepository.findDueRepetitions(userId, dateToCheck, statusToCheck, pageable)
                .map(repetitionMapper::toDto);
    }

    @Override
    @Transactional
    public RepetitionResponse update(UUID id, RepetitionUpdateRequest request) {
        Objects.requireNonNull(id, "Repetition ID must not be null");
        Objects.requireNonNull(request, "Repetition update request must not be null");
        log.debug("Updating repetition with ID: {}, request: {}", id, request);

        final Repetition repetition = validator.findRepetition(id);
        final ModuleProgress progress = repetition.getModuleProgress();
        final RepetitionStatus previousStatus = repetition.getStatus();

        repetitionMapper.updateFromDto(request, repetition);
        final RepetitionStatus newStatus = repetition.getStatus();

        if (previousStatus != RepetitionStatus.COMPLETED && newStatus == RepetitionStatus.COMPLETED) {
            log.debug("Status changed to COMPLETED for Repetition ID: {}", id);
            scheduleManager.updateFutureRepetitions(progress, repetition.getRepetitionOrder());
            scheduleManager.checkAndUpdateCycleStudied(progress);
        }

        final Repetition updatedRepetition = repetitionRepository.save(repetition);
        scheduleManager.updateNextStudyDate(progress);

        log.info("Repetition updated successfully with ID: {}", updatedRepetition.getId());
        return repetitionMapper.toDto(updatedRepetition);
    }
}
