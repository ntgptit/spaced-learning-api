package com.spacedlearning.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spacedlearning.dto.stats.LearningInsightDTO;
import com.spacedlearning.dto.stats.UserLearningStatsDTO;
import com.spacedlearning.entity.UserStatistics;
import com.spacedlearning.entity.enums.InsightType;
import com.spacedlearning.exception.SpacedLearningException;
import com.spacedlearning.repository.ModuleRepository;
import com.spacedlearning.repository.RepetitionRepository;
import com.spacedlearning.repository.UserRepository;
import com.spacedlearning.repository.UserStatisticsRepository;
import com.spacedlearning.service.LearningStatsService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service for retrieving and calculating learning statistics
 */
@Service
@Slf4j
@RequiredArgsConstructor
@CacheConfig(cacheNames = "userLearningStats")
public class LearningStatsServiceImpl implements LearningStatsService {

	private final UserRepository userRepository;
	private final UserStatisticsRepository statsRepository;
	private final RepetitionRepository repetitionRepository;
	private final ModuleRepository moduleRepository;

	/**
	 * Get dashboard statistics for a user
	 *
	 * @param userId       User ID
	 * @param refreshCache Whether to refresh cache
	 * @return User learning statistics
	 */
	@Override
	@Transactional(readOnly = true)
	@Cacheable(key = "#userId + '_dashboard'", condition = "#refreshCache == false")
	public UserLearningStatsDTO getDashboardStats(UUID userId, boolean refreshCache) {
		log.debug("Calculating dashboard stats for user ID: {}", userId);

		userRepository.findById(userId)
				.orElseThrow(() -> SpacedLearningException.resourceNotFound("User", userId));

		// Get pre-calculated statistics
		final Optional<UserStatistics> statsOpt = statsRepository.findByUserId(userId);

		// Create stats builder with defaults
		final UserLearningStatsDTO.UserLearningStatsDTOBuilder builder = UserLearningStatsDTO.builder()
				.lastUpdated(LocalDateTime.now());

		// Set pre-calculated stats if available
		if (statsOpt.isPresent()) {
			final UserStatistics stats = statsOpt.get();
			builder.streakDays(stats.getStreakDays()).streakWeeks(stats.getStreakWeeks())
					.longestStreakDays(stats.getLongestStreakDays())
					.totalCompletedModules(stats.getTotalCompletedModules())
					.totalInProgressModules(stats.getTotalInProgressModules()).totalWords(stats.getTotalWords())
					.learnedWords(stats.getLearnedWords()).vocabularyCompletionRate(stats.getVocabularyCompletionRate())
					.weeklyNewWordsRate(stats.getWeeklyNewWordsRate()).lastUpdated(stats.getLastStatisticsUpdate());
		} else {
			// Use database queries for basic stats if pre-calculated aren't available
			final int totalWords = moduleRepository.getTotalWordCountForUser(userId);
			final int learnedWords = moduleRepository.getLearnedWordCountForUser(userId);

			// Calculate vocabulary completion rate safely
			BigDecimal vocabularyCompletionRate = BigDecimal.ZERO;
			if (totalWords > 0) {
				final BigDecimal learnedWordsBD = BigDecimal.valueOf(learnedWords);
				final BigDecimal totalWordsBD = BigDecimal.valueOf(totalWords);
				vocabularyCompletionRate = learnedWordsBD.divide(totalWordsBD, 2, RoundingMode.HALF_UP)
						.multiply(BigDecimal.valueOf(100));
			}

			builder.streakDays(0).streakWeeks(0).longestStreakDays(0).totalWords(totalWords).learnedWords(learnedWords)
					.vocabularyCompletionRate(vocabularyCompletionRate).weeklyNewWordsRate(BigDecimal.ZERO)
					.totalCompletedModules(0).totalInProgressModules(0);
		}

		// Always calculate these statistics as they change frequently

		// Module counts
		final int totalModules = moduleRepository.countTotalModulesForUser(userId);
		final int completedModules = statsOpt.isPresent() ? statsOpt.get().getTotalCompletedModules()
				: moduleRepository.countCompletedModulesForUser(userId);
		final int inProgressModules = statsOpt.isPresent() ? statsOpt.get().getTotalInProgressModules()
				: moduleRepository.countInProgressModulesForUser(userId);

		// Calculate module completion rate safely
		final double moduleCompletionRate = totalModules > 0 ? (double) completedModules / totalModules * 100 : 0;

		// Due counts
		final int dueToday = repetitionRepository.countDueTodayForUser(userId);
		final int dueThisWeek = repetitionRepository.countDueThisWeekForUser(userId);
		final int dueThisMonth = repetitionRepository.countDueThisMonthForUser(userId);

		// Word counts for due items
		final int wordsDueToday = repetitionRepository.countWordsDueTodayForUser(userId);
		final int wordsDueThisWeek = repetitionRepository.countWordsDueThisWeekForUser(userId);
		final int wordsDueThisMonth = repetitionRepository.countWordsDueThisMonthForUser(userId);

		// Completed counts
		final int completedToday = repetitionRepository.countCompletedTodayForUser(userId);
		final int completedThisWeek = calculateCompletedThisWeek(userId);
		final int completedThisMonth = calculateCompletedThisMonth(userId);

		// Word counts for completed items
		final int wordsCompletedToday = repetitionRepository.countWordsCompletedTodayForUser(userId);
		final int wordsCompletedThisWeek = calculateWordsCompletedThisWeek(userId);
		final int wordsCompletedThisMonth = calculateWordsCompletedThisMonth(userId);

		// Calculate totalWords and learnedWords values for pending calculation
		final int totalWords = statsOpt.isPresent() ? statsOpt.get().getTotalWords()
				: moduleRepository.getTotalWordCountForUser(userId);

		final int learnedWords = statsOpt.isPresent() ? statsOpt.get().getLearnedWords()
				: moduleRepository.getLearnedWordCountForUser(userId);

		// Pending calculations - ensure we don't get negative values
		final int pendingWords = Math.max(0, totalWords - learnedWords);

		// Get streak days and weekly new words rate for insights
		final int streakDays = statsOpt.isPresent() ? statsOpt.get().getStreakDays() : 0;
		final BigDecimal weeklyNewWordsRate = statsOpt.isPresent() ? statsOpt.get().getWeeklyNewWordsRate()
				: BigDecimal.ZERO;

		// Generate insights based on statistics
		final String[] learningInsights = generateLearningInsights(weeklyNewWordsRate.doubleValue(), streakDays,
				pendingWords,
				dueToday);

		// Build the complete DTO
		return builder.totalModules(totalModules).completedModules(completedModules)
				.inProgressModules(inProgressModules).moduleCompletionRate(moduleCompletionRate).dueToday(dueToday)
				.dueThisWeek(dueThisWeek).dueThisMonth(dueThisMonth).wordsDueToday(wordsDueToday)
				.wordsDueThisWeek(wordsDueThisWeek).wordsDueThisMonth(wordsDueThisMonth).completedToday(completedToday)
				.completedThisWeek(completedThisWeek).completedThisMonth(completedThisMonth)
				.wordsCompletedToday(wordsCompletedToday).wordsCompletedThisWeek(wordsCompletedThisWeek)
				.wordsCompletedThisMonth(wordsCompletedThisMonth).pendingWords(pendingWords)
				.learningInsights(learningInsights).build();
	}

