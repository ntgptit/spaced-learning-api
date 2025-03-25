package com.spacedlearning.entity.enums;

/**
 * Enum representing the status of repetition.
 */
public enum RepetitionStatus {
	NOT_STARTED("NOT_STARTED"), COMPLETED("COMPLETED"), SKIPPED("SKIPPED");

	private final String value;

	RepetitionStatus(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}