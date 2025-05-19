// VocabularyMapper.java
package com.spacedlearning.mapper;

import com.spacedlearning.dto.vocabulary.VocabularyCreateRequest;
import com.spacedlearning.dto.vocabulary.VocabularyResponse;
import com.spacedlearning.dto.vocabulary.VocabularyUpdateRequest;
import com.spacedlearning.entity.Module;
import com.spacedlearning.entity.Vocabulary;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class VocabularyMapper extends AbstractGenericMapper<Vocabulary, VocabularyResponse> {

    @Override
    protected Vocabulary mapDtoToEntity(VocabularyResponse dto, Vocabulary entity) {
        if ((dto == null) || (entity == null)) {
            return entity;
        }

        if (StringUtils.isNotBlank(dto.getTerm())) {
            entity.setTerm(dto.getTerm());
        }
        entity.setDefinition(dto.getDefinition());
        entity.setExample(dto.getExample());
        entity.setPronunciation(dto.getPronunciation());

        if (StringUtils.isNotBlank(dto.getPartOfSpeech())) {
            entity.setPartOfSpeech(dto.getPartOfSpeech());
        }

        return entity;
    }

    @Override
    protected VocabularyResponse mapToDto(Vocabulary entity) {
        if (entity == null) {
            return null;
        }

        return VocabularyResponse.builder()
                .id(entity.getId())
                .moduleId(entity.getModule().getId())
                .moduleName(entity.getModule().getTitle())
                .term(entity.getTerm())
                .definition(entity.getDefinition())
                .example(entity.getExample())
                .pronunciation(entity.getPronunciation())
                .partOfSpeech(entity.getPartOfSpeech())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    @Override
    protected Vocabulary mapToEntity(VocabularyResponse dto) {
        if (dto == null) {
            return null;
        }

        final var vocabulary = new Vocabulary();
        vocabulary.setTerm(dto.getTerm());
        vocabulary.setDefinition(dto.getDefinition());
        vocabulary.setExample(dto.getExample());
        vocabulary.setPronunciation(dto.getPronunciation());
        vocabulary.setPartOfSpeech(dto.getPartOfSpeech());

        return vocabulary;
    }

    public Vocabulary toEntity(VocabularyCreateRequest request, Module module) {
        if ((request == null) || (module == null)) {
            return null;
        }

        final var vocabulary = new Vocabulary();
        vocabulary.setModule(module);
        vocabulary.setTerm(request.getTerm());
        vocabulary.setDefinition(request.getDefinition());
        vocabulary.setExample(request.getExample());
        vocabulary.setPronunciation(request.getPronunciation());
        vocabulary.setPartOfSpeech(request.getPartOfSpeech());

        return vocabulary;
    }

    public Vocabulary updateFromDto(VocabularyUpdateRequest request, Vocabulary entity) {
        if ((request == null) || (entity == null)) {
            return entity;
        }

        if (StringUtils.isNotBlank(request.getTerm())) {
            entity.setTerm(request.getTerm());
        }

        if (request.getDefinition() != null) {
            entity.setDefinition(request.getDefinition());
        }

        if (request.getExample() != null) {
            entity.setExample(request.getExample());
        }

        if (request.getPronunciation() != null) {
            entity.setPronunciation(request.getPronunciation());
        }

        if (StringUtils.isNotBlank(request.getPartOfSpeech())) {
            entity.setPartOfSpeech(request.getPartOfSpeech());
        }

        return entity;
    }

    public List<VocabularyResponse> toDtoList(List<Vocabulary> entities) {
        return entities.stream()
                .filter(Objects::nonNull)
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}