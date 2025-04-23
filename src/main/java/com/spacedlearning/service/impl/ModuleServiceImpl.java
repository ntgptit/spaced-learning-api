package com.spacedlearning.service.impl;

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
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Implementation of ModuleService
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ModuleServiceImpl implements ModuleService {

    public static final String RESOURCE_BOOK = "resource.book";
    public static final String MODULE_ID_MUST_NOT_BE_NULL = "Module ID must not be null";
    public static final String RESOURCE_MODULE = "resource.module";
    public static final String BOOK_ID_MUST_NOT_BE_NULL = "Book ID must not be null";
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
                () -> SpacedLearningException.resourceNotFound(messageSource, RESOURCE_BOOK, request.getBookId()));

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
        Objects.requireNonNull(id, MODULE_ID_MUST_NOT_BE_NULL);
        log.debug("Deleting module with ID: {}", id);

        final Module module = moduleRepository.findById(id)
                .orElseThrow(() -> SpacedLearningException.resourceNotFound(messageSource, RESOURCE_MODULE, id));

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
        Objects.requireNonNull(bookId, BOOK_ID_MUST_NOT_BE_NULL);
        log.debug("Finding all modules by book ID: {}", bookId);

        // Verify book exists
        if (!bookRepository.existsById(bookId)) {
            throw SpacedLearningException.resourceNotFound(messageSource, RESOURCE_BOOK, bookId);
        }

        final List<Module> modules = moduleRepository.findByBookIdOrderByModuleNo(bookId);
        return moduleMapper.toSummaryDtoList(modules);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ModuleSummaryResponse> findByBookId(final UUID bookId, final Pageable pageable) {
        Objects.requireNonNull(bookId, BOOK_ID_MUST_NOT_BE_NULL);
        Objects.requireNonNull(pageable, "Pageable must not be null");
        log.debug("Finding modules by book ID: {}, pageable: {}", bookId, pageable);

        // Verify book exists
        if (!bookRepository.existsById(bookId)) {
            throw SpacedLearningException.resourceNotFound(messageSource, RESOURCE_BOOK, bookId);
        }

        return moduleRepository.findByBookId(bookId, pageable).map(moduleMapper::toSummaryDto);
    }

    @Override
    @Transactional(readOnly = true)
//	@Cacheable(value = "modules", key = "#id")
    public ModuleDetailResponse findById(final UUID id) {
        Objects.requireNonNull(id, MODULE_ID_MUST_NOT_BE_NULL);
        log.debug("Finding module by ID: {}", id);

        return moduleRepository.findWithProgressById(id).map(moduleMapper::toDto)
                .orElseThrow(() -> SpacedLearningException.resourceNotFound(messageSource, RESOURCE_MODULE, id));
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getNextModuleNumber(final UUID bookId) {
        Objects.requireNonNull(bookId, BOOK_ID_MUST_NOT_BE_NULL);
        log.debug("Getting next module number for book ID: {}", bookId);

        // Verify book exists
        if (!bookRepository.existsById(bookId)) {
            throw SpacedLearningException.resourceNotFound(messageSource, RESOURCE_BOOK, bookId);
        }

        final Integer maxModuleNo = moduleRepository.findMaxModuleNoByBookId(bookId);
        return maxModuleNo + 1;
    }

    @Override
    @Transactional
//	@CacheEvict(value = { "modules", "bookModules" }, allEntries = true)
    public ModuleDetailResponse update(final UUID id, final ModuleUpdateRequest request) {
        Objects.requireNonNull(id, MODULE_ID_MUST_NOT_BE_NULL);
        Objects.requireNonNull(request, "Module update request must not be null");
        log.debug("Updating module with ID: {}, request: {}", id, request);

        final Module module = moduleRepository.findById(id)
                .orElseThrow(() -> SpacedLearningException.resourceNotFound(messageSource, RESOURCE_MODULE, id));

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