package com.spacedlearning.service.impl;

import com.spacedlearning.dto.learning.BookStatsResponse;
import com.spacedlearning.dto.learning.DashboardStatsResponse;
import com.spacedlearning.dto.learning.LearningModuleResponse;
import com.spacedlearning.exception.SpacedLearningException;
import com.spacedlearning.repository.BookRepository;
import com.spacedlearning.repository.custom.LearningModuleRepository;
import com.spacedlearning.service.LearningProgressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class LearningProgressServiceImpl implements LearningProgressService {

    // Số lượng module mặc định để lấy
    private static final int DEFAULT_PAGE_SIZE = 100;
    private final BookRepository bookRepository;
    private final LearningModuleRepository learningModuleRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardStatsResponse getDashboardStats(String bookFilter, LocalDate dateFilter) {
        log.info("Fetching dashboard stats with bookFilter: {}, dateFilter: {}", bookFilter, dateFilter);

        // Lấy tất cả modules (giới hạn 100 để tránh quá tải)

        // Áp dụng bộ lọc
        List<LearningModuleResponse> filteredModules = getAllModules();

        if (bookFilter != null && !"All".equals(bookFilter)) {
            filteredModules = filteredModules.stream()
                    .filter(module -> bookFilter.equals(module.getBookName()))
                    .toList();
        }

        if (dateFilter != null) {
            filteredModules = filteredModules.stream()
                    .filter(module -> dateFilter.equals(module.getProgressNextStudyDate()))
                    .toList();
        }

        // Tính toán thống kê
        final int totalModules = filteredModules.size();
        final int completedModules = (int) filteredModules.stream()
                .filter(m -> m.getProgressLatestPercentComplete() != null && m
                        .getProgressLatestPercentComplete() > 0)
                .count();
        final int activeModules = totalModules - completedModules;

        // Tính toán số module đến hạn
        final LocalDate today = LocalDate.now();
        final LocalDate weekEnd = today.plusDays(7);
        final LocalDate monthEnd = today.plusMonths(1);

        final int dueTodayCount = countDueModules(filteredModules, today, today.plusDays(1));
        final int dueThisWeekCount = countDueModules(filteredModules, today, weekEnd);
        final int dueThisMonthCount = countDueModules(filteredModules, today, monthEnd);

        // Tạo response
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
    public List<LearningModuleResponse> getAllModules() {
        log.info("Fetching all learning modules");
        return learningModuleRepository.findModuleStudyProgress(0, DEFAULT_PAGE_SIZE);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LearningModuleResponse> getDueModules(int daysThreshold) {
        log.info("Fetching modules due within {} days", daysThreshold);

        final LocalDate today = LocalDate.now();
        final LocalDate thresholdDate = today.plusDays(daysThreshold);

        // Lấy tất cả modules rồi lọc theo ngày
        return learningModuleRepository.findModuleStudyProgress(0, DEFAULT_PAGE_SIZE).stream()
                .filter(module -> isModuleDue(module, today, thresholdDate))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LearningModuleResponse> getCompletedModules() {
        log.info("Fetching completed modules");

        // Lấy tất cả modules rồi lọc các module đã hoàn thành
        return getAllModules().stream()
                .filter(module -> module.getProgressLatestPercentComplete() != null
                        && module.getProgressLatestPercentComplete() > 0)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getUniqueBooks() {
        log.info("Fetching unique book names");

        learningModuleRepository.findModuleStudyProgress(0, DEFAULT_PAGE_SIZE).stream()
                .map(LearningModuleResponse::getBookName)
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .toList();

        // Thêm tùy chọn "All" vào đầu danh sách
        return List.of("All").stream()
                .map(all -> all)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public BookStatsResponse getBookStats(String bookName) {
        log.info("Fetching stats for book: {}", bookName);

        bookRepository.findByName(bookName)
                .orElseThrow(() -> SpacedLearningException.resourceNotFound("Book", bookName));

        // Lấy tất cả modules của sách
        final List<LearningModuleResponse> bookModules = getAllModules().stream()
                .filter(module -> bookName.equals(module.getBookName()))
                .toList();

        // Tính toán thống kê
        final int totalModules = bookModules.size();
        final int totalWords = bookModules.stream()
                .mapToInt(module -> module.getModuleWordCount() != null ? module.getModuleWordCount() : 0)
                .sum();

        int completedModules = 0;
        int learnedWords = 0;

        for (final LearningModuleResponse module : bookModules) {
            final int wordCount = module.getModuleWordCount() != null ? module.getModuleWordCount() : 0;
            final int percentComplete = module.getProgressLatestPercentComplete() != null
                    ? module.getProgressLatestPercentComplete()
                    : 0;

            if (percentComplete == 100) {
                completedModules++;
                learnedWords += wordCount;
            } else if (percentComplete > 0) {
                learnedWords += wordCount * percentComplete / 100;
            }
        }

        final int activeModules = totalModules - completedModules;
        final double completionPercentage = totalWords > 0 ? (double) learnedWords / totalWords * 100 : 0;

        return BookStatsResponse.builder()
                .bookName(bookName)
                .totalModules(totalModules)
                .completedModules(completedModules)
                .activeModules(activeModules)
                .totalWords(totalWords)
                .learnedWords(learnedWords)
                .completionPercentage(Math.round(completionPercentage * 100) / 100.0)
                .build();
    }

    @Override
    @Transactional
    public Map<String, String> exportData() {
        try {
            final Map<String, String> result = new HashMap<>();
            result.put("filePath", "/downloads/learning_progress_export_" +
                    LocalDate.now() + ".csv");
            result.put("message", "Data exported successfully");
            return result;
        } catch (final Exception e) {
            log.error("Failed to export learning data", e);
            throw new SpacedLearningException("Failed to export data: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Đếm số lượng module đến hạn trong khoảng thời gian
     */
    private int countDueModules(List<LearningModuleResponse> modules, LocalDate start, LocalDate end) {
        return (int) modules.stream()
                .filter(m -> isModuleDue(m, start, end))
                .count();
    }

    /**
     * Kiểm tra xem module có đến hạn trong khoảng thời gian hay không
     */
    private boolean isModuleDue(LearningModuleResponse module, LocalDate start, LocalDate end) {
        final LocalDate dueDate = module.getProgressNextStudyDate();
        return dueDate != null &&
                !dueDate.isBefore(start) &&
                dueDate.isBefore(end);
    }
}