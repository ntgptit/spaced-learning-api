package com.spacedlearning.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spacedlearning.entity.User;
import com.spacedlearning.entity.UserStatistics;
import com.spacedlearning.entity.enums.UserStatus;
import com.spacedlearning.repository.ModuleRepository;
import com.spacedlearning.repository.UserRepository;
import com.spacedlearning.repository.UserStatisticsRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Scheduled job for updating user learning statistics
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class StatisticsUpdateJob {

	private final UserRepository userRepository;
	private final UserStatisticsRepository statisticsRepository;
	private final ModuleRepository moduleRepository;

	/**
	 * Update learning streak statistics Runs at 2:00 AM every day
	 */
	@Scheduled(cron = "0 0 2 * * *")
	@Transactional
	public void updateStreakStatistics() {
		log.info("Starting daily streak statistics update job");

		final List<User> activeUsers = userRepository.findByStatus(UserStatus.ACTIVE);
		log.debug("Found {} active users to update", activeUsers.size());

		final LocalDateTime now = LocalDateTime.now();
		final LocalDate today = now.toLocalDate();

		for (final User user : activeUsers) {
			try {
				log.debug("Processing streak for user ID: {}", user.getId());

				// Get or create user statistics
				final UserStatistics stats = findOrCreateUserStatistics(user);

				// Calculate streak
				final int streakDays = calculateStreakDays(user.getId(), today);
				final int streakWeeks = streakDays / 7;
				final int longestStreakDays = Math.max(stats.getLongestStreakDays(), streakDays);

				// Update database
				statisticsRepository.updateStreakStatistics(user.getId(), streakDays, streakWeeks, longestStreakDays,
						now);

				// Invalidate cache

				log.debug("Updated streak stats for user ID: {}, streakDays: {}", user.getId(), streakDays);
			} catch (final Exception e) {
				log.error("Error updating streak for user {}: {}", user.getId(), e.getMessage(), e);
			}
		}

		log.info("Completed daily streak statistics update job");
	}

	/**
	 * Update vocabulary statistics Runs at 3:00 AM every day
	 */
	@Scheduled(cron = "0 0 3 * * *")
	@Transactional
	public void updateVocabularyStatistics() {
		log.info("Starting daily vocabulary statistics update job");

		final List<User> activeUsers = userRepository.findByStatus(UserStatus.ACTIVE);
		log.debug("Found {} active users to update", activeUsers.size());

		final LocalDateTime now = LocalDateTime.now();

		for (final User user : activeUsers) {
			try {
				final UUID userId = user.getId();
				log.debug("Processing vocabulary stats for user ID: {}", userId);

				// Get or create user statistics
				findOrCreateUserStatistics(user);

				// Calculate vocabulary statistics
				final int totalWords = moduleRepository.getTotalWordCountForUser(userId);
				final int learnedWords = moduleRepository.getLearnedWordCountForUser(userId);
				final double vocabularyCompletionRate = totalWords > 0 ? (double) learnedWords / totalWords * 100 : 0;

				// Calculate weekly learning rate (simplified)
				final double weeklyNewWordsRate = calculateWeeklyNewWordsRate(userId);

				// Update database
				statisticsRepository.updateVocabularyStatistics(userId, totalWords, learnedWords,
						vocabularyCompletionRate, weeklyNewWordsRate, now);

				// Invalidate cache

				log.debug("Updated vocabulary stats for user ID: {}, totalWords: {}, learnedWords: {}", userId,
						totalWords, learnedWords);
			} catch (final Exception e) {
				log.error("Error updating vocabulary stats for user {}: {}", user.getId(), e.getMessage(), e);
			}
		}

		log.info("Completed daily vocabulary statistics update job");
	}

	/**
	 * Update module statistics Runs at 4:00 AM every day
	 */
	@Scheduled(cron = "0 0 4 * * *")
	@Transactional
	public void updateModuleStatistics() {
		log.info("Starting daily module statistics update job");

		final List<User> activeUsers = userRepository.findByStatus(UserStatus.ACTIVE);
		log.debug("Found {} active users to update", activeUsers.size());

		final LocalDateTime now = LocalDateTime.now();

		for (final User user : activeUsers) {
			try {
				final UUID userId = user.getId();
				log.debug("Processing module stats for user ID: {}", userId);

				// Get or create user statistics
				findOrCreateUserStatistics(user);

				// Calculate module statistics
				final int completedModules = moduleRepository.countCompletedModulesForUser(userId);
				final int inProgressModules = moduleRepository.countInProgressModulesForUser(userId);

				// Update database
				statisticsRepository.updateModuleStatistics(userId, completedModules, inProgressModules, now);

				log.debug("Updated module stats for user ID: {}, completedModules: {}, inProgressModules: {}", userId,
						completedModules, inProgressModules);
			} catch (final Exception e) {
				log.error("Error updating module stats for user {}: {}", user.getId(), e.getMessage(), e);
			}
		}

		log.info("Completed daily module statistics update job");
	}

	/**
	 * Find existing user statistics or create a new one
	 *
	 * @param user User
	 * @return User statistics
	 */
	private UserStatistics findOrCreateUserStatistics(User user) {
		return statisticsRepository.findByUserId(user.getId()).orElseGet(() -> {
			final UserStatistics newStats = new UserStatistics(user);
			return statisticsRepository.save(newStats);
		});
	}

	/**
	 * Calculate streak days for a user
	 *
	 * @param userId User ID
	 * @param today  Today's date
	 * @return Number of consecutive streak days
	 */
	private int calculateStreakDays(UUID userId, LocalDate today) {
		// Start with yesterday
		LocalDate checkDate = today.minusDays(1);
		int streakDays = 0;

		// Check if user completed any repetitions yesterday
		int completedYesterday = getCompletedRepetitionsOnDate(userId, checkDate);

		// If nothing completed yesterday, streak is broken
		if (completedYesterday == 0) {
			return 0;
		}

		// Count streak days going backwards
		while (completedYesterday > 0) {
			streakDays++;
			checkDate = checkDate.minusDays(1);
			completedYesterday = getCompletedRepetitionsOnDate(userId, checkDate);
		}

		return streakDays;
	}

	/**
	 * Get number of completed repetitions for a user on a specific date
	 *
	 * @param userId User ID
	 * @param date   Date to check
	 * @return Number of completed repetitions
	 */
	private int getCompletedRepetitionsOnDate(UUID userId, LocalDate date) {
		// This is a placeholder - actual implementation would query the database
		// For a specific date in the past
		return 0; // For illustration only
	}

	/**
	 * Calculate weekly new words learning rate
	 *
	 * @param userId User ID
	 * @return Weekly new words learning rate
	 */
	private double calculateWeeklyNewWordsRate(UUID userId) {
		// Get today's date
		final LocalDate today = LocalDate.now();

		today.minusWeeks(4);

		// For demonstration, return a placeholder value
		return 5.5; // Placeholder value
	}

}