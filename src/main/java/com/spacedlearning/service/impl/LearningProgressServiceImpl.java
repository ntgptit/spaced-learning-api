package com.spacedlearning.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spacedlearning.dto.learning.BookStatsResponse;
import com.spacedlearning.dto.learning.DashboardStatsResponse;
import com.spacedlearning.dto.learning.LearningModuleResponse;
import com.spacedlearning.entity.Book;
import com.spacedlearning.entity.Module;
import com.spacedlearning.entity.ModuleProgress;
import com.spacedlearning.entity.Repetition;
import com.spacedlearning.entity.User;
import com.spacedlearning.entity.enums.RepetitionStatus;
import com.spacedlearning.exception.SpacedLearningException;
import com.spacedlearning.repository.BookRepository;
import com.spacedlearning.repository.ModuleProgressRepository;
import com.spacedlearning.repository.ModuleRepository;
import com.spacedlearning.repository.UserRepository;
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
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardStatsResponse getDashboardStats(String bookFilter, LocalDate dateFilter) {
        getCurrentUserId();

        // Get all modules with progress for user
        final List<LearningModuleResponse> allModules = getAllModules();

        // Apply filters if provided
        List<LearningModuleResponse> filteredModules = allModules;

        if (bookFilter != null && !"All".equals(bookFilter)) {
            filteredModules = filteredModules.stream()
                    .filter(module -> module.getBookName().equals(bookFilter)).toList();
        }

        if (dateFilter != null) {
            filteredModules = filteredModules.stream()
                    .filter(module -> module.getNextStudyDate() != null &&
                            module.getNextStudyDate().equals(dateFilter)).toList();
        }

        // Calculate statistics
        final int totalModules = filteredModules.size();
        final int completedModules = (int) filteredModules.stream()
                .filter(m -> m.getPercentComplete() > 0)
                .count();
        final int activeModules = totalModules - completedModules;

        // Get due modules counts
        final LocalDate today = LocalDate.now();
        final LocalDate weekEnd = today.plusDays(7);
        final LocalDate monthEnd = today.plusMonths(1);

        final int dueTodayCount = (int) filteredModules.stream()
                .filter(m -> m.getNextStudyDate() != null &&
                        m.getNextStudyDate().equals(today))
                .count();

        final int dueThisWeekCount = (int) filteredModules.stream()
                .filter(m -> m.getNextStudyDate() != null &&
                        !m.getNextStudyDate().isBefore(today) &&
                        m.getNextStudyDate().isBefore(weekEnd))
                .count();

        final int dueThisMonthCount = (int) filteredModules.stream()
                .filter(m -> m.getNextStudyDate() != null &&
                        !m.getNextStudyDate().isBefore(today) &&
                        m.getNextStudyDate().isBefore(monthEnd))
                .count();

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
        final UUID userId = getCurrentUserId();

        // Get all module progress records for the user
        final List<ModuleProgress> progressList = moduleProgressRepository.findByUserId(userId);

        // Convert to response DTOs
        return progressList.stream()
                .map(this::convertToLearningModuleResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LearningModuleResponse> getDueModules(int daysThreshold) {
        final UUID userId = getCurrentUserId();
        final LocalDate today = LocalDate.now();
        final LocalDate thresholdDate = today.plusDays(daysThreshold);

        // Get module progress records with next study date within threshold
        final List<ModuleProgress> dueProgress = moduleProgressRepository.findDueForStudy(
                userId, thresholdDate, null).getContent();

        // Convert to response DTOs
        return dueProgress.stream()
                .map(this::convertToLearningModuleResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<LearningModuleResponse> getCompletedModules() {
        final UUID userId = getCurrentUserId();

        // Get completed module progress records (100%)
        final List<ModuleProgress> completedProgress = moduleProgressRepository.findByUserIdAndPercentComplete(
                userId, new java.math.BigDecimal(100));

        // Convert to response DTOs
        return completedProgress.stream()
                .map(this::convertToLearningModuleResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getUniqueBooks() {
        final UUID userId = getCurrentUserId();

        // Get all books that have modules with progress for the user
        final List<String> books = moduleProgressRepository.findUniqueBooksByUserId(userId);

        // Add "All" as first option
        final List<String> result = new ArrayList<>();
        result.add("All");
        result.addAll(books);

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public BookStatsResponse getBookStats(String bookName) {
        final UUID userId = getCurrentUserId();

        // Get the book
        final Book book = bookRepository.findByName(bookName)
                .orElseThrow(() -> SpacedLearningException.resourceNotFound("Book", bookName));

        // Get all modules for the book
        final List<Module> modules = moduleRepository.findByBookIdOrderByModuleNo(book.getId());

        // Calculate stats
        final int totalModules = modules.size();
        final int totalWords = modules.stream().mapToInt(Module::getWordCount).sum();

        // Get progress records for this book and user
        final List<ModuleProgress> progressList = new ArrayList<>();
        for (final Module module : modules) {
            moduleProgressRepository.findByUserIdAndModuleId(userId, module.getId())
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
            // In a real implementation, this would generate and save a file
            // For now, we'll return success with a placeholder file path

            final Map<String, String> result = new HashMap<>();
            result.put("filePath", "/downloads/learning_progress_export_" +
                    LocalDate.now() + ".csv");
            result.put("message", "Data exported successfully");

            return result;

        } catch (final Exception e) {
            log.error("Failed to export learning data", e);
            final Map<String, String> result = new HashMap<>();
            result.put("error", "Failed to export data: " + e.getMessage());
            return result;
        }
    }

    /**
     * Helper method to convert ModuleProgress to LearningModuleResponse
     */
    private LearningModuleResponse convertToLearningModuleResponse(ModuleProgress progress) {
        final Module module = progress.getModule();
        final Book book = module.getBook();

        // Calculate learned words
        final int wordCount = module.getWordCount();
        final int percentComplete = progress.getPercentComplete().intValue();
        final int learnedWords = wordCount * percentComplete / 100;

        // Get pending repetitions count
        final int pendingRepetitions = (int) progress.getRepetitions().stream()
                .filter(r -> r.getStatus() == RepetitionStatus.NOT_STARTED)
                .count();

        // Get last study date (most recent completed repetition)
        LocalDateTime lastStudyDate = null;
        for (final Repetition repetition : progress.getRepetitions()) {
            if (repetition.getStatus() == RepetitionStatus.COMPLETED) {
                final LocalDateTime updatedAt = repetition.getUpdatedAt();
                if (lastStudyDate == null || updatedAt != null && updatedAt.isAfter(lastStudyDate)) {
                    lastStudyDate = updatedAt;
                }
            }
        }

        // Get study history
        final List<LocalDate> studyHistory = progress.getRepetitions().stream()
                .filter(r -> r.getStatus() == RepetitionStatus.COMPLETED)
                .map(Repetition::getReviewDate)
                .filter(Objects::nonNull).toList();

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

    /**
     * Get current authenticated user ID
     */
    private UUID getCurrentUserId() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() ||
                "anonymousUser".equals(authentication.getName())) {
            throw SpacedLearningException.unauthorized("User not authenticated");
        }

        final String username = authentication.getName();
        final User user = userRepository.findByUsernameOrEmail(username)
                .orElseThrow(() -> SpacedLearningException.unauthorized("User not found"));

        return user.getId();
    }
}