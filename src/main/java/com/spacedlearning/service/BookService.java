package com.spacedlearning.service;

import com.spacedlearning.dto.book.BookCreateRequest;
import com.spacedlearning.dto.book.BookDetailResponse;
import com.spacedlearning.dto.book.BookSummaryResponse;
import com.spacedlearning.dto.book.BookUpdateRequest;
import com.spacedlearning.entity.enums.BookStatus;
import com.spacedlearning.entity.enums.DifficultyLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for Book operations
 */
public interface BookService {

    /**
     * Create a new book
     *
     * @param request Book creation request
     * @return Created book detail response
     */
    BookDetailResponse create(BookCreateRequest request);

    /**
     * Delete a book
     *
     * @param id Book ID
     */
    void delete(UUID id);

    /**
     * Find all books with pagination
     *
     * @param pageable Pagination information
     * @return Page of book summaries
     */
    Page<BookSummaryResponse> findAll(Pageable pageable);

    /**
     * Find books with filters
     *
     * @param status          Book status
     * @param difficultyLevel Difficulty level
     * @param category        Category
     * @param pageable        Pagination information
     * @return Page of book summaries
     */
    Page<BookSummaryResponse> findByFilters(BookStatus status, DifficultyLevel difficultyLevel, String category,
                                            Pageable pageable);

    /**
     * Find book by ID
     *
     * @param id Book ID
     * @return Book detail response
     */
    BookDetailResponse findById(UUID id);

    /**
     * Get all categories
     *
     * @return List of unique categories
     */
    List<String> getAllCategories();

    /**
     * Search books by name
     *
     * @param searchTerm Search term
     * @param pageable   Pagination information
     * @return Page of book summaries
     */
    Page<BookSummaryResponse> searchByName(String searchTerm, Pageable pageable);

    /**
     * Update a book
     *
     * @param id      Book ID
     * @param request Book update request
     * @return Updated book detail response
     */
    BookDetailResponse update(UUID id, BookUpdateRequest request);

    /**
     * Share a book with users
     *
     * @param bookId  Book ID
     * @param userIds List of user IDs to share with
     * @return Number of users the book was shared with
     */
    int shareBookWithUsers(UUID bookId, List<UUID> userIds);

    /**
     * Unshare a book from users
     *
     * @param bookId  Book ID
     * @param userIds List of user IDs to unshare from
     * @return Number of users the book was unshared from
     */
    int unshareBookFromUsers(UUID bookId, List<UUID> userIds);

    /**
     * Get all users a book is shared with
     *
     * @param bookId Book ID
     * @return List of user IDs
     */
    List<UUID> getUsersWithAccessToBook(UUID bookId);
}