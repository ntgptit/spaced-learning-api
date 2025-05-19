// VocabularyServiceImpl.java
package com.spacedlearning.service.impl;

import com.spacedlearning.dto.vocabulary.VocabularyCreateRequest;
import com.spacedlearning.dto.vocabulary.VocabularyResponse;
import com.spacedlearning.dto.vocabulary.VocabularyUpdateRequest;
import com.spacedlearning.exception.SpacedLearningException;
import com.spacedlearning.mapper.VocabularyMapper;
import com.spacedlearning.repository.ModuleRepository;
import com.spacedlearning.repository.VocabularyRepository;
import com.spacedlearning.service.VocabularyService;
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
public class VocabularyServiceImpl implements VocabularyService {

    public static final String VOCABULARY_ID_MUST_NOT_BE_NULL = "Vocabulary ID must not be null";
    public static final String MODULE_ID_MUST_NOT_BE_NULL = "Module ID must not be null";
    public static final String PAGEABLE_MUST_NOT_BE_NULL = "Pageable must not be null";
    private static final String RESOURCE_MODULE = "resource.module";
    private static final String RESOURCE_VOCABULARY = "resource.vocabulary";
    private final VocabularyRepository vocabularyRepository;
    private final ModuleRepository moduleRepository;
    private final VocabularyMapper vocabularyMapper;
    private final MessageSource messageSource;

    @Override
    @Transactional
    public VocabularyResponse create(VocabularyCreateRequest request) {
        Objects.requireNonNull(request, "Vocabulary create request must not be null");
        Objects.requireNonNull(request.getModuleId(), MODULE_ID_MUST_NOT_BE_NULL);
        log.debug("Creating new vocabulary: {}", request);

        final var module = this.moduleRepository.findById(request.getModuleId())
                .orElseThrow(() -> SpacedLearningException.resourceNotFound(
                        this.messageSource, RESOURCE_MODULE, request.getModuleId()));

        final var vocabulary = this.vocabularyMapper.toEntity(request, module);
        final var savedVocabulary = this.vocabularyRepository.save(vocabulary);

        log.info("Vocabulary created successfully with ID: {}", savedVocabulary.getId());
        return this.vocabularyMapper.toDto(savedVocabulary);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Objects.requireNonNull(id, VOCABULARY_ID_MUST_NOT_BE_NULL);
        log.debug("Deleting vocabulary with ID: {}", id);

        final var vocabulary = this.vocabularyRepository.findById(id)
                .orElseThrow(() -> SpacedLearningException.resourceNotFound(
                        this.messageSource, RESOURCE_VOCABULARY, id));

        vocabulary.softDelete();
        this.vocabularyRepository.save(vocabulary);

        log.info("Vocabulary soft deleted successfully with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VocabularyResponse> findAll(Pageable pageable) {
        Objects.requireNonNull(pageable, PAGEABLE_MUST_NOT_BE_NULL);
        log.debug("Retrieving all vocabularies with pagination: {}", pageable);
        return this.vocabularyRepository.findAll(pageable)
                .map(this.vocabularyMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VocabularyResponse> findAllByModuleId(UUID moduleId) {
        Objects.requireNonNull(moduleId, MODULE_ID_MUST_NOT_BE_NULL);
        log.debug("Retrieving all vocabularies by module ID: {}", moduleId);

        if (!this.moduleRepository.existsById(moduleId)) {
            throw SpacedLearningException.resourceNotFound(this.messageSource, RESOURCE_MODULE, moduleId);
        }

        final var vocabularies = this.vocabularyRepository.findByModuleIdOrderByTermAsc(moduleId);
        return this.vocabularyMapper.toDtoList(vocabularies);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VocabularyResponse> findByModuleId(UUID moduleId, Pageable pageable) {
        Objects.requireNonNull(moduleId, MODULE_ID_MUST_NOT_BE_NULL);
        Objects.requireNonNull(pageable, PAGEABLE_MUST_NOT_BE_NULL);

        log.debug("Retrieving vocabularies by module ID: {}, pageable: {}", moduleId, pageable);

        if (!this.moduleRepository.existsById(moduleId)) {
            throw SpacedLearningException.resourceNotFound(this.messageSource, RESOURCE_MODULE, moduleId);
        }

        return this.vocabularyRepository.findByModuleId(moduleId, pageable)
                .map(this.vocabularyMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public VocabularyResponse findById(UUID id) {
        Objects.requireNonNull(id, VOCABULARY_ID_MUST_NOT_BE_NULL);
        log.debug("Retrieving vocabulary by ID: {}", id);

        return this.vocabularyRepository.findById(id)
                .map(this.vocabularyMapper::toDto)
                .orElseThrow(() -> SpacedLearningException.resourceNotFound(
                        this.messageSource, RESOURCE_VOCABULARY, id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VocabularyResponse> searchByTerm(String term, Pageable pageable) {
        Objects.requireNonNull(pageable, PAGEABLE_MUST_NOT_BE_NULL);
        log.debug("Searching vocabularies by term: {}, pageable: {}", term, pageable);

        return this.vocabularyRepository.searchByTerm(term, pageable)
                .map(this.vocabularyMapper::toDto);
    }

    @Override
    @Transactional
    public VocabularyResponse update(UUID id, VocabularyUpdateRequest request) {
        Objects.requireNonNull(id, VOCABULARY_ID_MUST_NOT_BE_NULL);
        Objects.requireNonNull(request, "Vocabulary update request must not be null");
        log.debug("Updating vocabulary with ID: {}, request: {}", id, request);

        final var vocabulary = this.vocabularyRepository.findById(id)
                .orElseThrow(() -> SpacedLearningException.resourceNotFound(
                        this.messageSource, RESOURCE_VOCABULARY, id));

        this.vocabularyMapper.updateFromDto(request, vocabulary);
        final var updatedVocabulary = this.vocabularyRepository.save(vocabulary);

        log.info("Vocabulary updated successfully with ID: {}", updatedVocabulary.getId());
        return this.vocabularyMapper.toDto(updatedVocabulary);
    }
}