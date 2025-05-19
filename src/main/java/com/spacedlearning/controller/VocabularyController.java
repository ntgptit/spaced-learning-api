// VocabularyController.java
package com.spacedlearning.controller;

import com.spacedlearning.dto.common.DataResponse;
import com.spacedlearning.dto.common.PageResponse;
import com.spacedlearning.dto.common.SuccessResponse;
import com.spacedlearning.dto.vocabulary.VocabularyCreateRequest;
import com.spacedlearning.dto.vocabulary.VocabularyResponse;
import com.spacedlearning.dto.vocabulary.VocabularyUpdateRequest;
import com.spacedlearning.service.VocabularyService;
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
@RequestMapping("/api/v1/vocabularies")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Vocabulary API", description = "Endpoints for managing vocabularies")
public class VocabularyController {

    private final VocabularyService vocabularyService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create vocabulary", description = "Creates a new vocabulary")
    public ResponseEntity<DataResponse<VocabularyResponse>> createVocabulary(
            @Valid @RequestBody VocabularyCreateRequest request) {
        log.debug("REST request to create vocabulary: {}", request);
        final var createdVocabulary = this.vocabularyService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(DataResponse.of(createdVocabulary));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete vocabulary", description = "Deletes a vocabulary by ID")
    public ResponseEntity<SuccessResponse> deleteVocabulary(@PathVariable UUID id) {
        log.debug("REST request to delete vocabulary with ID: {}", id);
        this.vocabularyService.delete(id);
        return ResponseEntity.ok(SuccessResponse.of("Vocabulary deleted successfully"));
    }

    @GetMapping
    @Operation(summary = "Get all vocabularies", description = "Retrieves a paginated list of all vocabularies")
    public ResponseEntity<PageResponse<VocabularyResponse>> getAllVocabularies(
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("REST request to get all vocabularies, pageable: {}", pageable);
        final var page = this.vocabularyService.findAll(pageable);
        return ResponseEntity.ok(PageUtils.createPageResponse(page, pageable));
    }

    @GetMapping("/module/{moduleId}/all")
    @Operation(summary = "Get all vocabularies by module", description = "Retrieves a list of all vocabularies for a module")
    public ResponseEntity<DataResponse<List<VocabularyResponse>>> getAllVocabulariesByModule(
            @PathVariable UUID moduleId) {
        log.debug("REST request to get all vocabularies by module ID: {}", moduleId);
        final var vocabularies = this.vocabularyService.findAllByModuleId(moduleId);
        return ResponseEntity.ok(DataResponse.of(vocabularies));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get vocabulary by ID", description = "Retrieves a vocabulary by its ID")
    public ResponseEntity<DataResponse<VocabularyResponse>> getVocabulary(@PathVariable UUID id) {
        log.debug("REST request to get vocabulary with ID: {}", id);
        final var vocabulary = this.vocabularyService.findById(id);
        return ResponseEntity.ok(DataResponse.of(vocabulary));
    }

    @GetMapping("/module/{moduleId}")
    @Operation(summary = "Get vocabularies by module", description = "Retrieves a paginated list of vocabularies for a module")
    public ResponseEntity<PageResponse<VocabularyResponse>> getVocabulariesByModule(
            @PathVariable UUID moduleId,
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("REST request to get vocabularies by module ID: {}, pageable: {}", moduleId, pageable);
        final var page = this.vocabularyService.findByModuleId(moduleId, pageable);
        return ResponseEntity.ok(PageUtils.createPageResponse(page, pageable));
    }

    @GetMapping("/search")
    @Operation(summary = "Search vocabularies", description = "Searches vocabularies by term")
    public ResponseEntity<PageResponse<VocabularyResponse>> searchVocabularies(
            @RequestParam String term,
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("REST request to search vocabularies with term: {}, pageable: {}", term, pageable);
        final var page = this.vocabularyService.searchByTerm(term, pageable);
        return ResponseEntity.ok(PageUtils.createPageResponse(page, pageable));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update vocabulary", description = "Updates an existing vocabulary")
    public ResponseEntity<DataResponse<VocabularyResponse>> updateVocabulary(
            @PathVariable UUID id,
            @Valid @RequestBody VocabularyUpdateRequest request) {
        log.debug("REST request to update vocabulary with ID: {}, request: {}", id, request);
        final var updatedVocabulary = this.vocabularyService.update(id, request);
        return ResponseEntity.ok(DataResponse.of(updatedVocabulary));
    }
}