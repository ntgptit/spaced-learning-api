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

@Service
@Slf4j
@RequiredArgsConstructor
public class DashboardStatsServiceImpl implements DashboardStatsService {

    private static final String RESOURCE_USER = "resource.user";
    private static final int SCALE_PRECISION = 2;
    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);

    private final UserRepository userRepository;
    private final UserStatisticsRepository statsRepository;
    private final ModuleRepository moduleRepository;
    private final MessageSource messageSource;

    @Override
    @Transactional(readOnly = true)
    public UserLearningStatsDTO getDashboardStats(UUID userId) {
        log.debug("Calculating dashboard stats for user ID: {}", userId);
        validateUserId(userId);
        verifyUserExists(userId);

        final Optional<UserStatistics> statsOpt = statsRepository.findByUserId(userId);
        final UserLearningStatsDTO.UserLearningStatsDTOBuilder builder = createBasicStatsBuilder(userId, statsOpt);
        return calculateDynamicStats(userId, builder);
    }

    private void validateUserId(UUID userId) {
        Objects.requireNonNull(userId, "User ID must not be null");
    }

    private void verifyUserExists(UUID userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> SpacedLearningException.resourceNotFound(messageSource, RESOURCE_USER, userId));
    }

    private UserLearningStatsDTO.UserLearningStatsDTOBuilder createBasicStatsBuilder(
            UUID userId, Optional<UserStatistics> statsOpt) {
        return statsOpt.map(this::buildStatsFromExisting)
                .orElseGet(() -> buildStatsFromScratch(userId));
    }

    private UserLearningStatsDTO.UserLearningStatsDTOBuilder buildStatsFromExisting(UserStatistics stats) {
        return UserLearningStatsDTO.builder()
                .lastUpdated(LocalDateTime.now())
                .streakDays(stats.getStreakDays())
                .streakWeeks(stats.getStreakWeeks())
                .longestStreakDays(stats.getLongestStreakDays())
                .totalCompletedModules(stats.getTotalCompletedModules())
                .totalInProgressModules(stats.getTotalInProgressModules())
                .totalWords(stats.getTotalWords())
                .learnedWords(stats.getLearnedWords())
                .vocabularyCompletionRate(stats.getVocabularyCompletionRate())
                .weeklyNewWordsRate(stats.getWeeklyNewWordsRate())
                .lastUpdated(stats.getLastStatisticsUpdate());
    }

    private UserLearningStatsDTO.UserLearningStatsDTOBuilder buildStatsFromScratch(UUID userId) {
        final int totalWords = moduleRepository.getTotalWordCountForUser(userId);
        final int learnedWords = moduleRepository.getLearnedWordCountForUser(userId);
        final BigDecimal vocabularyCompletionRate = calculateVocabularyCompletionRate(totalWords, learnedWords);

        return UserLearningStatsDTO.builder()
                .lastUpdated(LocalDateTime.now())
                .streakDays(0)
                .streakWeeks(0)
                .longestStreakDays(0)
                .totalWords(totalWords)
                .learnedWords(learnedWords)
                .vocabularyCompletionRate(vocabularyCompletionRate)
                .weeklyNewWordsRate(BigDecimal.ZERO)
                .totalCompletedModules(0)
                .totalInProgressModules(0);
    }

    private UserLearningStatsDTO calculateDynamicStats(UUID userId,
            UserLearningStatsDTO.UserLearningStatsDTOBuilder builder) {
        final int totalModules = moduleRepository.countTotalModules();
        final Map<String, Integer> cycleStats = calculateCycleStats();
        final StatsPeriod dueStats = calculateDueStats(userId);
        final StatsPeriod wordsDueStats = calculateWordsDueStats(userId);
        final StatsPeriod completedStats = calculateCompletedStats(userId);
        final StatsPeriod wordsCompletedStats = calculateWordsCompletedStats(userId);
        final VocabularyStats vocabularyStats = calculateVocabularyStats();

        return builder.totalModules(totalModules)
                .cycleStats(cycleStats)
                .dueToday(dueStats.today)
                .dueThisWeek(dueStats.week)
                .dueThisMonth(dueStats.month)
                .wordsDueToday(wordsDueStats.today)
                .wordsDueThisWeek(wordsDueStats.week)
                .wordsDueThisMonth(wordsDueStats.month)
                .completedToday(completedStats.today)
                .completedThisWeek(completedStats.week)
                .completedThisMonth(completedStats.month)
                .wordsCompletedToday(wordsCompletedStats.today)
                .wordsCompletedThisWeek(wordsCompletedStats.week)
                .wordsCompletedThisMonth(wordsCompletedStats.month)
                .totalWords(vocabularyStats.totalWords)
                .learnedWords(vocabularyStats.learnedWords)
                .pendingWords(vocabularyStats.pendingWords)
                .vocabularyCompletionRate(vocabularyStats.completionRate)
                .weeklyNewWordsRate(ONE_HUNDRED)
                .build();
    }

    private Map<String, Integer> calculateCycleStats() {
        final List<Object[]> moduleCycleStats = moduleRepository.getModuleCycleStudiedStats();
        final Map<String, Integer> cycleCounts = new HashMap<>();
        int totalStudied = 0;

        for (final Object[] row : moduleCycleStats) {
            final String cycleName = (String) row[0];
            final Integer count = ((Number) row[1]).intValue();
            cycleCounts.put(cycleName, count);
            totalStudied += count;
        }

        final int totalModules = moduleRepository.countTotalModules();
        cycleCounts.put("NOT_STUDIED", totalModules - totalStudied);
        return cycleCounts;
    }

    private StatsPeriod calculateDueStats(UUID userId) {
        return new StatsPeriod(
                moduleRepository.countDueTodayForUser(userId),
                moduleRepository.countDueThisWeekForUser(userId),
                moduleRepository.countDueThisMonthForUser(userId));
    }

    private StatsPeriod calculateWordsDueStats(UUID userId) {
        return new StatsPeriod(
                moduleRepository.countWordsDueTodayForUser(userId),
                moduleRepository.countWordsDueThisWeekForUser(userId),
                moduleRepository.countWordsDueThisMonthForUser(userId));
    }

    private StatsPeriod calculateCompletedStats(UUID userId) {
        return new StatsPeriod(
                moduleRepository.countCompletedTodayForUser(userId),
                calculateCompletedThisWeek(userId),
                calculateCompletedThisMonth(userId));
    }

    private StatsPeriod calculateWordsCompletedStats(UUID userId) {
        return new StatsPeriod(
                moduleRepository.countWordsCompletedTodayForUser(userId),
                calculateWordsCompletedThisWeek(userId),
                calculateWordsCompletedThisMonth(userId));
    }

    private VocabularyStats calculateVocabularyStats() {
        final int totalWords = moduleRepository.countTotalVocabularyWords();
        final int learnedWords = moduleRepository.countLearnedVocabularyWords();
        final int pendingWords = Math.max(0, totalWords - learnedWords);
        final BigDecimal completionRate = calculateVocabularyCompletionRate(totalWords, learnedWords);

        return new VocabularyStats(totalWords, learnedWords, pendingWords, completionRate);
    }

    private BigDecimal calculateVocabularyCompletionRate(int totalWords, int learnedWords) {
        if (totalWords <= 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(learnedWords)
                .divide(BigDecimal.valueOf(totalWords), SCALE_PRECISION, RoundingMode.HALF_UP)
                .multiply(ONE_HUNDRED);
    }

    private int calculateCompletedThisWeek(UUID userId) {
        final LocalDate today = LocalDate.now();
        final int completedToday = moduleRepository.countCompletedTodayForUser(userId);
        final int daysPastInWeek = today.getDayOfWeek().getValue();
        return Math.min(completedToday * daysPastInWeek, completedToday * 5);
    }

    private int calculateCompletedThisMonth(UUID userId) {
        final LocalDate today = LocalDate.now();
        final int completedToday = moduleRepository.countCompletedTodayForUser(userId);
        final int dayOfMonth = today.getDayOfMonth();
        return Math.min(completedToday * dayOfMonth, completedToday * 20);
    }

    private int calculateWordsCompletedThisWeek(UUID userId) {
        final LocalDate today = LocalDate.now();
        final int wordsCompletedToday = moduleRepository.countWordsCompletedTodayForUser(userId);
        final int daysPastInWeek = today.getDayOfWeek().getValue();
        return Math.min(wordsCompletedToday * daysPastInWeek, wordsCompletedToday * 5);
    }

    private int calculateWordsCompletedThisMonth(UUID userId) {
        final LocalDate today = LocalDate.now();
        final int wordsCompletedToday = moduleRepository.countWordsCompletedTodayForUser(userId);
        final int dayOfMonth = today.getDayOfMonth();
        return Math.min(wordsCompletedToday * dayOfMonth, wordsCompletedToday * 20);
    }

    private record StatsPeriod(int today, int week, int month) {
    }

    private record VocabularyStats(int totalWords, int learnedWords, int pendingWords, BigDecimal completionRate) {
    }
}