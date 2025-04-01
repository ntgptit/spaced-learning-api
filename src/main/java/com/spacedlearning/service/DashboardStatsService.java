package com.spacedlearning.service;

import java.util.UUID;
import com.spacedlearning.dto.stats.UserLearningStatsDTO;

/**
 * Service for calculating dashboard statistics
 */
public interface DashboardStatsService {

    public UserLearningStatsDTO getDashboardStats(final UUID userId);

}
