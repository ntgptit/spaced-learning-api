// File: src/main/java/com/spacedlearning/controller/BookController.java
package com.spacedlearning.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.spacedlearning.dto.book.BookCreateRequest;
import com.spacedlearning.dto.book.BookDetailResponse;
import com.spacedlearning.dto.book.BookSummaryResponse;
import com.spacedlearning.dto.book.BookUpdateRequest;
import com.spacedlearning.dto.common.DataResponse;
import com.spacedlearning.dto.common.PageResponse;
import com.spacedlearning.dto.common.SuccessResponse;
import com.spacedlearning.entity.enums.BookStatus;
import com.spacedlearning.entity.enums.DifficultyLevel;
import com.spacedlearning.service.BookService;
import com.spacedlearning.util.PageUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for Book operations
 */
@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Book API", description = "Endpoints for managing books")
public class BookController {

    private final BookService bookService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create book", description = "Creates a new book")
    public ResponseEntity<DataResponse<BookDetailResponse>> createBook(@Valid @RequestBody BookCreateRequest request) {
        log.debug("REST request to create book: {}", request);
        final BookDetailResponse createdBook = bookService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(DataResponse.of(createdBook));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete book", description = "Deletes a book by ID")
    public ResponseEntity<SuccessResponse> deleteBook(@PathVariable UUID id) {
        log.debug("REST request to delete book with ID: {}", id);
        bookService.delete(id);
        return ResponseEntity.ok(SuccessResponse.of("Book deleted successfully"));
    }

    @GetMapping("/filter")
    @Operation(summary = "Filter books", description = "Filters books by status, difficulty level, and category")
    public ResponseEntity<PageResponse<BookSummaryResponse>> filterBooks(
            @RequestParam(required = false) BookStatus status,
            @RequestParam(required = false) DifficultyLevel difficultyLevel,
            @RequestParam(required = false) String category, @PageableDefault(size = 20) Pageable pageable) {
        log.debug("REST request to filter books - status: {}, difficultyLevel: {}, category: {}, pageable: {}", status,
                difficultyLevel, category, pageable);
        final Page<BookSummaryResponse> page = bookService.findByFilters(status, difficultyLevel, category, pageable);
        return ResponseEntity.ok(PageUtils.createPageResponse(page, pageable));
    }

    @GetMapping
    @Operation(summary = "Get all books", description = "Retrieves a paginated list of all books")
    public ResponseEntity<PageResponse<BookSummaryResponse>> getAllBooks(
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("REST request to get all books, pageable: {}", pageable);
        final Page<BookSummaryResponse> page = bookService.findAll(pageable);
        return ResponseEntity.ok(PageUtils.createPageResponse(page, pageable));
    }

    @GetMapping("/categories")
    @Operation(summary = "Get all categories", description = "Retrieves a list of all unique book categories")
    public ResponseEntity<DataResponse<List<String>>> getAllCategories() {
        log.debug("REST request to get all book categories");
        final List<String> categories = bookService.getAllCategories();
        return ResponseEntity.ok(DataResponse.of(categories));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get book by ID", description = "Retrieves a book by its ID with detailed information")
    public ResponseEntity<DataResponse<BookDetailResponse>> getBook(@PathVariable UUID id) {
        log.debug("REST request to get book with ID: {}", id);
        final BookDetailResponse book = bookService.findById(id);
        return ResponseEntity.ok(DataResponse.of(book));
    }

    @GetMapping("/search")
    @Operation(summary = "Search books", description = "Searches books by name")
    public ResponseEntity<PageResponse<BookSummaryResponse>> searchBooks(
            @RequestParam String query,
            @PageableDefault(size = 20) Pageable pageable) {
        log.debug("REST request to search books with query: {}, pageable: {}", query, pageable);
        final Page<BookSummaryResponse> page = bookService.searchByName(query, pageable);
        return ResponseEntity.ok(PageUtils.createPageResponse(page, pageable));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update book", description = "Updates an existing book")
    public ResponseEntity<DataResponse<BookDetailResponse>> updateBook(@PathVariable UUID id,
            @Valid @RequestBody BookUpdateRequest request) {
        log.debug("REST request to update book with ID: {}, request: {}", id, request);
        final BookDetailResponse updatedBook = bookService.update(id, request);
        return ResponseEntity.ok(DataResponse.of(updatedBook));
    }

    @PostMapping("/{id}/share")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Share book with users", description = "Shares a book with specified users")
    public ResponseEntity<DataResponse<Integer>> shareBookWithUsers(
            @PathVariable("id") UUID bookId,
            @RequestBody List<UUID> userIds) {
        log.debug("REST request to share book ID: {} with users: {}", bookId, userIds);
        final int sharedCount = bookService.shareBookWithUsers(bookId, userIds);
        return ResponseEntity.ok(DataResponse.of(sharedCount));
    }

    @PostMapping("/{id}/unshare")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Unshare book from users", description = "Removes book access from specified users")
    public ResponseEntity<DataResponse<Integer>> unshareBookFromUsers(
            @PathVariable("id") UUID bookId,
            @RequestBody List<UUID> userIds) {
        log.debug("REST request to unshare book ID: {} from users: {}", bookId, userIds);
        final int unsharedCount = bookService.unshareBookFromUsers(bookId, userIds);
        return ResponseEntity.ok(DataResponse.of(unsharedCount));
    }

    @GetMapping("/{id}/users")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get users with access", description = "Gets all users who have access to a book")
    public ResponseEntity<DataResponse<List<UUID>>> getUsersWithAccess(@PathVariable("id") UUID bookId) {
        log.debug("REST request to get users with access to book ID: {}", bookId);
        final List<UUID> userIds = bookService.getUsersWithAccessToBook(bookId);
        return ResponseEntity.ok(DataResponse.of(userIds));
    }
}