	/**
	 * Generate learning insights for the dashboard
	 *
	 * @param vocabularyRate Vocabulary learning rate
	 * @param streakDays     Streak days
	 * @param pendingWords   Pending words
	 * @param dueToday       Due today
	 * @return Array of insight messages
	 */
	private String[] generateLearningInsights(double vocabularyRate, int streakDays, int pendingWords, int dueToday) {
		final List<String> insights = new ArrayList<>();

		// Vocabulary rate insight
		insights.add(String.format("You learn %.1f%% new vocabulary each week", vocabularyRate));

		// Streak insight
		if (streakDays > 0) {
			insights.add(String.format("Your current streak is %d days - keep going!", streakDays));
		} else {
			insights.add("Start a streak by completing today's sessions");
		}

		// Pending words
		if (pendingWords > 0) {
			insights.add(String.format("You have %d words pending to learn", pendingWords));
		} else {
			insights.add("Great job! You've learned all available words");
		}

		// Due today
		if (dueToday > 0) {
			insights.add(String.format("Complete today's %d sessions to maintain your streak", dueToday));
		} else {
			insights.add("No sessions due today - take a well-deserved break!");
		}

		return insights.toArray(new String[0]);
	}

	/**
	 * Calculate number of repetitions completed this week
	 *
	 * @param userId User ID
	 * @return Number of completed repetitions this week
	 */
	private int calculateCompletedThisWeek(UUID userId) {
		// Implementation would query the repository with date range for current week
		// This is a placeholder implementation
		final LocalDate today = LocalDate.now();
		today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

		// Actual implementation would query the database
		return 0; // Placeholder
	}

