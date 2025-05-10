package com.spacedlearning.service.impl;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spacedlearning.dto.repetition.RepetitionCompletionRequest;
import com.spacedlearning.dto.repetition.RepetitionCreateRequest;
import com.spacedlearning.dto.repetition.RepetitionRescheduleRequest;
import com.spacedlearning.dto.repetition.RepetitionResponse;
import com.spacedlearning.dto.repetition.RepetitionUpdateRequest;
import com.spacedlearning.entity.Repetition;
import com.spacedlearning.entity.enums.RepetitionOrder;
import com.spacedlearning.entity.enums.RepetitionStatus;
import com.spacedlearning.exception.SpacedLearningException;
import com.spacedlearning.mapper.RepetitionMapper;
import com.spacedlearning.repository.RepetitionRepository;
import com.spacedlearning.service.RepetitionService;
import com.spacedlearning.service.impl.repetition.LearningCycleManager;
import com.spacedlearning.service.impl.repetition.RepetitionRescheduler;
import com.spacedlearning.service.impl.repetition.RepetitionScheduleManager;
import com.spacedlearning.service.impl.repetition.RepetitionValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RepetitionServiceImpl implements RepetitionService {

    public static final String MODULE_PROGRESS_ID_MUST_NOT_BE_NULL = "Module progress ID must not be null";
    public static final String REPETITION_ID_MUST_NOT_BE_NULL = "Repetition ID must not be null";

    private final LearningCycleManager learningCycleManager;
    private final MessageSource messageSource;
    private final RepetitionMapper repetitionMapper;
    private final RepetitionRepository repetitionRepository;
    private final RepetitionRescheduler rescheduler;
    private final RepetitionScheduleManager scheduleManager;
    private final RepetitionValidator validator;

    @Override
    @Transactional
    public RepetitionResponse create(RepetitionCreateRequest request) {
        requireNonNull(request, "Repetition create request must not be null");
        log.debug("Creating new repetition: {}", request);

        final var progress = this.validator.findModuleProgress(request.getModuleProgressId());
        this.validator.validateRepetitionDoesNotExist(request.getModuleProgressId(), request.getRepetitionOrder());

        final var repetition = this.repetitionMapper.toEntity(request, progress);
        final var saved = this.repetitionRepository.save(repetition);

        this.scheduleManager.updateNextStudyDate(progress);
        log.info("Repetition created successfully with ID: {}", saved.getId());
        return this.repetitionMapper.toDto(saved);
    }

    @Override
    @Transactional
    public List<RepetitionResponse> createDefaultSchedule(UUID moduleProgressId) {
        requireNonNull(moduleProgressId, MODULE_PROGRESS_ID_MUST_NOT_BE_NULL);
        log.debug("Creating default repetition schedule for module progress ID: {}", moduleProgressId);

        final var progress = this.validator.findModuleProgress(moduleProgressId);
        final var existing = this.repetitionRepository.findByModuleProgressIdOrderByRepetitionOrder(
                moduleProgressId);

        if (!existing.isEmpty()) {
            log.info("Repetition schedule already exists for module progress ID: {}", moduleProgressId);
            return this.repetitionMapper.toDtoList(existing);
        }

        this.scheduleManager.initializeFirstLearningDate(progress);
        final var repetitions = this.scheduleManager.createRepetitionsForProgress(progress);
        final List<Repetition> saved = this.repetitionRepository.saveAll(repetitions);

        this.scheduleManager.updateNextStudyDate(progress);
        log.info("Created default repetition schedule with {} repetitions for module progress ID: {}", saved.size(),
                moduleProgressId);
        return this.repetitionMapper.toDtoList(saved);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        requireNonNull(id, REPETITION_ID_MUST_NOT_BE_NULL);
        log.debug("Deleting repetition with ID: {}", id);

        final var repetition = this.validator.findRepetition(id);
        final var progress = repetition.getModuleProgress();

        repetition.softDelete();
        this.repetitionRepository.save(repetition);

        this.scheduleManager.updateNextStudyDate(progress);
        log.info("Repetition soft deleted successfully with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RepetitionResponse> findAll(Pageable pageable) {
        requireNonNull(pageable, "Pageable must not be null");
        log.debug("Finding all repetitions with pagination: {}", pageable);
        return this.repetitionRepository.findAll(pageable).map(this.repetitionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public RepetitionResponse findById(UUID id) {
        requireNonNull(id, REPETITION_ID_MUST_NOT_BE_NULL);
        log.debug("Finding repetition by ID: {}", id);
        final var repetition = this.validator.findRepetition(id);
        return this.repetitionMapper.toDto(repetition);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RepetitionResponse> findByModuleProgressId(UUID moduleProgressId) {
        requireNonNull(moduleProgressId, MODULE_PROGRESS_ID_MUST_NOT_BE_NULL);
        log.debug("Finding repetitions by module progress ID: {}", moduleProgressId);

        this.validator.validateModuleProgressExists(moduleProgressId);
        final var reps = this.repetitionRepository.findByModuleProgressIdOrderByRepetitionOrder(
                moduleProgressId);
        return this.repetitionMapper.toDtoList(reps);
    }

    @Override
    @Transactional(readOnly = true)
    public RepetitionResponse findByModuleProgressIdAndOrder(UUID moduleProgressId, RepetitionOrder repetitionOrder) {
        requireNonNull(moduleProgressId, MODULE_PROGRESS_ID_MUST_NOT_BE_NULL);
        requireNonNull(repetitionOrder, "Repetition order must not be null");

        log.debug("Finding repetition by module progress ID: {} and order: {}", moduleProgressId, repetitionOrder);
        final var repetition = this.repetitionRepository.findByModuleProgressIdAndRepetitionOrder(
                moduleProgressId, repetitionOrder)
                .orElseThrow(() -> SpacedLearningException.resourceNotFound(
                        this.messageSource, "repetition.withProgressAndOrder",
                        new Object[] { moduleProgressId, repetitionOrder }));
        return this.repetitionMapper.toDto(repetition);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RepetitionResponse> findDueRepetitions(UUID userId, LocalDate reviewDate, RepetitionStatus status,
            Pageable pageable) {
        requireNonNull(userId, "User ID must not be null");
        requireNonNull(pageable, "Pageable must not be null");

        log.debug("Finding due repetitions for user ID: {} on or before date: {}, status: {}, pageable: {}",
                userId, reviewDate, status, pageable);

        final var targetDate = reviewDate != null ? reviewDate : LocalDate.now();
        final var targetStatus = status != null ? status : RepetitionStatus.NOT_STARTED;

        return this.repetitionRepository.findDueRepetitions(targetDate, targetStatus, pageable)
                .map(this.repetitionMapper::toDto);
    }

    @Override
    @Transactional
    public RepetitionResponse reschedule(UUID id, RepetitionRescheduleRequest request) {
        requireNonNull(id, REPETITION_ID_MUST_NOT_BE_NULL);
        requireNonNull(request, "Repetition reschedule request must not be null");
        requireNonNull(request.getReviewDate(), "Review date must not be null");

        log.debug("Rescheduling repetition with ID: {}, request: {}", id, request);
        final var repetition = this.validator.findRepetition(id);
        final var progress = repetition.getModuleProgress();

        repetition.setReviewDate(request.getReviewDate());
        if (request.isRescheduleFollowing()) {
            this.rescheduler.rescheduleFutureRepetitions(progress, repetition.getRepetitionOrder(), request
                    .getReviewDate());
        }

        final var updated = this.repetitionRepository.save(repetition);
        this.scheduleManager.updateNextStudyDate(progress);
        log.info("Repetition rescheduled successfully with ID: {}", updated.getId());
        return this.repetitionMapper.toDto(updated);
    }

    @Override
    @Transactional
    public RepetitionResponse update(UUID id, RepetitionUpdateRequest request) {
        requireNonNull(id, REPETITION_ID_MUST_NOT_BE_NULL);
        requireNonNull(request, "Repetition update request must not be null");

        log.debug("Updating repetition with ID: {}, request: {}", id, request);
        log.warn(
                "This method is deprecated and will be removed in a future version. Use updateCompletion or reschedule instead.");

        final var repetition = this.validator.findRepetition(id);
        final var progress = repetition.getModuleProgress();
        final var previousStatus = repetition.getStatus();

        this.repetitionMapper.updateFromDto(request, repetition);
        final var newStatus = repetition.getStatus();

        if (request.isRescheduleFollowing() && (request.getReviewDate() != null)) {
            this.rescheduler.rescheduleFutureRepetitions(progress, repetition.getRepetitionOrder(), request
                    .getReviewDate());
        } else if ((previousStatus != RepetitionStatus.COMPLETED) && (newStatus == RepetitionStatus.COMPLETED)) {
            this.rescheduler.updateFollowingAfterCompletion(progress, repetition);
            this.learningCycleManager.checkAndAdvanceCycle(progress);
        }

        final var updated = this.repetitionRepository.save(repetition);
        this.scheduleManager.updateNextStudyDate(progress);
        log.info("Repetition updated successfully with ID: {}", updated.getId());
        return this.repetitionMapper.toDto(updated);
    }

    @Override
    @Transactional
    public RepetitionResponse updateCompletion(UUID id, RepetitionCompletionRequest request) {
        requireNonNull(id, REPETITION_ID_MUST_NOT_BE_NULL);
        requireNonNull(request, "Repetition completion request must not be null");
        requireNonNull(request.getStatus(), "Status must not be null");

        log.debug("Updating completion for repetition with ID: {}, request: {}", id, request);
        final var repetition = this.validator.findRepetition(id);
        final var progress = repetition.getModuleProgress();

        final var previousStatus = repetition.getStatus();
        repetition.setStatus(request.getStatus());
        progress.setPercentComplete(request.getScore());

        final var newStatus = repetition.getStatus();
        if ((previousStatus != RepetitionStatus.COMPLETED) && (newStatus == RepetitionStatus.COMPLETED)) {
            this.rescheduler.updateFollowingAfterCompletion(progress, repetition);
            this.learningCycleManager.checkAndAdvanceCycle(progress);
        }

        final var updated = this.repetitionRepository.save(repetition);
        this.scheduleManager.updateNextStudyDate(progress);
        log.info("Repetition completion updated successfully with ID: {}", updated.getId());
        return this.repetitionMapper.toDto(updated);
    }
}
