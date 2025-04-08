package com.spacedlearning.service.impl.repetition;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.spacedlearning.entity.ModuleProgress;
import com.spacedlearning.entity.Repetition;
import com.spacedlearning.entity.enums.RepetitionOrder;
import com.spacedlearning.exception.SpacedLearningException;
import com.spacedlearning.repository.ModuleProgressRepository;
import com.spacedlearning.repository.RepetitionRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RepetitionValidator {
    private final RepetitionRepository repetitionRepository;
    private final ModuleProgressRepository progressRepository;

    public ModuleProgress findModuleProgress(UUID moduleProgressId) {
        return progressRepository.findById(moduleProgressId)
                .orElseThrow(() -> SpacedLearningException.resourceNotFound("ModuleProgress", moduleProgressId));
    }

    public Repetition findRepetition(UUID id) {
        return repetitionRepository.findById(id)
                .orElseThrow(() -> SpacedLearningException.resourceNotFound("Repetition", id));
    }

    public void validateRepetitionDoesNotExist(UUID moduleProgressId, RepetitionOrder repetitionOrder) {
        if (repetitionRepository.existsByModuleProgressIdAndRepetitionOrder(moduleProgressId, repetitionOrder)) {
            throw SpacedLearningException.resourceAlreadyExists("Repetition",
                    "module_progress_id and repetition_order", moduleProgressId + ", " + repetitionOrder);
        }
    }

    public void validateModuleProgressExists(UUID moduleProgressId) {
        if (!progressRepository.existsById(moduleProgressId)) {
            throw SpacedLearningException.resourceNotFound("ModuleProgress", moduleProgressId);
        }
    }
}