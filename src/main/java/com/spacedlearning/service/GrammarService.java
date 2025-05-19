// GrammarService.java
package com.spacedlearning.service;

import com.spacedlearning.dto.grammar.GrammarCreateRequest;
import com.spacedlearning.dto.grammar.GrammarResponse;
import com.spacedlearning.dto.grammar.GrammarUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface GrammarService {

    GrammarResponse create(GrammarCreateRequest request);

    void delete(UUID id);

    Page<GrammarResponse> findAll(Pageable pageable);

    List<GrammarResponse> findAllByModuleId(UUID moduleId);

    Page<GrammarResponse> findByModuleId(UUID moduleId, Pageable pageable);

    GrammarResponse findById(UUID id);

    Page<GrammarResponse> searchByTitle(String title, Pageable pageable);

    GrammarResponse update(UUID id, GrammarUpdateRequest request);
}