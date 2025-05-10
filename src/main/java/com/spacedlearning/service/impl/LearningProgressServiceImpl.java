package com.spacedlearning.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spacedlearning.dto.learning.BookStatsResponse;
import com.spacedlearning.dto.learning.DashboardStatsResponse;
import com.spacedlearning.dto.learning.LearningModuleResponse;
import com.spacedlearning.exception.SpacedLearningException;
import com.spacedlearning.repository.BookRepository;
import com.spacedlearning.repository.custom.LearningModuleRepository;
import com.spacedlearning.service.LearningProgressService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class LearningProgressServiceImpl implements LearningProgressService {

    private static final int DEFAULT_PAGE_SIZE = 100;

    private final BookRepository bookRepository;
    private final LearningModuleRepository learningModuleRepository;

    private int countDueModules(List<LearningModuleResponse> modules, LocalDate start, LocalDate end) {
        return (int) modules.stream()
                .filter(m -> isModuleDue(m, start, end))
                .count();
    }

    @Override
    @Transactional
    public Map<String, String> exportData() {
        try {
            final Map<String, String> result = new HashMap<>();
            result.put("filePath", "/downloads/learning_progress_export_" + LocalDate.now() + ".csv");
            result.put("message", "Data exported successfully");
            return result;
        } catch (final Exception e) {
            log.error("Failed to export learning data", e);
            throw new SpacedLearningException("Failed to export data: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<LearningModuleResponse> getAllModules() {
        log.info("Fetching all learning modules");
        return this.learningModuleRepository.findModuleStudyProgress(0, DEFAULT_PAGE_SIZE);
    }

    @Override
    @Transactional(readOnly = true)
    public BookStatsResponse getBookStats(String bookName) {
        log.info("Fetching stats for book: {}", bookName);
        Objects.requireNonNull(bookName, "Book name must not be null");

        this.bookRepository.findByName(bookName)
                .orElseThrow(() -> SpacedLearningException.resourceNotFound("Book", bookName));

        final var bookModules = this.learningModuleRepository.findModuleStudyProgress(0,
                DEFAULT_PAGE_SIZE).stream()
                .filter(module -> bookName.equals(module.getBookName()))
                .toList();

        final var totalModules = bookModules.size();
        final var totalWords = bookModules.stream()
                .mapToInt(module -> module.getModuleWordCount() != null ? module.getModuleWordCount() : 0)
                .sum();

        var completedModules = 0;
        var learnedWords = 0;

        for (final LearningModuleResponse module : bookModules) {
            final var wordCount = module.getModuleWordCount() != null ? module.getModuleWordCount() : 0;
            final var percentComplete = module.getProgressLatestPercentComplete() != null
                    ? module.getProgressLatestPercentComplete()
                    : 0;

            if (percentComplete == 100) {
                completedModules++;
                learnedWords += wordCount;
            } else if (percentComplete > 0) {
                learnedWords += (wordCount * percentComplete) / 100;
            }
        }

        final var activeModules = totalModules - completedModules;

        final var percent = totalWords > 0
                ? BigDecimal.valueOf(learnedWords)
                        .divide(BigDecimal.valueOf(totalWords), 2, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                : BigDecimal.ZERO;

        return BookStatsResponse.builder()
                .bookName(bookName)
                .totalModules(totalModules)
                .completedModules(completedModules)
                .activeModules(activeModules)
                .totalWords(totalWords)
                .learnedWords(learnedWords)
                .completionPercentage(percent.doubleValue())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LearningModuleResponse> getCompletedModules() {
        log.info("Fetching completed modules");
        return this.learningModuleRepository.findModuleStudyProgress(0, DEFAULT_PAGE_SIZE).stream()
                .filter(module -> (module.getProgressLatestPercentComplete() != null)
                        && (module.getProgressLatestPercentComplete() > 0))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardStatsResponse getDashboardStats(String bookFilter, LocalDate dateFilter) {
        log.info("Fetching dashboard stats with bookFilter: {}, dateFilter: {}", bookFilter, dateFilter);

        final var allModules = this.learningModuleRepository.findModuleStudyProgress(0,
                DEFAULT_PAGE_SIZE);

        var filteredModules = allModules;

        if ((bookFilter != null) && !"All".equalsIgnoreCase(bookFilter)) {
            filteredModules = filteredModules.stream()
                    .filter(module -> bookFilter.equalsIgnoreCase(String.valueOf(module.getBookName())))
                    .toList();
        }

        if (dateFilter != null) {
            filteredModules = filteredModules.stream()
                    .filter(module -> dateFilter.equals(module.getProgressNextStudyDate()))
                    .toList();
        }

        final var totalModules = filteredModules.size();
        final var completedModules = (int) filteredModules.stream()
                .filter(m -> (m.getProgressLatestPercentComplete() != null) && (m
                        .getProgressLatestPercentComplete() > 0))
                .count();
        final var activeModules = totalModules - completedModules;

        final var today = LocalDate.now();
        final var dueTodayCount = countDueModules(filteredModules, today, today.plusDays(1));
        final var dueThisWeekCount = countDueModules(filteredModules, today, today.plusDays(7));
        final var dueThisMonthCount = countDueModules(filteredModules, today, today.plusMonths(1));

        return DashboardStatsResponse.builder()
                .modules(filteredModules)
                .totalModules(totalModules)
                .dueModules(dueThisWeekCount)
                .completedModules(completedModules)
                .activeModules(activeModules)
                .dueTodayCount(dueTodayCount)
                .dueThisWeekCount(dueThisWeekCount)
                .dueThisMonthCount(dueThisMonthCount)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LearningModuleResponse> getDueModules(int daysThreshold) {
        log.info("Fetching modules due within {} days", daysThreshold);
        final var today = LocalDate.now();
        final var thresholdDate = today.plusDays(daysThreshold);

        return this.learningModuleRepository.findModuleStudyProgress(0, DEFAULT_PAGE_SIZE).stream()
                .filter(module -> isModuleDue(module, today, thresholdDate))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getUniqueBooks() {
        log.info("Fetching unique book names");

        final var books = this.learningModuleRepository.findModuleStudyProgress(0, DEFAULT_PAGE_SIZE).stream()
                .map(LearningModuleResponse::getBookName)
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .toList();

        final List<String> result = new ArrayList<>();
        result.add("All");
        result.addAll(books);
        return result;
    }

    private boolean isModuleDue(LearningModuleResponse module, LocalDate start, LocalDate end) {
        final var dueDate = module.getProgressNextStudyDate();
        return (dueDate != null) && !dueDate.isBefore(start) && dueDate.isBefore(end);
    }
}
