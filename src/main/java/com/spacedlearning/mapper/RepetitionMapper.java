// File: src/main/java/com/spacedlearning/mapper/RepetitionMapper.java
package com.spacedlearning.mapper;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.spacedlearning.dto.repetition.RepetitionCreateRequest;
import com.spacedlearning.dto.repetition.RepetitionResponse;
import com.spacedlearning.dto.repetition.RepetitionUpdateRequest;
import com.spacedlearning.entity.ModuleProgress;
import com.spacedlearning.entity.Repetition;

/**
 * Mapper for Repetition entity and DTOs
 */
@Component
public class RepetitionMapper extends AbstractGenericMapper<Repetition, RepetitionResponse> {

	@Override
	protected Repetition mapDtoToEntity(RepetitionResponse dto, Repetition entity) {
		if (dto.getRepetitionOrder() != null) {
			entity.setRepetitionOrder(dto.getRepetitionOrder());
		}
		if (dto.getStatus() != null) {
			entity.setStatus(dto.getStatus());
		}
		if (dto.getReviewDate() != null) {
			entity.setReviewDate(dto.getReviewDate());
		}
		return entity;
	}

	@Override
	protected RepetitionResponse mapToDto(Repetition entity) {
		return RepetitionResponse.builder().id(entity.getId()).moduleProgressId(entity.getModuleProgress().getId())
				.repetitionOrder(entity.getRepetitionOrder()).status(entity.getStatus())
				.reviewDate(entity.getReviewDate()).createdAt(entity.getCreatedAt()).updatedAt(entity.getUpdatedAt())
				.build();
	}

	@Override
	protected Repetition mapToEntity(RepetitionResponse dto) {
		final Repetition repetition = new Repetition();

		// ModuleProgress will be set separately
		repetition.setRepetitionOrder(dto.getRepetitionOrder());
		repetition.setStatus(dto.getStatus());
		repetition.setReviewDate(dto.getReviewDate());

		return repetition;
	}

	/**
	 * Maps a RepetitionCreateRequest DTO to a Repetition entity
	 * 
	 * @param request  The RepetitionCreateRequest DTO
	 * @param progress The ModuleProgress entity
	 * @return Repetition entity
	 */
	public Repetition toEntity(RepetitionCreateRequest request, ModuleProgress progress) {
		if (request == null) {
			return null;
		}

		final Repetition repetition = new Repetition();
		repetition.setModuleProgress(progress);
		repetition.setRepetitionOrder(request.getRepetitionOrder());
		repetition.setStatus(Optional.ofNullable(request.getStatus()).orElse(repetition.getStatus()));
		repetition.setReviewDate(request.getReviewDate());

		return repetition;
	}

	/**
	 * Updates a Repetition entity from a RepetitionUpdateRequest DTO
	 * 
	 * @param request The RepetitionUpdateRequest DTO
	 * @param entity  The Repetition entity to update
	 * @return Updated Repetition entity
	 */
	public Repetition updateFromDto(RepetitionUpdateRequest request, Repetition entity) {
		if (request == null || entity == null) {
			return entity;
		}

		if (request.getStatus() != null) {
			entity.setStatus(request.getStatus());
		}

		if (request.getReviewDate() != null) {
			entity.setReviewDate(request.getReviewDate());
		}

		return entity;
	}
}
