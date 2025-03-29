package com.spacedlearning.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.context.MessageSource;
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
	private final MessageSource messageSource;

	@Override
	@Transactional
//	@CacheEvict(value = "bookModules", key = "#result.bookId")
	public ModuleDetailResponse create(final ModuleCreateRequest request) {
		Objects.requireNonNull(request, "Module create request must not be null");
		log.debug("Creating new module: {}", request);

		// Validate book existence
		final Book book = bookRepository.findById(request.getBookId()).orElseThrow(
				() -> SpacedLearningException.resourceNotFound(messageSource, "resource.book", request.getBookId()));

		// Check if module number already exists
		if (moduleRepository.existsByBookIdAndModuleNo(request.getBookId(), request.getModuleNo())) {
			throw SpacedLearningException.validationError(messageSource, "error.module.duplicate.number",
					request.getModuleNo());
		}

		// Create and save module
		final Module module = moduleMapper.toEntity(request, book);
		final Module savedModule = moduleRepository.save(module);

		log.info("Module created successfully with ID: {}", savedModule.getId());
		return moduleMapper.toDto(savedModule);
	}

	@Override
	@Transactional
//	@CacheEvict(value = { "modules", "bookModules" }, allEntries = true)
	public void delete(final UUID id) {
		Objects.requireNonNull(id, "Module ID must not be null");
		log.debug("Deleting module with ID: {}", id);

		final Module module = moduleRepository.findById(id)
				.orElseThrow(() -> SpacedLearningException.resourceNotFound(messageSource, "resource.module", id));

		module.softDelete(); // Use soft delete
		moduleRepository.save(module);

		log.info("Module soft deleted successfully with ID: {}", id);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<ModuleSummaryResponse> findAll(final Pageable pageable) {
		Objects.requireNonNull(pageable, "Pageable must not be null");
		log.debug("Finding all modules with pagination: {}", pageable);
		return moduleRepository.findAll(pageable).map(moduleMapper::toSummaryDto);
	}

	@Override
	@Transactional(readOnly = true)
//	@Cacheable(value = "bookModules", key = "#bookId")
	public List<ModuleSummaryResponse> findAllByBookId(final UUID bookId) {
		Objects.requireNonNull(bookId, "Book ID must not be null");
		log.debug("Finding all modules by book ID: {}", bookId);

		// Verify book exists
		if (!bookRepository.existsById(bookId)) {
			throw SpacedLearningException.resourceNotFound(messageSource, "resource.book", bookId);
		}

		final List<Module> modules = moduleRepository.findByBookIdOrderByModuleNo(bookId);
		return moduleMapper.toSummaryDtoList(modules);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<ModuleSummaryResponse> findByBookId(final UUID bookId, final Pageable pageable) {
		Objects.requireNonNull(bookId, "Book ID must not be null");
		Objects.requireNonNull(pageable, "Pageable must not be null");
		log.debug("Finding modules by book ID: {}, pageable: {}", bookId, pageable);

		// Verify book exists
		if (!bookRepository.existsById(bookId)) {
			throw SpacedLearningException.resourceNotFound(messageSource, "resource.book", bookId);
		}

		return moduleRepository.findByBookId(bookId, pageable).map(moduleMapper::toSummaryDto);
	}

	@Override
	@Transactional(readOnly = true)
//	@Cacheable(value = "modules", key = "#id")
	public ModuleDetailResponse findById(final UUID id) {
		Objects.requireNonNull(id, "Module ID must not be null");
		log.debug("Finding module by ID: {}", id);

		return moduleRepository.findWithProgressById(id).map(moduleMapper::toDto)
				.orElseThrow(() -> SpacedLearningException.resourceNotFound(messageSource, "resource.module", id));
	}

	@Override
	@Transactional(readOnly = true)
	public Integer getNextModuleNumber(final UUID bookId) {
		Objects.requireNonNull(bookId, "Book ID must not be null");
		log.debug("Getting next module number for book ID: {}", bookId);

		// Verify book exists
		if (!bookRepository.existsById(bookId)) {
			throw SpacedLearningException.resourceNotFound(messageSource, "resource.book", bookId);
		}

		final Integer maxModuleNo = moduleRepository.findMaxModuleNoByBookId(bookId);
		return maxModuleNo + 1;
	}

	@Override
	@Transactional
//	@CacheEvict(value = { "modules", "bookModules" }, allEntries = true)
	public ModuleDetailResponse update(final UUID id, final ModuleUpdateRequest request) {
		Objects.requireNonNull(id, "Module ID must not be null");
		Objects.requireNonNull(request, "Module update request must not be null");
		log.debug("Updating module with ID: {}, request: {}", id, request);

		final Module module = moduleRepository.findById(id)
				.orElseThrow(() -> SpacedLearningException.resourceNotFound(messageSource, "resource.module", id));

		// Check if module number already exists (if being changed)
		if (request.getModuleNo() != null && !request.getModuleNo().equals(module.getModuleNo())
				&& moduleRepository.existsByBookIdAndModuleNo(module.getBook().getId(), request.getModuleNo())) {

			throw SpacedLearningException.validationError(messageSource, "error.module.duplicate.number",
					request.getModuleNo());
		}

		moduleMapper.updateFromDto(request, module);
		final Module updatedModule = moduleRepository.save(module);

		log.info("Module updated successfully with ID: {}", updatedModule.getId());
		return moduleMapper.toDto(updatedModule);
	}
}