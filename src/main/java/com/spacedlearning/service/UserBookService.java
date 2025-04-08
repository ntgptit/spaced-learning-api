package com.spacedlearning.service;

import java.util.List;
import java.util.UUID;

import com.spacedlearning.dto.book.BookSummaryResponse;
import com.spacedlearning.dto.user_books.UserBookShareInfo;

/**
 * Service interface for User-Book operations
 */
public interface UserBookService {

    /**
     * Get all books for a user
     *
     * @param userId User ID
     * @return List of book summaries
     */
    List<BookSummaryResponse> getBooksForUser(UUID userId);

    /**
     * Get information about all shared books
     *
     * @return List of shared book information
     */
    List<UserBookShareInfo> getAllSharedBooks();
}