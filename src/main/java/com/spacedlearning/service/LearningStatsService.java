package com.spacedlearning.service;

import java.util.List;
import java.util.UUID;

import com.spacedlearning.dto.stats.LearningInsightDTO;
import com.spacedlearning.dto.stats.UserLearningStatsDTO;

/**
 * Service interface for learning statistics
 */
public interface LearningStatsService {

	/**
	 * Get dashboard statistics for a user
	 * 
	 * @param userId       User ID
	 * @param refreshCache Whether to refresh cache
	 * @return User learning statistics
	 */
	UserLearningStatsDTO getDashboardStats(UUID userId, boolean refreshCache);

	/**
	 * Get learning insights for a user
	 * 
	 * @param userId User ID
	 * @return List of learning insights
	 */
	List<LearningInsightDTO> getLearningInsights(UUID userId);
}