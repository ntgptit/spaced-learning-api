// File: src/main/java/com/spacedlearning/service/impl/RepetitionServiceImpl.java
package com.spacedlearning.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spacedlearning.dto.repetition.RepetitionCreateRequest;
import com.spacedlearning.dto.repetition.RepetitionResponse;
import com.spacedlearning.dto.repetition.RepetitionUpdateRequest;
import com.spacedlearning.entity.ModuleProgress;
import com.spacedlearning.entity.Repetition;
import com.spacedlearning.entity.enums.CycleStudied;
import com.spacedlearning.entity.enums.RepetitionOrder;
import com.spacedlearning.entity.enums.RepetitionStatus;
import com.spacedlearning.exception.SpacedLearningException;
import com.spacedlearning.mapper.RepetitionMapper;
import com.spacedlearning.repository.ModuleProgressRepository;
import com.spacedlearning.repository.RepetitionRepository;
import com.spacedlearning.service.RepetitionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of RepetitionService
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RepetitionServiceImpl implements RepetitionService {

    // Base parameters for spaced repetition algorithm
    private static final double BASE_DAILY_WORDS = 41.7;
    private static final int[] REVIEW_MULTIPLIERS = { 2, 4, 8, 13, 19, 26 };

    private final RepetitionRepository repetitionRepository;
    private final ModuleProgressRepository progressRepository;
    private final RepetitionMapper repetitionMapper;

    @Override
    @Transactional
    public RepetitionResponse create(RepetitionCreateRequest request) {
        log.debug("Creating new repetition: {}", request);

        // Validate module progress existence
        final ModuleProgress progress = progressRepository.findById(request.getModuleProgressId()).orElseThrow(
                () -> SpacedLearningException.resourceNotFound("ModuleProgress", request.getModuleProgressId()));

        // Check if repetition already exists
        if (repetitionRepository.existsByModuleProgressIdAndRepetitionOrder(request.getModuleProgressId(),
                request.getRepetitionOrder())) {
            throw SpacedLearningException.resourceAlreadyExists("Repetition", "module_progress_id and repetition_order",
                    request.getModuleProgressId() + ", " + request.getRepetitionOrder());
        }

        // Create and save repetition
        final Repetition repetition = repetitionMapper.toEntity(request, progress);
        final Repetition savedRepetition = repetitionRepository.save(repetition);

        // Update next study date in module progress
        updateNextStudyDate(progress);

        log.info("Repetition created successfully with ID: {}", savedRepetition.getId());
        return repetitionMapper.toDto(savedRepetition);
    }

    @Override
    @Transactional
    public List<RepetitionResponse> createDefaultSchedule(UUID moduleProgressId) {
        log.debug("Creating default repetition schedule for module progress ID: {}", moduleProgressId);

        // Validate module progress existence
        final ModuleProgress progress = progressRepository.findById(moduleProgressId)
                .orElseThrow(() -> SpacedLearningException.resourceNotFound("ModuleProgress", moduleProgressId));

        // Check if schedule already exists
        final List<Repetition> existingRepetitions = repetitionRepository
                .findByModuleProgressIdOrderByRepetitionOrder(moduleProgressId);
        if (!existingRepetitions.isEmpty()) {
            log.info("Repetition schedule already exists for module progress ID: {}", moduleProgressId);
            return repetitionMapper.toDtoList(existingRepetitions);
        }

        // Get reference date (first learning date or today)
        LocalDate referenceDate = progress.getFirstLearningDate();
        if (referenceDate == null) {
            referenceDate = LocalDate.now();
            progress.setFirstLearningDate(referenceDate);
            progressRepository.save(progress);
        }

        final List<Repetition> repetitions = new ArrayList<>();
        final RepetitionOrder[] orders = RepetitionOrder.values();

        // Create repetitions based on advanced algorithm
        for (int i = 0; i < orders.length; i++) {
            final RepetitionOrder order = orders[i];

            // Calculate review date based on algorithm
            final LocalDate reviewDate = calculateReviewDate(progress, i);

            final Repetition repetition = new Repetition();
            repetition.setModuleProgress(progress);
            repetition.setRepetitionOrder(order);
            repetition.setStatus(RepetitionStatus.NOT_STARTED);
            repetition.setReviewDate(reviewDate);

            repetitions.add(repetition);
        }

        final List<Repetition> savedRepetitions = repetitionRepository.saveAll(repetitions);

        // Update next study date in module progress
        updateNextStudyDate(progress);

        log.info("Created default repetition schedule with {} repetitions for module progress ID: {}",
                savedRepetitions.size(), moduleProgressId);

        return repetitionMapper.toDtoList(savedRepetitions);
    }

    /**
     * Calculates the appropriate review date based on the Excel formula algorithm
     * 
     * @param progress    The module progress
     * @param reviewIndex The index of the current review (0-based)
     * @return The calculated review date
     */
    private LocalDate calculateReviewDate(ModuleProgress progress, int reviewIndex) {
        // Get base parameters from progress
        final Integer wordCount = ObjectUtils.defaultIfNull(progress.getModule().getWordCount(), 0);
        final double dailyWordCount = Math.max(20, wordCount > 0 ? wordCount : BASE_DAILY_WORDS);
        final CycleStudied cyclesStudied = progress.getCyclesStudied();
        final int studyCycleCount = getStudyCycleCount(cyclesStudied);
        getCompletedReviewCount(progress);
        final BigDecimal percentComplete = ObjectUtils.defaultIfNull(progress.getPercentComplete(),
                BigDecimal.valueOf(100));

        // Apply formula calculations
        final double progressMultiplier = Math.max(0.7, Math.min(1.5, percentComplete.doubleValue() / 100.0));
        final int adjustedCycleCount = Math.max(1, studyCycleCount);
        final double wordFactor = Math.min(3, Math.max(dailyWordCount, BASE_DAILY_WORDS) / BASE_DAILY_WORDS);

        // Calculate interval based on review index
        final int baseMultiplier = reviewIndex < REVIEW_MULTIPLIERS.length ? REVIEW_MULTIPLIERS[reviewIndex]
                : REVIEW_MULTIPLIERS[REVIEW_MULTIPLIERS.length - 1];

        // Calculate intervals using Excel formula logic
        final double baseInterval = wordFactor * Math.min(31, adjustedCycleCount * baseMultiplier) * progressMultiplier;

        final double additionalInterval;
        if (reviewIndex == 0) {
            // For first review, keep it simple
            additionalInterval = 0;
        } else {
            // For subsequent reviews, add additional spacing
            additionalInterval = (wordFactor * Math.min(31, adjustedCycleCount * 25) + 7) * studyCycleCount;
        }

        // Calculate total days and round to nearest integer
        final int totalDays = (int) Math.round(baseInterval + (reviewIndex > 0 ? additionalInterval : 0));

        // Get reference date
        LocalDate referenceDate = progress.getFirstLearningDate();
        if (referenceDate == null) {
            referenceDate = LocalDate.now();
        }

        // Return the calculated date
        return referenceDate.plusDays(totalDays);
    }

    /**
     * Updates the nextStudyDate field in ModuleProgress based on the earliest
     * upcoming repetition
     * 
     * @param progress The module progress to update
     */
    private void updateNextStudyDate(ModuleProgress progress) {
        final List<Repetition> notCompletedRepetitions = repetitionRepository
                .findByModuleProgressIdAndStatusOrderByReviewDate(progress.getId(), RepetitionStatus.NOT_STARTED);

        if (!notCompletedRepetitions.isEmpty()) {
            // Set next study date to the earliest upcoming repetition
            final LocalDate nextStudyDate = notCompletedRepetitions.get(0).getReviewDate();
            progress.setNextStudyDate(nextStudyDate);
            progressRepository.save(progress);
            log.debug("Updated next study date to {} for progress ID: {}", nextStudyDate, progress.getId());
        }
    }

    /**
     * Convert CycleStudied enum to numeric value for calculations
     */
    private int getStudyCycleCount(CycleStudied cyclesStudied) {
        if (cyclesStudied == null) {
            return 0;
        }

        switch (cyclesStudied) {
        case FIRST_TIME:
            return 0;
        case FIRST_REVIEW:
            return 1;
        case SECOND_REVIEW:
            return 2;
        case THIRD_REVIEW:
            return 3;
        case MORE_THAN_THREE_REVIEWS:
            return 3;
        default:
            return 0;
        }
    }

    /**
     * Count completed repetitions for a module progress
     */
    private int getCompletedReviewCount(ModuleProgress progress) {
        return (int) repetitionRepository.countByModuleProgressIdAndStatus(progress.getId(),
                RepetitionStatus.COMPLETED);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        log.debug("Deleting repetition with ID: {}", id);

        final Repetition repetition = repetitionRepository.findById(id)
                .orElseThrow(() -> SpacedLearningException.resourceNotFound("Repetition", id));

        final ModuleProgress progress = repetition.getModuleProgress();

        repetition.softDelete(); // Use soft delete
        repetitionRepository.save(repetition);

        // Update next study date after deletion
        updateNextStudyDate(progress);

        log.info("Repetition soft deleted successfully with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RepetitionResponse> findAll(Pageable pageable) {
        log.debug("Finding all repetitions with pagination: {}", pageable);
        return repetitionRepository.findAll(pageable).map(repetitionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public RepetitionResponse findById(UUID id) {
        log.debug("Finding repetition by ID: {}", id);
        final Repetition repetition = repetitionRepository.findById(id)
                .orElseThrow(() -> SpacedLearningException.resourceNotFound("Repetition", id));
        return repetitionMapper.toDto(repetition);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RepetitionResponse> findByModuleProgressId(UUID moduleProgressId) {
        log.debug("Finding repetitions by module progress ID: {}", moduleProgressId);

        // Verify module progress exists
        if (!progressRepository.existsById(moduleProgressId)) {
            throw SpacedLearningException.resourceNotFound("ModuleProgress", moduleProgressId);
        }

        final List<Repetition> repetitions = repetitionRepository
                .findByModuleProgressIdOrderByRepetitionOrder(moduleProgressId);
        return repetitionMapper.toDtoList(repetitions);
    }

    @Override
    @Transactional(readOnly = true)
    public RepetitionResponse findByModuleProgressIdAndOrder(UUID moduleProgressId, RepetitionOrder repetitionOrder) {
        log.debug("Finding repetition by module progress ID: {} and order: {}", moduleProgressId, repetitionOrder);

        final Repetition repetition = repetitionRepository
                .findByModuleProgressIdAndRepetitionOrder(moduleProgressId, repetitionOrder)
                .orElseThrow(() -> SpacedLearningException.resourceNotFound(
                        "Repetition for ModuleProgress " + moduleProgressId + " and Order " + repetitionOrder, null));

        return repetitionMapper.toDto(repetition);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RepetitionResponse> findDueRepetitions(UUID userId, LocalDate reviewDate, RepetitionStatus status,
            Pageable pageable) {
        log.debug("Finding repetitions due for review for user ID: {} on or before date: {}, status: {}, pageable: {}",
                userId, reviewDate, status, pageable);

        final LocalDate dateToCheck = reviewDate != null ? reviewDate : LocalDate.now();
        final RepetitionStatus statusToCheck = status != null ? status : RepetitionStatus.NOT_STARTED;

        return repetitionRepository.findDueRepetitions(userId, dateToCheck, statusToCheck, pageable)
                .map(repetitionMapper::toDto);
    }

    @Override
    @Transactional
    public RepetitionResponse update(UUID id, RepetitionUpdateRequest request) {
        log.debug("Updating repetition with ID: {}, request: {}", id, request);

        final Repetition repetition = repetitionRepository.findById(id)
                .orElseThrow(() -> SpacedLearningException.resourceNotFound("Repetition", id));

        final ModuleProgress progress = repetition.getModuleProgress();

        // Keep previous status to detect changes
        final RepetitionStatus previousStatus = repetition.getStatus();

        repetitionMapper.updateFromDto(request, repetition);
        final Repetition updatedRepetition = repetitionRepository.save(repetition);

        // If status changed to COMPLETED, check if all repetitions are completed
        if (previousStatus != RepetitionStatus.COMPLETED &&
                updatedRepetition.getStatus() == RepetitionStatus.COMPLETED) {

            // Check if all repetitions in this cycle are completed
            checkAndUpdateCycleStudied(progress);
        }

        // Update next study date after status change
        updateNextStudyDate(progress);

        log.info("Repetition updated successfully with ID: {}", updatedRepetition.getId());
        return repetitionMapper.toDto(updatedRepetition);
    }

    /**
     * Updates the cycle studied status after completing all repetitions in a cycle
     */
    private void checkAndUpdateCycleStudied(ModuleProgress progress) {
        // Get total repetition count for this module progress
        final long totalRepetitionCount = repetitionRepository.countByModuleProgressId(progress.getId());

        // Get completed repetition count
        final long completedCount = getCompletedReviewCount(progress);

        // Only update cycle when all repetitions in the current cycle are completed
        if (completedCount >= totalRepetitionCount) {
            // All repetitions are completed, update to next cycle
            switch (progress.getCyclesStudied()) {
            case FIRST_TIME:
                progress.setCyclesStudied(CycleStudied.FIRST_REVIEW);
                progressRepository.save(progress);
                log.info("Updated cycle studied to FIRST_REVIEW for progress ID: {}", progress.getId());
                break;
            case FIRST_REVIEW:
                progress.setCyclesStudied(CycleStudied.SECOND_REVIEW);
                progressRepository.save(progress);
                log.info("Updated cycle studied to SECOND_REVIEW for progress ID: {}", progress.getId());
                break;
            case SECOND_REVIEW:
                progress.setCyclesStudied(CycleStudied.THIRD_REVIEW);
                progressRepository.save(progress);
                log.info("Updated cycle studied to THIRD_REVIEW for progress ID: {}", progress.getId());
                break;
            case THIRD_REVIEW:
                progress.setCyclesStudied(CycleStudied.MORE_THAN_THREE_REVIEWS);
                progressRepository.save(progress);
                log.info("Updated cycle studied to MORE_THAN_THREE_REVIEWS for progress ID: {}", progress.getId());
                break;
            default:
                // No change needed for MORE_THAN_THREE_REVIEWS
                break;
            }

            // After completing a full cycle, create a new set of repetitions
            // for the next cycle and reschedule them according to the new cycle level
            createNewRepetitionCycle(progress);
        }
    }

    /**
     * Creates a new set of repetitions for the next study cycle
     */
    private void createNewRepetitionCycle(ModuleProgress progress) {
        // First, mark all existing repetitions as completed if they're not already
        final List<Repetition> existingRepetitions = repetitionRepository
                .findByModuleProgressIdOrderByRepetitionOrder(progress.getId());

        for (final Repetition rep : existingRepetitions) {
            if (rep.getStatus() != RepetitionStatus.COMPLETED) {
                rep.setStatus(RepetitionStatus.COMPLETED);
            }
        }
        repetitionRepository.saveAll(existingRepetitions);

        // Create new repetitions for the next cycle with appropriate intervals
        final List<Repetition> newRepetitions = new ArrayList<>();
        final RepetitionOrder[] orders = RepetitionOrder.values();

        // Get reference date (today)
        final LocalDate referenceDate = LocalDate.now();

        // Update first learning date for this new cycle
        progress.setFirstLearningDate(referenceDate);
        progressRepository.save(progress);

        // Create repetitions for each order with recalculated intervals
        for (int i = 0; i < orders.length; i++) {
            final RepetitionOrder order = orders[i];

            // Calculate review date based on algorithm with new cycle
            final LocalDate reviewDate = calculateReviewDate(progress, i);

            final Repetition repetition = new Repetition();
            repetition.setModuleProgress(progress);
            repetition.setRepetitionOrder(order);
            repetition.setStatus(RepetitionStatus.NOT_STARTED);
            repetition.setReviewDate(reviewDate);

            newRepetitions.add(repetition);
        }

        repetitionRepository.saveAll(newRepetitions);
        log.info("Created new repetition cycle with {} repetitions for module progress ID: {}", newRepetitions.size(),
                progress.getId());

        // Update next study date
        updateNextStudyDate(progress);
    }
}