package com.spacedlearning.mapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;
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
	protected ModuleProgress mapDtoToEntity(final ModuleProgressDetailResponse dto, final ModuleProgress entity) {
		if (dto == null || entity == null) {
			return entity;
		}

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
	protected ModuleProgressDetailResponse mapToDto(final ModuleProgress entity) {
		if (entity == null) {
			return null;
		}

		final List<RepetitionResponse> repetitions = repetitionMapper.toDtoList(entity.getRepetitions());

		return ModuleProgressDetailResponse.builder().id(entity.getId()).moduleId(entity.getModule().getId())
				.moduleTitle(entity.getModule().getTitle()).userId(entity.getUser().getId())
				.userName(entity.getUser().getName()).firstLearningDate(entity.getFirstLearningDate())
				.cyclesStudied(entity.getCyclesStudied()).nextStudyDate(entity.getNextStudyDate())
				.percentComplete(entity.getPercentComplete()).createdAt(entity.getCreatedAt())
				.updatedAt(entity.getUpdatedAt()).repetitions(repetitions).build();
	}

	@Override
	protected ModuleProgress mapToEntity(final ModuleProgressDetailResponse dto) {
		if (dto == null) {
			return null;
		}

		final ModuleProgress progress = new ModuleProgress();

		// Module and User will be set separately
		progress.setFirstLearningDate(dto.getFirstLearningDate());
		progress.setCyclesStudied(dto.getCyclesStudied());
		progress.setNextStudyDate(dto.getNextStudyDate());
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
	public ModuleProgress toEntity(final ModuleProgressCreateRequest request, final Module module, final User user) {

		if (request == null) {
			return null;
		}

		Objects.requireNonNull(module, "Module must not be null");
		Objects.requireNonNull(user, "User must not be null");

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
	public ModuleProgressSummaryResponse toSummaryDto(final ModuleProgress entity) {
		if (entity == null) {
			return null;
		}

		return ModuleProgressSummaryResponse.builder().id(entity.getId()).moduleId(entity.getModule().getId())
				.userId(entity.getUser().getId()).firstLearningDate(entity.getFirstLearningDate())
				.cyclesStudied(entity.getCyclesStudied()).nextStudyDate(entity.getNextStudyDate())
				.percentComplete(entity.getPercentComplete()).createdAt(entity.getCreatedAt())
				.updatedAt(entity.getUpdatedAt()).repetitionCount(CollectionUtils.size(entity.getRepetitions()))
				.build();
	}

	/**
	 * Maps a list of ModuleProgress entities to a list of
	 * ModuleProgressSummaryResponse DTOs
	 * 
	 * @param entities The ModuleProgress entities
	 * @return List of ModuleProgressSummaryResponse DTOs
	 */
	public List<ModuleProgressSummaryResponse> toSummaryDtoList(final List<ModuleProgress> entities) {
		if (CollectionUtils.isEmpty(entities)) {
			return Collections.emptyList();
		}

		return entities.stream().filter(Objects::nonNull).map(this::toSummaryDto).toList();
	}

	/**
	 * Updates a ModuleProgress entity from a ModuleProgressUpdateRequest DTO
	 * 
	 * @param request The ModuleProgressUpdateRequest DTO
	 * @param entity  The ModuleProgress entity to update
	 * @return Updated ModuleProgress entity
	 */
	public ModuleProgress updateFromDto(final ModuleProgressUpdateRequest request, final ModuleProgress entity) {
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