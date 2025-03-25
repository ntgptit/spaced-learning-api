package com.spacedlearning.exception;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class SpacedLearningException extends RuntimeException {

    private static final long serialVersionUID = 1509401079030096266L;
    private final HttpStatus status;

    public SpacedLearningException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public SpacedLearningException(String message, Throwable cause, HttpStatus status) {
        super(message, cause);
        this.status = status;
    }

    public static SpacedLearningException resourceNotFound(String resourceName, Object id) {
        return new SpacedLearningException(String.format("%s not found with id: %s", resourceName, id), HttpStatus.NOT_FOUND);
    }

    public static SpacedLearningException resourceNotFound(MessageSource messageSource, String resourceKey, Object id) {
        return new SpacedLearningException(messageSource.getMessage("error.resource.notfound", new Object[]{
                messageSource.getMessage(resourceKey, null, LocaleContextHolder.getLocale()), id
        }, LocaleContextHolder.getLocale()), HttpStatus.NOT_FOUND);
    }

    public static SpacedLearningException resourceAlreadyExists(String resourceName, String field, Object value) {
        return new SpacedLearningException(
            String.format("%s already exists with %s: %s", resourceName, field, value),
            HttpStatus.CONFLICT);
    }

    public static SpacedLearningException resourceAlreadyExists(
            MessageSource messageSource,
            String resourceKey,
            String field,
            Object value) {
        return new SpacedLearningException(messageSource.getMessage("error.resource.alreadyexists", new Object[]{
                messageSource.getMessage(resourceKey, null, LocaleContextHolder.getLocale()), field, value
        }, LocaleContextHolder.getLocale()), HttpStatus.CONFLICT);
    }

    public static SpacedLearningException validationError(String message) {
        return new SpacedLearningException(message, HttpStatus.BAD_REQUEST);
    }

    public static SpacedLearningException validationError(MessageSource messageSource, String key, Object... args) {
        return new SpacedLearningException(
            messageSource.getMessage(key, args, LocaleContextHolder.getLocale()),
            HttpStatus.BAD_REQUEST);
    }

    public static SpacedLearningException forbidden(String message) {
        return new SpacedLearningException(message, HttpStatus.FORBIDDEN);
    }

    public static SpacedLearningException forbidden(MessageSource messageSource, String key, Object... args) {
        return new SpacedLearningException(
            messageSource.getMessage(key, args, LocaleContextHolder.getLocale()),
            HttpStatus.FORBIDDEN);
    }
}