package com.spacedlearning.entity.enums;

/**
 * Enum representing possible book statuses.
 */
/**
 * Enum representing the status of a book.
 * <p>
 * The possible statuses are:
 * <ul>
 * <li>{@link #PUBLISHED} - Indicates that the book is published and available.</li>
 * <li>{@link #DRAFT} - Indicates that the book is in draft state and not yet published.</li>
 * <li>{@link #ARCHIVED} - Indicates that the book is archived and no longer actively used.</li>
 * </ul>
 */
public enum BookStatus {
	PUBLISHED("PUBLISHED"), DRAFT("DRAFT"), ARCHIVED("ARCHIVED");

	/**
	 * The string representation of the book status.
	 */
	private final String value;

	BookStatus(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
