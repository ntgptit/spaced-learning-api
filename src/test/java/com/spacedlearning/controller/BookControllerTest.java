package com.spacedlearning.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spacedlearning.dto.book.BookCreateRequest;
import com.spacedlearning.dto.book.BookDetailResponse;
import com.spacedlearning.service.BookService;



@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private BookService bookService;

    @InjectMocks
    private BookController bookController;

    @Test
    void testCreateBook() throws Exception {
        BookCreateRequest request = new BookCreateRequest();
        BookDetailResponse response = new BookDetailResponse();
        when(bookService.create(any(BookCreateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/books").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))).andExpect(status().isCreated())
                .andExpect(jsonPath("$.data").exists());

        verify(bookService, times(1)).create(any(BookCreateRequest.class));
    }

    @Test
    void testDeleteBook() throws Exception {
        UUID bookId = UUID.randomUUID();
        doNothing().when(bookService).delete(bookId);

        mockMvc.perform(delete("/api/v1/books/{id}", bookId)).andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Book deleted successfully"));

        verify(bookService, times(1)).delete(bookId);
    }

    @Test
    void testGetAllBooks() throws Exception {
        mockMvc.perform(get("/api/v1/books")).andExpect(status().isOk());

        verify(bookService, times(1)).findAll(any());
    }

    @Test
    void testGetBookById() throws Exception {
        UUID bookId = UUID.randomUUID();
        BookDetailResponse response = new BookDetailResponse();
        when(bookService.findById(bookId)).thenReturn(response);

        mockMvc.perform(get("/api/v1/books/{id}", bookId)).andExpect(status().isOk())
                .andExpect(jsonPath("$.data").exists());

        verify(bookService, times(1)).findById(bookId);
    }

    @Test
    void testUpdateBook() throws Exception {
        UUID bookId = UUID.randomUUID();
        BookDetailResponse response = new BookDetailResponse();
        when(bookService.update(eq(bookId), any())).thenReturn(response);

        mockMvc.perform(put("/api/v1/books/{id}", bookId).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new BookCreateRequest())))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data").exists());

        verify(bookService, times(1)).update(eq(bookId), any());
    }

    @Test
    void testGetAllCategories() throws Exception {
        when(bookService.getAllCategories()).thenReturn(List.of("Category1", "Category2"));

        mockMvc.perform(get("/api/v1/books/categories")).andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());

        verify(bookService, times(1)).getAllCategories();
    }

    @Test
    void testSearchBooks() throws Exception {
        mockMvc.perform(get("/api/v1/books/search").param("query", "test"))
                .andExpect(status().isOk());

        verify(bookService, times(1)).searchByName(eq("test"), any());
    }

    @Test
    void testShareBookWithUsers() throws Exception {
        UUID bookId = UUID.randomUUID();
        List<UUID> userIds = List.of(UUID.randomUUID(), UUID.randomUUID());
        when(bookService.shareBookWithUsers(bookId, userIds)).thenReturn(2);

        mockMvc.perform(
                post("/api/v1/books/{id}/share", bookId).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userIds)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data").value(2));

        verify(bookService, times(1)).shareBookWithUsers(bookId, userIds);
    }

    @Test
    void testUnshareBookFromUsers() throws Exception {
        UUID bookId = UUID.randomUUID();
        List<UUID> userIds = List.of(UUID.randomUUID(), UUID.randomUUID());
        when(bookService.unshareBookFromUsers(bookId, userIds)).thenReturn(2);

        mockMvc.perform(
                post("/api/v1/books/{id}/unshare", bookId).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userIds)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data").value(2));

        verify(bookService, times(1)).unshareBookFromUsers(bookId, userIds);
    }

    @Test
    void testGetUsersWithAccess() throws Exception {
        UUID bookId = UUID.randomUUID();
        List<UUID> userIds = List.of(UUID.randomUUID(), UUID.randomUUID());
        when(bookService.getUsersWithAccessToBook(bookId)).thenReturn(userIds);

        mockMvc.perform(get("/api/v1/books/{id}/users", bookId)).andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());

        verify(bookService, times(1)).getUsersWithAccessToBook(bookId);
    }
}
