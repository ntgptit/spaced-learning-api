// GrammarMapper.java
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

        if (StringUtils.isNotBlank(dto.getTitle())) {
            entity.setTitle(dto.getTitle());
        }
        entity.setExplanation(dto.getExplanation());
        entity.setUsageNote(dto.getUsageNote());
        entity.setExample(dto.getExample());

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
                .title(entity.getTitle())
                .explanation(entity.getExplanation())
                .usageNote(entity.getUsageNote())
                .example(entity.getExample())
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
        grammar.setTitle(dto.getTitle());
        grammar.setExplanation(dto.getExplanation());
        grammar.setUsageNote(dto.getUsageNote());
        grammar.setExample(dto.getExample());

        return grammar;
    }

    public Grammar toEntity(GrammarCreateRequest request, Module module) {
        if ((request == null) || (module == null)) {
            return null;
        }

        final var grammar = new Grammar();
        grammar.setModule(module);
        grammar.setTitle(request.getTitle());
        grammar.setExplanation(request.getExplanation());
        grammar.setUsageNote(request.getUsageNote());
        grammar.setExample(request.getExample());

        return grammar;
    }

    public Grammar updateFromDto(GrammarUpdateRequest request, Grammar entity) {
        if ((request == null) || (entity == null)) {
            return entity;
        }

        if (StringUtils.isNotBlank(request.getTitle())) {
            entity.setTitle(request.getTitle());
        }

        if (request.getExplanation() != null) {
            entity.setExplanation(request.getExplanation());
        }

        if (request.getUsageNote() != null) {
            entity.setUsageNote(request.getUsageNote());
        }

        if (request.getExample() != null) {
            entity.setExample(request.getExample());
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