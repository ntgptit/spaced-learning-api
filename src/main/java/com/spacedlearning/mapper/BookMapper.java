// File: src/main/java/com/spacedlearning/mapper/BookMapper.java
package com.spacedlearning.mapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.spacedlearning.dto.book.BookCreateRequest;
import com.spacedlearning.dto.book.BookDetailResponse;
import com.spacedlearning.dto.book.BookSummaryResponse;
import com.spacedlearning.dto.book.BookUpdateRequest;
import com.spacedlearning.dto.module.ModuleDetailResponse;
import com.spacedlearning.entity.Book;

import lombok.RequiredArgsConstructor;

/**
 * Mapper for Book entity and DTOs
 */
@Component
@RequiredArgsConstructor
public class BookMapper extends AbstractGenericMapper<Book, BookDetailResponse> {

	private final ModuleMapper moduleMapper;

	@Override
    protected Book mapDtoToEntity(BookDetailResponse dto, Book entity) {
        if (StringUtils.isNotBlank(dto.getName())) {
            entity.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            entity.setDescription(dto.getDescription());
        }
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
	protected BookDetailResponse mapToDto(Book entity) {
		final List<ModuleDetailResponse> modules = moduleMapper.toDtoList(entity.getModules());

		return BookDetailResponse.builder().id(entity.getId()).name(entity.getName())
				.description(entity.getDescription()).status(entity.getStatus())
				.difficultyLevel(entity.getDifficultyLevel()).category(entity.getCategory())
				.createdAt(entity.getCreatedAt()).updatedAt(entity.getUpdatedAt()).modules(modules).build();
	}

	@Override
	protected Book mapToEntity(BookDetailResponse dto) {
		final Book book = new Book();
		book.setName(dto.getName());
		book.setDescription(dto.getDescription());
		book.setStatus(dto.getStatus());
		book.setDifficultyLevel(dto.getDifficultyLevel());
		book.setCategory(dto.getCategory());
		return book;
	}

	/**
	 * Maps a BookCreateRequest DTO to a Book entity
	 *
	 * @param request The BookCreateRequest DTO
	 * @return Book entity
	 */
	public Book toEntity(BookCreateRequest request) {
		if (request == null) {
			return null;
		}

		final Book book = new Book();
		book.setName(request.getName());
		book.setDescription(request.getDescription());
		book.setStatus(Optional.ofNullable(request.getStatus()).orElse(book.getStatus()));
		book.setDifficultyLevel(request.getDifficultyLevel());
		book.setCategory(request.getCategory());
		book.setModules(new ArrayList<>());

		return book;
	}

	/**
	 * Maps a Book entity to a BookSummaryResponse DTO
	 *
	 * @param entity The Book entity
	 * @return BookSummaryResponse DTO
	 */
	public BookSummaryResponse toSummaryDto(Book entity) {
		return entity != null
				? BookSummaryResponse.builder().id(entity.getId()).name(entity.getName()).status(entity.getStatus())
						.difficultyLevel(entity.getDifficultyLevel()).category(entity.getCategory())
						.createdAt(entity.getCreatedAt()).updatedAt(entity.getUpdatedAt())
						.moduleCount(entity.getModules().size()).build()
				: null;
	}

	/**
	 * Maps a list of Book entities to a list of BookSummaryResponse DTOs
	 *
	 * @param entities The Book entities
	 * @return List of BookSummaryResponse DTOs
	 */
	public List<BookSummaryResponse> toSummaryDtoList(List<Book> entities) {
		if (entities == null) {
			return Collections.emptyList();
		}
		return entities.stream().map(this::toSummaryDto).toList();
	}

	/**
	 * Updates a Book entity from a BookUpdateRequest DTO
	 *
	 * @param request The BookUpdateRequest DTO
	 * @param entity  The Book entity to update
	 * @return Updated Book entity
	 */
	public Book updateFromDto(BookUpdateRequest request, Book entity) {
		if (request == null || entity == null) {
			return entity;
		}

		if (StringUtils.isNotBlank(request.getName())) {
			entity.setName(request.getName());
		}

		if (request.getDescription() != null) {
			entity.setDescription(request.getDescription());
		}

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