package com.spacedlearning.exception;

import java.util.Objects;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class SpacedLearningException extends RuntimeException {

    private static final long serialVersionUID = 1509401079030096266L;
    /**
     * Creates a forbidden exception with a message from MessageSource
     *
     * @param messageSource The message source
     * @param key The message key
     * @param args The message arguments
     * @return The exception
     */
    public static SpacedLearningException forbidden(
            MessageSource messageSource, String key, Object... args) {

        Objects.requireNonNull(messageSource, "MessageSource must not be null");
        Objects.requireNonNull(key, "Message key must not be null");

        return new SpacedLearningException(
            messageSource.getMessage(key, args, LocaleContextHolder.getLocale()),
            HttpStatus.FORBIDDEN);
    }

    /**
     * Creates a forbidden exception with a custom message
     *
     * @param message The error message
     * @return The exception
     */
    public static SpacedLearningException forbidden(String message) {
        Objects.requireNonNull(message, "Message must not be null");
        return new SpacedLearningException(message, HttpStatus.FORBIDDEN);
    }

    /**
     * Creates a resource already exists exception with a message from MessageSource
     *
     * @param messageSource The message source
     * @param resourceKey The resource key
     * @param field The field name
     * @param value The field value
     * @return The exception
     */
    public static SpacedLearningException resourceAlreadyExists(
            MessageSource messageSource,
            String resourceKey,
            String field,
            Object value) {

        Objects.requireNonNull(messageSource, "MessageSource must not be null");
        Objects.requireNonNull(resourceKey, "Resource key must not be null");
        Objects.requireNonNull(field, "Field name must not be null");

        final String resourceName = messageSource.getMessage(
                resourceKey,
                null,
                resourceKey,
                LocaleContextHolder.getLocale());

        return new SpacedLearningException(messageSource.getMessage(
                "error.resource.alreadyexists",
                new Object[]{ resourceName, field, value },
                LocaleContextHolder.getLocale()),
                HttpStatus.CONFLICT);
    }

	/**
     * Creates a resource already exists exception with a custom message
     *
     * @param resourceName The name of the resource
     * @param field The field name
     * @param value The field value
     * @return The exception
     */
    public static SpacedLearningException resourceAlreadyExists(
            String resourceName, String field, Object value) {

        Objects.requireNonNull(resourceName, "Resource name must not be null");
        Objects.requireNonNull(field, "Field name must not be null");

        return new SpacedLearningException(
            String.format("%s already exists with %s: %s", resourceName, field, value),
            HttpStatus.CONFLICT);
    }

	/**
	 * Creates a resource not found exception with a message from MessageSource
	 * 
	 * @param messageSource The message source
	 * @param resourceKey   The resource key
	 * @param id            The resource ID
	 * @return The exception
	 */
	public static SpacedLearningException resourceNotFound(MessageSource messageSource, String resourceKey, Object id) {

		Objects.requireNonNull(messageSource, "MessageSource must not be null");
		Objects.requireNonNull(resourceKey, "Resource key must not be null");

		final String resourceName = messageSource.getMessage(resourceKey, null, resourceKey,
				LocaleContextHolder.getLocale());

		return new SpacedLearningException(messageSource.getMessage("error.resource.notfound",
				new Object[] { resourceName, id }, LocaleContextHolder.getLocale()), HttpStatus.NOT_FOUND);
    }

	/**
     * Creates a resource not found exception with a custom message
     *
     * @param resourceName The name of the resource
     * @param id The resource ID
     * @return The exception
     */
    public static SpacedLearningException resourceNotFound(String resourceName, Object id) {
        Objects.requireNonNull(resourceName, "Resource name must not be null");
        return new SpacedLearningException(
                String.format("%s not found with id: %s", resourceName, id),
                HttpStatus.NOT_FOUND);
    }

	/**
     * Creates an unauthorized exception with a message from MessageSource
     *
     * @param messageSource The message source
     * @param key The message key
     * @param args The message arguments
     * @return The exception
     */
    public static SpacedLearningException unauthorized(
            MessageSource messageSource, String key, Object... args) {

        Objects.requireNonNull(messageSource, "MessageSource must not be null");
        Objects.requireNonNull(key, "Message key must not be null");

        return new SpacedLearningException(
            messageSource.getMessage(key, args, LocaleContextHolder.getLocale()),
            HttpStatus.UNAUTHORIZED);
    }

	/**
     * Creates an unauthorized exception with a custom message
     *
     * @param message The error message
     * @return The exception
     */
    public static SpacedLearningException unauthorized(String message) {
        Objects.requireNonNull(message, "Message must not be null");
        return new SpacedLearningException(message, HttpStatus.UNAUTHORIZED);
    }

	/**
	 * Creates a validation error exception with a message from MessageSource
	 * 
	 * @param messageSource The message source
	 * @param key           The message key
	 * @param args          The message arguments
	 * @return The exception
	 */
	public static SpacedLearningException validationError(MessageSource messageSource, String key, Object... args) {

		Objects.requireNonNull(messageSource, "MessageSource must not be null");
		Objects.requireNonNull(key, "Message key must not be null");

        return new SpacedLearningException(
            messageSource.getMessage(key, args, LocaleContextHolder.getLocale()),
            HttpStatus.BAD_REQUEST);
    }

	/**
     * Creates a validation error exception with a custom message
     *
     * @param message The error message
     * @return The exception
     */
    public static SpacedLearningException validationError(String message) {
        Objects.requireNonNull(message, "Message must not be null");
        return new SpacedLearningException(message, HttpStatus.BAD_REQUEST);
    }

	private final HttpStatus status;

	public SpacedLearningException(String message, HttpStatus status) {
		super(message);
		this.status = status;
	}

	public SpacedLearningException(String message, Throwable cause, HttpStatus status) {
        super(message, cause);
        this.status = status;
    }
}