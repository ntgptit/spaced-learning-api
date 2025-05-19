// GrammarController.java
package com.spacedlearning.controller;

import com.spacedlearning.dto.common.DataResponse;
import com.spacedlearning.dto.common.PageResponse;
import com.spacedlearning.dto.common.SuccessResponse;
import com.spacedlearning.dto.grammar.GrammarCreateRequest;
import com.spacedlearning.dto.grammar.GrammarResponse;
import com.spacedlearning.dto.grammar.GrammarUpdateRequest;
import com.spacedlearning.service.GrammarService;
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

@RestController
@RequestMapping("/api/v1/grammars")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Grammar API", description = "Endpoints for managing grammars")
public class GrammarController {

    private final GrammarService grammarService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create grammar", description = "Creates a new grammar")
    public ResponseEntity<DataResponse<GrammarResponse>> createGrammar(
            @Valid @RequestBody GrammarCreateRequest request) {
        log.debug("REST request to create grammar: {}", request);
        final var createdGrammar = this.grammarService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(DataResponse.of(createdGrammar));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete grammar", description = "Deletes a grammar by ID")
    public ResponseEntity<SuccessResponse> deleteGrammar(@PathVariable UUID id) {
        log.debug("REST request to delete grammar with ID: {}", id);
        this.grammarService.delete(id);
        return ResponseEntity.ok(SuccessResponse.of("Grammar deleted successfully"));
    }

    @GetMapping
    @Operation(summary = "Get all grammars", description = "Retrieves a paginated list of all grammars")
    public ResponseEntity<PageResponse<GrammarResponse>> getAllGrammars(
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("REST request to get all grammars, pageable: {}", pageable);
        final var page = this.grammarService.findAll(pageable);
        return ResponseEntity.ok(PageUtils.createPageResponse(page, pageable));
    }

    @GetMapping("/module/{moduleId}/all")
    @Operation(summary = "Get all grammars by module", description = "Retrieves a list of all grammars for a module")
    public ResponseEntity<DataResponse<List<GrammarResponse>>> getAllGrammarsByModule(
            @PathVariable UUID moduleId) {
        log.debug("REST request to get all grammars by module ID: {}", moduleId);
        final var grammars = this.grammarService.findAllByModuleId(moduleId);
        return ResponseEntity.ok(DataResponse.of(grammars));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get grammar by ID", description = "Retrieves a grammar by its ID")
    public ResponseEntity<DataResponse<GrammarResponse>> getGrammar(@PathVariable UUID id) {
        log.debug("REST request to get grammar with ID: {}", id);
        final var grammar = this.grammarService.findById(id);
        return ResponseEntity.ok(DataResponse.of(grammar));
    }

    @GetMapping("/module/{moduleId}")
    @Operation(summary = "Get grammars by module", description = "Retrieves a paginated list of grammars for a module")
    public ResponseEntity<PageResponse<GrammarResponse>> getGrammarsByModule(
            @PathVariable UUID moduleId,
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("REST request to get grammars by module ID: {}, pageable: {}", moduleId, pageable);
        final var page = this.grammarService.findByModuleId(moduleId, pageable);
        return ResponseEntity.ok(PageUtils.createPageResponse(page, pageable));
    }

    @GetMapping("/search")
    @Operation(summary = "Search grammars", description = "Searches grammars by title")
    public ResponseEntity<PageResponse<GrammarResponse>> searchGrammars(
            @RequestParam String title,
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("REST request to search grammars with title: {}, pageable: {}", title, pageable);
        final var page = this.grammarService.searchByTitle(title, pageable);
        return ResponseEntity.ok(PageUtils.createPageResponse(page, pageable));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update grammar", description = "Updates an existing grammar")
    public ResponseEntity<DataResponse<GrammarResponse>> updateGrammar(
            @PathVariable UUID id,
            @Valid @RequestBody GrammarUpdateRequest request) {
        log.debug("REST request to update grammar with ID: {}, request: {}", id, request);
        final var updatedGrammar = this.grammarService.update(id, request);
        return ResponseEntity.ok(DataResponse.of(updatedGrammar));
    }
}