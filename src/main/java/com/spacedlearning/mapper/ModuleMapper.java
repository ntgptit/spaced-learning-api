// File: src/main/java/com/spacedlearning/mapper/ModuleMapper.java
package com.spacedlearning.mapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.spacedlearning.dto.module.ModuleCreateRequest;
import com.spacedlearning.dto.module.ModuleDetailResponse;
import com.spacedlearning.dto.module.ModuleSummaryResponse;
import com.spacedlearning.dto.module.ModuleUpdateRequest;
import com.spacedlearning.dto.progress.ModuleProgressSummaryResponse;
import com.spacedlearning.entity.Book;
import com.spacedlearning.entity.Module;

import lombok.RequiredArgsConstructor;

/**
 * Mapper for Module entity and DTOs
 */
@Component
@RequiredArgsConstructor
public class ModuleMapper extends AbstractGenericMapper<Module, ModuleDetailResponse> {

	private final ModuleProgressMapper progressMapper;

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
        return entity;
    }

	@Override
	protected ModuleDetailResponse mapToDto(Module entity) {
		final List<ModuleProgressSummaryResponse> progress = progressMapper.toSummaryDtoList(entity.getProgress());

		return ModuleDetailResponse.builder().id(entity.getId()).bookId(entity.getBook().getId())
				.bookName(entity.getBook().getName()).moduleNo(entity.getModuleNo()).title(entity.getTitle())
				.wordCount(entity.getWordCount()).createdAt(entity.getCreatedAt()).updatedAt(entity.getUpdatedAt())
				.progress(progress).build();
	}

	@Override
	protected Module mapToEntity(ModuleDetailResponse dto) {
		final Module module = new Module();

		// Book will be set separately
		module.setModuleNo(dto.getModuleNo());
		module.setTitle(dto.getTitle());
		module.setWordCount(dto.getWordCount());

		return module;
	}

	/**
	 * Maps a ModuleCreateRequest DTO to a Module entity
	 * 
	 * @param request The ModuleCreateRequest DTO
	 * @param book    The parent Book entity
	 * @return Module entity
	 */
	public Module toEntity(ModuleCreateRequest request, Book book) {
		if (request == null) {
			return null;
		}

		final Module module = new Module();
		module.setBook(book);
		module.setModuleNo(request.getModuleNo());
		module.setTitle(request.getTitle());
		module.setWordCount(Optional.ofNullable(request.getWordCount()).orElse(0));
		module.setProgress(new ArrayList<>());

		return module;
	}

	/**
	 * Maps a Module entity to a ModuleSummaryResponse DTO
	 * 
	 * @param entity The Module entity
	 * @return ModuleSummaryResponse DTO
	 */
	public ModuleSummaryResponse toSummaryDto(Module entity) {
		return entity != null
				? ModuleSummaryResponse.builder().id(entity.getId()).bookId(entity.getBook().getId())
						.moduleNo(entity.getModuleNo()).title(entity.getTitle()).wordCount(entity.getWordCount())
						.createdAt(entity.getCreatedAt()).updatedAt(entity.getUpdatedAt()).build()
				: null;
	}

	/**
	 * Maps a list of Module entities to a list of ModuleSummaryResponse DTOs
	 * 
	 * @param entities The Module entities
	 * @return List of ModuleSummaryResponse DTOs
	 */
	public List<ModuleSummaryResponse> toSummaryDtoList(List<Module> entities) {
		if (entities == null) {
			return Collections.emptyList();
		}
		return entities.stream().map(this::toSummaryDto).toList();
	}

	/**
	 * Updates a Module entity from a ModuleUpdateRequest DTO
	 * 
	 * @param request The ModuleUpdateRequest DTO
	 * @param entity  The Module entity to update
	 * @return Updated Module entity
	 */
	public Module updateFromDto(ModuleUpdateRequest request, Module entity) {
		if (request == null || entity == null) {
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

		return entity;
	}
}