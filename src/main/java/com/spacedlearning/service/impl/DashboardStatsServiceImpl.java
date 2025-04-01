package com.spacedlearning.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spacedlearning.dto.stats.UserLearningStatsDTO;
import com.spacedlearning.entity.UserStatistics;
import com.spacedlearning.exception.SpacedLearningException;
import com.spacedlearning.repository.ModuleRepository;
import com.spacedlearning.repository.UserRepository;
import com.spacedlearning.repository.UserStatisticsRepository;
import com.spacedlearning.service.DashboardStatsService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service for calculating dashboard statistics
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DashboardStatsServiceImpl implements DashboardStatsService {

    // Constants
    private static final String RESOURCE_USER = "resource.user";
    private static final int SCALE_PRECISION = 2;
    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);

    // Dependencies
    private final UserRepository userRepository;
    private final UserStatisticsRepository statsRepository;
    private final ModuleRepository moduleRepository;
    private final MessageSource messageSource;

    @Override
	@Transactional(readOnly = true)
    public UserLearningStatsDTO getDashboardStats(final UUID userId) {
        log.debug("Calculating dashboard stats for user ID: {}", userId);
        Objects.requireNonNull(userId, "User ID must not be null");

        userRepository.findById(userId).orElseThrow(() -> SpacedLearningException
                .resourceNotFound(messageSource, RESOURCE_USER, userId));

        final Optional<UserStatistics> statsOpt = statsRepository.findByUserId(userId);
        final UserLearningStatsDTO.UserLearningStatsDTOBuilder builder =
                createBasicStatsBuilder(userId, statsOpt);
        return calculateDynamicStats(userId, builder);
    }

    private UserLearningStatsDTO.UserLearningStatsDTOBuilder createBasicStatsBuilder(
            final UUID userId, final Optional<UserStatistics> statsOpt) {
        return statsOpt.map(stats -> UserLearningStatsDTO.builder().lastUpdated(LocalDateTime.now())
                .streakDays(stats.getStreakDays()).streakWeeks(stats.getStreakWeeks())
                .longestStreakDays(stats.getLongestStreakDays())
                .totalCompletedModules(stats.getTotalCompletedModules())
                .totalInProgressModules(stats.getTotalInProgressModules())
                .totalWords(stats.getTotalWords()).learnedWords(stats.getLearnedWords())
                .vocabularyCompletionRate(stats.getVocabularyCompletionRate())
                .weeklyNewWordsRate(stats.getWeeklyNewWordsRate())
                .lastUpdated(stats.getLastStatisticsUpdate())).orElseGet(() -> {
                    final int totalWords = moduleRepository.getTotalWordCountForUser(userId);
                    final int learnedWords = moduleRepository.getLearnedWordCountForUser(userId);
                    final BigDecimal vocabularyCompletionRate =
                            calculateVocabularyCompletionRate(totalWords, learnedWords);

                    return UserLearningStatsDTO.builder().lastUpdated(LocalDateTime.now())
                            .streakDays(0).streakWeeks(0).longestStreakDays(0)
                            .totalWords(totalWords).learnedWords(learnedWords)
                            .vocabularyCompletionRate(vocabularyCompletionRate)
                            .weeklyNewWordsRate(BigDecimal.ZERO).totalCompletedModules(0)
                            .totalInProgressModules(0);
                });
    }

    private UserLearningStatsDTO calculateDynamicStats(final UUID userId,
            final UserLearningStatsDTO.UserLearningStatsDTOBuilder builder) {

		final int totalModules = moduleRepository.countTotalModules();
		final List<Object[]> moduleCycleStudiedStatsList = moduleRepository.getModuleCycleStudiedStats();
		final Map<String, Integer> cycleCounts = new HashMap<>();
		for (final Object[] row : moduleCycleStudiedStatsList) {
			final String cycleName = (String) row[0];
			final Integer count = ((Number) row[1]).intValue();
			cycleCounts.put(cycleName, count);
		}

		// Tính tổng số module đã học
		final int totalStudied = cycleCounts.values().stream().mapToInt(Integer::intValue).sum();

		// Tính số module chưa học và thêm vào map
		final int notStudied = totalModules - totalStudied;
		cycleCounts.put("NOT_STUDIED", notStudied);

		final int dueToday = moduleRepository.countDueTodayForUser(userId);
		final int dueThisWeek = moduleRepository.countDueThisWeekForUser(userId);
		final int dueThisMonth = moduleRepository.countDueThisMonthForUser(userId);

		final int wordsDueToday = moduleRepository.countWordsDueTodayForUser(userId);
		final int wordsDueThisWeek = moduleRepository.countWordsDueThisWeekForUser(userId);
		final int wordsDueThisMonth = moduleRepository.countWordsDueThisMonthForUser(userId);

		final int completedToday = moduleRepository.countCompletedTodayForUser(userId);
        final int completedThisWeek = calculateCompletedThisWeek(userId);
        final int completedThisMonth = calculateCompletedThisMonth(userId);

		final int wordsCompletedToday = moduleRepository.countWordsCompletedTodayForUser(userId);
        final int wordsCompletedThisWeek = calculateWordsCompletedThisWeek(userId);
        final int wordsCompletedThisMonth = calculateWordsCompletedThisMonth(userId);

		final int totalWords = moduleRepository.countTotalVocabularyWords();
        final int learnedWords = moduleRepository.countLearnedVocabularyWords();
        final int pendingWords = Math.max(0, totalWords - learnedWords);

		return builder.totalModules(totalModules).cycleStats(cycleCounts).dueToday(dueToday).dueThisWeek(dueThisWeek)
				.dueThisMonth(dueThisMonth).wordsDueToday(wordsDueToday).wordsDueThisWeek(wordsDueThisWeek)
				.wordsDueThisMonth(wordsDueThisMonth).completedToday(completedToday)
				.completedThisWeek(completedThisWeek).completedThisMonth(completedThisMonth)
                .wordsCompletedToday(wordsCompletedToday)
                .wordsCompletedThisWeek(wordsCompletedThisWeek)
				.wordsCompletedThisMonth(wordsCompletedThisMonth).totalWords(totalWords).learnedWords(learnedWords)
				.pendingWords(pendingWords)
				.vocabularyCompletionRate(calculateVocabularyCompletionRate(totalWords, learnedWords))
				.weeklyNewWordsRate(ONE_HUNDRED)
                .build();
    }

    private BigDecimal calculateVocabularyCompletionRate(final int totalWords,
            final int learnedWords) {
        if (totalWords <= 0) {
            return BigDecimal.ZERO;
        }
        final BigDecimal learnedWordsBD = BigDecimal.valueOf(learnedWords);
        final BigDecimal totalWordsBD = BigDecimal.valueOf(totalWords);
        return learnedWordsBD.divide(totalWordsBD, SCALE_PRECISION, RoundingMode.HALF_UP)
                .multiply(ONE_HUNDRED);
    }

    private int calculateCompletedThisWeek(final UUID userId) {
        final LocalDate today = LocalDate.now();
		final int completedToday = moduleRepository.countCompletedTodayForUser(userId);
        final int daysPastInWeek = today.getDayOfWeek().getValue();
        return Math.min(completedToday * daysPastInWeek, completedToday * 5);
    }

    private int calculateCompletedThisMonth(final UUID userId) {
        final LocalDate today = LocalDate.now();
		final int completedToday = moduleRepository.countCompletedTodayForUser(userId);
        final int dayOfMonth = today.getDayOfMonth();
        return Math.min(completedToday * dayOfMonth, completedToday * 20);
    }

    private int calculateWordsCompletedThisWeek(final UUID userId) {
        final LocalDate today = LocalDate.now();
		final int wordsCompletedToday = moduleRepository.countWordsCompletedTodayForUser(userId);
        final int daysPastInWeek = today.getDayOfWeek().getValue();
        return Math.min(wordsCompletedToday * daysPastInWeek, wordsCompletedToday * 5);
    }

    private int calculateWordsCompletedThisMonth(final UUID userId) {
        final LocalDate today = LocalDate.now();
		final int wordsCompletedToday = moduleRepository.countWordsCompletedTodayForUser(userId);
        final int dayOfMonth = today.getDayOfMonth();
        return Math.min(wordsCompletedToday * dayOfMonth, wordsCompletedToday * 20);
    }
}
