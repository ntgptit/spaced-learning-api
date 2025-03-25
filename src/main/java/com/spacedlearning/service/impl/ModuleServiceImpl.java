// File: src/main/java/com/spacedlearning/service/impl/ModuleServiceImpl.java
package com.spacedlearning.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spacedlearning.dto.module.ModuleCreateRequest;
import com.spacedlearning.dto.module.ModuleDetailResponse;
import com.spacedlearning.dto.module.ModuleSummaryResponse;
import com.spacedlearning.dto.module.ModuleUpdateRequest;
import com.spacedlearning.entity.Book;
import com.spacedlearning.entity.Module;
import com.spacedlearning.exception.SpacedLearningException;
import com.spacedlearning.mapper.ModuleMapper;
import com.spacedlearning.repository.BookRepository;
import com.spacedlearning.repository.ModuleRepository;
import com.spacedlearning.service.ModuleService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of ModuleService
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ModuleServiceImpl implements ModuleService {

	private final ModuleRepository moduleRepository;
	private final BookRepository bookRepository;
	private final ModuleMapper moduleMapper;

	@Override
	@Transactional
	@CacheEvict(value = "bookModules", key = "#result.bookId")
	public ModuleDetailResponse create(ModuleCreateRequest request) {
		log.debug("Creating new module: {}", request);

		// Validate book existence
		final Book book = bookRepository.findById(request.getBookId())
				.orElseThrow(() -> SpacedLearningException.resourceNotFound("Book", request.getBookId()));

		// Check if module number already exists
		if (moduleRepository.existsByBookIdAndModuleNo(request.getBookId(), request.getModuleNo())) {
			throw SpacedLearningException
					.validationError("Module with number " + request.getModuleNo() + " already exists for this book");
		}

		// Create and save module
		final Module module = moduleMapper.toEntity(request, book);
		final Module savedModule = moduleRepository.save(module);

		log.info("Module created successfully with ID: {}", savedModule.getId());
		return moduleMapper.toDto(savedModule);
	}

	@Override
	@Transactional
	@CacheEvict(value = { "modules", "bookModules" }, allEntries = true)
	public void delete(UUID id) {
		log.debug("Deleting module with ID: {}", id);

		final Module module = moduleRepository.findById(id)
				.orElseThrow(() -> SpacedLearningException.resourceNotFound("Module", id));

		module.softDelete(); // Use soft delete
		moduleRepository.save(module);

		log.info("Module soft deleted successfully with ID: {}", id);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<ModuleSummaryResponse> findAll(Pageable pageable) {
		log.debug("Finding all modules with pagination: {}", pageable);
		return moduleRepository.findAll(pageable).map(moduleMapper::toSummaryDto);
	}

	@Override
	@Transactional(readOnly = true)
	@Cacheable(value = "bookModules", key = "#bookId")
	public List<ModuleSummaryResponse> findAllByBookId(UUID bookId) {
		log.debug("Finding all modules by book ID: {}", bookId);
		// Verify book exists
		if (!bookRepository.existsById(bookId)) {
			throw SpacedLearningException.resourceNotFound("Book", bookId);
		}

		final List<Module> modules = moduleRepository.findByBookIdOrderByModuleNo(bookId);
		return moduleMapper.toSummaryDtoList(modules);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<ModuleSummaryResponse> findByBookId(UUID bookId, Pageable pageable) {
		log.debug("Finding modules by book ID: {}, pageable: {}", bookId, pageable);
		// Verify book exists
		if (!bookRepository.existsById(bookId)) {
			throw SpacedLearningException.resourceNotFound("Book", bookId);
		}

		return moduleRepository.findByBookId(bookId, pageable).map(moduleMapper::toSummaryDto);
	}

	@Override
	@Transactional(readOnly = true)
	@Cacheable(value = "modules", key = "#id")
	public ModuleDetailResponse findById(UUID id) {
		log.debug("Finding module by ID: {}", id);
		final Module module = moduleRepository.findWithProgressById(id)
				.orElseThrow(() -> SpacedLearningException.resourceNotFound("Module", id));
		return moduleMapper.toDto(module);
	}

	@Override
	@Transactional(readOnly = true)
	public Integer getNextModuleNumber(UUID bookId) {
		log.debug("Getting next module number for book ID: {}", bookId);

		// Verify book exists
		if (!bookRepository.existsById(bookId)) {
			throw SpacedLearningException.resourceNotFound("Book", bookId);
		}

		final Integer maxModuleNo = moduleRepository.findMaxModuleNoByBookId(bookId);
		return maxModuleNo + 1;
	}

	@Override
	@Transactional
	@CacheEvict(value = { "modules", "bookModules" }, allEntries = true)
	public ModuleDetailResponse update(UUID id, ModuleUpdateRequest request) {
		log.debug("Updating module with ID: {}, request: {}", id, request);

		final Module module = moduleRepository.findById(id)
				.orElseThrow(() -> SpacedLearningException.resourceNotFound("Module", id));

		// Check if module number already exists (if being changed)
		if ((request.getModuleNo() != null && !request.getModuleNo().equals(module.getModuleNo()))
				&& moduleRepository.existsByBookIdAndModuleNo(module.getBook().getId(), request.getModuleNo())) {
			throw SpacedLearningException
					.validationError("Module with number " + request.getModuleNo() + " already exists for this book");
		}

		moduleMapper.updateFromDto(request, module);
		final Module updatedModule = moduleRepository.save(module);

		log.info("Module updated successfully with ID: {}", updatedModule.getId());
		return moduleMapper.toDto(updatedModule);
	}
}
