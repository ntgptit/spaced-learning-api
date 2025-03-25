package com.spacedlearning.util;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import com.spacedlearning.dto.common.PageResponse;

/**
 * Utility class for handling pagination operations. Provides methods to convert
 * Spring Data Page objects to PageResponse DTOs.
 */
@Component
public class PageUtils {

	/**
	 * Creates a PageResponse from a list of items, total count, pageable and
	 * mapping function. Useful for scenarios where you need to manually handle
	 * pagination logic.
	 *
	 * @param <T>           The source item type
	 * @param <R>           The target DTO type
	 * @param content       The list of source items
	 * @param totalElements Total number of elements
	 * @param pageable      The Pageable object with pagination information
	 * @param mapper        The function to map from source to target type
	 * @return A PageResponse containing the mapped items
	 */
	public static <T, R> PageResponse<R> createPageResponse(final List<T> content, final long totalElements,
			final Pageable pageable, final Function<T, R> mapper) {

		Objects.requireNonNull(content, "Content must not be null");
		Objects.requireNonNull(pageable, "Pageable must not be null");
		Objects.requireNonNull(mapper, "Mapper function must not be null");

		final List<R> mappedContent = CollectionUtils.emptyIfNull(content).stream().filter(Objects::nonNull).map(mapper)
				.collect(Collectors.toList());

		final int totalPages = pageable.getPageSize() > 0
				? (int) Math.ceil((double) totalElements / pageable.getPageSize())
				: 0;

		return PageResponse.<R>builder().content(mappedContent).page(pageable.getPageNumber())
				.size(pageable.getPageSize()).totalElements(totalElements).totalPages(totalPages)
				.first(pageable.getPageNumber() == 0).last(pageable.getPageNumber() >= totalPages - 1).build();
	}

	/**
	 * Creates a PageResponse directly from a Spring Data Page of DTOs.
	 *
	 * @param <R>      The DTO type
	 * @param page     The Spring Data Page object already containing DTOs
	 * @param pageable The Pageable object used for the query
	 * @return A PageResponse containing the DTOs
	 */
	public static <R> PageResponse<R> createPageResponse(final Page<R> page, final Pageable pageable) {
		Objects.requireNonNull(page, "Page must not be null");
		Objects.requireNonNull(pageable, "Pageable must not be null");

		return PageResponse.<R>builder().content(page.getContent()).page(pageable.getPageNumber())
				.size(pageable.getPageSize()).totalElements(page.getTotalElements()).totalPages(page.getTotalPages())
				.first(page.isFirst()).last(page.isLast()).build();
	}

	/**
	 * Creates a PageResponse from a Spring Data Page and a mapping function.
	 *
	 * @param <T>    The entity type
	 * @param <R>    The DTO type
	 * @param page   The Spring Data Page object
	 * @param mapper The function to map from entity to DTO
	 * @return A PageResponse containing the mapped DTOs
	 */
	public static <T, R> PageResponse<R> createPageResponse(final Page<T> page, final Function<T, R> mapper) {
		Objects.requireNonNull(page, "Page must not be null");
		Objects.requireNonNull(mapper, "Mapper function must not be null");

		final List<R> content = page.getContent().stream().filter(Objects::nonNull).map(mapper)
				.collect(Collectors.toList());

		return PageResponse.<R>builder().content(content).page(page.getNumber()).size(page.getSize())
				.totalElements(page.getTotalElements()).totalPages(page.getTotalPages()).first(page.isFirst())
				.last(page.isLast()).build();
	}

	/**
	 * Creates an empty PageResponse.
	 *
	 * @param <R>      The DTO type
	 * @param pageable The Pageable object used for the query
	 * @return An empty PageResponse
	 */
	public static <R> PageResponse<R> emptyPageResponse(final Pageable pageable) {
		Objects.requireNonNull(pageable, "Pageable must not be null");

		return PageResponse.<R>builder().content(List.of()).page(pageable.getPageNumber()).size(pageable.getPageSize())
				.totalElements(0L).totalPages(0).first(true).last(true).build();
	}

	/**
	 * Private constructor to prevent instantiation of utility class.
	 */
	private PageUtils() {
		throw new UnsupportedOperationException("Utility class should not be instantiated");
	}
}