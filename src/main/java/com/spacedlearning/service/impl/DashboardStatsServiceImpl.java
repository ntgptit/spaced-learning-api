package com.spacedlearning.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
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

    private record StatsPeriod(int today, int week, int month) {
    }

    private record VocabularyStats(int totalWords, int learnedWords, int pendingWords, BigDecimal completionRate) {
    }

    private static final String RESOURCE_USER = "resource.user";
    private static final int SCALE_PRECISION = 2;
    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);

    private static final int MAX_BUSINESS_DAYS_PER_WEEK = 5;
    private static final int MAX_LEARNING_DAYS_PER_MONTH = 20;
    private final UserRepository userRepository;
    private final UserStatisticsRepository statsRepository;

    private final ModuleRepository moduleRepository;

    private final MessageSource messageSource;

    private UserLearningStatsDTO.UserLearningStatsDTOBuilder buildStatsFromExisting(final UserStatistics stats) {
        return UserLearningStatsDTO.builder()
                .lastUpdated(stats.getLastStatisticsUpdate())
                .streakDays(stats.getStreakDays())
                .streakWeeks(stats.getStreakWeeks())
                .longestStreakDays(stats.getLongestStreakDays());
    }

    private UserLearningStatsDTO.UserLearningStatsDTOBuilder buildStatsFromScratch() {
        final var vocabularyStats = calculateVocabularyStats();

        return UserLearningStatsDTO.builder()
                .lastUpdated(LocalDateTime.now())
                .streakDays(0)
                .streakWeeks(0)
                .longestStreakDays(0)
                .totalWords(vocabularyStats.totalWords)
                .learnedWords(vocabularyStats.learnedWords)
                .pendingWords(vocabularyStats.pendingWords)
                .vocabularyCompletionRate(vocabularyStats.completionRate)
                .weeklyNewWordsRate(BigDecimal.ZERO)
                .totalCompletedModules(0)
                .totalInProgressModules(0);
    }

    private StatsPeriod calculateCompletedStats() {
        final var today = this.moduleRepository.countCompletedToday();
        return new StatsPeriod(today, estimateThisWeek(today), estimateThisMonth(today));
    }

    private Map<String, Integer> calculateCycleStats() {
        final var stats = this.moduleRepository.getModuleCycleStudiedStats();
        final Map<String, Integer> result = new HashMap<>();
        var totalStudied = 0;

        for (final Object[] row : stats) {
            final var cycle = (String) row[0];
            final var count = ((Number) row[1]).intValue();
            result.put(cycle, count);
            totalStudied += count;
        }

        final var totalModules = this.moduleRepository.countTotalModules();
        result.put("NOT_STUDIED", Math.max(0, totalModules - totalStudied));
        return result;
    }

    private StatsPeriod calculateDueStats() {
        return new StatsPeriod(
                this.moduleRepository.countDueToday(),
                this.moduleRepository.countDueThisWeek(),
                this.moduleRepository.countDueThisMonth());
    }

    private UserLearningStatsDTO calculateDynamicStats(final UserLearningStatsDTO.UserLearningStatsDTOBuilder builder) {
        final var totalModules = this.moduleRepository.countTotalModules();
        final var cycleStats = calculateCycleStats();
        final var dueStats = calculateDueStats();
        final var wordsDueStats = calculateWordsDueStats();
        final var completedStats = calculateCompletedStats();
        final var wordsCompletedStats = calculateWordsCompletedStats();
        final var vocabularyStats = calculateVocabularyStats();

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

    private BigDecimal calculateVocabularyCompletionRate(final int total, final int learned) {
        if (total <= 0) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(learned)
                .divide(BigDecimal.valueOf(total), SCALE_PRECISION, RoundingMode.HALF_UP)
                .multiply(ONE_HUNDRED);
    }

    private VocabularyStats calculateVocabularyStats() {
        final var total = this.moduleRepository.countTotalVocabularyWords();
        final var learned = this.moduleRepository.countLearnedVocabularyWords();
        final var pending = Math.max(0, total - learned);
        final var rate = calculateVocabularyCompletionRate(total, learned);
        return new VocabularyStats(total, learned, pending, rate);
    }

    private StatsPeriod calculateWordsCompletedStats() {
        final var today = this.moduleRepository.countWordsCompletedToday();
        return new StatsPeriod(today, estimateThisWeek(today), estimateThisMonth(today));
    }

    private StatsPeriod calculateWordsDueStats() {
        return new StatsPeriod(
                this.moduleRepository.countWordsDueToday(),
                this.moduleRepository.countWordsDueThisWeek(),
                this.moduleRepository.countWordsDueThisMonth());
    }

    private int estimateThisMonth(final int todayCount) {
        final var dayOfMonth = LocalDate.now().getDayOfMonth();
        return todayCount * Math.min(dayOfMonth, MAX_LEARNING_DAYS_PER_MONTH);
    }

    private int estimateThisWeek(final int todayCount) {
        final var dayOfWeek = LocalDate.now().getDayOfWeek().getValue();
        return todayCount * Math.min(dayOfWeek, MAX_BUSINESS_DAYS_PER_WEEK);
    }

    @Override
    @Transactional(readOnly = true)
    public UserLearningStatsDTO getDashboardStats(final UUID userId) {
        log.debug("Calculating dashboard stats for user ID: {}", userId);
        validateUserId(userId);
        verifyUserExists(userId);

        final var statsOpt = this.statsRepository.findByUserId(userId);
        final var builder = statsOpt.map(this::buildStatsFromExisting)
                .orElseGet(this::buildStatsFromScratch);

        return calculateDynamicStats(builder);
    }

    private void validateUserId(final UUID userId) {
        Objects.requireNonNull(userId, "User ID must not be null");
    }

    private void verifyUserExists(final UUID userId) {
        this.userRepository.findById(userId).orElseThrow(() -> SpacedLearningException.resourceNotFound(
                this.messageSource, RESOURCE_USER, userId));
    }
}
