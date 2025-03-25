package com.spacedlearning.entity.enums;

/**
 * Enum representing possible book statuses.
 */
public enum BookStatus {
	PUBLISHED("PUBLISHED"), DRAFT("DRAFT"), ARCHIVED("ARCHIVED");

	private final String value;

	BookStatus(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}