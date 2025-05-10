package com.spacedlearning.exception;

import java.util.Objects;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class SpacedLearningException extends RuntimeException {

    private static final long serialVersionUID = 1509401079030096266L;

    public static SpacedLearningException forbidden(MessageSource messageSource, String key, Object... args) {
        validate(messageSource, key);
        return new SpacedLearningException(
                messageSource.getMessage(key, args, LocaleContextHolder.getLocale()),
                HttpStatus.FORBIDDEN);
    }

    public static SpacedLearningException forbidden(String message) {
        return new SpacedLearningException(validateMsg(message), HttpStatus.FORBIDDEN);
    }

    public static SpacedLearningException resourceAlreadyExists(
            MessageSource messageSource, String resourceKey, String field, Object value) {

        validate(messageSource, resourceKey, field);
        final var resourceName = messageSource.getMessage(resourceKey, null, resourceKey, LocaleContextHolder
                .getLocale());

        return new SpacedLearningException(
                messageSource.getMessage("error.resource.alreadyexists",
                        new Object[] { resourceName, field, value },
                        LocaleContextHolder.getLocale()),
                HttpStatus.CONFLICT);
    }

    // ========== FACTORY METHODS ==========

    public static SpacedLearningException resourceAlreadyExists(String resourceName, String field, Object value) {
        validate(resourceName, field);
        return new SpacedLearningException(
                String.format("%s already exists with %s: %s", resourceName, field, value),
                HttpStatus.CONFLICT);
    }

    public static SpacedLearningException resourceNotFound(MessageSource messageSource, String resourceKey, Object id) {
        validate(messageSource, resourceKey);
        final var resourceName = messageSource.getMessage(resourceKey, null, resourceKey, LocaleContextHolder
                .getLocale());

        return new SpacedLearningException(
                messageSource.getMessage("error.resource.notfound",
                        new Object[] { resourceName, id }, LocaleContextHolder.getLocale()),
                HttpStatus.NOT_FOUND);
    }

    public static SpacedLearningException resourceNotFound(String resourceName, Object id) {
        validate(resourceName);
        return new SpacedLearningException(
                String.format("%s not found with id: %s", resourceName, id),
                HttpStatus.NOT_FOUND);
    }

    public static SpacedLearningException unauthorized(MessageSource messageSource, String key, Object... args) {
        validate(messageSource, key);
        return new SpacedLearningException(
                messageSource.getMessage(key, args, LocaleContextHolder.getLocale()),
                HttpStatus.UNAUTHORIZED);
    }

    public static SpacedLearningException unauthorized(String message) {
        return new SpacedLearningException(validateMsg(message), HttpStatus.UNAUTHORIZED);
    }

    private static void validate(Object... args) {
        for (final Object arg : args) {
            Objects.requireNonNull(arg, arg + " must not be null");
        }
    }

    private static String validateMsg(String msg) {
        return Objects.requireNonNull(msg, "Message must not be null");
    }

    public static SpacedLearningException validationError(MessageSource messageSource, String key, Object... args) {
        validate(messageSource, key);
        return new SpacedLearningException(
                messageSource.getMessage(key, args, LocaleContextHolder.getLocale()),
                HttpStatus.BAD_REQUEST);
    }

    public static SpacedLearningException validationError(String message) {
        return new SpacedLearningException(validateMsg(message), HttpStatus.BAD_REQUEST);
    }

    private final HttpStatus status;

    // ========== VALIDATION HELPERS ==========

    public SpacedLearningException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public SpacedLearningException(String message, Throwable cause, HttpStatus status) {
        super(message, cause);
        this.status = status;
    }
}
