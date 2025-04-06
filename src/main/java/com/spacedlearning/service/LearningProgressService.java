package com.spacedlearning.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.spacedlearning.dto.learning.BookStatsResponse;
import com.spacedlearning.dto.learning.DashboardStatsResponse;
import com.spacedlearning.dto.learning.LearningModuleResponse;

/**
 * Service interface for Learning Progress operations
 */
public interface LearningProgressService {

    /**
     * Get dashboard statistics including modules
     *
     * @param book Optional book filter
     * @param date Optional date filter
     * @return Dashboard statistics response
     */
    DashboardStatsResponse getDashboardStats(String book, LocalDate date);

    /**
     * Get all learning modules for current user
     *
     * @return List of learning modules
     */
    List<LearningModuleResponse> getAllModules();

    /**
     * Get modules due for review within threshold
     *
     * @param daysThreshold Number of days threshold
     * @return List of due modules
     */
    List<LearningModuleResponse> getDueModules(int daysThreshold);

    /**
     * Get completed modules
     *
     * @return List of completed modules
     */
    List<LearningModuleResponse> getCompletedModules();

    /**
     * Get list of unique books
     *
     * @return List of unique book names
     */
    List<String> getUniqueBooks();

    /**
     * Get statistics for a specific book
     *
     * @param book Book name
     * @return Book statistics
     */
    BookStatsResponse getBookStats(String book);

    /**
     * Export learning data
     *
     * @return Result information
     */
    Map<String, String> exportData();
}