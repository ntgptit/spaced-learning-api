package com.spacedlearning.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.spacedlearning.dto.stats.LearningInsightDTO;
import com.spacedlearning.dto.stats.UserLearningStatsDTO;
import com.spacedlearning.entity.enums.InsightType;
import com.spacedlearning.service.DashboardStatsService;
import com.spacedlearning.service.LearningStatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service for retrieving and calculating learning statistics
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class LearningStatsServiceImpl implements LearningStatsService {

	// Insight message formats
	private static final String INSIGHT_VOCABULARY_FORMAT =
			"You learn %.1f%% new vocabulary each week";
	private static final String INSIGHT_STREAK_FORMAT =
			"Your current streak is %d days - keep going!";
	private static final String INSIGHT_START_STREAK =
			"Start a streak by completing today's sessions";
	private static final String INSIGHT_PENDING_WORDS_FORMAT = "You have %d words pending to learn";
	private static final String INSIGHT_ALL_WORDS_LEARNED =
			"Great job! You've learned all available words";
	private static final String INSIGHT_DUE_TODAY_FORMAT =
			"Complete today's %d sessions to maintain your streak";
	private static final String INSIGHT_NO_SESSIONS_DUE =
			"No sessions due today - take a well-deserved break!";

	// Dependency
	private final DashboardStatsService dashboardStatsService;

	@Override
	public UserLearningStatsDTO getDashboardStats(final UUID userId) {
		return dashboardStatsService.getDashboardStats(userId);
	}

	@Override
	@Transactional(readOnly = true)
	public List<LearningInsightDTO> getLearningInsights(final UUID userId) {
		log.debug("Generating learning insights for user ID: {}", userId);
		Objects.requireNonNull(userId, "User ID must not be null");

		UserLearningStatsDTO stats = dashboardStatsService.getDashboardStats(userId);
		List<LearningInsightDTO> insights = new ArrayList<>();

		insights.add(createVocabularyRateInsight(stats.getWeeklyNewWordsRate().doubleValue()));
		insights.add(createStreakInsight(stats.getStreakDays()));
		insights.add(createPendingWordsInsight(stats.getPendingWords()));
		insights.add(createDueTodayInsight(stats.getDueToday()));

		return insights;
	}

	private LearningInsightDTO createVocabularyRateInsight(final double weeklyRate) {
		return LearningInsightDTO.builder().type(InsightType.VOCABULARY_RATE)
				.message(String.format(INSIGHT_VOCABULARY_FORMAT, weeklyRate)).icon("trending_up")
				.color("blue").dataPoint(weeklyRate).priority(1).build();
	}

	private LearningInsightDTO createStreakInsight(final int streakDays) {
		String message = streakDays > 0 ? String.format(INSIGHT_STREAK_FORMAT, streakDays)
				: INSIGHT_START_STREAK;
		return LearningInsightDTO.builder().type(InsightType.STREAK).message(message)
				.icon("local_fire_department").color("orange").dataPoint(streakDays).priority(2)
				.build();
	}

	private LearningInsightDTO createPendingWordsInsight(final int pendingWords) {
		String message =
				pendingWords > 0 ? String.format(INSIGHT_PENDING_WORDS_FORMAT, pendingWords)
						: INSIGHT_ALL_WORDS_LEARNED;
		return LearningInsightDTO.builder().type(InsightType.PENDING_WORDS).message(message)
				.icon("menu_book").color("teal").dataPoint(pendingWords).priority(3).build();
	}

	private LearningInsightDTO createDueTodayInsight(final int dueToday) {
		String message = dueToday > 0 ? String.format(INSIGHT_DUE_TODAY_FORMAT, dueToday)
				: INSIGHT_NO_SESSIONS_DUE;
		return LearningInsightDTO.builder().type(InsightType.DUE_TODAY).message(message)
				.icon("today").color("red").dataPoint(dueToday).priority(4).build();
	}
}
