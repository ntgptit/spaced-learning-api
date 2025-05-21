package com.spacedlearning.mapper;

import com.spacedlearning.dto.grammar.GrammarCreateRequest;
import com.spacedlearning.dto.grammar.GrammarResponse;
import com.spacedlearning.dto.grammar.GrammarUpdateRequest;
import com.spacedlearning.entity.Grammar;
import com.spacedlearning.entity.Module;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class GrammarMapper extends AbstractGenericMapper<Grammar, GrammarResponse> {

    @Override
    protected Grammar mapDtoToEntity(GrammarResponse dto, Grammar entity) {
        if ((dto == null) || (entity == null)) {
            return entity;
        }

        if (StringUtils.isNotBlank(dto.getGrammarPattern())) {
            entity.setGrammarPattern(dto.getGrammarPattern());
        }
        entity.setDefinition(dto.getDefinition());
        entity.setStructure(dto.getStructure());
        entity.setConjugation(dto.getConjugation());
        entity.setExamples(dto.getExamples());
        entity.setCommonPhrases(dto.getCommonPhrases());
        entity.setNotes(dto.getNotes());

        return entity;
    }

    @Override
    protected GrammarResponse mapToDto(Grammar entity) {
        if (entity == null) {
            return null;
        }

        return GrammarResponse.builder()
                .id(entity.getId())
                .moduleId(entity.getModule().getId())
                .moduleName(entity.getModule().getTitle())
                .grammarPattern(entity.getGrammarPattern())
                .definition(entity.getDefinition())
                .structure(entity.getStructure())
                .conjugation(entity.getConjugation())
                .examples(entity.getExamples())
                .commonPhrases(entity.getCommonPhrases())
                .notes(entity.getNotes())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    @Override
    protected Grammar mapToEntity(GrammarResponse dto) {
        if (dto == null) {
            return null;
        }

        final var grammar = new Grammar();
        grammar.setGrammarPattern(dto.getGrammarPattern());
        grammar.setDefinition(dto.getDefinition());
        grammar.setStructure(dto.getStructure());
        grammar.setConjugation(dto.getConjugation());
        grammar.setExamples(dto.getExamples());
        grammar.setCommonPhrases(dto.getCommonPhrases());
        grammar.setNotes(dto.getNotes());

        return grammar;
    }

    public Grammar toEntity(GrammarCreateRequest request, Module module) {
        if ((request == null) || (module == null)) {
            return null;
        }

        final var grammar = new Grammar();
        grammar.setModule(module);
        grammar.setGrammarPattern(request.getGrammarPattern());
        grammar.setDefinition(request.getDefinition());
        grammar.setStructure(request.getStructure());
        grammar.setConjugation(request.getConjugation());
        grammar.setExamples(request.getExamples());
        grammar.setCommonPhrases(request.getCommonPhrases());
        grammar.setNotes(request.getNotes());

        return grammar;
    }

    public Grammar updateFromDto(GrammarUpdateRequest request, Grammar entity) {
        if ((request == null) || (entity == null)) {
            return entity;
        }

        if (StringUtils.isNotBlank(request.getGrammarPattern())) {
            entity.setGrammarPattern(request.getGrammarPattern());
        }

        if (request.getDefinition() != null) {
            entity.setDefinition(request.getDefinition());
        }

        if (request.getStructure() != null) {
            entity.setStructure(request.getStructure());
        }

        if (request.getConjugation() != null) {
            entity.setConjugation(request.getConjugation());
        }

        if (request.getExamples() != null) {
            entity.setExamples(request.getExamples());
        }

        if (request.getCommonPhrases() != null) {
            entity.setCommonPhrases(request.getCommonPhrases());
        }

        if (request.getNotes() != null) {
            entity.setNotes(request.getNotes());
        }

        return entity;
    }

    public List<GrammarResponse> toDtoList(List<Grammar> entities) {
        return entities.stream()
                .filter(Objects::nonNull)
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}