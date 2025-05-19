// GrammarServiceImpl.java
package com.spacedlearning.service.impl;

import com.spacedlearning.dto.grammar.GrammarCreateRequest;
import com.spacedlearning.dto.grammar.GrammarResponse;
import com.spacedlearning.dto.grammar.GrammarUpdateRequest;
import com.spacedlearning.exception.SpacedLearningException;
import com.spacedlearning.mapper.GrammarMapper;
import com.spacedlearning.repository.GrammarRepository;
import com.spacedlearning.repository.ModuleRepository;
import com.spacedlearning.service.GrammarService;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class GrammarServiceImpl implements GrammarService {

    public static final String GRAMMAR_ID_MUST_NOT_BE_NULL = "Grammar ID must not be null";
    public static final String MODULE_ID_MUST_NOT_BE_NULL = "Module ID must not be null";
    public static final String PAGEABLE_MUST_NOT_BE_NULL = "Pageable must not be null";
    private static final String RESOURCE_MODULE = "resource.module";
    private static final String RESOURCE_GRAMMAR = "resource.grammar";
    private final GrammarRepository grammarRepository;
    private final ModuleRepository moduleRepository;
    private final GrammarMapper grammarMapper;
    private final MessageSource messageSource;

    @Override
    @Transactional
    public GrammarResponse create(GrammarCreateRequest request) {
        Objects.requireNonNull(request, "Grammar create request must not be null");
        Objects.requireNonNull(request.getModuleId(), MODULE_ID_MUST_NOT_BE_NULL);
        log.debug("Creating new grammar: {}", request);

        final var module = this.moduleRepository.findById(request.getModuleId())
                .orElseThrow(() -> SpacedLearningException.resourceNotFound(
                        this.messageSource, RESOURCE_MODULE, request.getModuleId()));

        final var grammar = this.grammarMapper.toEntity(request, module);
        final var savedGrammar = this.grammarRepository.save(grammar);

        log.info("Grammar created successfully with ID: {}", savedGrammar.getId());
        return this.grammarMapper.toDto(savedGrammar);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Objects.requireNonNull(id, GRAMMAR_ID_MUST_NOT_BE_NULL);
        log.debug("Deleting grammar with ID: {}", id);

        final var grammar = this.grammarRepository.findById(id)
                .orElseThrow(() -> SpacedLearningException.resourceNotFound(
                        this.messageSource, RESOURCE_GRAMMAR, id));

        grammar.softDelete();
        this.grammarRepository.save(grammar);

        log.info("Grammar soft deleted successfully with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GrammarResponse> findAll(Pageable pageable) {
        Objects.requireNonNull(pageable, PAGEABLE_MUST_NOT_BE_NULL);
        log.debug("Retrieving all grammars with pagination: {}", pageable);
        return this.grammarRepository.findAll(pageable)
                .map(this.grammarMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GrammarResponse> findAllByModuleId(UUID moduleId) {
        Objects.requireNonNull(moduleId, MODULE_ID_MUST_NOT_BE_NULL);
        log.debug("Retrieving all grammars by module ID: {}", moduleId);

        if (!this.moduleRepository.existsById(moduleId)) {
            throw SpacedLearningException.resourceNotFound(this.messageSource, RESOURCE_MODULE, moduleId);
        }

        final var grammars = this.grammarRepository.findByModuleIdOrderByTitleAsc(moduleId);
        return this.grammarMapper.toDtoList(grammars);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GrammarResponse> findByModuleId(UUID moduleId, Pageable pageable) {
        Objects.requireNonNull(moduleId, MODULE_ID_MUST_NOT_BE_NULL);
        Objects.requireNonNull(pageable, PAGEABLE_MUST_NOT_BE_NULL);

        log.debug("Retrieving grammars by module ID: {}, pageable: {}", moduleId, pageable);

        if (!this.moduleRepository.existsById(moduleId)) {
            throw SpacedLearningException.resourceNotFound(this.messageSource, RESOURCE_MODULE, moduleId);
        }

        return this.grammarRepository.findByModuleId(moduleId, pageable)
                .map(this.grammarMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public GrammarResponse findById(UUID id) {
        Objects.requireNonNull(id, GRAMMAR_ID_MUST_NOT_BE_NULL);
        log.debug("Retrieving grammar by ID: {}", id);

        return this.grammarRepository.findById(id)
                .map(this.grammarMapper::toDto)
                .orElseThrow(() -> SpacedLearningException.resourceNotFound(
                        this.messageSource, RESOURCE_GRAMMAR, id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GrammarResponse> searchByTitle(String title, Pageable pageable) {
        Objects.requireNonNull(pageable, PAGEABLE_MUST_NOT_BE_NULL);
        log.debug("Searching grammars by title: {}, pageable: {}", title, pageable);

        return this.grammarRepository.searchByTitle(title, pageable)
                .map(this.grammarMapper::toDto);
    }

    @Override
    @Transactional
    public GrammarResponse update(UUID id, GrammarUpdateRequest request) {
        Objects.requireNonNull(id, GRAMMAR_ID_MUST_NOT_BE_NULL);
        Objects.requireNonNull(request, "Grammar update request must not be null");
        log.debug("Updating grammar with ID: {}, request: {}", id, request);

        final var grammar = this.grammarRepository.findById(id)
                .orElseThrow(() -> SpacedLearningException.resourceNotFound(
                        this.messageSource, RESOURCE_GRAMMAR, id));

        this.grammarMapper.updateFromDto(request, grammar);
        final var updatedGrammar = this.grammarRepository.save(grammar);

        log.info("Grammar updated successfully with ID: {}", updatedGrammar.getId());
        return this.grammarMapper.toDto(updatedGrammar);
    }
}