package com.spacedlearning.dto.stats;

import com.spacedlearning.entity.enums.InsightType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningInsightDTO {

	private InsightType type;
	private String message;
	private String icon;
	private String color;
	private double dataPoint;
	private int priority;

}
