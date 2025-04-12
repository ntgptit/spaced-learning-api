package com.spacedlearning.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import com.spacedlearning.dto.book.BookCreateRequest;
import com.spacedlearning.dto.book.BookDetailResponse;
import com.spacedlearning.dto.book.BookSummaryResponse;
import com.spacedlearning.dto.book.BookUpdateRequest;
import com.spacedlearning.entity.Book;
import com.spacedlearning.entity.User;
import com.spacedlearning.entity.enums.BookStatus;
import com.spacedlearning.entity.enums.DifficultyLevel;
import com.spacedlearning.mapper.BookMapper;
import com.spacedlearning.repository.BookRepository;
import com.spacedlearning.repository.UserRepository;



class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookMapper bookMapper;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private BookServiceImpl bookService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreate() {
        BookCreateRequest request = new BookCreateRequest();
        request.setName("Test Book");

        Book book = new Book();
        Book savedBook = new Book();
        savedBook.setId(UUID.randomUUID());

        when(bookMapper.toEntity(request)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(savedBook);
        when(bookMapper.toDto(savedBook)).thenReturn(new BookDetailResponse());

        BookDetailResponse response = bookService.create(request);

        assertNotNull(response);
        verify(bookRepository).save(book);
    }

    @Test
    void testDelete() {
        UUID bookId = UUID.randomUUID();
        Book book = new Book();

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        bookService.delete(bookId);

        verify(bookRepository).save(book);
    }

    @Test
    void testFindAll() {
        PageRequest pageable = PageRequest.of(0, 10);
        Page<Book> books = new PageImpl<>(List.of(new Book()));

        when(bookRepository.findAll(pageable)).thenReturn(books);
        when(bookMapper.toSummaryDto(any(Book.class))).thenReturn(new BookSummaryResponse());

        Page<BookSummaryResponse> response = bookService.findAll(pageable);

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
    }

    @Test
    void testFindByFilters() {
        PageRequest pageable = PageRequest.of(0, 10);
        Page<Book> books = new PageImpl<>(List.of(new Book()));

        when(bookRepository.findBooksByFilters(any(), any(), any(), eq(pageable)))
                .thenReturn(books);
        when(bookMapper.toSummaryDto(any(Book.class))).thenReturn(new BookSummaryResponse());

        Page<BookSummaryResponse> response = bookService.findByFilters(BookStatus.PUBLISHED,
                DifficultyLevel.BEGINNER, "Category", pageable);

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
    }

    @Test
    void testFindById() {
        UUID bookId = UUID.randomUUID();
        Book book = new Book();

        when(bookRepository.findWithModulesById(bookId)).thenReturn(Optional.of(book));
        when(bookMapper.toDto(book)).thenReturn(new BookDetailResponse());

        BookDetailResponse response = bookService.findById(bookId);

        assertNotNull(response);
    }

    @Test
    void testGetAllCategories() {
        List<String> categories = Arrays.asList("Category1", "Category2");

        when(bookRepository.findAllCategories()).thenReturn(categories);

        List<String> response = bookService.getAllCategories();

        assertNotNull(response);
        assertEquals(2, response.size());
    }

    @Test
    void testSearchByName() {
        PageRequest pageable = PageRequest.of(0, 10);
        Page<Book> books = new PageImpl<>(List.of(new Book()));

        when(bookRepository.findByNameContainingIgnoreCase(anyString(), eq(pageable)))
                .thenReturn(books);
        when(bookMapper.toSummaryDto(any(Book.class))).thenReturn(new BookSummaryResponse());

        Page<BookSummaryResponse> response = bookService.searchByName("Test", pageable);

        assertNotNull(response);
        assertEquals(1, response.getContent().size());
    }

    @Test
    void testUpdate() {
        UUID bookId = UUID.randomUUID();
        BookUpdateRequest request = new BookUpdateRequest();
        Book book = new Book();
        Book updatedBook = new Book();
        updatedBook.setId(bookId);

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(bookRepository.save(book)).thenReturn(updatedBook);
        when(bookMapper.toDto(updatedBook)).thenReturn(new BookDetailResponse());

        BookDetailResponse response = bookService.update(bookId, request);

        assertNotNull(response);
        verify(bookRepository).save(book);
    }

    @Test
    void testShareBookWithUsers() {
        UUID bookId = UUID.randomUUID();
        List<UUID> userIds = List.of(UUID.randomUUID());
        Book book = new Book();
        User user = new User();

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(userRepository.findAllById(userIds)).thenReturn(List.of(user));

        int sharedCount = bookService.shareBookWithUsers(bookId, userIds);

        assertEquals(1, sharedCount);
        verify(userRepository).saveAll(anyList());
    }

    @Test
    void testUnshareBookFromUsers() {
        UUID bookId = UUID.randomUUID();
        List<UUID> userIds = List.of(UUID.randomUUID());
        Book book = new Book();
        User user = new User();

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        when(userRepository.findAllById(userIds)).thenReturn(List.of(user));

        int unsharedCount = bookService.unshareBookFromUsers(bookId, userIds);

        assertEquals(1, unsharedCount);
        verify(userRepository).saveAll(anyList());
    }

    @Test
    void testGetUsersWithAccessToBook() {
        UUID bookId = UUID.randomUUID();
        Book book = new Book();
        User user = new User();
        user.setId(UUID.randomUUID());
        book.setUsers(Set.of(user));

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

        List<UUID> userIds = bookService.getUsersWithAccessToBook(bookId);

        assertNotNull(userIds);
        assertEquals(1, userIds.size());
    }
}
