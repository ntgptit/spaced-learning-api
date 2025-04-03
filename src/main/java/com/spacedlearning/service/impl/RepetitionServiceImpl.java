// File: src/main/java/com/spacedlearning/service/impl/RepetitionServiceImpl.java
package com.spacedlearning.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
 * Implementation of RepetitionService that handles the spaced repetition algorithm
 * for learning modules.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RepetitionServiceImpl implements RepetitionService {

    // Base parameters for spaced repetition algorithm
    private static final double BASE_DAILY_WORDS = 41.7;
    private static final int[] REVIEW_MULTIPLIERS = { 2, 4, 8, 13, 19, 26 };
    private static final int MAX_WORD_FACTOR = 3;
    private static final int MAX_INTERVAL_DAYS = 31;
    private static final int MIN_DAILY_WORDS = 20;
    private final RepetitionRepository repetitionRepository;
    private final ModuleProgressRepository progressRepository;
    private final RepetitionMapper repetitionMapper;

    @Override
    @Transactional
    public RepetitionResponse create(final RepetitionCreateRequest request) {
        Objects.requireNonNull(request, "Repetition create request must not be null");
        log.debug("Creating new repetition: {}", request);

        // Validate module progress existence
        final ModuleProgress progress = findModuleProgress(request.getModuleProgressId());

        // Check if repetition already exists
        validateRepetitionDoesNotExist(request.getModuleProgressId(), request.getRepetitionOrder());

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
    public List<RepetitionResponse> createDefaultSchedule(final UUID moduleProgressId) {
        Objects.requireNonNull(moduleProgressId, "Module progress ID must not be null");
        log.debug("Creating default repetition schedule for module progress ID: {}", moduleProgressId);

        // Validate module progress existence
        final ModuleProgress progress = findModuleProgress(moduleProgressId);

        // Check if schedule already exists
        final List<Repetition> existingRepetitions = repetitionRepository
                .findByModuleProgressIdOrderByRepetitionOrder(moduleProgressId);

        if (!existingRepetitions.isEmpty()) {
            log.info("Repetition schedule already exists for module progress ID: {}", moduleProgressId);
            return repetitionMapper.toDtoList(existingRepetitions);
        }

        // Initialize first learning date if not set
        initializeFirstLearningDate(progress);

        // Create repetitions based on advanced algorithm
        final List<Repetition> repetitions = createRepetitionsForProgress(progress);
        final List<Repetition> savedRepetitions = repetitionRepository.saveAll(repetitions);

        // Update next study date in module progress
        updateNextStudyDate(progress);

        log.info("Created default repetition schedule with {} repetitions for module progress ID: {}",
                savedRepetitions.size(), moduleProgressId);

        return repetitionMapper.toDtoList(savedRepetitions);
    }

    /**
     * Creates repetition schedule based on the spaced repetition algorithm
     *
     * @param progress The module progress
     * @return List of repetitions
     */
    private List<Repetition> createRepetitionsForProgress(final ModuleProgress progress) {
        final List<Repetition> repetitions = new ArrayList<>();
        final RepetitionOrder[] orders = RepetitionOrder.values();

        for (int i = 0; i < orders.length; i++) {
            final RepetitionOrder order = orders[i];
            final LocalDate reviewDate = calculateReviewDate(progress, i);

            final Repetition repetition = new Repetition();
            repetition.setModuleProgress(progress);
            repetition.setRepetitionOrder(order);
            repetition.setStatus(RepetitionStatus.NOT_STARTED);
            repetition.setReviewDate(reviewDate);

            repetitions.add(repetition);
        }

        return repetitions;
    }

    /**
     * Initializes first learning date if not set
     *
     * @param progress The module progress to update
     */
    private void initializeFirstLearningDate(final ModuleProgress progress) {
        if (progress.getFirstLearningDate() == null) {
            progress.setFirstLearningDate(LocalDate.now());
            progressRepository.save(progress);
        }
    }

    /**
     * Calculates the appropriate review date based on the spaced repetition algorithm
     *
     * @param progress    The module progress
     * @param reviewIndex The index of the current review (0-based)
     * @return The calculated review date
     */
    private LocalDate calculateReviewDate(final ModuleProgress progress, final int reviewIndex) {
        // Get base parameters from progress
        final Integer wordCount = progress.getModule() != null ? progress.getModule().getWordCount() : 0;
        final double dailyWordCount = Math.max(MIN_DAILY_WORDS, wordCount > 0 ? wordCount : BASE_DAILY_WORDS);
        final int studyCycleCount = getStudyCycleCount(progress.getCyclesStudied());
        final BigDecimal percentComplete = progress.getPercentComplete();

        // Apply formula calculations
        final int adjustedCycleCount = Math.max(1, studyCycleCount);
        final double wordFactor = calculateWordFactor(dailyWordCount);
        double baseInterval = calculateBaseInterval(reviewIndex, adjustedCycleCount, wordFactor);

        // Get reference date
        final LocalDate referenceDate = progress.getFirstLearningDate() != null ? progress.getFirstLearningDate()
                : LocalDate.now();

        // Calculate additional interval (only for non-first review)
        final double additionalInterval = reviewIndex > 0 ? (wordFactor * Math.min(31, adjustedCycleCount * 25) + 7)
                * studyCycleCount : 0;

        baseInterval = baseInterval + additionalInterval;

        // Calculate days to add based on percentage
        final double daysToAdd = percentComplete == null || percentComplete.doubleValue() == 0.0
                ? baseInterval
                : baseInterval * percentComplete.doubleValue() / 100.0;

        final long roundedDaysToAdd = Math.round(daysToAdd);

        // Calculate initial review date
        final LocalDate initialCalculatedDate = referenceDate.plusDays(roundedDaysToAdd);

        // Check constraint (not more than 60 days from first learning date)
        LocalDate dateBeforeOptimization = initialCalculatedDate;
        if (progress.getFirstLearningDate() != null && initialCalculatedDate.minusDays(60).isAfter(progress
                .getFirstLearningDate())) {
            dateBeforeOptimization = progress.getFirstLearningDate().plusDays(60);
        }

        // Find optimal date with proper distribution
        return findOptimalDate(dateBeforeOptimization);
    }

    /**
     * Finds an optimal date that doesn't overload the user's schedule
     *
     * @param initialDate The initially calculated review date
     * @return An optimized date with better workload distribution
     */
    private LocalDate findOptimalDate(final LocalDate initialDate) {
        int repetitionCount;
        try {
            repetitionCount = repetitionRepository.countReviewDateExisted(initialDate);
        } catch (final Exception e) {
            log.error("Error counting repetitions for date {}: {}", initialDate, e.getMessage(), e);
            return initialDate;
        }

        // If date is not overloaded, return as is
        if (repetitionCount <= 3) {
            return initialDate;
        }

        // Try to find a date with fewer repetitions
        log.warn("Initial date {} is overloaded (count={}). Searching for alternatives within 7 days...",
                initialDate, repetitionCount);

        for (int i = 1; i <= 7; i++) {
            final LocalDate candidateDate = initialDate.plusDays(i);
            int candidateCount;
            try {
                candidateCount = repetitionRepository.countReviewDateExisted(candidateDate);
            } catch (final Exception e) {
                log.error("Error counting repetitions for candidate date {}: {}", candidateDate, e.getMessage(), e);
                continue;
            }

            if (candidateCount <= 3) {
                log.info("Found suitable alternative date: {}", candidateDate);
                return candidateDate;
            }
        }

        // If all dates are heavily loaded, return date with smallest offset (+1 day)
        final LocalDate fallbackDate = initialDate.plusDays(1);
        log.warn("No suitable alternative found within 7 days. Falling back to: {}", fallbackDate);
        return fallbackDate;
    }

    /**
     * Calculates the word factor based on daily word count
     *
     * @param dailyWordCount The daily word count
     * @return The calculated word factor
     */
    private double calculateWordFactor(final double dailyWordCount) {
        return Math.min(MAX_WORD_FACTOR, Math.max(dailyWordCount, BASE_DAILY_WORDS) / BASE_DAILY_WORDS);
    }

    /**
     * Calculates the base interval for the spaced repetition algorithm
     *
     * @param reviewIndex        The review index (0-based)
     * @param adjustedCycleCount The adjusted cycle count
     * @param wordFactor         The word factor
     * @return The calculated base interval
     */
    private double calculateBaseInterval(final int reviewIndex, final int adjustedCycleCount, final double wordFactor) {
        final int baseMultiplier = reviewIndex < REVIEW_MULTIPLIERS.length ? REVIEW_MULTIPLIERS[reviewIndex]
                : REVIEW_MULTIPLIERS[REVIEW_MULTIPLIERS.length - 1];
        return wordFactor * Math.min(MAX_INTERVAL_DAYS, adjustedCycleCount * baseMultiplier);
    }

    /**
     * Updates the nextStudyDate field in ModuleProgress based on the earliest
     * upcoming repetition
     *
     * @param progress The module progress to update
     */
    private void updateNextStudyDate(final ModuleProgress progress) {
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
     *
     * @param cyclesStudied The cycle studied enum
     * @return The numeric value for calculations
     */
    private int getStudyCycleCount(final CycleStudied cyclesStudied) {
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
        case MORE_THAN_THREE_REVIEWS:
            return 3;
        default:
            return 0;
        }
    }

    /**
     * Count completed repetitions for a module progress
     *
     * @param progress The module progress
     * @return The count of completed repetitions
     */
    private int getCompletedReviewCount(final ModuleProgress progress) {
        return (int) repetitionRepository.countByModuleProgressIdAndStatus(progress.getId(),
                RepetitionStatus.COMPLETED);
    }

    @Override
    @Transactional
    public void delete(final UUID id) {
        Objects.requireNonNull(id, "Repetition ID must not be null");
        log.debug("Deleting repetition with ID: {}", id);

        final Repetition repetition = findRepetition(id);
        final ModuleProgress progress = repetition.getModuleProgress();

        repetition.softDelete(); // Use soft delete
        repetitionRepository.save(repetition);

        // Update next study date after deletion
        updateNextStudyDate(progress);

        log.info("Repetition soft deleted successfully with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RepetitionResponse> findAll(final Pageable pageable) {
        Objects.requireNonNull(pageable, "Pageable must not be null");
        log.debug("Finding all repetitions with pagination: {}", pageable);
        return repetitionRepository.findAll(pageable).map(repetitionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public RepetitionResponse findById(final UUID id) {
        Objects.requireNonNull(id, "Repetition ID must not be null");
        log.debug("Finding repetition by ID: {}", id);
        final Repetition repetition = findRepetition(id);
        return repetitionMapper.toDto(repetition);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RepetitionResponse> findByModuleProgressId(final UUID moduleProgressId) {
        Objects.requireNonNull(moduleProgressId, "Module progress ID must not be null");
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
    public RepetitionResponse findByModuleProgressIdAndOrder(final UUID moduleProgressId,
            final RepetitionOrder repetitionOrder) {
        Objects.requireNonNull(moduleProgressId, "Module progress ID must not be null");
        Objects.requireNonNull(repetitionOrder, "Repetition order must not be null");
        log.debug("Finding repetition by module progress ID: {} and order: {}", moduleProgressId, repetitionOrder);

        final Repetition repetition = repetitionRepository
                .findByModuleProgressIdAndRepetitionOrder(moduleProgressId, repetitionOrder)
                .orElseThrow(() -> SpacedLearningException.resourceNotFound(
                        "Repetition for ModuleProgress " + moduleProgressId + " and Order " + repetitionOrder, null));

        return repetitionMapper.toDto(repetition);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RepetitionResponse> findDueRepetitions(final UUID userId, final LocalDate reviewDate,
            final RepetitionStatus status, final Pageable pageable) {
        Objects.requireNonNull(userId, "User ID must not be null");
        Objects.requireNonNull(pageable, "Pageable must not be null");
        log.debug("Finding repetitions due for review for user ID: {} on or before date: {}, status: {}, pageable: {}",
                userId, reviewDate, status, pageable);

        final LocalDate dateToCheck = reviewDate != null ? reviewDate : LocalDate.now();
        final RepetitionStatus statusToCheck = status != null ? status : RepetitionStatus.NOT_STARTED;

        return repetitionRepository.findDueRepetitions(userId, dateToCheck, statusToCheck, pageable)
                .map(repetitionMapper::toDto);
    }

    @Override
    @Transactional
    public RepetitionResponse update(final UUID id, final RepetitionUpdateRequest request) {
        Objects.requireNonNull(id, "Repetition ID must not be null");
        Objects.requireNonNull(request, "Repetition update request must not be null");
        log.debug("Updating repetition with ID: {}, request: {}", id, request);

        final Repetition repetition = findRepetition(id);
        final ModuleProgress progress = repetition.getModuleProgress();
        final RepetitionStatus previousStatus = repetition.getStatus();

        // Apply updates from DTO
        repetitionMapper.updateFromDto(request, repetition);
        final RepetitionStatus newStatus = repetition.getStatus();

        // If status is changing to COMPLETED, recalculate next review date
        if (previousStatus != RepetitionStatus.COMPLETED &&
                newStatus == RepetitionStatus.COMPLETED) {

            log.debug("Status changed from {} to COMPLETED for Repetition ID: {}", previousStatus, id);

            // Find the next repetition (if any) and update its date
            findAndUpdateNextRepetition(progress, repetition.getRepetitionOrder());

            // Check if this was the last repetition in the cycle
            checkAndUpdateCycleStudied(progress);
        }

        final Repetition updatedRepetition = repetitionRepository.save(repetition);

        // Update next study date after all changes
        updateNextStudyDate(progress);

        log.info("Repetition updated successfully with ID: {}", updatedRepetition.getId());
        return repetitionMapper.toDto(updatedRepetition);
    }

    /**
     * Finds the next repetition in sequence and updates its review date based on current progress
     *
     * @param progress     The module progress
     * @param currentOrder The current repetition order that was completed
     */
    private void findAndUpdateNextRepetition(ModuleProgress progress, RepetitionOrder currentOrder) {
        final RepetitionOrder[] orders = RepetitionOrder.values();
        int currentIndex = -1;
        for (int i = 0; i < orders.length; i++) {
            if (orders[i] == currentOrder) {
                currentIndex = i;
                break;
            }
        }

        // If this is the last repetition or order not found, no next repetition to update
        if (currentIndex == -1 || currentIndex >= orders.length - 1) {
            return;
        }

        final RepetitionOrder nextOrder = orders[currentIndex + 1];
        final int nextReviewIndex = currentIndex + 1;

        final Optional<Repetition> nextRepetitionOpt = repetitionRepository
                .findByModuleProgressIdAndRepetitionOrderAndStatus(progress.getId(), nextOrder,
                        RepetitionStatus.NOT_STARTED);

        if (nextRepetitionOpt.isPresent()) {
            final Repetition nextRepetition = nextRepetitionOpt.get();

            // Only recalculate if not already completed
            if (nextRepetition.getStatus() != RepetitionStatus.COMPLETED) {
                final LocalDate oldReviewDate = nextRepetition.getReviewDate();
                final LocalDate newReviewDate = calculateReviewDate(progress, nextReviewIndex);

                // Update the next repetition's review date
                nextRepetition.setReviewDate(newReviewDate);
                repetitionRepository.save(nextRepetition);

                log.info(
                        "Updated next repetition review date - ProgressID: {}, OrderCompleted: {}, NextOrder: {}, OldDate: {}, NewDate: {}",
                        progress.getId(), currentOrder, nextOrder, oldReviewDate, newReviewDate);
            }
        }
    }

    /**
     * Updates the cycle studied status after completing all repetitions in a cycle
     *
     * @param progress The module progress to update
     */
    private void checkAndUpdateCycleStudied(final ModuleProgress progress) {
        // Get total repetition count for this module progress
        final long totalRepetitionCount = repetitionRepository.countByModuleProgressId(progress.getId());

        // Get completed repetition count
        final long completedCount = getCompletedReviewCount(progress);

        // Only update cycle when all repetitions in the current cycle are completed
        if (completedCount >= totalRepetitionCount) {
            updateToNextCycleLevel(progress);
            createNewRepetitionCycle(progress);
        }
    }

    /**
     * Updates the cycle studied level to the next level
     *
     * @param progress The module progress to update
     */
    private void updateToNextCycleLevel(final ModuleProgress progress) {
        switch (progress.getCyclesStudied()) {
        case FIRST_TIME:
            updateCycleStudied(progress, CycleStudied.FIRST_REVIEW);
            break;
        case FIRST_REVIEW:
            updateCycleStudied(progress, CycleStudied.SECOND_REVIEW);
            break;
        case SECOND_REVIEW:
            updateCycleStudied(progress, CycleStudied.THIRD_REVIEW);
            break;
        case THIRD_REVIEW:
            updateCycleStudied(progress, CycleStudied.MORE_THAN_THREE_REVIEWS);
            break;
        default:
            // No change needed for MORE_THAN_THREE_REVIEWS
            break;
        }
    }

    /**
     * Updates the cycle studied level and saves the progress
     *
     * @param progress The module progress to update
     * @param newCycle The new cycle studied level
     */
    private void updateCycleStudied(final ModuleProgress progress, final CycleStudied newCycle) {
        progress.setCyclesStudied(newCycle);
        progressRepository.save(progress);
        log.info("Updated cycle studied to {} for progress ID: {}", newCycle, progress.getId());
    }

    /**
     * Creates a new set of repetitions for the next study cycle
     *
     * @param progress The module progress
     */
    private void createNewRepetitionCycle(final ModuleProgress progress) {
        // Mark all existing repetitions as completed
        markExistingRepetitionsAsCompleted(progress);

        // Create new repetitions for the next cycle
        final List<Repetition> newRepetitions = createRepetitionsWithCurrentDate(progress);
        repetitionRepository.saveAll(newRepetitions);

        log.info("Created new repetition cycle with {} repetitions for module progress ID: {}",
                newRepetitions.size(), progress.getId());

        // Update next study date
        updateNextStudyDate(progress);
    }

    /**
     * Mark all existing repetitions as completed
     *
     * @param progress The module progress
     */
    private void markExistingRepetitionsAsCompleted(final ModuleProgress progress) {
        final List<Repetition> existingRepetitions = repetitionRepository
                .findByModuleProgressIdOrderByRepetitionOrder(progress.getId());

        for (final Repetition rep : existingRepetitions) {
            if (rep.getStatus() != RepetitionStatus.COMPLETED) {
                rep.setStatus(RepetitionStatus.COMPLETED);
            }
        }
        repetitionRepository.saveAll(existingRepetitions);
    }

    /**
     * Create new repetitions using current date as reference
     *
     * @param progress The module progress
     * @return List of new repetitions
     */
    private List<Repetition> createRepetitionsWithCurrentDate(final ModuleProgress progress) {
        progress.setFirstLearningDate(LocalDate.now());
        progressRepository.save(progress);

        // Create repetitions based on updated progress
        return createRepetitionsForProgress(progress);
    }

    /**
     * Finds a module progress by ID or throws an exception if not found
     *
     * @param moduleProgressId The module progress ID
     * @return The module progress
     * @throws SpacedLearningException if not found
     */
    private ModuleProgress findModuleProgress(final UUID moduleProgressId) {
        return progressRepository.findById(moduleProgressId)
                .orElseThrow(() -> SpacedLearningException.resourceNotFound("ModuleProgress", moduleProgressId));
    }

    /**
     * Finds a repetition by ID or throws an exception if not found
     *
     * @param id The repetition ID
     * @return The repetition
     * @throws SpacedLearningException if not found
     */
    private Repetition findRepetition(final UUID id) {
        return repetitionRepository.findById(id)
                .orElseThrow(() -> SpacedLearningException.resourceNotFound("Repetition", id));
    }

    /**
     * Validates that a repetition does not already exist for the given module progress and order
     *
     * @param moduleProgressId The module progress ID
     * @param repetitionOrder  The repetition order
     * @throws SpacedLearningException if the repetition already exists
     */
    private void validateRepetitionDoesNotExist(final UUID moduleProgressId, final RepetitionOrder repetitionOrder) {
        if (repetitionRepository.existsByModuleProgressIdAndRepetitionOrder(moduleProgressId, repetitionOrder)) {
            throw SpacedLearningException.resourceAlreadyExists("Repetition", "module_progress_id and repetition_order",
                    moduleProgressId + ", " + repetitionOrder);
        }
    }
}