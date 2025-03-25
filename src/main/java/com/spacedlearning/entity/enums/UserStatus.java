package com.spacedlearning.entity.enums;

/**
 * Enum representing possible user statuses.
 */
public enum UserStatus {
	ACTIVE("ACTIVE"), INACTIVE("INACTIVE"), SUSPENDED("SUSPENDED");

	private final String value;

	UserStatus(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}