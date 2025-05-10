package com.spacedlearning.service;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.spacedlearning.dto.progress.ModuleProgressCreateRequest;
import com.spacedlearning.dto.progress.ModuleProgressDetailResponse;
import com.spacedlearning.dto.progress.ModuleProgressSummaryResponse;
import com.spacedlearning.dto.progress.ModuleProgressUpdateRequest;

/**
 * Service interface for ModuleProgress operations
 */
public interface ModuleProgressService {

    /**
     * Create a new progress record
     *
     * @param request Progress creation request
     * @return Created progress detail response
     */
    ModuleProgressDetailResponse create(ModuleProgressCreateRequest request);

    /**
     * Delete a progress record
     *
     * @param id Progress ID
     */
    void delete(UUID id);

    /**
     * Find all progress records with pagination
     *
     * @param pageable Pagination information
     * @return Page of progress summaries
     */
    Page<ModuleProgressSummaryResponse> findAll(Pageable pageable);

    /**
     * Find progress by book ID
     *
     * @param bookId   Book ID
     * @param pageable Pagination information
     * @return Page of progress summaries
     */
    Page<ModuleProgressSummaryResponse> findByBookId(UUID bookId, Pageable pageable);

    /**
     * Find progress by ID
     *
     * @param id Progress ID
     * @return Progress detail response
     */
    ModuleProgressDetailResponse findById(UUID id);

    /**
     * Find progress by module ID
     *
     * @param moduleId Module ID
     * @return Progress detail response
     */
    ModuleProgressDetailResponse findByModuleId(UUID moduleId);

    /**
     * Find progress by module ID
     *
     * @param moduleId Module ID
     * @param pageable Pagination information
     * @return Page of progress summaries
     */
    Page<ModuleProgressSummaryResponse> findByModuleId(UUID moduleId, Pageable pageable);

    /**
     * Find progress records due for study
     *
     * @param studyDate Date to study on or before
     * @param pageable  Pagination information
     * @return Page of progress summaries
     */
    Page<ModuleProgressDetailResponse> findDueForStudy(LocalDate studyDate, Pageable pageable);

    /**
     * Find progress for a module or create a new one if it doesn't exist
     *
     * @param moduleId Module ID
     * @return Progress detail response
     */
    ModuleProgressDetailResponse findOrCreateProgressForModule(UUID moduleId);

    /**
     * Update a progress record
     *
     * @param id      Progress ID
     * @param request Progress update request
     * @return Updated progress detail response
     */
    ModuleProgressDetailResponse update(UUID id, ModuleProgressUpdateRequest request);
}