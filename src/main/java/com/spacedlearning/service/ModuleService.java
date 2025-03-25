// File: src/main/java/com/spacedlearning/service/ModuleService.java
package com.spacedlearning.service;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.spacedlearning.dto.module.ModuleCreateRequest;
import com.spacedlearning.dto.module.ModuleDetailResponse;
import com.spacedlearning.dto.module.ModuleSummaryResponse;
import com.spacedlearning.dto.module.ModuleUpdateRequest;

/**
 * Service interface for Module operations
 */
public interface ModuleService {

    /**
     * Create a new module
     *
     * @param request Module creation request
     * @return Created module detail response
     */
    ModuleDetailResponse create(ModuleCreateRequest request);

    /**
     * Delete a module
     *
     * @param id Module ID
     */
    void delete(UUID id);

    /**
     * Find all modules with pagination
     *
     * @param pageable Pagination information
     * @return Page of module summaries
     */
    Page<ModuleSummaryResponse> findAll(Pageable pageable);

    /**
     * Find all modules for a book
     *
     * @param bookId Book ID
     * @return List of module summaries
     */
	List<ModuleSummaryResponse> findAllByBookId(UUID bookId);

	/**
	 * Find modules by book ID
	 *
	 * @param bookId   Book ID
	 * @param pageable Pagination information
	 * @return Page of module summaries
	 */
	Page<ModuleSummaryResponse> findByBookId(UUID bookId, Pageable pageable);

	/**
	 * Find module by ID
	 *
	 * @param id Module ID
	 * @return Module detail response
	 */
	ModuleDetailResponse findById(UUID id);

	/**
	 * Get next module number for a book
	 * 
	 * @param bookId Book ID
	 * @return Next available module number
	 */
	Integer getNextModuleNumber(UUID bookId);

	/**
	 * Update a module
	 * 
	 * @param id      Module ID
	 * @param request Module update request
	 * @return Updated module detail response
	 */
	ModuleDetailResponse update(UUID id, ModuleUpdateRequest request);
}
