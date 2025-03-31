package com.spacedlearning.repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

	/**
	 * Update streak statistics
	 * 
	 * @param userId            User ID
	 * @param streakDays        Number of consecutive streak days
	 * @param streakWeeks       Number of consecutive streak weeks
	 * @param longestStreakDays Longest streak days
	 * @param updateTime        Update timestamp
	 */
	@Modifying
	@Query("""
			    UPDATE UserStatistics s
			    SET s.streakDays = :streakDays,
			        s.streakWeeks = :streakWeeks,
			        s.longestStreakDays = CASE WHEN :longestStreakDays > s.longestStreakDays
			                                 THEN :longestStreakDays
			                                 ELSE s.longestStreakDays
			                              END,
			        s.lastStatisticsUpdate = :updateTime
			    WHERE s.user.id = :userId
			""")
	void updateStreakStatistics(@Param("userId") UUID userId, @Param("streakDays") int streakDays,
			@Param("streakWeeks") int streakWeeks, @Param("longestStreakDays") int longestStreakDays,
			@Param("updateTime") LocalDateTime updateTime);

	/**
	 * Update vocabulary statistics
	 * 
	 * @param userId                   User ID
	 * @param totalWords               Total number of words
	 * @param learnedWords             Number of learned words
	 * @param vocabularyCompletionRate Vocabulary completion rate
	 * @param weeklyNewWordsRate       Weekly new words learning rate
	 * @param updateTime               Update timestamp
	 */
	@Modifying
	@Query("""
			    UPDATE UserStatistics s
			    SET s.totalWords = :totalWords,
			        s.learnedWords = :learnedWords,
			        s.vocabularyCompletionRate = :vocabularyCompletionRate,
			        s.weeklyNewWordsRate = :weeklyNewWordsRate,
			        s.lastStatisticsUpdate = :updateTime
			    WHERE s.user.id = :userId
			""")
	void updateVocabularyStatistics(@Param("userId") UUID userId, @Param("totalWords") int totalWords,
			@Param("learnedWords") int learnedWords, @Param("vocabularyCompletionRate") double vocabularyCompletionRate,
			@Param("weeklyNewWordsRate") double weeklyNewWordsRate, @Param("updateTime") LocalDateTime updateTime);

	/**
	 * Update module statistics
	 * 
	 * @param userId                 User ID
	 * @param totalCompletedModules  Total completed modules
	 * @param totalInProgressModules Total modules in progress
	 * @param updateTime             Update timestamp
	 */
	@Modifying
	@Query("""
			    UPDATE UserStatistics s
			    SET s.totalCompletedModules = :totalCompletedModules,
			        s.totalInProgressModules = :totalInProgressModules,
			        s.lastStatisticsUpdate = :updateTime
			    WHERE s.user.id = :userId
			""")
	void updateModuleStatistics(@Param("userId") UUID userId, @Param("totalCompletedModules") int totalCompletedModules,
			@Param("totalInProgressModules") int totalInProgressModules, @Param("updateTime") LocalDateTime updateTime);
}