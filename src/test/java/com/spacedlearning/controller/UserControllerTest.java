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
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spacedlearning.dto.user.UserDetailedResponse;
import com.spacedlearning.dto.user.UserResponse;
import com.spacedlearning.dto.user.UserUpdateRequest;
import com.spacedlearning.service.UserService;



@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteUser() throws Exception {
        UUID userId = UUID.randomUUID();
        doNothing().when(userService).delete(userId);

        mockMvc.perform(delete("/api/v1/users/{id}", userId)).andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User deleted successfully"));

        verify(userService, times(1)).delete(userId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAllUsers() throws Exception {
        mockMvc.perform(get("/api/v1/users")).andExpect(status().isOk());
        verify(userService, times(1)).findAll(any());
    }

    @Test
    @WithMockUser
    void testGetCurrentUser() throws Exception {
        UserResponse userResponse = new UserResponse();
        when(userService.getCurrentUser()).thenReturn(userResponse);

        mockMvc.perform(get("/api/v1/users/me")).andExpect(status().isOk());

        verify(userService, times(1)).getCurrentUser();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetUserById() throws Exception {
        UUID userId = UUID.randomUUID();
        UserDetailedResponse userResponse = new UserDetailedResponse();
        when(userService.findById(userId)).thenReturn(userResponse);

        mockMvc.perform(get("/api/v1/users/{id}", userId)).andExpect(status().isOk());

        verify(userService, times(1)).findById(userId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testRestoreUser() throws Exception {
        UUID userId = UUID.randomUUID();
        UserResponse restoredUser = new UserResponse();
        when(userService.restore(userId)).thenReturn(restoredUser);

        mockMvc.perform(post("/api/v1/users/{id}/restore", userId)).andExpect(status().isOk());

        verify(userService, times(1)).restore(userId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateUser() throws Exception {
        UUID userId = UUID.randomUUID();
        UserUpdateRequest updateRequest = new UserUpdateRequest();
        UserResponse updatedUser = new UserResponse();
        when(userService.update(eq(userId), any(UserUpdateRequest.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/api/v1/users/{id}", userId).contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk());

        verify(userService, times(1)).update(eq(userId), any(UserUpdateRequest.class));
    }
}
