package com.spacedlearning.exception;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Global exception handler for the application. Centralizes exception handling
 * for all controllers. Includes improved logging for exception origins.
 */
@RestControllerAdvice
@Slf4j // Sử dụng SLF4j để ghi log
@RequiredArgsConstructor
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private final MessageSource messageSource;

    /**
     * Extract path from WebRequest
     *
     * @param request WebRequest
     * @return Path string
     */
    private String extractPath(final WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }

    /**
     * Helper method to get the origin (class, method, line) of the exception.
     * Note: This gets the top of the stack trace, which is usually where the
     * exception was thrown or last caught before reaching the handler.
     *
     * @param ex The exception
     * @return A string representation of the origin, or "Unknown Origin".
     */
    private String getOriginatingClassName(Throwable ex) {
        if (ex != null && ex.getStackTrace() != null && ex.getStackTrace().length > 0) {
            final StackTraceElement element = ex.getStackTrace()[0];
            // Trả về định dạng Class.method(FileName:LineNumber)
            return String.format("%s.%s(%s:%d)",
                    element.getClassName(),
                    element.getMethodName(),
                    element.getFileName(),
                    element.getLineNumber());
        }
        return "Unknown Origin";
    }

    /**
     * Handle access denied exceptions
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDeniedException(final AccessDeniedException ex,
            final WebRequest request) {
        // Log lỗi với thông tin origin và đầy đủ stack trace (do truyền 'ex' vào cuối)
        log.error("Access denied - Origin: [{}]", getOriginatingClassName(ex), ex);

        final String message = messageSource.getMessage(
                "error.auth.accessDenied",
                null,
                "Access denied: insufficient permissions",
                LocaleContextHolder.getLocale());

        final ApiError apiError = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.FORBIDDEN.value())
                .error(HttpStatus.FORBIDDEN.getReasonPhrase())
                .message(message)
                .path(extractPath(request))
                .build();

        return new ResponseEntity<>(apiError, HttpStatus.FORBIDDEN);
    }

    /**
     * Fallback for all other exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAll(final Exception ex, final WebRequest request) {
        // Log lỗi với thông tin origin và đầy đủ stack trace
        log.error("Unhandled exception - Origin: [{}]", getOriginatingClassName(ex), ex);

        final String message = messageSource.getMessage(
                "error.server.internal",
                null,
                "An unexpected error occurred",
                LocaleContextHolder.getLocale());

        final ApiError apiError = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message(message)
                .path(extractPath(request))
                .build();

        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Handle authentication exceptions
     */
    @ExceptionHandler({ AuthenticationException.class, BadCredentialsException.class })
    public ResponseEntity<ApiError> handleAuthenticationException(final Exception ex, final WebRequest request) {
        // Log lỗi với thông tin origin và đầy đủ stack trace
        log.error("Authentication error - Origin: [{}]", getOriginatingClassName(ex), ex);

        final String message = messageSource.getMessage(
                "error.auth.invalidCredentials",
                null,
                "Authentication failed: " + ex.getMessage(), // Có thể giữ lại message gốc ở đây nếu muốn
                LocaleContextHolder.getLocale());

        final ApiError apiError = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .message(message)
                .path(extractPath(request))
                .build();

        return new ResponseEntity<>(apiError, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handle constraint violation exceptions (validation errors)
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolationException(
            final ConstraintViolationException ex,
            final WebRequest request) {

        // Log lỗi với thông tin origin và đầy đủ stack trace
        log.error("Constraint violation - Origin: [{}]", getOriginatingClassName(ex), ex);

        final Map<String, String> errors = ex.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        violation -> violation.getPropertyPath().toString(),
                        ConstraintViolation::getMessage,
                        (error1, error2) -> error1 + "; " + error2));

        final String message = messageSource.getMessage(
                "error.validation.general",
                null,
                "Validation failed",
                LocaleContextHolder.getLocale());

        final ApiError apiError = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(message)
                .path(extractPath(request))
                .errors(errors)
                .build();

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle data integrity violations (e.g., unique constraint violations)
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrityViolationException(
            final DataIntegrityViolationException ex,
            final WebRequest request) {

        // Log lỗi với thông tin origin và đầy đủ stack trace
        log.error("Data integrity violation - Origin: [{}]", getOriginatingClassName(ex), ex);

        final String message = messageSource.getMessage(
                "error.database.constraint",
                null,
                "Database constraint violation",
                LocaleContextHolder.getLocale());

        final ApiError apiError = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .error(HttpStatus.CONFLICT.getReasonPhrase())
                .message(message)
                .path(extractPath(request))
                .build();

        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }

    /**
     * Handle entity not found exceptions
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiError> handleEntityNotFoundException(
            final EntityNotFoundException ex,
            final WebRequest request) {

        // Log lỗi với thông tin origin và đầy đủ stack trace
        log.error("Entity not found - Origin: [{}]", getOriginatingClassName(ex), ex);

        final ApiError apiError = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(ex.getMessage()) // Giữ lại message cụ thể của exception này
                .path(extractPath(request))
                .build();

        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    /**
     * Handle validation exceptions from @Valid
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            @NonNull final MethodArgumentNotValidException ex,
            @NonNull final HttpHeaders headers,
            @NonNull final HttpStatusCode status,
            @NonNull final WebRequest request) {

        // Log lỗi với thông tin origin và đầy đủ stack trace
        log.error("Method argument validation error - Origin: [{}]", getOriginatingClassName(ex), ex);

        // Group field errors by field name
        final Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fieldError -> fieldError.getDefaultMessage() == null ? "Invalid value"
                                : fieldError.getDefaultMessage(),
                        // If multiple errors for same field, join them
                        (error1, error2) -> error1 + "; " + error2));

        final String message = messageSource.getMessage(
                "error.validation.general",
                null,
                "Validation failed",
                LocaleContextHolder.getLocale());

        final ApiError apiError = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(message)
                .path(extractPath(request))
                .errors(errors)
                .build();

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle method argument type mismatch
     */
    @SuppressWarnings("null") // Giữ lại annotation này nếu cần thiết
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleMethodArgumentTypeMismatch(
            final MethodArgumentTypeMismatchException ex,
            final WebRequest request) {

        // Log lỗi với thông tin origin và đầy đủ stack trace
        log.error("Method argument type mismatch - Origin: [{}]", getOriginatingClassName(ex), ex);

        final Map<String, String> errors = new HashMap<>();
        final String requiredType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown";

        errors.put(ex.getName(), "Should be of type " + requiredType);

        final String message = messageSource.getMessage(
                "error.validation.typeMismatch",
                new Object[] { ex.getName(), requiredType },
                "Type mismatch for parameter",
                LocaleContextHolder.getLocale());

        final ApiError apiError = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(message)
                .path(extractPath(request))
                .errors(errors)
                .build();

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle missing request parameters
     */
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            @NonNull final MissingServletRequestParameterException ex,
            @NonNull final HttpHeaders headers,
            @NonNull final HttpStatusCode status,
            @NonNull final WebRequest request) {

        // Log lỗi với thông tin origin và đầy đủ stack trace
        log.error("Missing servlet request parameter - Origin: [{}]", getOriginatingClassName(ex), ex);

        final Map<String, String> errors = new HashMap<>();
        errors.put(ex.getParameterName(), "Parameter is missing");

        final String message = messageSource.getMessage(
                "error.validation.missingParameter",
                new Object[] { ex.getParameterName() },
                "Required request parameter is missing",
                LocaleContextHolder.getLocale());

        final ApiError apiError = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(message)
                .path(extractPath(request))
                .errors(errors)
                .build();

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle custom SpacedLearning exceptions
     */
    @ExceptionHandler(SpacedLearningException.class)
    public ResponseEntity<ApiError> handleSpacedLearningException(
            final SpacedLearningException ex,
            final WebRequest request) {

        // Log lỗi với thông tin origin và đầy đủ stack trace
        log.error("SpacedLearningException (Business exception) - Origin: [{}]", getOriginatingClassName(ex), ex);

        final ApiError apiError = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .status(ex.getStatus().value())
                .error(ex.getStatus().getReasonPhrase())
                .message(ex.getMessage()) // Giữ lại message cụ thể của exception này
                .path(extractPath(request))
                .build();

        return new ResponseEntity<>(apiError, ex.getStatus());
    }
}