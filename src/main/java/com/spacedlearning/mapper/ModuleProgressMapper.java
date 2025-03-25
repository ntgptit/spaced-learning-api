// File: src/main/java/com/spacedlearning/mapper/ModuleProgressMapper.java
package com.spacedlearning.mapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.spacedlearning.dto.progress.ModuleProgressCreateRequest;
import com.spacedlearning.dto.progress.ModuleProgressDetailResponse;
import com.spacedlearning.dto.progress.ModuleProgressSummaryResponse;
import com.spacedlearning.dto.progress.ModuleProgressUpdateRequest;
import com.spacedlearning.dto.repetition.RepetitionResponse;
import com.spacedlearning.entity.Module;
import com.spacedlearning.entity.ModuleProgress;
import com.spacedlearning.entity.User;

import lombok.RequiredArgsConstructor;

/**
 * Mapper for ModuleProgress entity and DTOs
 */
@Component
@RequiredArgsConstructor
public class ModuleProgressMapper extends AbstractGenericMapper<ModuleProgress, ModuleProgressDetailResponse> {

	private final RepetitionMapper repetitionMapper;

	@Override
    protected ModuleProgress mapDtoToEntity(ModuleProgressDetailResponse dto, ModuleProgress entity) {
        if (dto.getFirstLearningDate() != null) {
            entity.setFirstLearningDate(dto.getFirstLearningDate());
        }
        if (dto.getCyclesStudied() != null) {
            entity.setCyclesStudied(dto.getCyclesStudied());
        }
        if (dto.getNextStudyDate() != null) {
            entity.setNextStudyDate(dto.getNextStudyDate());
        }
        if (dto.getPercentComplete() != null) {
            entity.setPercentComplete(dto.getPercentComplete());
        }
        return entity;
    }

	@Override
	protected ModuleProgressDetailResponse mapToDto(ModuleProgress entity) {
		final List<RepetitionResponse> repetitions = repetitionMapper.toDtoList(entity.getRepetitions());

		return ModuleProgressDetailResponse.builder().id(entity.getId()).moduleId(entity.getModule().getId())
				.moduleTitle(entity.getModule().getTitle()).userId(entity.getUser().getId())
				.userName(entity.getUser().getName()).firstLearningDate(entity.getFirstLearningDate())
				.cyclesStudied(entity.getCyclesStudied()).nextStudyDate(entity.getNextStudyDate())
				.percentComplete(entity.getPercentComplete()).createdAt(entity.getCreatedAt())
				.updatedAt(entity.getUpdatedAt()).repetitions(repetitions).build();
	}

	@Override
	protected ModuleProgress mapToEntity(ModuleProgressDetailResponse dto) {
		final ModuleProgress progress = new ModuleProgress();

		// Module and User will be set separately
		progress.setFirstLearningDate(dto.getFirstLearningDate());
		progress.setCyclesStudied(dto.getCyclesStudied());
		progress.setNextStudyDate(dto.getNextStudyDate());
		// Continuing: src/main/java/com/spacedlearning/mapper/ModuleProgressMapper.java
		progress.setPercentComplete(dto.getPercentComplete());
		progress.setRepetitions(new ArrayList<>());

		return progress;
	}

	/**
	 * Maps a ModuleProgressCreateRequest DTO to a ModuleProgress entity
	 * 
	 * @param request The ModuleProgressCreateRequest DTO
	 * @param module  The Module entity
	 * @param user    The User entity
	 * @return ModuleProgress entity
	 */
	public ModuleProgress toEntity(ModuleProgressCreateRequest request, Module module, User user) {
		if (request == null) {
			return null;
		}

		final ModuleProgress progress = new ModuleProgress();
		progress.setModule(module);
		progress.setUser(user);
		progress.setFirstLearningDate(request.getFirstLearningDate());
		progress.setCyclesStudied(Optional.ofNullable(request.getCyclesStudied()).orElse(progress.getCyclesStudied()));
		progress.setNextStudyDate(request.getNextStudyDate());
		progress.setPercentComplete(
				Optional.ofNullable(request.getPercentComplete()).orElse(progress.getPercentComplete()));
		progress.setRepetitions(new ArrayList<>());

		return progress;
	}

	/**
	 * Maps a ModuleProgress entity to a ModuleProgressSummaryResponse DTO
	 * 
	 * @param entity The ModuleProgress entity
	 * @return ModuleProgressSummaryResponse DTO
	 */
	public ModuleProgressSummaryResponse toSummaryDto(ModuleProgress entity) {
		return entity != null
				? ModuleProgressSummaryResponse.builder().id(entity.getId()).moduleId(entity.getModule().getId())
						.userId(entity.getUser().getId()).firstLearningDate(entity.getFirstLearningDate())
						.cyclesStudied(entity.getCyclesStudied()).nextStudyDate(entity.getNextStudyDate())
						.percentComplete(entity.getPercentComplete()).createdAt(entity.getCreatedAt())
						.updatedAt(entity.getUpdatedAt()).repetitionCount(entity.getRepetitions().size()).build()
				: null;
	}

	/**
	 * Maps a list of ModuleProgress entities to a list of
	 * ModuleProgressSummaryResponse DTOs
	 * 
	 * @param entities The ModuleProgress entities
	 * @return List of ModuleProgressSummaryResponse DTOs
	 */
	public List<ModuleProgressSummaryResponse> toSummaryDtoList(List<ModuleProgress> entities) {
		if (entities == null) {
			return Collections.emptyList();
		}
		return entities.stream().map(this::toSummaryDto).toList();
	}

	/**
	 * Updates a ModuleProgress entity from a ModuleProgressUpdateRequest DTO
	 * 
	 * @param request The ModuleProgressUpdateRequest DTO
	 * @param entity  The ModuleProgress entity to update
	 * @return Updated ModuleProgress entity
	 */
	public ModuleProgress updateFromDto(ModuleProgressUpdateRequest request, ModuleProgress entity) {
		if (request == null || entity == null) {
			return entity;
		}

		if (request.getFirstLearningDate() != null) {
			entity.setFirstLearningDate(request.getFirstLearningDate());
		}

		if (request.getCyclesStudied() != null) {
			entity.setCyclesStudied(request.getCyclesStudied());
		}

		if (request.getNextStudyDate() != null) {
			entity.setNextStudyDate(request.getNextStudyDate());
		}

		if (request.getPercentComplete() != null) {
			entity.setPercentComplete(request.getPercentComplete());
		}

		return entity;
	}
}