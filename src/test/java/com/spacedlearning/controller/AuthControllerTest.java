package com.spacedlearning.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spacedlearning.dto.auth.AuthRequest;
import com.spacedlearning.dto.auth.AuthResponse;
import com.spacedlearning.dto.auth.RefreshTokenRequest;
import com.spacedlearning.dto.auth.RegisterRequest;
import com.spacedlearning.dto.user.UserResponse;
import com.spacedlearning.service.AuthService;



@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Test
    void loginTest() throws Exception {
        AuthRequest request = new AuthRequest("user", "password");
        AuthResponse response = new AuthResponse("token", "refreshToken");

        when(authService.authenticate(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").value("token"))
                .andExpect(jsonPath("$.data.refreshToken").value("refreshToken"));
    }

    @Test
    void refreshTokenTest() throws Exception {
        RefreshTokenRequest request = new RefreshTokenRequest("refreshToken");
        AuthResponse response = new AuthResponse("newToken", "newRefreshToken");

        when(authService.refreshToken(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/refresh-token").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))).andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").value("newToken"))
                .andExpect(jsonPath("$.data.refreshToken").value("newRefreshToken"));
    }

    @Test
    void registerTest() throws Exception {
        RegisterRequest request = new RegisterRequest("user", "user@test.com", "password");
        UserResponse response = UserResponse.builder().id(UUID.randomUUID()).username("user")
                .email("user@test.com").build();

        when(authService.register(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/register").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))).andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.username").value("user"))
                .andExpect(jsonPath("$.data.email").value("user@test.com"));
    }

    @Test
    void validateTokenValidTest() throws Exception {
        when(authService.validateToken(any())).thenReturn(true);

        mockMvc.perform(get("/api/v1/auth/validate").param("token", "validToken"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.message").value("Token is valid"))
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void validateTokenInvalidTest() throws Exception {
        when(authService.validateToken(any())).thenReturn(false);

        mockMvc.perform(get("/api/v1/auth/validate").param("token", "invalidToken"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid token"))
                .andExpect(jsonPath("$.success").value(false));
    }
}
