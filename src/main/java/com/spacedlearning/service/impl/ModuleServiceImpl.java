package com.spacedlearning.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spacedlearning.dto.module.ModuleCreateRequest;
import com.spacedlearning.dto.module.ModuleDetailResponse;
import com.spacedlearning.dto.module.ModuleSummaryResponse;
import com.spacedlearning.dto.module.ModuleUpdateRequest;
import com.spacedlearning.exception.SpacedLearningException;
import com.spacedlearning.mapper.ModuleMapper;
import com.spacedlearning.repository.BookRepository;
import com.spacedlearning.repository.ModuleRepository;
import com.spacedlearning.service.ModuleService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ModuleServiceImpl implements ModuleService {

    private static final String RESOURCE_BOOK = "resource.book";
    private static final String RESOURCE_MODULE = "resource.module";
    private static final String MODULE_ID_MUST_NOT_BE_NULL = "Module ID must not be null";
    private static final String BOOK_ID_MUST_NOT_BE_NULL = "Book ID must not be null";

    private final ModuleRepository moduleRepository;
    private final BookRepository bookRepository;
    private final ModuleMapper moduleMapper;
    private final MessageSource messageSource;

    @Override
    @Transactional
    // @CacheEvict(value = "bookModules", key = "#result.bookId")
    public ModuleDetailResponse create(ModuleCreateRequest request) {
        Objects.requireNonNull(request, "Module create request must not be null");
        log.debug("Creating new module: {}", request);

        final var book = this.bookRepository.findById(request.getBookId())
                .orElseThrow(() -> SpacedLearningException.resourceNotFound(this.messageSource, RESOURCE_BOOK, request
                        .getBookId()));

        if (this.moduleRepository.existsByBookIdAndModuleNo(request.getBookId(), request.getModuleNo())) {
            throw SpacedLearningException.validationError(this.messageSource, "error.module.duplicate.number", request
                    .getModuleNo());
        }

        final var module = this.moduleMapper.toEntity(request, book);
        final var savedModule = this.moduleRepository.save(module);

        log.info("Module created successfully with ID: {}", savedModule.getId());
        return this.moduleMapper.toDto(savedModule);
    }

    @Override
    @Transactional
    // @CacheEvict(value = { "modules", "bookModules" }, allEntries = true)
    public void delete(UUID id) {
        Objects.requireNonNull(id, MODULE_ID_MUST_NOT_BE_NULL);
        log.debug("Deleting module with ID: {}", id);

        final var module = this.moduleRepository.findById(id)
                .orElseThrow(() -> SpacedLearningException.resourceNotFound(this.messageSource, RESOURCE_MODULE, id));

        module.softDelete();
        this.moduleRepository.save(module);

        log.info("Module soft deleted successfully with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ModuleSummaryResponse> findAll(Pageable pageable) {
        Objects.requireNonNull(pageable, "Pageable must not be null");
        log.debug("Retrieving all modules with pagination: {}", pageable);
        return this.moduleRepository.findAll(pageable)
                .map(this.moduleMapper::toSummaryDto);
    }

    @Override
    @Transactional(readOnly = true)
    // @Cacheable(value = "bookModules", key = "#bookId")
    public List<ModuleSummaryResponse> findAllByBookId(UUID bookId) {
        Objects.requireNonNull(bookId, BOOK_ID_MUST_NOT_BE_NULL);
        log.debug("Retrieving all modules by book ID: {}", bookId);

        if (!this.bookRepository.existsById(bookId)) {
            throw SpacedLearningException.resourceNotFound(this.messageSource, RESOURCE_BOOK, bookId);
        }

        final var modules = this.moduleRepository.findByBookIdOrderByModuleNo(bookId);
        return this.moduleMapper.toSummaryDtoList(modules);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ModuleSummaryResponse> findByBookId(UUID bookId, Pageable pageable) {
        Objects.requireNonNull(bookId, BOOK_ID_MUST_NOT_BE_NULL);
        Objects.requireNonNull(pageable, "Pageable must not be null");

        log.debug("Retrieving modules by book ID: {}, pageable: {}", bookId, pageable);

        if (!this.bookRepository.existsById(bookId)) {
            throw SpacedLearningException.resourceNotFound(this.messageSource, RESOURCE_BOOK, bookId);
        }

        var effectivePageable = pageable;
        if (pageable.getSort().isUnsorted()) {
            effectivePageable = PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by(Sort.Direction.ASC, "moduleNo"));
        }

        return this.moduleRepository.findByBookId(bookId, effectivePageable)
                .map(this.moduleMapper::toSummaryDto);
    }

    @Override
    @Transactional(readOnly = true)
    // @Cacheable(value = "modules", key = "#id")
    public ModuleDetailResponse findById(UUID id) {
        Objects.requireNonNull(id, MODULE_ID_MUST_NOT_BE_NULL);
        log.debug("Retrieving module by ID: {}", id);

        return this.moduleRepository.findWithProgressById(id)
                .map(this.moduleMapper::toDto)
                .orElseThrow(() -> SpacedLearningException.resourceNotFound(this.messageSource, RESOURCE_MODULE, id));
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getNextModuleNumber(UUID bookId) {
        Objects.requireNonNull(bookId, BOOK_ID_MUST_NOT_BE_NULL);
        log.debug("Getting next module number for book ID: {}", bookId);

        if (!this.bookRepository.existsById(bookId)) {
            throw SpacedLearningException.resourceNotFound(this.messageSource, RESOURCE_BOOK, bookId);
        }

        final var maxModuleNo = this.moduleRepository.findMaxModuleNoByBookId(bookId);
        return maxModuleNo + 1;
    }

    @Override
    @Transactional
    // @CacheEvict(value = { "modules", "bookModules" }, allEntries = true)
    public ModuleDetailResponse update(UUID id, ModuleUpdateRequest request) {
        Objects.requireNonNull(id, MODULE_ID_MUST_NOT_BE_NULL);
        Objects.requireNonNull(request, "Module update request must not be null");
        log.debug("Updating module with ID: {}, request: {}", id, request);

        final var module = this.moduleRepository.findById(id)
                .orElseThrow(() -> SpacedLearningException.resourceNotFound(this.messageSource, RESOURCE_MODULE, id));

        final var numberChanged = (request.getModuleNo() != null) &&
                !request.getModuleNo().equals(module.getModuleNo());

        if (numberChanged && this.moduleRepository.existsByBookIdAndModuleNo(module.getBook().getId(), request
                .getModuleNo())) {
            throw SpacedLearningException.validationError(this.messageSource, "error.module.duplicate.number", request
                    .getModuleNo());
        }

        this.moduleMapper.updateFromDto(request, module);
        final var updatedModule = this.moduleRepository.save(module);

        log.info("Module updated successfully with ID: {}", updatedModule.getId());
        return this.moduleMapper.toDto(updatedModule);
    }
}
