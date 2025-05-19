// File: src/main/java/com/spacedlearning/controller/ModuleController.java
package com.spacedlearning.controller;

import com.spacedlearning.dto.common.DataResponse;
import com.spacedlearning.dto.common.PageResponse;
import com.spacedlearning.dto.common.SuccessResponse;
import com.spacedlearning.dto.module.ModuleCreateRequest;
import com.spacedlearning.dto.module.ModuleDetailResponse;
import com.spacedlearning.dto.module.ModuleSummaryResponse;
import com.spacedlearning.dto.module.ModuleUpdateRequest;
import com.spacedlearning.service.ModuleService;
import com.spacedlearning.util.PageUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for Module operations
 */
@RestController
@RequestMapping("/api/v1/modules")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Module API", description = "Endpoints for managing modules")
public class ModuleController {

    private final ModuleService moduleService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create module", description = "Creates a new module")
    public ResponseEntity<DataResponse<ModuleDetailResponse>> createModule(
            @Valid @RequestBody ModuleCreateRequest request) {
        log.debug("REST request to create module: {}", request);
        final var createdModule = this.moduleService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(DataResponse.of(createdModule));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete module", description = "Deletes a module by ID")
    public ResponseEntity<SuccessResponse> deleteModule(@PathVariable UUID id) {
        log.debug("REST request to delete module with ID: {}", id);
        this.moduleService.delete(id);
        return ResponseEntity.ok(SuccessResponse.of("Module deleted successfully"));
    }

    @GetMapping
    @Operation(summary = "Get all modules", description = "Retrieves a paginated list of all modules")
    public ResponseEntity<PageResponse<ModuleSummaryResponse>> getAllModules(
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("REST request to get all modules, pageable: {}", pageable);
        final var page = this.moduleService.findAll(pageable);
        return ResponseEntity.ok(PageUtils.createPageResponse(page, pageable));
    }

    @GetMapping("/book/{bookId}/all")
    @Operation(summary = "Get all modules by book ID", description = "Retrieves a list of all modules for a book")
    public ResponseEntity<DataResponse<List<ModuleSummaryResponse>>> getAllModulesByBookId(@PathVariable UUID bookId) {
        log.debug("REST request to get all modules by book ID: {}", bookId);
        final var modules = this.moduleService.findAllByBookId(bookId);
        return ResponseEntity.ok(DataResponse.of(modules));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get module by ID", description = "Retrieves a module by its ID with detailed information")
    public ResponseEntity<DataResponse<ModuleDetailResponse>> getModule(@PathVariable UUID id) {
        log.debug("REST request to get module with ID: {}", id);
        final var module = this.moduleService.findById(id);
        return ResponseEntity.ok(DataResponse.of(module));
    }

    @GetMapping("/book/{bookId}")
    @Operation(summary = "Get modules by book ID", description = "Retrieves a paginated list of modules for a book")
    public ResponseEntity<PageResponse<ModuleSummaryResponse>> getModulesByBookId(@PathVariable UUID bookId,
                                                                                  @PageableDefault(size = 30) Pageable pageable) {
        log.debug("REST request to get modules by book ID: {}, pageable: {}", bookId, pageable);
        final var page = this.moduleService.findByBookId(bookId, pageable);
        return ResponseEntity.ok(PageUtils.createPageResponse(page, pageable));
    }

    @GetMapping("/book/{bookId}/next-number")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get next module number", description = "Gets the next available module number for a book")
    public ResponseEntity<DataResponse<Integer>> getNextModuleNumber(@PathVariable UUID bookId) {
        log.debug("REST request to get next module number for book ID: {}", bookId);
        final var nextNumber = this.moduleService.getNextModuleNumber(bookId);
        return ResponseEntity.ok(DataResponse.of(nextNumber));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update module", description = "Updates an existing module")
    public ResponseEntity<DataResponse<ModuleDetailResponse>> updateModule(@PathVariable UUID id,
                                                                           @Valid @RequestBody ModuleUpdateRequest request) {
        log.debug("REST request to update module with ID: {}, request: {}", id, request);
        final var updatedModule = this.moduleService.update(id, request);
        return ResponseEntity.ok(DataResponse.of(updatedModule));
    }

    // ModuleController.java (bổ sung các endpoints)
    @GetMapping("/{id}/vocabulary-count")
    @Operation(summary = "Get vocabulary count", description = "Gets the vocabulary count for a module")
    public ResponseEntity<DataResponse<Integer>> getVocabularyCount(@PathVariable UUID id) {
        log.debug("REST request to get vocabulary count for module ID: {}", id);
        final var count = this.moduleService.getVocabularyCount(id);
        return ResponseEntity.ok(DataResponse.of(count));
    }

    @GetMapping("/{id}/grammar-count")
    @Operation(summary = "Get grammar count", description = "Gets the grammar count for a module")
    public ResponseEntity<DataResponse<Integer>> getGrammarCount(@PathVariable UUID id) {
        log.debug("REST request to get grammar count for module ID: {}", id);
        final var count = this.moduleService.getGrammarCount(id);
        return ResponseEntity.ok(DataResponse.of(count));
    }
}