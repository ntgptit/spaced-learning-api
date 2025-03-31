package com.spacedlearning.dto.stats;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user learning statistics
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLearningStatsDTO {
	// Module statistics
	private int totalModules;
	private int completedModules;
	private int inProgressModules;
	private double moduleCompletionRate;

	// Due sessions
	private int dueToday;
	private int dueThisWeek;
	private int dueThisMonth;

	// Due words
	private int wordsDueToday;
	private int wordsDueThisWeek;
	private int wordsDueThisMonth;

	// Completed sessions
	private int completedToday;
	private int completedThisWeek;
	private int completedThisMonth;

	// Completed words
	private int wordsCompletedToday;
	private int wordsCompletedThisWeek;
	private int wordsCompletedThisMonth;

	// Streak stats
	private int streakDays;
	private int streakWeeks;
	private int longestStreakDays;

	// Vocabulary stats
	private int totalWords;
	private int totalCompletedModules;
	private int totalInProgressModules;
	private int learnedWords;
	private int pendingWords;
	private BigDecimal vocabularyCompletionRate;
	private BigDecimal weeklyNewWordsRate;

	// Insights for UI
	private String[] learningInsights;

	// Metadata
	private LocalDateTime lastUpdated;
}