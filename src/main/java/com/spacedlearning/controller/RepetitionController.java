// File: src/main/java/com/spacedlearning/controller/RepetitionController.java
package com.spacedlearning.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import com.spacedlearning.dto.repetition.RepetitionCreateRequest;
import com.spacedlearning.dto.repetition.RepetitionResponse;
import com.spacedlearning.dto.repetition.RepetitionUpdateRequest;
import com.spacedlearning.entity.enums.RepetitionOrder;
import com.spacedlearning.entity.enums.RepetitionStatus;
import com.spacedlearning.service.RepetitionService;
import com.spacedlearning.util.PageUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for Repetition operations
 */
@RestController
@RequestMapping("/api/v1/repetitions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Repetition API", description = "Endpoints for managing repetitions")
public class RepetitionController {

    private final RepetitionService repetitionService;

    @PostMapping("/progress/{progressId}/schedule")
    @Operation(summary = "Create default schedule", description = "Creates a default repetition schedule for a progress record")
    public ResponseEntity<DataResponse<List<RepetitionResponse>>> createDefaultSchedule(@PathVariable UUID progressId) {
        log.debug("REST request to create default repetition schedule for progress ID: {}", progressId);
        final List<RepetitionResponse> schedule = repetitionService.createDefaultSchedule(progressId);
        return ResponseEntity.status(HttpStatus.CREATED).body(DataResponse.of(schedule));
    }

    @PostMapping
    @Operation(summary = "Create repetition", description = "Creates a new repetition")
    public ResponseEntity<DataResponse<RepetitionResponse>> createRepetition(
            @Valid @RequestBody RepetitionCreateRequest request) {
        log.debug("REST request to create repetition: {}", request);
        final RepetitionResponse createdRepetition = repetitionService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(DataResponse.of(createdRepetition));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete repetition", description = "Deletes a repetition by ID")
    public ResponseEntity<SuccessResponse> deleteRepetition(@PathVariable UUID id) {
        log.debug("REST request to delete repetition with ID: {}", id);
        repetitionService.delete(id);
        return ResponseEntity.ok(SuccessResponse.of("Repetition deleted successfully"));
    }

    @GetMapping
    @Operation(summary = "Get all repetitions", description = "Retrieves a paginated list of all repetitions")
    public ResponseEntity<PageResponse<RepetitionResponse>> getAllRepetitions(
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("REST request to get all repetitions, pageable: {}", pageable);
        final Page<RepetitionResponse> page = repetitionService.findAll(pageable);
        return ResponseEntity.ok(PageUtils.createPageResponse(page, pageable));
    }

    @GetMapping("/user/{userId}/due")
    @Operation(summary = "Get due repetitions", description = "Retrieves a paginated list of repetitions due for review")
    public ResponseEntity<PageResponse<RepetitionResponse>> getDueRepetitions(@PathVariable UUID userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate reviewDate,
            @RequestParam(required = false) RepetitionStatus status, @PageableDefault(size = 20) Pageable pageable) {
        log.debug("REST request to get due repetitions for user ID: {} on date: {}, status: {}, pageable: {}", userId,
                reviewDate, status, pageable);
        final Page<RepetitionResponse> page = repetitionService.findDueRepetitions(userId, reviewDate, status,
                pageable);
        return ResponseEntity.ok(PageUtils.createPageResponse(page, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get repetition by ID", description = "Retrieves a repetition by its ID")
    public ResponseEntity<DataResponse<RepetitionResponse>> getRepetition(@PathVariable UUID id) {
        log.debug("REST request to get repetition with ID: {}", id);
        final RepetitionResponse repetition = repetitionService.findById(id);
        return ResponseEntity.ok(DataResponse.of(repetition));
    }

    @GetMapping("/progress/{progressId}/order/{order}")
    @Operation(summary = "Get repetition by progress ID and order", description = "Retrieves a repetition for a specific progress record and order")
    public ResponseEntity<DataResponse<RepetitionResponse>> getRepetitionByProgressIdAndOrder(
            @PathVariable UUID progressId, @PathVariable RepetitionOrder order) {
        log.debug("REST request to get repetition by progress ID: {} and order: {}", progressId, order);
        final RepetitionResponse repetition = repetitionService.findByModuleProgressIdAndOrder(progressId, order);
        return ResponseEntity.ok(DataResponse.of(repetition));
    }

    @GetMapping("/progress/{progressId}")
    @Operation(summary = "Get repetitions by progress ID", description = "Retrieves a list of repetitions for a progress record")
    public ResponseEntity<DataResponse<List<RepetitionResponse>>> getRepetitionsByProgressId(
            @PathVariable UUID progressId) {
        log.debug("REST request to get repetitions by progress ID: {}", progressId);
        final List<RepetitionResponse> repetitions = repetitionService.findByModuleProgressId(progressId);
        return ResponseEntity.ok(DataResponse.of(repetitions));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update repetition", description = "Updates an existing repetition")
    public ResponseEntity<DataResponse<RepetitionResponse>> updateRepetition(@PathVariable UUID id,
            @Valid @RequestBody RepetitionUpdateRequest request) {
        log.debug("REST request to update repetition with ID: {}, request: {}", id, request);
        final RepetitionResponse updatedRepetition = repetitionService.update(id, request);
        return ResponseEntity.ok(DataResponse.of(updatedRepetition));
    }
}