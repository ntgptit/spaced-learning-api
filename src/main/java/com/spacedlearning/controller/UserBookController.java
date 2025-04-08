package com.spacedlearning.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.spacedlearning.dto.book.BookSummaryResponse;
import com.spacedlearning.dto.common.DataResponse;
import com.spacedlearning.dto.user_books.UserBookShareInfo;
import com.spacedlearning.entity.User;
import com.spacedlearning.exception.SpacedLearningException;
import com.spacedlearning.repository.UserRepository;
import com.spacedlearning.service.UserBookService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for User-Book operations
 */
@RestController
@RequestMapping("/api/v1/user-books")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Book API", description = "Endpoints for managing user book access")
public class UserBookController {

    private final UserBookService userBookService;
    private final UserRepository userRepository;

    @GetMapping("/my-books")
    @Operation(summary = "Get current user's books", description = "Retrieves all books available to the current user")
    public ResponseEntity<DataResponse<List<BookSummaryResponse>>> getCurrentUserBooks() {
        log.debug("REST request to get books for current user");

        // Get current user ID
        final UUID userId = getCurrentUserId();

        final List<BookSummaryResponse> books = userBookService.getBooksForUser(userId);
        return ResponseEntity.ok(DataResponse.of(books));
    }

    @GetMapping("/admin/shared-books")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all shared book info", description = "Retrieves information about all book shares (admin only)")
    public ResponseEntity<DataResponse<List<UserBookShareInfo>>> getAllSharedBooks() {
        log.debug("REST request to get all shared book information");

        final List<UserBookShareInfo> shareInfo = userBookService.getAllSharedBooks();
        return ResponseEntity.ok(DataResponse.of(shareInfo));
    }

    private UUID getCurrentUserId() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() ||
                "anonymousUser".equals(authentication.getName())) {
            throw SpacedLearningException.unauthorized("User not authenticated");
        }

        final String username = authentication.getName();
        final User user = userRepository.findByUsernameOrEmail(username)
                .orElseThrow(() -> SpacedLearningException.unauthorized("User not found"));

        return user.getId();
    }

    /**
     * DTO for book sharing information
     */

}