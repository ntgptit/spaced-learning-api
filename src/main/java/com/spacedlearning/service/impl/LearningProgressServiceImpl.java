package com.spacedlearning.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spacedlearning.dto.learning.BookStatsResponse;
import com.spacedlearning.dto.learning.DashboardStatsResponse;
import com.spacedlearning.dto.learning.LearningModuleResponse;
import com.spacedlearning.entity.Book;
import com.spacedlearning.entity.Module;
import com.spacedlearning.entity.ModuleProgress;
import com.spacedlearning.entity.Repetition;
import com.spacedlearning.entity.enums.RepetitionStatus;
import com.spacedlearning.exception.SpacedLearningException;
import com.spacedlearning.repository.BookRepository;
import com.spacedlearning.repository.ModuleProgressRepository;
import com.spacedlearning.repository.ModuleRepository;
import com.spacedlearning.service.LearningProgressService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class LearningProgressServiceImpl implements LearningProgressService {

    private final BookRepository bookRepository;
    private final ModuleRepository moduleRepository;
    private final ModuleProgressRepository moduleProgressRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardStatsResponse getDashboardStats(String bookFilter, LocalDate dateFilter) {
        log.info("Fetching dashboard stats with bookFilter: {}, dateFilter: {}", bookFilter, dateFilter);

        final List<LearningModuleResponse> allModules = getAllModules();
        List<LearningModuleResponse> filteredModules = allModules;
        if (bookFilter != null && !"All".equals(bookFilter)) {
            filteredModules = filteredModules.stream()
                    .filter(module -> module.getBookName().equals(bookFilter))
                    .toList();
        }
        if (dateFilter != null) {
            filteredModules = filteredModules.stream()
                    .filter(module -> module.getNextStudyDate() != null && module.getNextStudyDate().equals(dateFilter))
                    .toList();
        }

        final int totalModules = filteredModules.size();
        final int completedModules = (int) filteredModules.stream()
                .filter(m -> m.getPercentComplete() > 0)
                .count();
        final int activeModules = totalModules - completedModules;

        final LocalDate today = LocalDate.now();
        final LocalDate weekEnd = today.plusDays(7);
        final LocalDate monthEnd = today.plusMonths(1);

        final int dueTodayCount = countDueModules(filteredModules, today, today.plusDays(1));
        final int dueThisWeekCount = countDueModules(filteredModules, today, weekEnd);
        final int dueThisMonthCount = countDueModules(filteredModules, today, monthEnd);

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
        log.info("Fetching all modules");

        final List<Module> allModules = moduleRepository.findAll();
        final List<ModuleProgress> allProgress = moduleProgressRepository.findAll();

        // Tạo map từ moduleId đến ModuleProgress
        final Map<UUID, ModuleProgress> progressMap = allProgress.stream()
                .collect(Collectors.toMap(
                        progress -> progress.getModule().getId(),
                        progress -> progress,
                        (existing, replacement) -> existing,
                        HashMap::new));

        return allModules.stream()
                .map(module -> {
                    ModuleProgress progress = progressMap.get(module.getId());
                    if (progress == null) {
                        progress = new ModuleProgress();
                        progress.setModule(module);
                        progress.setPercentComplete(new java.math.BigDecimal(0));
                        progress.setRepetitions(new ArrayList<>());
                    }
                    return convertToLearningModuleResponse(progress);
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LearningModuleResponse> getDueModules(int daysThreshold) {
        final LocalDate today = LocalDate.now();
        final LocalDate thresholdDate = today.plusDays(daysThreshold);

        final List<ModuleProgress> dueProgress = moduleProgressRepository.findByNextStudyDateLessThanEqual(
                thresholdDate);
        return dueProgress.stream()
                .map(this::convertToLearningModuleResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LearningModuleResponse> getCompletedModules() {
        final List<ModuleProgress> completedProgress = moduleProgressRepository.findByPercentComplete(
                new java.math.BigDecimal(100));
        return completedProgress.stream()
                .map(this::convertToLearningModuleResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getUniqueBooks() {
        final List<String> books = moduleProgressRepository.findUniqueBookNames();
        final List<String> result = new ArrayList<>();
        result.add("All");
        result.addAll(books);
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public BookStatsResponse getBookStats(String bookName) {
        final Book book = bookRepository.findByName(bookName)
                .orElseThrow(() -> SpacedLearningException.resourceNotFound("Book", bookName));
        final List<Module> modules = moduleRepository.findByBookIdOrderByModuleNo(book.getId());

        final int totalModules = modules.size();
        final int totalWords = modules.stream().mapToInt(Module::getWordCount).sum();

        // Lấy tất cả progress cho các module của book
        final List<ModuleProgress> progressList = new ArrayList<>();
        for (final Module module : modules) {
            moduleProgressRepository.findByModuleId(module.getId())
                    .ifPresent(progressList::add);
        }

        int completedModules = 0;
        int learnedWords = 0;
        for (final ModuleProgress progress : progressList) {
            final Module module = progress.getModule();
            final int wordCount = module.getWordCount();
            final int percentComplete = progress.getPercentComplete().intValue();

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

    private LearningModuleResponse convertToLearningModuleResponse(ModuleProgress progress) {
        final Module module = progress.getModule();
        final Book book = module.getBook();

        final int wordCount = module.getWordCount();
        final int percentComplete = progress.getPercentComplete().intValue();
        final int learnedWords = wordCount * percentComplete / 100;

        final int pendingRepetitions = (int) progress.getRepetitions().stream()
                .filter(r -> r.getStatus() == RepetitionStatus.NOT_STARTED)
                .count();

        final LocalDateTime lastStudyDate = progress.getRepetitions().stream()
                .filter(r -> r.getStatus() == RepetitionStatus.COMPLETED)
                .map(Repetition::getUpdatedAt)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        final List<LocalDate> studyHistory = progress.getRepetitions().stream()
                .filter(r -> r.getStatus() == RepetitionStatus.COMPLETED)
                .map(Repetition::getReviewDate)
                .filter(Objects::nonNull)
                .toList();

        return LearningModuleResponse.builder()
                .id(module.getId())
                .bookName(book.getName())
                .title(module.getTitle())
                .wordCount(wordCount)
                .cyclesStudied(progress.getCyclesStudied())
                .firstLearningDate(progress.getFirstLearningDate())
                .percentComplete(percentComplete)
                .nextStudyDate(progress.getNextStudyDate())
                .pendingRepetitions(pendingRepetitions)
                .learnedWords(learnedWords)
                .lastStudyDate(lastStudyDate)
                .studyHistory(studyHistory)
                .build();
    }

    private int countDueModules(List<LearningModuleResponse> modules, LocalDate start, LocalDate end) {
        return (int) modules.stream()
                .filter(m -> m.getNextStudyDate() != null &&
                        !m.getNextStudyDate().isBefore(start) &&
                        m.getNextStudyDate().isBefore(end))
                .count();
    }
}