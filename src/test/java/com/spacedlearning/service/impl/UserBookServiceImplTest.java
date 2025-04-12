package com.spacedlearning.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.spacedlearning.dto.book.BookSummaryResponse;
import com.spacedlearning.dto.user_books.UserBookShareInfo;
import com.spacedlearning.entity.Book;
import com.spacedlearning.entity.User;
import com.spacedlearning.exception.SpacedLearningException;
import com.spacedlearning.mapper.BookMapper;
import com.spacedlearning.repository.BookRepository;
import com.spacedlearning.repository.UserRepository;



class UserBookServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private UserBookServiceImpl userBookService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetBooksForUser_Success() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setBooks(Set.of(new Book(), new Book()));

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookMapper.toSummaryDto(any(Book.class))).thenReturn(new BookSummaryResponse());

        List<BookSummaryResponse> result = userBookService.getBooksForUser(userId);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userRepository, times(1)).findById(userId);
        verify(bookMapper, times(2)).toSummaryDto(any(Book.class));
    }

    @Test
    void testGetBooksForUser_UserNotFound() {
        UUID userId = UUID.randomUUID();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(SpacedLearningException.class, () -> userBookService.getBooksForUser(userId));
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    void testGetAllSharedBooks_Success() {
        Book book1 = new Book();
        book1.setId(UUID.randomUUID());
        book1.setName("Book 1");
        User user1 = new User();
        user1.setId(UUID.randomUUID());
        user1.setUsername("user1");
        user1.setEmail("user1@example.com");
        book1.setUsers(Collections.singleton(user1));

        when(bookRepository.findAll()).thenReturn(Collections.singletonList(book1));

        List<UserBookShareInfo> result = userBookService.getAllSharedBooks();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Book 1", result.get(0).getBookName());
        assertEquals(1, result.get(0).getSharedWithUserCount());
        assertEquals("user1", result.get(0).getSharedWith().get(0).getUsername());
        verify(bookRepository, times(1)).findAll();
    }

    @Test
    void testGetAllSharedBooks_NoSharedBooks() {
        when(bookRepository.findAll()).thenReturn(Collections.emptyList());

        List<UserBookShareInfo> result = userBookService.getAllSharedBooks();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(bookRepository, times(1)).findAll();
    }
}
