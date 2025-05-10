package com.spacedlearning.mapper;

import org.springframework.stereotype.Component;

import com.spacedlearning.dto.repetition.RepetitionCreateRequest;
import com.spacedlearning.dto.repetition.RepetitionResponse;
import com.spacedlearning.dto.repetition.RepetitionUpdateRequest;
import com.spacedlearning.entity.ModuleProgress;
import com.spacedlearning.entity.Repetition;
import com.spacedlearning.entity.enums.RepetitionStatus;

@Component
public class RepetitionMapper extends AbstractGenericMapper<Repetition, RepetitionResponse> {

    @Override
    protected Repetition mapDtoToEntity(RepetitionResponse dto, Repetition entity) {
        if ((dto == null) || (entity == null)) {
            return entity;
        }

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
        if (entity == null) {
            return null;
        }

        return RepetitionResponse.builder()
                .id(entity.getId())
                .moduleProgressId(entity.getModuleProgress().getId())
                .repetitionOrder(entity.getRepetitionOrder())
                .status(entity.getStatus())
                .reviewDate(entity.getReviewDate())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    @Override
    protected Repetition mapToEntity(RepetitionResponse dto) {
        if (dto == null) {
            return null;
        }

        final var repetition = new Repetition();
        repetition.setRepetitionOrder(dto.getRepetitionOrder());
        repetition.setStatus(dto.getStatus());
        repetition.setReviewDate(dto.getReviewDate());

        return repetition;
    }

    public Repetition toEntity(RepetitionCreateRequest request, ModuleProgress progress) {
        if ((request == null) || (progress == null)) {
            return null;
        }

        final var repetition = new Repetition();
        repetition.setModuleProgress(progress);
        repetition.setRepetitionOrder(request.getRepetitionOrder());
        repetition.setStatus(request.getStatus() != null ? request.getStatus() : RepetitionStatus.NOT_STARTED);
        repetition.setReviewDate(request.getReviewDate());

        return repetition;
    }

    public Repetition updateFromDto(RepetitionUpdateRequest request, Repetition entity) {
        if ((request == null) || (entity == null)) {
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