	/**
	 * Calculate number of repetitions completed this month
	 *
	 * @param userId User ID
	 * @return Number of completed repetitions this month
	 */
	private int calculateCompletedThisMonth(UUID userId) {
		// Implementation would query the repository with date range for current month
		// This is a placeholder implementation
		final LocalDate today = LocalDate.now();
		today.withDayOfMonth(1);

		// Actual implementation would query the database
		return 0; // Placeholder
	}

	/**
	 * Calculate number of words completed this week
	 *
	 * @param userId User ID
	 * @return Number of words completed this week
	 */
	private int calculateWordsCompletedThisWeek(UUID userId) {
		// Implementation would query the repository with date range for current week
		// This is a placeholder implementation
		return 0; // Placeholder
	}

	/**
	 * Calculate number of words completed this month
	 *
	 * @param userId User ID
	 * @return Number of words completed this month
	 */
	private int calculateWordsCompletedThisMonth(UUID userId) {
		// Implementation would query the repository with date range for current month
		// This is a placeholder implementation
		return 0; // Placeholder
	}

	/**
	 * Get learning insights for a user
	 *
	 * @param userId User ID
	 * @return List of learning insights
	 */
	@Override
	@Transactional(readOnly = true)
	public List<LearningInsightDTO> getLearningInsights(UUID userId) {
		log.debug("Generating learning insights for user ID: {}", userId);

		// Get user stats
		final UserLearningStatsDTO stats = getDashboardStats(userId, false);

		final List<LearningInsightDTO> insights = new ArrayList<>();

		// Vocabulary rate insight
		insights.add(LearningInsightDTO.builder().type(InsightType.VOCABULARY_RATE)
				.message(String.format("You learn %.1f%% new vocabulary each week", stats.getWeeklyNewWordsRate()))
				.icon("trending_up").color("blue").dataPoint(stats.getWeeklyNewWordsRate().doubleValue()).priority(1)
				.build());

		// Streak insight
		insights.add(LearningInsightDTO.builder().type(InsightType.STREAK)
				.message(String.format("Your current streak is %d days - keep going!", stats.getStreakDays()))
				.icon("local_fire_department").color("orange").dataPoint(stats.getStreakDays()).priority(2).build());

		// Pending words
		insights.add(LearningInsightDTO.builder().type(InsightType.PENDING_WORDS)
				.message(String.format("You have %d words pending to learn", stats.getPendingWords())).icon("menu_book")
				.color("teal").dataPoint(stats.getPendingWords()).priority(3).build());

		// Due today
		insights.add(LearningInsightDTO.builder().type(InsightType.DUE_TODAY)
				.message(String.format("Complete today's %d sessions to maintain your streak", stats.getDueToday()))
				.icon("today").color("red").dataPoint(stats.getDueToday()).priority(4).build());

		return insights;
	}
}