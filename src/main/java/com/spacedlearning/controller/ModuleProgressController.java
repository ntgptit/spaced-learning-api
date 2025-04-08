package com.spacedlearning.controller;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.spacedlearning.dto.common.DataResponse;
import com.spacedlearning.dto.common.PageResponse;
import com.spacedlearning.dto.common.SuccessResponse;
import com.spacedlearning.dto.progress.ModuleProgressCreateRequest;
import com.spacedlearning.dto.progress.ModuleProgressDetailResponse;
import com.spacedlearning.dto.progress.ModuleProgressSummaryResponse;
import com.spacedlearning.dto.progress.ModuleProgressUpdateRequest;
import com.spacedlearning.service.ModuleProgressService;
import com.spacedlearning.util.PageUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for ModuleProgress operations
 */
@RestController
@RequestMapping("/api/v1/progress")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Module Progress API", description = "Endpoints for managing module progress")
public class ModuleProgressController {

    private final ModuleProgressService progressService;

    @PostMapping
    @Operation(summary = "Create progress", description = "Creates a new progress record")
    public ResponseEntity<DataResponse<ModuleProgressDetailResponse>> createProgress(
            @Valid @RequestBody ModuleProgressCreateRequest request) {
        log.debug("REST request to create progress: {}", request);
        final ModuleProgressDetailResponse createdProgress = progressService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(DataResponse.of(createdProgress));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete progress", description = "Deletes a progress record by ID")
    public ResponseEntity<SuccessResponse> deleteProgress(@PathVariable UUID id) {
        log.debug("REST request to delete progress with ID: {}", id);
        progressService.delete(id);
        return ResponseEntity.ok(SuccessResponse.of("Progress deleted successfully"));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all progress records", description = "Retrieves a paginated list of all progress records")
    public ResponseEntity<PageResponse<ModuleProgressSummaryResponse>> getAllProgress(
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("REST request to get all progress records, pageable: {}", pageable);
        final Page<ModuleProgressSummaryResponse> page = progressService.findAll(pageable);
        return ResponseEntity.ok(PageUtils.createPageResponse(page, pageable));
    }

    @GetMapping("/due")
    @Operation(summary = "Get due progress records", description = "Retrieves a paginated list of progress records due for study")
    public ResponseEntity<PageResponse<ModuleProgressSummaryResponse>> getDueProgress(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate studyDate,
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("REST request to get due progress records on date: {}, pageable: {}", studyDate, pageable);
        final Page<ModuleProgressSummaryResponse> page = progressService.findDueForStudy(studyDate, pageable);
        return ResponseEntity.ok(PageUtils.createPageResponse(page, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get progress by ID", description = "Retrieves a progress record by its ID with detailed information")
    public ResponseEntity<DataResponse<ModuleProgressDetailResponse>> getProgress(@PathVariable UUID id) {
        log.debug("REST request to get progress with ID: {}", id);
        final ModuleProgressDetailResponse progress = progressService.findById(id);
        return ResponseEntity.ok(DataResponse.of(progress));
    }

    @GetMapping("/module/{moduleId}")
    @Operation(summary = "Get progress by module ID", description = "Retrieves a paginated list of progress records for a module")
    public ResponseEntity<PageResponse<ModuleProgressSummaryResponse>> getProgressByModuleId(
            @PathVariable UUID moduleId, @PageableDefault(size = 20) Pageable pageable) {
        log.debug("REST request to get progress by module ID: {}, pageable: {}", moduleId, pageable);
        final Page<ModuleProgressSummaryResponse> page = progressService.findByModuleId(moduleId, pageable);
        return ResponseEntity.ok(PageUtils.createPageResponse(page, pageable));
    }

    @GetMapping("/book/{bookId}")
    @Operation(summary = "Get progress by book", description = "Retrieves a paginated list of progress records for a book")
    public ResponseEntity<PageResponse<ModuleProgressSummaryResponse>> getProgressByBook(
            @PathVariable UUID bookId, @PageableDefault(size = 20) Pageable pageable) {
        log.debug("REST request to get progress by book ID: {}, pageable: {}", bookId, pageable);
        final Page<ModuleProgressSummaryResponse> page = progressService.findByBookId(bookId, pageable);
        return ResponseEntity.ok(PageUtils.createPageResponse(page, pageable));
    }

    @GetMapping("/module/{moduleId}/detail")
    @Operation(summary = "Get detailed progress by module ID", description = "Retrieves a detailed progress record for a specific module")
    public ResponseEntity<DataResponse<ModuleProgressDetailResponse>> getProgressDetailByModule(
            @PathVariable UUID moduleId) {
        log.debug("REST request to get detailed progress for module ID: {}", moduleId);
        final ModuleProgressDetailResponse progress = progressService.findByModuleId(moduleId);
        return ResponseEntity.ok(DataResponse.of(progress));
    }

    @GetMapping("/module/{moduleId}/find-or-create")
    @Operation(summary = "Find or create progress for module", description = "Finds existing progress for a module or creates a new one if it doesn't exist")
    public ResponseEntity<DataResponse<ModuleProgressDetailResponse>> findOrCreateProgressForModule(
            @PathVariable UUID moduleId) {
        log.debug("REST request to find or create progress for module ID: {}", moduleId);
        final ModuleProgressDetailResponse progress = progressService.findOrCreateProgressForModule(moduleId);
        return ResponseEntity.ok(DataResponse.of(progress));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update progress", description = "Updates an existing progress record")
    public ResponseEntity<DataResponse<ModuleProgressDetailResponse>> updateProgress(@PathVariable UUID id,
            @Valid @RequestBody ModuleProgressUpdateRequest request) {
        log.debug("REST request to update progress with ID: {}, request: {}", id, request);
        final ModuleProgressDetailResponse updatedProgress = progressService.update(id, request);
        return ResponseEntity.ok(DataResponse.of(updatedProgress));
    }
}