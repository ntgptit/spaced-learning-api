package com.spacedlearning.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.spacedlearning.dto.common.DataResponse;
import com.spacedlearning.dto.learning.BookStatsResponse;
import com.spacedlearning.dto.learning.DashboardStatsResponse;
import com.spacedlearning.dto.learning.LearningModuleResponse;
import com.spacedlearning.service.LearningProgressService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/learning")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Learning Progress API", description = "Endpoints for learning progress data")
public class LearningProgressController {

    private final LearningProgressService learningProgressService;

    @GetMapping("/dashboard-stats")
    @Operation(summary = "Get dashboard stats", description = "Retrieves learning statistics and modules for dashboard")
    public ResponseEntity<DataResponse<DashboardStatsResponse>> getDashboardStats(
            @RequestParam(required = false) String book,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        log.debug("REST request to get dashboard stats, book filter: {}, date filter: {}", book, date);
        final DashboardStatsResponse stats = learningProgressService.getDashboardStats(book, date);
        return ResponseEntity.ok(DataResponse.of(stats));
    }

    @GetMapping("/modules")
    @Operation(summary = "Get all learning modules", description = "Retrieves all learning modules for the current user")
    public ResponseEntity<DataResponse<List<LearningModuleResponse>>> getAllModules() {
        log.debug("REST request to get all learning modules");
        final List<LearningModuleResponse> modules = learningProgressService.getAllModules();
        return ResponseEntity.ok(DataResponse.of(modules));
    }

    @GetMapping("/modules/due")
    @Operation(summary = "Get due modules", description = "Retrieves modules due for review within a specified threshold")
    public ResponseEntity<DataResponse<List<LearningModuleResponse>>> getDueModules(
            @RequestParam(defaultValue = "7") int daysThreshold) {

        log.debug("REST request to get due modules with threshold: {} days", daysThreshold);
        final List<LearningModuleResponse> modules = learningProgressService.getDueModules(daysThreshold);
        return ResponseEntity.ok(DataResponse.of(modules));
    }

    @GetMapping("/modules/completed")
    @Operation(summary = "Get completed modules", description = "Retrieves completed learning modules")
    public ResponseEntity<DataResponse<List<LearningModuleResponse>>> getCompletedModules() {
        log.debug("REST request to get completed modules");
        final List<LearningModuleResponse> modules = learningProgressService.getCompletedModules();
        return ResponseEntity.ok(DataResponse.of(modules));
    }

    @GetMapping("/books")
    @Operation(summary = "Get unique books", description = "Retrieves a list of unique books")
    public ResponseEntity<DataResponse<List<String>>> getUniqueBooks() {
        log.debug("REST request to get unique books");
        final List<String> books = learningProgressService.getUniqueBooks();
        return ResponseEntity.ok(DataResponse.of(books));
    }

    @GetMapping("/books/{book}/stats")
    @Operation(summary = "Get book stats", description = "Retrieves statistics for a specific book")
    public ResponseEntity<DataResponse<BookStatsResponse>> getBookStats(
            @PathVariable String book) {

        log.debug("REST request to get stats for book: {}", book);
        final BookStatsResponse stats = learningProgressService.getBookStats(book);
        return ResponseEntity.ok(DataResponse.of(stats));
    }

    @PostMapping("/export")
    @Operation(summary = "Export learning data", description = "Exports learning data to a file")
    public ResponseEntity<DataResponse<Map<String, String>>> exportData() {
        log.debug("REST request to export learning data");
        final Map<String, String> result = learningProgressService.exportData();
        return ResponseEntity.ok(DataResponse.of(result));
    }
}