package com.spacedlearning.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.spacedlearning.entity.UserStatistics;

/**
 * Repository for UserStatistics entity
 */
@Repository
public interface UserStatisticsRepository extends JpaRepository<UserStatistics, UUID> {

    /**
     * Find statistics by user ID
     *
     * @param userId User ID
     * @return Optional containing user statistics if found
     */
    Optional<UserStatistics> findByUserId(UUID userId);
}