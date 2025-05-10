package com.spacedlearning.mapper;

import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.spacedlearning.dto.auth.RegisterRequest;
import com.spacedlearning.dto.user.UserDetailedResponse;
import com.spacedlearning.dto.user.UserResponse;
import com.spacedlearning.dto.user.UserUpdateRequest;
import com.spacedlearning.entity.Role;
import com.spacedlearning.entity.User;
import com.spacedlearning.entity.enums.UserStatus;
import com.spacedlearning.security.CustomUserDetailsService;

import lombok.extern.slf4j.Slf4j;

/**
 * Mapper for User entity and DTOs
 */
@Component
@Slf4j
public class UserMapper {

    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService userDetailsService;

    public UserMapper(PasswordEncoder passwordEncoder, @Lazy CustomUserDetailsService userDetailsService) {
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
    }

    public UserDetails loadUserByUsername(String username) {
        try {
            return this.userDetailsService.loadUserByUsername(username);
        } catch (final Exception e) {
            log.error("Error loading user details for username: {}, Error: {}", username, e.getMessage(), e);
            throw e;
        }
    }

    public User registerRequestToEntity(RegisterRequest request) {
        if (request == null) {
            return null;
        }

        final var user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(this.passwordEncoder.encode(request.getPassword()));

        var displayName = new StringBuilder().append(StringUtils.defaultString(request.getFirstName()));
        if (StringUtils.isNotBlank(request.getLastName())) {
            displayName.append(" ").append(request.getLastName());
        }
        user.setName(displayName.toString().trim());

        user.setStatus(UserStatus.ACTIVE);
        return user;
    }

    public UserDetailedResponse toDetailedDto(User user) {
        if (user == null) {
            return null;
        }

        return UserDetailedResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .displayName(user.getName())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .deletedAt(user.getDeletedAt())
                .build();
    }

    public UserResponse toDto(User user) {
        if (user == null) {
            return null;
        }

        final var fullName = StringUtils.defaultString(user.getName()).trim();
        final var nameParts = StringUtils.split(fullName, " ", 2);
        final var firstName = (nameParts != null) && (nameParts.length > 0) ? nameParts[0] : "";
        final var lastName = (nameParts != null) && (nameParts.length > 1) ? nameParts[1] : "";

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(firstName)
                .lastName(lastName)
                .displayName(user.getName())
                .createdAt(user.getCreatedAt())
                .roles(user.getRoles() != null
                        ? user.getRoles().stream().map(Role::getName).collect(Collectors.toList())
                        : null)
                .build();
    }

    public void updateFromDto(UserUpdateRequest request, User user) {
        if ((request == null) || (user == null)) {
            return;
        }

        if (StringUtils.isNotBlank(request.getDisplayName())) {
            user.setName(request.getDisplayName().trim());
        }

        if (StringUtils.isNotBlank(request.getPassword())) {
            user.setPassword(this.passwordEncoder.encode(request.getPassword()));
        }
    }
}
