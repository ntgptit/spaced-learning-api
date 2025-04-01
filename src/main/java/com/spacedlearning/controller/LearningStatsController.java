package com.spacedlearning.controller;

import java.util.List;
import java.util.UUID;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.spacedlearning.dto.common.DataResponse;
import com.spacedlearning.dto.stats.LearningInsightDTO;
import com.spacedlearning.dto.stats.UserLearningStatsDTO;
import com.spacedlearning.entity.User;
import com.spacedlearning.exception.SpacedLearningException;
import com.spacedlearning.repository.UserRepository;
import com.spacedlearning.service.LearningStatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for Learning Statistics
 */
@RestController
@RequestMapping("/api/v1/stats")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Learning Statistics API", description = "Endpoints for learning statistics")
public class LearningStatsController {

    private static final String ERROR_USER_NOT_FOUND = "error.resource.notfound";
    private static final String ERROR_NOT_AUTHENTICATED = "error.auth.notAuthenticated";
    private static final String DEFAULT_USER_NOT_FOUND = "User not found";
    private static final String DEFAULT_NOT_AUTHENTICATED = "User not authenticated";

    private final LearningStatsService statsService;
    private final UserRepository userRepository;
    private final MessageSource messageSource;

    /**
     * Get dashboard statistics for current user
     *
     * @param refreshCache Whether to refresh the cache
     * @return Dashboard statistics
     */
    @GetMapping("/dashboard")
    @Operation(summary = "Get dashboard stats",
            description = "Retrieves learning statistics for the current user's dashboard")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Successfully retrieved dashboard statistics"),
            @ApiResponse(responseCode = "401", description = "User not authenticated")})
    public ResponseEntity<DataResponse<UserLearningStatsDTO>> getDashboardStats(
            @RequestParam(defaultValue = "false") boolean refreshCache) {

        log.debug("REST request to get dashboard stats for current user, refreshCache: {}",
                refreshCache);

        // Get current user
        final Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getName())) {
            final String message = messageSource.getMessage(ERROR_NOT_AUTHENTICATED, null,
                    DEFAULT_NOT_AUTHENTICATED, LocaleContextHolder.getLocale());
            throw SpacedLearningException.unauthorized(message);
        }

        final String email = authentication.getName();

        final User user = userRepository.findByEmail(email).orElseThrow(() -> {
            final String message =
                    messageSource.getMessage(ERROR_USER_NOT_FOUND, new Object[] {"User", email},
                            DEFAULT_USER_NOT_FOUND, LocaleContextHolder.getLocale());
            return SpacedLearningException.unauthorized(message);
        });

        // Get dashboard stats
        final UserLearningStatsDTO stats = statsService.getDashboardStats(user.getId());

        return ResponseEntity.ok(DataResponse.of(stats));
    }

    /**
     * Get dashboard statistics for a specific user (admin only)
     *
     * @param userId User ID
     * @param refreshCache Whether to refresh the cache
     * @return Dashboard statistics
     */
    @GetMapping("/users/{userId}/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get dashboard stats for user",
            description = "Retrieves learning statistics for a specific user's dashboard (admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Successfully retrieved dashboard statistics"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "User not found")})
    public ResponseEntity<DataResponse<UserLearningStatsDTO>> getUserDashboardStats(
            @PathVariable UUID userId, @RequestParam(defaultValue = "false") boolean refreshCache) {

        log.debug("REST request to get dashboard stats for user ID: {}, refreshCache: {}", userId,
                refreshCache);

        // Verify user exists
        if (!userRepository.existsById(userId)) {
            final String message =
                    messageSource.getMessage(ERROR_USER_NOT_FOUND, new Object[] {"User", userId},
                            DEFAULT_USER_NOT_FOUND, LocaleContextHolder.getLocale());
            throw SpacedLearningException.resourceNotFound(message, userId.toString());
        }

        // Get dashboard stats
        final UserLearningStatsDTO stats = statsService.getDashboardStats(userId);

        return ResponseEntity.ok(DataResponse.of(stats));
    }

    /**
     * Get learning insights for current user
     *
     * @return List of learning insights
     */
    @GetMapping("/insights")
    @Operation(summary = "Get learning insights",
            description = "Retrieves learning insights for the current user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Successfully retrieved learning insights"),
            @ApiResponse(responseCode = "401", description = "User not authenticated")})
    public ResponseEntity<DataResponse<List<LearningInsightDTO>>> getLearningInsights() {

        log.debug("REST request to get learning insights for current user");

        // Get current user
        final Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getName())) {
            final String message = messageSource.getMessage(ERROR_NOT_AUTHENTICATED, null,
                    DEFAULT_NOT_AUTHENTICATED, LocaleContextHolder.getLocale());
            throw SpacedLearningException.unauthorized(message);
        }

        final String email = authentication.getName();

        final User user = userRepository.findByEmail(email).orElseThrow(() -> {
            final String message =
                    messageSource.getMessage(ERROR_USER_NOT_FOUND, new Object[] {"User", email},
                            DEFAULT_USER_NOT_FOUND, LocaleContextHolder.getLocale());
            return SpacedLearningException.unauthorized(message);
        });

        // Get learning insights
        final List<LearningInsightDTO> insights = statsService.getLearningInsights(user.getId());

        return ResponseEntity.ok(DataResponse.of(insights));
    }

    /**
     * Get learning insights for a specific user (admin only)
     *
     * @param userId User ID
     * @return List of learning insights
     */
    @GetMapping("/users/{userId}/insights")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get learning insights for user",
            description = "Retrieves learning insights for a specific user (admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Successfully retrieved learning insights"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "User not found")})
    public ResponseEntity<DataResponse<List<LearningInsightDTO>>> getUserLearningInsights(
            @PathVariable UUID userId) {

        log.debug("REST request to get learning insights for user ID: {}", userId);

        // Verify user exists
        if (!userRepository.existsById(userId)) {
            final String message =
                    messageSource.getMessage(ERROR_USER_NOT_FOUND, new Object[] {"User", userId},
                            DEFAULT_USER_NOT_FOUND, LocaleContextHolder.getLocale());
            throw SpacedLearningException.resourceNotFound(message, userId.toString());
        }

        // Get learning insights
        final List<LearningInsightDTO> insights = statsService.getLearningInsights(userId);

        return ResponseEntity.ok(DataResponse.of(insights));
    }
}
