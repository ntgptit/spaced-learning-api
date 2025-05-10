package com.spacedlearning.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
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

@Service
@Slf4j
@RequiredArgsConstructor
public class LearningStatsServiceImpl implements LearningStatsService {

    // --- Icon constants ---
    private static final String ICON_VOCABULARY = "trending_up";
    private static final String ICON_STREAK = "local_fire_department";
    private static final String ICON_PENDING = "menu_book";
    private static final String ICON_DUE = "today";

    // --- Color constants ---
    private static final String COLOR_BLUE = "blue";
    private static final String COLOR_ORANGE = "orange";
    private static final String COLOR_TEAL = "teal";
    private static final String COLOR_RED = "red";

    // --- Message options ---
    private static final List<String> VOCABULARY_MESSAGES = List.of(
            "You’re expanding your vocabulary by %.1f%% each week – impressive!",
            "Great progress! You've learned %.1f%% more words this week.",
            "You're picking up new words at %.1f%% per week. Keep going!",
            "%.1f%% vocabulary gain weekly – you’re on fire!");

    private static final List<String> STREAK_MESSAGES = List.of(
            "You're on a %d-day streak – don't break the chain!",
            "Amazing! %d days of continuous learning.",
            "Streak at %d days – your dedication is paying off!",
            "🔥 %d-day streak – let’s make it longer!");

    private static final List<String> PENDING_WORDS_MESSAGES = List.of(
            "You still have %d words to conquer – stay sharp!",
            "Keep pushing! %d words are waiting to be learned.",
            "%d pending words – time to close the gap!",
            "You're almost there – %d words left to master.");

    private static final List<String> DUE_TODAY_MESSAGES = List.of(
            "You have %d sessions due today – keep your streak alive!",
            "Don’t forget: %d sessions are waiting for you today.",
            "%d sessions due today – time to stay consistent.",
            "Learning time! %d sessions on your plate today.");

    // --- Fallback messages ---
    private static final String INSIGHT_START_STREAK = "Start a streak by completing today's sessions";
    private static final String INSIGHT_ALL_WORDS_LEARNED = "Great job! You've learned all available words";
    private static final String INSIGHT_NO_SESSIONS_DUE = "No sessions due today – take a well-deserved break!";

    // --- Dependencies ---
    private final DashboardStatsService dashboardStatsService;

    private LearningInsightDTO createDueTodayInsight(int dueToday) {
        final var message = dueToday > 0
                ? getRandomMessage(DUE_TODAY_MESSAGES, dueToday)
                : INSIGHT_NO_SESSIONS_DUE;
        return LearningInsightDTO.builder()
                .type(InsightType.DUE_TODAY)
                .message(message)
                .icon(ICON_DUE)
                .color(COLOR_RED)
                .dataPoint(dueToday)
                .priority(4)
                .build();
    }

    private LearningInsightDTO createPendingWordsInsight(int pendingWords) {
        final var message = pendingWords > 0
                ? getRandomMessage(PENDING_WORDS_MESSAGES, pendingWords)
                : INSIGHT_ALL_WORDS_LEARNED;
        return LearningInsightDTO.builder()
                .type(InsightType.PENDING_WORDS)
                .message(message)
                .icon(ICON_PENDING)
                .color(COLOR_TEAL)
                .dataPoint(pendingWords)
                .priority(3)
                .build();
    }

    private LearningInsightDTO createStreakInsight(int streakDays) {
        final var message = streakDays > 0
                ? getRandomMessage(STREAK_MESSAGES, streakDays)
                : INSIGHT_START_STREAK;
        return LearningInsightDTO.builder()
                .type(InsightType.STREAK)
                .message(message)
                .icon(ICON_STREAK)
                .color(COLOR_ORANGE)
                .dataPoint(streakDays)
                .priority(2)
                .build();
    }

    private LearningInsightDTO createVocabularyRateInsight(double weeklyRate) {
        final var message = getRandomMessage(VOCABULARY_MESSAGES, weeklyRate);
        return LearningInsightDTO.builder()
                .type(InsightType.VOCABULARY_RATE)
                .message(message)
                .icon(ICON_VOCABULARY)
                .color(COLOR_BLUE)
                .dataPoint(weeklyRate)
                .priority(1)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public UserLearningStatsDTO getDashboardStats(UUID userId) {
        return this.dashboardStatsService.getDashboardStats(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LearningInsightDTO> getLearningInsights(UUID userId) {
        log.debug("Generating learning insights for user ID: {}", userId);
        Objects.requireNonNull(userId, "User ID must not be null");

        final var stats = this.dashboardStatsService.getDashboardStats(userId);
        final List<LearningInsightDTO> insights = new ArrayList<>();

        insights.add(createVocabularyRateInsight(stats.getWeeklyNewWordsRate().doubleValue()));
        insights.add(createStreakInsight(stats.getStreakDays()));
        insights.add(createPendingWordsInsight(stats.getPendingWords()));
        insights.add(createDueTodayInsight(stats.getDueToday()));

        return insights;
    }

    /**
     * Helper: Select a random formatted message from a list.
     */
    private String getRandomMessage(List<String> templates, Number value) {
        final var index = new Random().nextInt(templates.size());
        return String.format(templates.get(index), value);
    }
}
