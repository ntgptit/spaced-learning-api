package com.spacedlearning.service;

import com.spacedlearning.dto.stats.LearningInsightDTO;
import com.spacedlearning.dto.stats.UserLearningStatsDTO;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for learning statistics
 */
public interface LearningStatsService {

    /**
     * Get dashboard statistics for a user
     *
     * @param userId User ID
     * @return User learning statistics
     */
    UserLearningStatsDTO getDashboardStats(UUID userId);

    /**
     * Get learning insights for a user
     *
     * @param userId User ID
     * @return List of learning insights
     */
    List<LearningInsightDTO> getLearningInsights(UUID userId);
}
