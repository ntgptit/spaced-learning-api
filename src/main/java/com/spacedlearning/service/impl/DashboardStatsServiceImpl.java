package com.spacedlearning.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
import com.spacedlearning.repository.RepetitionRepository;
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
    private final RepetitionRepository repetitionRepository;
    private final ModuleRepository moduleRepository;
    private final MessageSource messageSource;

    @Transactional(readOnly = true)
    public UserLearningStatsDTO getDashboardStats(final UUID userId) {
        log.debug("Calculating dashboard stats for user ID: {}", userId);
        Objects.requireNonNull(userId, "User ID must not be null");

        userRepository.findById(userId).orElseThrow(() -> SpacedLearningException
                .resourceNotFound(messageSource, RESOURCE_USER, userId));

        Optional<UserStatistics> statsOpt = statsRepository.findByUserId(userId);
        UserLearningStatsDTO.UserLearningStatsDTOBuilder builder =
                createBasicStatsBuilder(userId, statsOpt);
        return calculateDynamicStats(userId, statsOpt, builder);
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
                    int totalWords = moduleRepository.getTotalWordCountForUser(userId);
                    int learnedWords = moduleRepository.getLearnedWordCountForUser(userId);
                    BigDecimal vocabularyCompletionRate =
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
            final Optional<UserStatistics> statsOpt,
            final UserLearningStatsDTO.UserLearningStatsDTOBuilder builder) {
        int totalModules = moduleRepository.countTotalModulesForUser(userId);
        int completedModules = statsOpt.map(UserStatistics::getTotalCompletedModules)
                .orElseGet(() -> moduleRepository.countCompletedModulesForUser(userId));
        int inProgressModules = statsOpt.map(UserStatistics::getTotalInProgressModules)
                .orElseGet(() -> moduleRepository.countInProgressModulesForUser(userId));
        double moduleCompletionRate = calculateModuleCompletionRate(totalModules, completedModules);

        int dueToday = repetitionRepository.countDueTodayForUser(userId);
        int dueThisWeek = repetitionRepository.countDueThisWeekForUser(userId);
        int dueThisMonth = repetitionRepository.countDueThisMonthForUser(userId);

        int wordsDueToday = repetitionRepository.countWordsDueTodayForUser(userId);
        int wordsDueThisWeek = repetitionRepository.countWordsDueThisWeekForUser(userId);
        int wordsDueThisMonth = repetitionRepository.countWordsDueThisMonthForUser(userId);

        int completedToday = repetitionRepository.countCompletedTodayForUser(userId);
        int completedThisWeek = calculateCompletedThisWeek(userId);
        int completedThisMonth = calculateCompletedThisMonth(userId);

        int wordsCompletedToday = repetitionRepository.countWordsCompletedTodayForUser(userId);
        int wordsCompletedThisWeek = calculateWordsCompletedThisWeek(userId);
        int wordsCompletedThisMonth = calculateWordsCompletedThisMonth(userId);

        int totalWords = statsOpt.map(UserStatistics::getTotalWords)
                .orElseGet(() -> moduleRepository.getTotalWordCountForUser(userId));
        int learnedWords = statsOpt.map(UserStatistics::getLearnedWords)
                .orElseGet(() -> moduleRepository.getLearnedWordCountForUser(userId));
        int pendingWords = Math.max(0, totalWords - learnedWords);

        return builder.totalModules(totalModules).completedModules(completedModules)
                .inProgressModules(inProgressModules).moduleCompletionRate(moduleCompletionRate)
                .dueToday(dueToday).dueThisWeek(dueThisWeek).dueThisMonth(dueThisMonth)
                .wordsDueToday(wordsDueToday).wordsDueThisWeek(wordsDueThisWeek)
                .wordsDueThisMonth(wordsDueThisMonth).completedToday(completedToday)
                .completedThisWeek(completedThisWeek).completedThisMonth(completedThisMonth)
                .wordsCompletedToday(wordsCompletedToday)
                .wordsCompletedThisWeek(wordsCompletedThisWeek)
                .wordsCompletedThisMonth(wordsCompletedThisMonth).pendingWords(pendingWords)
                .build();
    }

    private BigDecimal calculateVocabularyCompletionRate(final int totalWords,
            final int learnedWords) {
        if (totalWords <= 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal learnedWordsBD = BigDecimal.valueOf(learnedWords);
        BigDecimal totalWordsBD = BigDecimal.valueOf(totalWords);
        return learnedWordsBD.divide(totalWordsBD, SCALE_PRECISION, RoundingMode.HALF_UP)
                .multiply(ONE_HUNDRED);
    }

    private double calculateModuleCompletionRate(final int totalModules,
            final int completedModules) {
        return totalModules > 0 ? (double) completedModules / totalModules * 100 : 0;
    }

    private int calculateCompletedThisWeek(final UUID userId) {
        LocalDate today = LocalDate.now();
        int completedToday = repetitionRepository.countCompletedTodayForUser(userId);
        int daysPastInWeek = today.getDayOfWeek().getValue();
        return Math.min(completedToday * daysPastInWeek, completedToday * 5);
    }

    private int calculateCompletedThisMonth(final UUID userId) {
        LocalDate today = LocalDate.now();
        int completedToday = repetitionRepository.countCompletedTodayForUser(userId);
        int dayOfMonth = today.getDayOfMonth();
        return Math.min(completedToday * dayOfMonth, completedToday * 20);
    }

    private int calculateWordsCompletedThisWeek(final UUID userId) {
        LocalDate today = LocalDate.now();
        int wordsCompletedToday = repetitionRepository.countWordsCompletedTodayForUser(userId);
        int daysPastInWeek = today.getDayOfWeek().getValue();
        return Math.min(wordsCompletedToday * daysPastInWeek, wordsCompletedToday * 5);
    }

    private int calculateWordsCompletedThisMonth(final UUID userId) {
        LocalDate today = LocalDate.now();
        int wordsCompletedToday = repetitionRepository.countWordsCompletedTodayForUser(userId);
        int dayOfMonth = today.getDayOfMonth();
        return Math.min(wordsCompletedToday * dayOfMonth, wordsCompletedToday * 20);
    }
}
