package com.spacedlearning.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spacedlearning.dto.book.BookCreateRequest;
import com.spacedlearning.dto.book.BookDetailResponse;
import com.spacedlearning.dto.book.BookSummaryResponse;
import com.spacedlearning.dto.book.BookUpdateRequest;
import com.spacedlearning.entity.Book;
import com.spacedlearning.entity.enums.BookStatus;
import com.spacedlearning.entity.enums.DifficultyLevel;
import com.spacedlearning.exception.SpacedLearningException;
import com.spacedlearning.mapper.BookMapper;
import com.spacedlearning.repository.BookRepository;
import com.spacedlearning.service.BookService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of BookService
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BookServiceImpl implements BookService {

	private final BookRepository bookRepository;
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
//	@CacheEvict(value = "books", key = "#id")
	public void delete(final UUID id) {
		Objects.requireNonNull(id, "Book ID must not be null");
		log.debug("Deleting book with ID: {}", id);

		final Book book = bookRepository.findById(id)
				.orElseThrow(() -> SpacedLearningException.resourceNotFound(messageSource, "resource.book", id));

		book.softDelete(); // Use soft delete
		bookRepository.save(book);

		log.info("Book soft deleted successfully with ID: {}", id);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<BookSummaryResponse> findAll(final Pageable pageable) {
		Objects.requireNonNull(pageable, "Pageable must not be null");
		log.debug("Finding all books with pagination: {}", pageable);

		return bookRepository.findAll(pageable).map(bookMapper::toSummaryDto);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<BookSummaryResponse> findByFilters(final BookStatus status, final DifficultyLevel difficultyLevel,
			final String category, final Pageable pageable) {

		Objects.requireNonNull(pageable, "Pageable must not be null");
		log.debug("Finding books by filters - status: {}, difficultyLevel: {}, category: {}, pageable: {}", status,
				difficultyLevel, category, pageable);

		return bookRepository.findBooksByFilters(status, difficultyLevel, category, pageable)
				.map(bookMapper::toSummaryDto);
	}

	@Override
	@Transactional(readOnly = true)
//	@Cacheable(value = "books", key = "#id")
	public BookDetailResponse findById(final UUID id) {
		Objects.requireNonNull(id, "Book ID must not be null");
		log.debug("Finding book by ID: {}", id);

		return bookRepository.findWithModulesById(id).map(bookMapper::toDto)
				.orElseThrow(() -> SpacedLearningException.resourceNotFound(messageSource, "resource.book", id));
	}

	@Override
	@Transactional(readOnly = true)
//	@Cacheable(value = "bookCategories")
	public List<String> getAllCategories() {
		log.debug("Getting all book categories");
		return bookRepository.findAllCategories();
	}

	@Override
    @Transactional(readOnly = true)
	public Page<BookSummaryResponse> searchByName(final String searchTerm, final Pageable pageable) {
		Objects.requireNonNull(pageable, "Pageable must not be null");
        log.debug("Searching books by name containing: {}", searchTerm);

        if (StringUtils.isBlank(searchTerm)) {
            return findAll(pageable);
        }

        return bookRepository.findByNameContainingIgnoreCase(searchTerm, pageable)
                .map(bookMapper::toSummaryDto);
    }

	@Override
	@Transactional
//	@CacheEvict(value = "books", key = "#id")
	public BookDetailResponse update(final UUID id, final BookUpdateRequest request) {
		Objects.requireNonNull(id, "Book ID must not be null");
		Objects.requireNonNull(request, "Book update request must not be null");
		log.debug("Updating book with ID: {}, request: {}", id, request);

		final Book book = bookRepository.findById(id)
				.orElseThrow(() -> SpacedLearningException.resourceNotFound(messageSource, "resource.book", id));

		bookMapper.updateFromDto(request, book);
		final Book updatedBook = bookRepository.save(book);

		log.info("Book updated successfully with ID: {}", updatedBook.getId());
		return bookMapper.toDto(updatedBook);
	}
}