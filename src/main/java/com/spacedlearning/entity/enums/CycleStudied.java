package com.spacedlearning.entity.enums;

/**
 * Enum representing the cycles studied status.
 */
public enum CycleStudied {
	FIRST_TIME("First time"), FIRST_REVIEW("1st review"), SECOND_REVIEW("2nd review"), THIRD_REVIEW("3rd review"),
	MORE_THAN_THREE_REVIEWS("More than 3 reviews");

	private final String value;

	CycleStudied(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
