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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
import com.spacedlearning.entity.User;
import com.spacedlearning.exception.SpacedLearningException;
import com.spacedlearning.repository.UserRepository;
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
    private final UserRepository userRepository;

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

    @GetMapping("/user/{userId}/due")
    @Operation(summary = "Get due progress records", description = "Retrieves a paginated list of progress records due for study")
    public ResponseEntity<PageResponse<ModuleProgressSummaryResponse>> getDueProgress(@PathVariable UUID userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate studyDate,
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("REST request to get due progress records for user ID: {} on date: {}, pageable: {}", userId,
                studyDate, pageable);
        final Page<ModuleProgressSummaryResponse> page = progressService.findDueForStudy(userId, studyDate, pageable);
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
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get progress by module ID", description = "Retrieves a paginated list of progress records for a module")
    public ResponseEntity<PageResponse<ModuleProgressSummaryResponse>> getProgressByModuleId(
            @PathVariable UUID moduleId, @PageableDefault(size = 20) Pageable pageable) {
        log.debug("REST request to get progress by module ID: {}, pageable: {}", moduleId, pageable);
        final Page<ModuleProgressSummaryResponse> page = progressService.findByModuleId(moduleId, pageable);
        return ResponseEntity.ok(PageUtils.createPageResponse(page, pageable));
    }

    @GetMapping("/user/{userId}/book/{bookId}")
    @Operation(summary = "Get progress by user and book", description = "Retrieves a paginated list of progress records for a user and book")
    public ResponseEntity<PageResponse<ModuleProgressSummaryResponse>> getProgressByUserAndBook(
            @PathVariable UUID userId, @PathVariable UUID bookId, @PageableDefault(size = 20) Pageable pageable) {
        log.debug("REST request to get progress by user ID: {} and book ID: {}, pageable: {}", userId, bookId,
                pageable);
        final Page<ModuleProgressSummaryResponse> page = progressService.findByUserIdAndBookId(userId, bookId,
                pageable);
        return ResponseEntity.ok(PageUtils.createPageResponse(page, pageable));
    }

    @GetMapping("/user/{userId}/module/{moduleId}")
    @Operation(summary = "Get progress by user and module", description = "Retrieves a progress record for a specific user and module")
    public ResponseEntity<DataResponse<ModuleProgressDetailResponse>> getProgressByUserAndModule(
            @PathVariable UUID userId, @PathVariable UUID moduleId) {
        log.debug("REST request to get progress by user ID: {} and module ID: {}", userId, moduleId);
        final ModuleProgressDetailResponse progress = progressService.findByUserIdAndModuleId(userId, moduleId);
        return ResponseEntity.ok(DataResponse.of(progress));
    }

    @GetMapping("/user/current/module/{moduleId}")
    @Operation(summary = "Get progress for current user and module", description = "Retrieves a progress record for the current authenticated user and module")
    public ResponseEntity<DataResponse<ModuleProgressDetailResponse>> getCurrentUserProgressByModuleId(
            @PathVariable UUID moduleId) {
        log.debug("REST request to get progress for current user and module ID: {}", moduleId);

        // Lấy thông tin xác thực hiện tại
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Kiểm tra nếu authentication là null hoặc không được xác thực
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getName())) {
            log.error("User not authenticated when accessing progress for module ID: {}", moduleId);
            throw SpacedLearningException.unauthorized("User not authenticated");
        }

        // Lấy email của người dùng từ thông tin xác thực
        final String userEmail = authentication.getName();
        log.debug("Getting progress for authenticated user: {} and module ID: {}", userEmail, moduleId);

        // Tìm thông tin người dùng từ email
        final User user = userRepository.findByUsernameOrEmail(userEmail).orElseThrow(() -> {
            log.error("User with email {} not found in database", userEmail);
            return SpacedLearningException.unauthorized("User not found in database");
        });

        // Tìm progress cho user và module
        try {
            final ModuleProgressDetailResponse progress = progressService.findByUserIdAndModuleId(user.getId(),
                    moduleId);
            return ResponseEntity.ok(DataResponse.of(progress));
        } catch (final Exception e) {
            log.error("Error finding progress for user ID: {} and module ID: {}: {}", user.getId(), moduleId,
                    e.getMessage());

            // Xử lý trường hợp không tìm thấy progress
            if (e instanceof SpacedLearningException && e.getMessage().contains("not found")) {
                return ResponseEntity.ok(DataResponse.of(null));
            }

            // Rethrow if it's a different kind of exception
            throw e;
        }
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get progress by user ID", description = "Retrieves a paginated list of progress records for a user")
    public ResponseEntity<PageResponse<ModuleProgressSummaryResponse>> getProgressByUserId(@PathVariable UUID userId,
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("REST request to get progress by user ID: {}, pageable: {}", userId, pageable);
        final Page<ModuleProgressSummaryResponse> page = progressService.findByUserId(userId, pageable);
        return ResponseEntity.ok(PageUtils.createPageResponse(page, pageable));
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