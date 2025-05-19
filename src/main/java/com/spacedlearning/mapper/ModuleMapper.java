package com.spacedlearning.mapper;

import com.spacedlearning.dto.grammar.GrammarResponse;
import com.spacedlearning.dto.module.ModuleCreateRequest;
import com.spacedlearning.dto.module.ModuleDetailResponse;
import com.spacedlearning.dto.module.ModuleSummaryResponse;
import com.spacedlearning.dto.module.ModuleUpdateRequest;
import com.spacedlearning.dto.vocabulary.VocabularyResponse;
import com.spacedlearning.entity.Book;
import com.spacedlearning.entity.Module;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Mapper for Module entity and its DTOs.
 */
@Component
@RequiredArgsConstructor
public class ModuleMapper extends AbstractGenericMapper<Module, ModuleDetailResponse> {

    private final ModuleProgressMapper progressMapper;
    private final VocabularyMapper vocabularyMapper;
    private final GrammarMapper grammarMapper;

    @Override
    protected Module mapDtoToEntity(ModuleDetailResponse dto, Module entity) {
        if (dto.getModuleNo() != null) {
            entity.setModuleNo(dto.getModuleNo());
        }
        if (StringUtils.isNotBlank(dto.getTitle())) {
            entity.setTitle(dto.getTitle());
        }
        if (dto.getWordCount() != null) {
            entity.setWordCount(dto.getWordCount());
        }
        // Map URL field
        entity.setUrl(dto.getUrl());
        return entity;
    }

    @Override
    protected ModuleDetailResponse mapToDto(Module entity) {
        final var progress = this.progressMapper.toSummaryDtoList(entity.getProgress());

        List<VocabularyResponse> vocabularies = Collections.emptyList();
        List<GrammarResponse> grammars = Collections.emptyList();

        if (entity.getVocabularies() != null && !entity.getVocabularies().isEmpty()) {
            vocabularies = this.vocabularyMapper.toDtoList(entity.getVocabularies());
        }

        if (entity.getGrammars() != null && !entity.getGrammars().isEmpty()) {
            grammars = this.grammarMapper.toDtoList(entity.getGrammars());
        }

        return ModuleDetailResponse.builder()
                .id(entity.getId())
                .bookId(entity.getBook().getId())
                .bookName(entity.getBook().getName())
                .moduleNo(entity.getModuleNo())
                .title(entity.getTitle())
                .wordCount(entity.getWordCount())
                .url(entity.getUrl())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .progress(progress)
                .vocabularies(vocabularies)
                .grammars(grammars)
                .vocabularyCount(entity.getVocabularies().size())
                .grammarCount(entity.getGrammars().size())
                .build();
    }

    @Override
    protected Module mapToEntity(ModuleDetailResponse dto) {
        final var module = new Module();
        module.setModuleNo(dto.getModuleNo());
        module.setTitle(dto.getTitle());
        module.setWordCount(dto.getWordCount());
        module.setUrl(dto.getUrl());
        return module;
    }

    /**
     * Maps a ModuleCreateRequest DTO to a Module entity.
     *
     * @param request The DTO
     * @param book    The associated Book entity
     * @return Mapped Module entity
     */
    public Module toEntity(ModuleCreateRequest request, Book book) {
        if ((request == null) || (book == null)) {
            return null;
        }

        final var module = new Module();
        module.setBook(book);
        module.setModuleNo(request.getModuleNo());
        module.setTitle(request.getTitle());
        module.setWordCount(Optional.ofNullable(request.getWordCount()).orElse(0));
        module.setUrl(request.getUrl());
        module.setProgress(new ArrayList<>());
        return module;
    }

    /**
     * Maps a Module entity to a ModuleSummaryResponse DTO.
     *
     * @param entity The Module
     * @return Summary DTO
     */
    public ModuleSummaryResponse toSummaryDto(Module entity) {
        if (entity == null) {
            return null;
        }

        int vocabularyCount = 0;
        int grammarCount = 0;

        if (entity.getVocabularies() != null) {
            vocabularyCount = entity.getVocabularies().size();
        }

        if (entity.getGrammars() != null) {
            grammarCount = entity.getGrammars().size();
        }

        return ModuleSummaryResponse.builder()
                .id(entity.getId())
                .bookId(entity.getBook().getId())
                .moduleNo(entity.getModuleNo())
                .title(entity.getTitle())
                .wordCount(entity.getWordCount())
                .url(entity.getUrl())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .vocabularyCount(vocabularyCount)
                .grammarCount(grammarCount)
                .build();
    }

    /**
     * Maps a list of Module entities to summary DTOs.
     *
     * @param entities List of modules
     * @return List of summary DTOs
     */
    public List<ModuleSummaryResponse> toSummaryDtoList(List<Module> entities) {
        if ((entities == null) || entities.isEmpty()) {
            return Collections.emptyList();
        }
        return entities.stream().map(this::toSummaryDto).toList();
    }

    /**
     * Updates a Module entity from a ModuleUpdateRequest.
     *
     * @param request The update DTO
     * @param entity  The entity to update
     * @return Updated entity
     */
    public Module updateFromDto(ModuleUpdateRequest request, Module entity) {
        if ((request == null) || (entity == null)) {
            return entity;
        }

        if (request.getModuleNo() != null) {
            entity.setModuleNo(request.getModuleNo());
        }

        if (StringUtils.isNotBlank(request.getTitle())) {
            entity.setTitle(request.getTitle());
        }

        if (request.getWordCount() != null) {
            entity.setWordCount(request.getWordCount());
        }

        // Update URL if provided in request
        if (request.getUrl() != null) {
            entity.setUrl(request.getUrl());
        }

        return entity;
    }
}