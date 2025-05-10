package com.spacedlearning.mapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.spacedlearning.dto.book.BookCreateRequest;
import com.spacedlearning.dto.book.BookDetailResponse;
import com.spacedlearning.dto.book.BookSummaryResponse;
import com.spacedlearning.dto.book.BookUpdateRequest;
import com.spacedlearning.entity.Book;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BookMapper extends AbstractGenericMapper<Book, BookDetailResponse> {

    private final ModuleMapper moduleMapper;

    @Override
    protected Book mapDtoToEntity(final BookDetailResponse dto, final Book entity) {
        if ((dto == null) || (entity == null)) {
            return entity;
        }

        if (StringUtils.isNotBlank(dto.getName())) {
            entity.setName(dto.getName());
        }

        entity.setDescription(dto.getDescription());

        if (dto.getStatus() != null) {
            entity.setStatus(dto.getStatus());
        }

        if (dto.getDifficultyLevel() != null) {
            entity.setDifficultyLevel(dto.getDifficultyLevel());
        }

        if (StringUtils.isNotBlank(dto.getCategory())) {
            entity.setCategory(dto.getCategory());
        }

        return entity;
    }

    @Override
    protected BookDetailResponse mapToDto(final Book entity) {
        if (entity == null) {
            return null;
        }

        final var modules = this.moduleMapper.toDtoList(entity.getModules());

        return BookDetailResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .status(entity.getStatus())
                .difficultyLevel(entity.getDifficultyLevel())
                .category(entity.getCategory())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .modules(modules)
                .build();
    }

    @Override
    protected Book mapToEntity(final BookDetailResponse dto) {
        if (dto == null) {
            return null;
        }

        final var book = new Book();
        book.setName(dto.getName());
        book.setDescription(dto.getDescription());
        book.setStatus(dto.getStatus());
        book.setDifficultyLevel(dto.getDifficultyLevel());
        book.setCategory(dto.getCategory());

        return book;
    }

    public Book toEntity(final BookCreateRequest request) {
        if (request == null) {
            return null;
        }

        final var book = new Book();
        book.setName(request.getName());
        book.setDescription(request.getDescription());
        book.setStatus(request.getStatus());
        book.setDifficultyLevel(request.getDifficultyLevel());
        book.setCategory(request.getCategory());
        book.setModules(new ArrayList<>());

        return book;
    }

    public BookSummaryResponse toSummaryDto(final Book entity) {
        if (entity == null) {
            return null;
        }

        return BookSummaryResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .status(entity.getStatus())
                .difficultyLevel(entity.getDifficultyLevel())
                .category(entity.getCategory())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .moduleCount(CollectionUtils.size(entity.getModules()))
                .build();
    }

    public List<BookSummaryResponse> toSummaryDtoList(final List<Book> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return Collections.emptyList();
        }

        return entities.stream()
                .filter(Objects::nonNull)
                .map(this::toSummaryDto)
                .toList();
    }

    public Book updateFromDto(final BookUpdateRequest request, final Book entity) {
        if ((request == null) || (entity == null)) {
            return entity;
        }

        if (StringUtils.isNotBlank(request.getName())) {
            entity.setName(request.getName());
        }

        entity.setDescription(request.getDescription());

        if (request.getStatus() != null) {
            entity.setStatus(request.getStatus());
        }

        if (request.getDifficultyLevel() != null) {
            entity.setDifficultyLevel(request.getDifficultyLevel());
        }

        if (StringUtils.isNotBlank(request.getCategory())) {
            entity.setCategory(request.getCategory());
        }

        return entity;
    }
}
