// VocabularyService.java
package com.spacedlearning.service;

import com.spacedlearning.dto.vocabulary.VocabularyCreateRequest;
import com.spacedlearning.dto.vocabulary.VocabularyResponse;
import com.spacedlearning.dto.vocabulary.VocabularyUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface VocabularyService {

    VocabularyResponse create(VocabularyCreateRequest request);

    void delete(UUID id);

    Page<VocabularyResponse> findAll(Pageable pageable);

    List<VocabularyResponse> findAllByModuleId(UUID moduleId);

    Page<VocabularyResponse> findByModuleId(UUID moduleId, Pageable pageable);

    VocabularyResponse findById(UUID id);

    Page<VocabularyResponse> searchByTerm(String term, Pageable pageable);

    VocabularyResponse update(UUID id, VocabularyUpdateRequest request);
}