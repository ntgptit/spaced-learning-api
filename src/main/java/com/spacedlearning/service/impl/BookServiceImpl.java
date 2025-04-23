package com.spacedlearning.service.impl;

import com.spacedlearning.dto.book.BookCreateRequest;
import com.spacedlearning.dto.book.BookDetailResponse;
import com.spacedlearning.dto.book.BookSummaryResponse;
import com.spacedlearning.dto.book.BookUpdateRequest;
import com.spacedlearning.entity.Book;
import com.spacedlearning.entity.User;
import com.spacedlearning.entity.enums.BookStatus;
import com.spacedlearning.entity.enums.DifficultyLevel;
import com.spacedlearning.exception.SpacedLearningException;
import com.spacedlearning.mapper.BookMapper;
import com.spacedlearning.repository.BookRepository;
import com.spacedlearning.repository.UserRepository;
import com.spacedlearning.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Implementation of BookService
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BookServiceImpl implements BookService {

    public static final String BOOK_ID_MUST_NOT_BE_NULL = "Book ID must not be null";
    public static final String RESOURCE_BOOK = "resource.book";
    public static final String PAGEABLE_MUST_NOT_BE_NULL = "Pageable must not be null";
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final BookMapper bookMapper;
    private final MessageSource messageSource;

    @Override
    @Transactional
    public BookDetailResponse create(final BookCreateRequest request) {
        Objects.requireNonNull(request, "Book create request must not be null");
        Objects.requireNonNull(request.getName(), "Book name must not be null");

        log.debug("Creating new book: {}", request);
        final Book book = bookMapper.toEntity(request);
        final Book savedBook = bookRepository.save(book);

        log.info("Book created successfully with ID: {}", savedBook.getId());
        return bookMapper.toDto(savedBook);
    }

    @Override
    @Transactional
    public void delete(final UUID id) {
        Objects.requireNonNull(id, BOOK_ID_MUST_NOT_BE_NULL);
        log.debug("Deleting book with ID: {}", id);

        final Book book = bookRepository.findById(id)
                .orElseThrow(() -> SpacedLearningException.resourceNotFound(messageSource, RESOURCE_BOOK, id));

        book.softDelete(); // Use soft delete
        bookRepository.save(book);

        log.info("Book soft deleted successfully with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookSummaryResponse> findAll(final Pageable pageable) {
        Objects.requireNonNull(pageable, PAGEABLE_MUST_NOT_BE_NULL);
        log.debug("Finding all books with pagination: {}", pageable);

        return bookRepository.findAll(pageable).map(bookMapper::toSummaryDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookSummaryResponse> findByFilters(final BookStatus status, final DifficultyLevel difficultyLevel,
                                                   final String category, final Pageable pageable) {

        Objects.requireNonNull(pageable, PAGEABLE_MUST_NOT_BE_NULL);
        log.debug("Finding books by filters - status: {}, difficultyLevel: {}, category: {}, pageable: {}", status,
                difficultyLevel, category, pageable);

        return bookRepository.findBooksByFilters(status, difficultyLevel, category, pageable)
                .map(bookMapper::toSummaryDto);
    }

    @Override
    @Transactional(readOnly = true)
    public BookDetailResponse findById(final UUID id) {
        Objects.requireNonNull(id, BOOK_ID_MUST_NOT_BE_NULL);
        log.debug("Finding book by ID: {}", id);

        return bookRepository.findWithModulesById(id).map(bookMapper::toDto)
                .orElseThrow(() -> SpacedLearningException.resourceNotFound(messageSource, RESOURCE_BOOK, id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAllCategories() {
        log.debug("Getting all book categories");
        return bookRepository.findAllCategories();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookSummaryResponse> searchByName(final String searchTerm, final Pageable pageable) {
        Objects.requireNonNull(pageable, PAGEABLE_MUST_NOT_BE_NULL);
        log.debug("Searching books by name containing: {}", searchTerm);

        if (StringUtils.isBlank(searchTerm)) {
            return findAll(pageable);
        }

        return bookRepository.findByNameContainingIgnoreCase(searchTerm, pageable)
                .map(bookMapper::toSummaryDto);
    }

    @Override
    @Transactional
    public BookDetailResponse update(final UUID id, final BookUpdateRequest request) {
        Objects.requireNonNull(id, BOOK_ID_MUST_NOT_BE_NULL);
        Objects.requireNonNull(request, "Book update request must not be null");
        log.debug("Updating book with ID: {}, request: {}", id, request);

        final Book book = bookRepository.findById(id)
                .orElseThrow(() -> SpacedLearningException.resourceNotFound(messageSource, RESOURCE_BOOK, id));

        bookMapper.updateFromDto(request, book);
        final Book updatedBook = bookRepository.save(book);

        log.info("Book updated successfully with ID: {}", updatedBook.getId());
        return bookMapper.toDto(updatedBook);
    }

    @Override
    @Transactional
    public int shareBookWithUsers(UUID bookId, List<UUID> userIds) {
        Objects.requireNonNull(bookId, BOOK_ID_MUST_NOT_BE_NULL);
        Objects.requireNonNull(userIds, "User IDs list must not be null");

        if (userIds.isEmpty()) {
            return 0;
        }

        log.debug("Sharing book ID: {} with {} users", bookId, userIds.size());

        final Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> SpacedLearningException.resourceNotFound(messageSource, RESOURCE_BOOK, bookId));

        final List<User> users = userRepository.findAllById(userIds);

        int sharedCount = 0;
        for (final User user : users) {
            if (!user.getBooks().contains(book)) {
                user.addBook(book);
                sharedCount++;
            }
        }

        if (sharedCount > 0) {
            userRepository.saveAll(users);
            log.info("Book ID: {} shared with {} users", bookId, sharedCount);
        }

        return sharedCount;
    }

    @Override
    @Transactional
    public int unshareBookFromUsers(UUID bookId, List<UUID> userIds) {
        Objects.requireNonNull(bookId, BOOK_ID_MUST_NOT_BE_NULL);
        Objects.requireNonNull(userIds, "User IDs list must not be null");

        if (userIds.isEmpty()) {
            return 0;
        }

        log.debug("Unsharing book ID: {} from {} users", bookId, userIds.size());

        final Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> SpacedLearningException.resourceNotFound(messageSource, RESOURCE_BOOK, bookId));

        final List<User> users = userRepository.findAllById(userIds);

        int unsharedCount = 0;
        for (final User user : users) {
            if (user.removeBook(book)) {
                unsharedCount++;
            }
        }

        if (unsharedCount > 0) {
            userRepository.saveAll(users);
            log.info("Book ID: {} unshared from {} users", bookId, unsharedCount);
        }

        return unsharedCount;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UUID> getUsersWithAccessToBook(UUID bookId) {
        Objects.requireNonNull(bookId, BOOK_ID_MUST_NOT_BE_NULL);
        log.debug("Getting users with access to book ID: {}", bookId);

        final Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> SpacedLearningException.resourceNotFound(messageSource, RESOURCE_BOOK, bookId));

        return book.getUsers().stream()
                .map(User::getId)
                .toList();
    }
}