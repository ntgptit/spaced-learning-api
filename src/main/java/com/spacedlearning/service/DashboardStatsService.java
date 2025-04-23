package com.spacedlearning.service;

import com.spacedlearning.dto.stats.UserLearningStatsDTO;

import java.util.UUID;

/**
 * Service for calculating dashboard statistics
 */
public interface DashboardStatsService {

    UserLearningStatsDTO getDashboardStats(final UUID userId);

}
