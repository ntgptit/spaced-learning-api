package com.spacedlearning.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import com.spacedlearning.dto.user.UserDetailedResponse;
import com.spacedlearning.dto.user.UserResponse;
import com.spacedlearning.dto.user.UserUpdateRequest;
import com.spacedlearning.entity.User;
import com.spacedlearning.mapper.UserMapper;
import com.spacedlearning.repository.UserRepository;
import com.spacedlearning.security.CustomUserDetailsService;



class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testDelete() {
        UUID userId = UUID.randomUUID();
        User user = mock(User.class);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.delete(userId);

        verify(user).softDelete();
        verify(userRepository).save(user);
    }

    @Test
    void testExistsByEmail() {
        String email = "test@example.com";
        when(userRepository.existsByEmail(email)).thenReturn(true);

        boolean exists = userService.existsByEmail(email);

        assertTrue(exists);
        verify(userRepository).existsByEmail(email);
    }

    @Test
    void testFindAll() {
        Pageable pageable = mock(Pageable.class);
        User user = mock(User.class);
        Page<User> userPage = new PageImpl<>(Collections.singletonList(user));
        UserDetailedResponse response = mock(UserDetailedResponse.class);

        when(userRepository.findAll(pageable)).thenReturn(userPage);
        when(userMapper.toDetailedDto(user)).thenReturn(response);

        Page<UserDetailedResponse> result = userService.findAll(pageable);

        assertEquals(1, result.getTotalElements());
        verify(userRepository).findAll(pageable);
        verify(userMapper).toDetailedDto(user);
    }

    @Test
    void testFindByUsernameOrEmail() {
        String email = "test@example.com";
        User user = mock(User.class);
        UserResponse response = mock(UserResponse.class);

        when(userRepository.findByUsernameOrEmail(email)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(response);

        UserResponse result = userService.findByUsernameOrEmail(email);

        assertEquals(response, result);
        verify(userRepository).findByUsernameOrEmail(email);
        verify(userMapper).toDto(user);
    }

    @Test
    void testFindById() {
        UUID userId = UUID.randomUUID();
        User user = mock(User.class);
        UserDetailedResponse response = mock(UserDetailedResponse.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toDetailedDto(user)).thenReturn(response);

        UserDetailedResponse result = userService.findById(userId);

        assertEquals(response, result);
        verify(userRepository).findById(userId);
        verify(userMapper).toDetailedDto(user);
    }

    @Test
    void testGetCurrentUser() {
        String email = "test@example.com";
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        UserResponse response = mock(UserResponse.class);

        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn(email);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(userRepository.findByUsernameOrEmail(email)).thenReturn(Optional.of(mock(User.class)));
        when(userMapper.toDto(any(User.class))).thenReturn(response);

        UserResponse result = userService.getCurrentUser();

        assertEquals(response, result);
        verify(userRepository).findByUsernameOrEmail(email);
    }

    @Test
    void testRestore() {
        UUID userId = UUID.randomUUID();
        User user = mock(User.class);
        UserResponse response = mock(UserResponse.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(user.isDeleted()).thenReturn(true);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(response);

        UserResponse result = userService.restore(userId);

        assertEquals(response, result);
        verify(user).restore();
        verify(userRepository).save(user);
    }

    @Test
    void testUpdate() {
        UUID userId = UUID.randomUUID();
        User user = mock(User.class);
        UserUpdateRequest request = mock(UserUpdateRequest.class);
        UserResponse response = mock(UserResponse.class);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(response);

        UserResponse result = userService.update(userId, request);

        assertEquals(response, result);
        verify(userMapper).updateFromDto(request, user);
        verify(userRepository).save(user);
    }
}
