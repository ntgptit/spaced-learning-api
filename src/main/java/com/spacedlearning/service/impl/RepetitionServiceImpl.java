package com.spacedlearning.service.impl;

import com.spacedlearning.dto.repetition.*;
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
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RepetitionServiceImpl implements RepetitionService {

    public static final String MODULE_PROGRESS_ID_MUST_NOT_BE_NULL = "Module progress ID must not be null";
    public static final String REPETITION_ID_MUST_NOT_BE_NULL = "Repetition ID must not be null";
    private final RepetitionRepository repetitionRepository;
    private final RepetitionMapper repetitionMapper;
    private final RepetitionScheduleManager scheduleManager;
    private final RepetitionValidator validator;
    private final MessageSource messageSource;

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
        Objects.requireNonNull(moduleProgressId, MODULE_PROGRESS_ID_MUST_NOT_BE_NULL);
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
        Objects.requireNonNull(id, REPETITION_ID_MUST_NOT_BE_NULL);
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
        Objects.requireNonNull(id, REPETITION_ID_MUST_NOT_BE_NULL);
        log.debug("Finding repetition by ID: {}", id);
        final Repetition repetition = validator.findRepetition(id);
        return repetitionMapper.toDto(repetition);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RepetitionResponse> findByModuleProgressId(UUID moduleProgressId) {
        Objects.requireNonNull(moduleProgressId, MODULE_PROGRESS_ID_MUST_NOT_BE_NULL);
        log.debug("Finding repetitions by module progress ID: {}", moduleProgressId);
        validator.validateModuleProgressExists(moduleProgressId);
        final List<Repetition> repetitions = repetitionRepository.findByModuleProgressIdOrderByRepetitionOrder(
                moduleProgressId);
        return repetitionMapper.toDtoList(repetitions);
    }

    @Override
    @Transactional(readOnly = true)
    public RepetitionResponse findByModuleProgressIdAndOrder(UUID moduleProgressId, RepetitionOrder repetitionOrder) {
        Objects.requireNonNull(moduleProgressId, MODULE_PROGRESS_ID_MUST_NOT_BE_NULL);
        Objects.requireNonNull(repetitionOrder, "Repetition order must not be null");
        log.debug("Finding repetition by module progress ID: {} and order: {}", moduleProgressId, repetitionOrder);

        final Repetition repetition = repetitionRepository.findByModuleProgressIdAndRepetitionOrder(moduleProgressId,
                        repetitionOrder)
                .orElseThrow(() -> SpacedLearningException.resourceNotFound(
                        messageSource,
                        "repetition.withProgressAndOrder",
                        new Object[]{moduleProgressId, repetitionOrder}));
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
        return repetitionRepository.findDueRepetitions(dateToCheck, statusToCheck, pageable)
                .map(repetitionMapper::toDto);
    }

    @Override
    @Transactional
    public RepetitionResponse update(UUID id, RepetitionUpdateRequest request) {
        Objects.requireNonNull(id, REPETITION_ID_MUST_NOT_BE_NULL);
        Objects.requireNonNull(request, "Repetition update request must not be null");
        log.debug("Updating repetition with ID: {}, request: {}", id, request);
        log.warn("This method is deprecated and will be removed in a future version. Use updateCompletion or reschedule instead.");

        final Repetition repetition = validator.findRepetition(id);
        final ModuleProgress progress = repetition.getModuleProgress();
        final RepetitionStatus previousStatus = repetition.getStatus();

        repetitionMapper.updateFromDto(request, repetition);
        final RepetitionStatus newStatus = repetition.getStatus();

        if (request.isRescheduleFollowing() && request.getReviewDate() != null) {
            scheduleManager.rescheduleFutureRepetitions(progress, repetition.getRepetitionOrder(),
                    request.getReviewDate());
        } else if (previousStatus != RepetitionStatus.COMPLETED && newStatus == RepetitionStatus.COMPLETED) {
            log.debug("Status changed to COMPLETED for Repetition ID: {}", id);
            scheduleManager.updateFutureRepetitions(progress, repetition.getRepetitionOrder());
            scheduleManager.checkAndUpdateCycleStudied(progress);
        }

        final Repetition updatedRepetition = repetitionRepository.save(repetition);
        scheduleManager.updateNextStudyDate(progress);

        log.info("Repetition updated successfully with ID: {}", updatedRepetition.getId());
        return repetitionMapper.toDto(updatedRepetition);
    }

    @Override
    @Transactional
    public RepetitionResponse updateCompletion(UUID id, RepetitionCompletionRequest request) {
        Objects.requireNonNull(id, REPETITION_ID_MUST_NOT_BE_NULL);
        Objects.requireNonNull(request, "Repetition completion request must not be null");
        Objects.requireNonNull(request.getStatus(), "Status must not be null");
        log.debug("Updating completion for repetition with ID: {}, request: {}", id, request);

        final Repetition repetition = validator.findRepetition(id);
        final ModuleProgress progress = repetition.getModuleProgress();
        final RepetitionStatus previousStatus = repetition.getStatus();

        // Cập nhật status
        repetition.setStatus(request.getStatus());
        final RepetitionStatus newStatus = repetition.getStatus();

        // Xử lý logic khi chuyển trạng thái từ NOT_STARTED/SKIPPED sang COMPLETED
        if (previousStatus != RepetitionStatus.COMPLETED && newStatus == RepetitionStatus.COMPLETED) {
            log.debug("Status changed to COMPLETED for Repetition ID: {}", id);
            scheduleManager.updateFutureRepetitions(progress, repetition.getRepetitionOrder());
            scheduleManager.checkAndUpdateCycleStudied(progress);
        }

        final Repetition updatedRepetition = repetitionRepository.save(repetition);
        scheduleManager.updateNextStudyDate(progress);

        log.info("Repetition completion updated successfully with ID: {}", updatedRepetition.getId());
        return repetitionMapper.toDto(updatedRepetition);
    }

    @Override
    @Transactional
    public RepetitionResponse reschedule(UUID id, RepetitionRescheduleRequest request) {
        Objects.requireNonNull(id, REPETITION_ID_MUST_NOT_BE_NULL);
        Objects.requireNonNull(request, "Repetition reschedule request must not be null");
        Objects.requireNonNull(request.getReviewDate(), "Review date must not be null");
        log.debug("Rescheduling repetition with ID: {}, request: {}", id, request);

        final Repetition repetition = validator.findRepetition(id);
        final ModuleProgress progress = repetition.getModuleProgress();

        // Cập nhật ngày xem lại
        repetition.setReviewDate(request.getReviewDate());

        // Lập lịch lại các repetition tiếp theo nếu cần
        if (request.isRescheduleFollowing()) {
            scheduleManager.rescheduleFutureRepetitions(progress, repetition.getRepetitionOrder(), request.getReviewDate());
        }

        final Repetition updatedRepetition = repetitionRepository.save(repetition);
        scheduleManager.updateNextStudyDate(progress);

        log.info("Repetition rescheduled successfully with ID: {}", updatedRepetition.getId());
        return repetitionMapper.toDto(updatedRepetition);
    }
}