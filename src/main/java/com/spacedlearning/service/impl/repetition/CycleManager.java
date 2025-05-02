package com.spacedlearning.service.impl.repetition;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import com.spacedlearning.entity.ModuleProgress;
import com.spacedlearning.entity.Repetition;
import com.spacedlearning.entity.enums.CycleStudied;
import com.spacedlearning.entity.enums.RepetitionStatus;
import com.spacedlearning.repository.ModuleProgressRepository;
import com.spacedlearning.repository.RepetitionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Manages learning cycles for module progress.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CycleManager {
    private final RepetitionRepository repetitionRepository;
    private final ModuleProgressRepository progressRepository;

    /**
     * Initializes the first cycle for a module progress if not already recorded.
     */
    public void initializeFirstCycle(@NonNull ModuleProgress progress) {
        if (progress.findLatestCycleStart(CycleStudied.FIRST_TIME) != null) {
            return;
        }

        LocalDate startDate = progress.getFirstLearningDate();
        if (startDate == null) {
            startDate = LocalDate.now();
            progress.setFirstLearningDate(startDate);
        }

        progress.addCycleStart(CycleStudied.FIRST_TIME, startDate);
        progressRepository.save(progress);
        log.info("Initialized first cycle record for progress ID: {} starting on {}",
                progress.getId(), startDate);
    }

    /**
     * Checks and updates the study cycle for a module progress based on completed repetitions.
     * Uses the provided function to create new repetitions when needed.
     *
     * @param progress          The module progress to check
     * @param repetitionCreator Function to create repetitions for the next cycle
     */
    public void checkAndUpdateCycleStudied(@NonNull ModuleProgress progress,
            Function<ModuleProgress, List<Repetition>> repetitionCreator) {
        // Check completion status
        if (!isAllRepetitionsCompleted(progress)) {
            return;
        }

        log.info("All repetitions completed for progress ID: {}. Checking for cycle update.", progress.getId());

        // Determine next cycle
        final CycleStudied currentCycle = progress.getCyclesStudied() != null ? progress.getCyclesStudied()
                : CycleStudied.FIRST_TIME;
        final CycleStudied nextCycle = getNextCycle(currentCycle);

        if (nextCycle == currentCycle) {
            log.info("Current cycle {} is the final cycle or no update needed for progress ID: {}",
                    currentCycle, progress.getId());
            return;
        }

        // Add new cycle record
        final LocalDate now = LocalDate.now();
        progress.addCycleStart(nextCycle, now);

        progress.setCyclesStudied(nextCycle);
        progressRepository.save(progress);
        log.info("Updated cycle studied from {} to {} for progress ID: {} starting on {}",
                currentCycle, nextCycle, progress.getId(), now);

        // Create new repetitions for the next cycle
        createNewRepetitionCycle(progress, repetitionCreator);
    }

    /**
     * Checks if all repetitions for a module progress are completed.
     */
    private boolean isAllRepetitionsCompleted(ModuleProgress progress) {
        final long totalRepetitionCount = repetitionRepository.countByModuleProgressId(progress.getId());
        if (totalRepetitionCount <= 0) {
            log.debug("No repetitions found for progress ID: {}. Cannot check cycle.", progress.getId());
            return false;
        }

        final long completedCount = repetitionRepository.countByModuleProgressIdAndStatus(
                progress.getId(), RepetitionStatus.COMPLETED);

        if (completedCount < totalRepetitionCount) {
            log.debug("Not all repetitions completed ({} / {}) for progress ID: {}. No cycle update.",
                    completedCount, totalRepetitionCount, progress.getId());
            return false;
        }

        return true;
    }

    /**
     * Determines the next cycle based on the current cycle.
     */
    private CycleStudied getNextCycle(CycleStudied currentCycle) {
        return switch (currentCycle) {
        case FIRST_TIME -> CycleStudied.FIRST_REVIEW;
        case FIRST_REVIEW -> CycleStudied.SECOND_REVIEW;
        case SECOND_REVIEW -> CycleStudied.THIRD_REVIEW;
        case THIRD_REVIEW -> CycleStudied.MORE_THAN_THREE_REVIEWS;
        default -> currentCycle;
        };
    }

    /**
     * Creates a new repetition cycle for a module progress after completing the previous cycle.
     */
    private void createNewRepetitionCycle(ModuleProgress progress,
            Function<ModuleProgress, List<Repetition>> repetitionCreator) {
        log.info("Creating new repetition cycle for progress ID: {}", progress.getId());

        // Mark any remaining non-completed repetitions as completed
        completeRemainingRepetitions(progress);

        // Create new repetitions based on current state
        final List<Repetition> newRepetitions = createRepetitionsWithCurrentDate(progress, repetitionCreator);
        if (newRepetitions.isEmpty()) {
            log.warn("Failed to create new repetitions for the next cycle for progress ID: {}", progress.getId());
            return;
        }

        repetitionRepository.saveAll(newRepetitions);
        log.info("Created and saved {} new repetitions for the next cycle for progress ID: {}",
                newRepetitions.size(), progress.getId());
    }

    /**
     * Completes any remaining non-completed repetitions.
     */
    private void completeRemainingRepetitions(ModuleProgress progress) {
        final List<Repetition> existingRepetitions = repetitionRepository.findByModuleProgressIdOrderByRepetitionOrder(
                progress.getId());

        boolean requiresSave = false;
        for (final Repetition rep : existingRepetitions) {
            if (rep.getStatus() == RepetitionStatus.COMPLETED) {
                continue;
            }
            rep.setStatus(RepetitionStatus.COMPLETED);
            requiresSave = true;
        }

        if (requiresSave) {
            repetitionRepository.saveAll(existingRepetitions);
            log.debug("Marked previously non-completed repetitions as COMPLETED for progress ID: {}", progress.getId());
        }
    }

    /**
     * Prepares the module progress and creates repetitions for a new cycle.
     */
    @NonNull
    private List<Repetition> createRepetitionsWithCurrentDate(
            @NonNull ModuleProgress progress,
            Function<ModuleProgress, List<Repetition>> repetitionCreator) {

        final Optional<LocalDate> lastCompletedDateOpt = repetitionRepository.findLastCompletedRepetitionDate(progress
                .getId());

        LocalDate newStartDate;
        if (lastCompletedDateOpt.isEmpty()) {
            log.warn("Could not find last completed repetition date for progress ID: {}. " +
                    "Using existing/fallback firstLearningDate.", progress.getId());

            if (progress.getFirstLearningDate() == null) {
                newStartDate = LocalDate.now();
                progress.setFirstLearningDate(newStartDate);
            } else {
                newStartDate = progress.getFirstLearningDate();
            }
        } else {
            // Set new start date to day after last completion
            newStartDate = lastCompletedDateOpt.get().plusDays(1);
        }

        // Add new cycle start record
        progress.addCycleStart(progress.getCyclesStudied(), newStartDate);

        progressRepository.save(progress);
        log.debug("Set new reference date for cycle {} to {} for progress ID: {}",
                progress.getCyclesStudied(), newStartDate, progress.getId());

        // Use the provided function to create repetitions
        return repetitionCreator.apply(progress);
    }
}