package com.spacedlearning.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import com.spacedlearning.dto.auth.AuthRequest;
import com.spacedlearning.dto.auth.AuthResponse;
import com.spacedlearning.dto.auth.RefreshTokenRequest;
import com.spacedlearning.dto.auth.RegisterRequest;
import com.spacedlearning.dto.user.UserResponse;
import com.spacedlearning.entity.Role;
import com.spacedlearning.entity.User;
import com.spacedlearning.exception.SpacedLearningException;
import com.spacedlearning.mapper.UserMapper;
import com.spacedlearning.repository.RoleRepository;
import com.spacedlearning.repository.UserRepository;
import com.spacedlearning.security.CustomUserDetails;
import com.spacedlearning.security.CustomUserDetailsService;
import com.spacedlearning.security.JwtTokenProvider;



class AuthServiceImplTest {

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private MessageSource messageSource;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAuthenticate_Success() {
        AuthRequest request = new AuthRequest("testUser", "password");
        Authentication authentication = mock(Authentication.class);
        CustomUserDetails userDetails = mock(CustomUserDetails.class);
        User user = new User();
        UserResponse userResponse = new UserResponse();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUser()).thenReturn(user);
        when(tokenProvider.generateToken(authentication)).thenReturn("accessToken");
        when(tokenProvider.generateRefreshToken(authentication)).thenReturn("refreshToken");
        when(userMapper.toDto(user)).thenReturn(userResponse);

        AuthResponse response = authService.authenticate(request);

        assertNotNull(response);
        assertEquals("accessToken", response.getToken());
        assertEquals("refreshToken", response.getRefreshToken());
        assertEquals(userResponse, response.getUser());
    }

    @Test
    void testAuthenticate_Failure() {
        AuthRequest request = new AuthRequest("testUser", "wrongPassword");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("Authentication failed"));

        Exception exception =
                assertThrows(RuntimeException.class, () -> authService.authenticate(request));
        assertEquals("Authentication failed", exception.getMessage());
    }

    @Test
    void testGetUsernameFromToken_Success() {
        String token = "validToken";

        when(tokenProvider.getUsernameFromToken(token)).thenReturn("testUser");

        String username = authService.getUsernameFromToken(token);

        assertEquals("testUser", username);
    }

    @Test
    void testGetUsernameFromToken_InvalidToken() {
        String token = "invalidToken";

        when(tokenProvider.getUsernameFromToken(token))
                .thenThrow(new RuntimeException("Invalid token"));

        Exception exception = assertThrows(SpacedLearningException.class,
                () -> authService.getUsernameFromToken(token));
        assertEquals("error.auth.invalidToken", exception.getMessage());
    }

    @Test
    void testRefreshToken_Success() {
        RefreshTokenRequest request = new RefreshTokenRequest("validRefreshToken");
        mock(Authentication.class);
        User user = new User();
        UserResponse userResponse = new UserResponse();

        when(tokenProvider.validateToken(request.getRefreshToken())).thenReturn(true);
        when(tokenProvider.isRefreshToken(request.getRefreshToken())).thenReturn(true);
        when(tokenProvider.getUsernameFromToken(request.getRefreshToken())).thenReturn("testUser");
        when(userRepository.findByUsernameOrEmailWithRoles("testUser"))
                .thenReturn(Optional.of(user));
        when(userDetailsService.loadUserByUsername("testUser"))
                .thenReturn(mock(CustomUserDetails.class));
        when(tokenProvider.generateToken(any(Authentication.class))).thenReturn("newAccessToken");
        when(tokenProvider.generateRefreshToken(any(Authentication.class)))
                .thenReturn("newRefreshToken");
        when(userMapper.toDto(user)).thenReturn(userResponse);

        AuthResponse response = authService.refreshToken(request);

        assertNotNull(response);
        assertEquals("newAccessToken", response.getToken());
        assertEquals("newRefreshToken", response.getRefreshToken());
        assertEquals(userResponse, response.getUser());
    }

    @Test
    void testRegister_Success() {
        RegisterRequest request = new RegisterRequest("testUser", "test@example.com", "password");
        User user = new User();
        Role role = new Role();
        UserResponse userResponse = new UserResponse();

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(request.getUsername())).thenReturn(false);
        when(userMapper.registerRequestToEntity(request)).thenReturn(user);
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(role));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userResponse);

        UserResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals(userResponse, response);
    }

    @Test
    void testValidateToken_Valid() {
        String token = "validToken";

        when(tokenProvider.validateToken(token)).thenReturn(true);

        boolean isValid = authService.validateToken(token);

        assertTrue(isValid);
    }

    @Test
    void testValidateToken_Invalid() {
        String token = "invalidToken";

        when(tokenProvider.validateToken(token)).thenReturn(false);

        boolean isValid = authService.validateToken(token);

        assertFalse(isValid);
    }
}
