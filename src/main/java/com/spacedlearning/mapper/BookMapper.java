package com.spacedlearning.mapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;
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
	protected Book mapDtoToEntity(final BookDetailResponse dto, final Book entity) {
		if (dto == null || entity == null) {
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

		final List<ModuleDetailResponse> modules = moduleMapper.toDtoList(entity.getModules());

		return BookDetailResponse.builder().id(entity.getId()).name(entity.getName())
				.description(entity.getDescription()).status(entity.getStatus())
				.difficultyLevel(entity.getDifficultyLevel()).category(entity.getCategory())
				.createdAt(entity.getCreatedAt()).updatedAt(entity.getUpdatedAt()).modules(modules).build();
	}

	@Override
	protected Book mapToEntity(final BookDetailResponse dto) {
		if (dto == null) {
			return null;
		}

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
	public Book toEntity(final BookCreateRequest request) {
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
	public BookSummaryResponse toSummaryDto(final Book entity) {
		if (entity == null) {
			return null;
		}

		return BookSummaryResponse.builder().id(entity.getId()).name(entity.getName()).status(entity.getStatus())
				.difficultyLevel(entity.getDifficultyLevel()).category(entity.getCategory())
				.createdAt(entity.getCreatedAt()).updatedAt(entity.getUpdatedAt())
				.moduleCount(entity.getModules() != null ? entity.getModules().size() : 0).build();
	}

	/**
	 * Maps a list of Book entities to a list of BookSummaryResponse DTOs
	 *
	 * @param entities The Book entities
	 * @return List of BookSummaryResponse DTOs
	 */
	public List<BookSummaryResponse> toSummaryDtoList(final List<Book> entities) {
		if (CollectionUtils.isEmpty(entities)) {
			return Collections.emptyList();
		}

		return entities.stream().filter(Objects::nonNull).map(this::toSummaryDto).toList();
	}

	/**
	 * Updates a Book entity from a BookUpdateRequest DTO
	 *
	 * @param request The BookUpdateRequest DTO
	 * @param entity  The Book entity to update
	 * @return Updated Book entity
	 */
	public Book updateFromDto(final BookUpdateRequest request, final Book entity) {
		if (request == null || entity == null) {
			return entity;
		}

		if (StringUtils.isNotBlank(request.getName())) {
			entity.setName(request.getName());
		}

		// Description can be set to null to clear it
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