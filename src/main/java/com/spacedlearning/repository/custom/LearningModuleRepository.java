package com.spacedlearning.repository.custom;

import java.util.List;

import com.spacedlearning.dto.learning.LearningModuleResponse;

public interface LearningModuleRepository {
    List<LearningModuleResponse> findModuleStudyProgress(int offset, int limit);
}
