// File: src/main/java/com/spacedlearning/service/impl/BookServiceImpl.java
package com.spacedlearning.service.impl;

import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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

	@Override
	@Transactional
	public BookDetailResponse create(BookCreateRequest request) {
		log.debug("Creating new book: {}", request);
		final Book book = bookMapper.toEntity(request);
		final Book savedBook = bookRepository.save(book);
		log.info("Book created successfully with ID: {}", savedBook.getId());
		return bookMapper.toDto(savedBook);
	}

	@Override
	@Transactional
	@CacheEvict(value = "books", key = "#id")
	public void delete(UUID id) {
		log.debug("Deleting book with ID: {}", id);
		final Book book = bookRepository.findById(id)
				.orElseThrow(() -> SpacedLearningException.resourceNotFound("Book", id));

		book.softDelete(); // Use soft delete
		bookRepository.save(book);
		log.info("Book soft deleted successfully with ID: {}", id);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<BookSummaryResponse> findAll(Pageable pageable) {
		log.debug("Finding all books with pagination: {}", pageable);
		return bookRepository.findAll(pageable).map(bookMapper::toSummaryDto);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<BookSummaryResponse> findByFilters(BookStatus status, DifficultyLevel difficultyLevel, String category,
			Pageable pageable) {
		log.debug("Finding books by filters - status: {}, difficultyLevel: {}, category: {}, pageable: {}", status,
				difficultyLevel, category, pageable);
		return bookRepository.findBooksByFilters(status, difficultyLevel, category, pageable)
				.map(bookMapper::toSummaryDto);
	}

	@Override
	@Transactional(readOnly = true)
	@Cacheable(value = "books", key = "#id")
	public BookDetailResponse findById(UUID id) {
		log.debug("Finding book by ID: {}", id);
		final Book book = bookRepository.findWithModulesById(id)
				.orElseThrow(() -> SpacedLearningException.resourceNotFound("Book", id));
		return bookMapper.toDto(book);
	}

	@Override
	@Transactional(readOnly = true)
	@Cacheable(value = "bookCategories")
	public List<String> getAllCategories() {
		log.debug("Getting all book categories");
		return bookRepository.findAllCategories();
	}

	@Override
    @Transactional(readOnly = true)
    public Page<BookSummaryResponse> searchByName(String searchTerm, Pageable pageable) {
        log.debug("Searching books by name containing: {}", searchTerm);
        if (StringUtils.isBlank(searchTerm)) {
            return findAll(pageable);
        }
        return bookRepository.findByNameContainingIgnoreCase(searchTerm, pageable)
                .map(bookMapper::toSummaryDto);
    }

	@Override
	@Transactional
	@CacheEvict(value = "books", key = "#id")
	public BookDetailResponse update(UUID id, BookUpdateRequest request) {
		log.debug("Updating book with ID: {}, request: {}", id, request);
		final Book book = bookRepository.findById(id)
				.orElseThrow(() -> SpacedLearningException.resourceNotFound("Book", id));

		bookMapper.updateFromDto(request, book);
		final Book updatedBook = bookRepository.save(book);
		log.info("Book updated successfully with ID: {}", updatedBook.getId());
		return bookMapper.toDto(updatedBook);
	}
}