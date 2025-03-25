package com.spacedlearning.entity.enums;

/**
 * Enum representing possible difficulty levels.
 */
public enum DifficultyLevel {
	BEGINNER("BEGINNER"), INTERMEDIATE("INTERMEDIATE"), ADVANCED("ADVANCED"), EXPERT("EXPERT");

	private final String value;

	DifficultyLevel(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}