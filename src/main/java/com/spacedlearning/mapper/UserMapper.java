package com.spacedlearning.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.spacedlearning.dto.auth.RegisterRequest;
import com.spacedlearning.dto.user.UserDetailedResponse;
import com.spacedlearning.dto.user.UserResponse;
import com.spacedlearning.dto.user.UserUpdateRequest;
import com.spacedlearning.entity.Role;
import com.spacedlearning.entity.User;
import com.spacedlearning.security.CustomUserDetails;

import lombok.RequiredArgsConstructor;

/**
 * Mapper for User entity and DTOs
 */
@Component
@RequiredArgsConstructor
public class UserMapper extends AbstractGenericMapper<User, UserResponse> {

    private final PasswordEncoder passwordEncoder;

    /**
     * Loads UserDetails by username from the user entity
     * 
     * @param username the username (email) to lookup
     * @return UserDetails object
     */
    public UserDetails loadUserByUsername(String username) {
        if (username == null) {
        }
        
        // This method should be used in conjunction with a repository lookup,
        // we're just providing the signature for compatibility with AuthServiceImpl
        return null;
    }

    @Override
    protected User mapDtoToEntity(UserResponse dto, User entity) {
        if (StringUtils.isNotBlank(dto.getDisplayName())) {
            entity.setName(dto.getDisplayName());
        }
        
        return entity;
    }
    
    @Override
    protected UserResponse mapToDto(User entity) {
        if (entity == null) {
            return null;
        }
        
        return UserResponse.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .displayName(entity.getName())
                .createdAt(entity.getCreatedAt())
                .roles(entity.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toList()))
                .build();
    }
    
    @Override
    protected User mapToEntity(UserResponse dto) {
        if (dto == null) {
            return null;
        }
        
        final User user = new User();
        user.setEmail(dto.getEmail());
        user.setName(dto.getDisplayName());
        
        // Roles will be set separately
        
        return user;
    }
    
    /**
     * Maps a RegisterRequest DTO to a User entity
     * 
     * @param request The RegisterRequest DTO
     * @return User entity
     */
    public User registerRequestToEntity(RegisterRequest request) {
        if (request == null) {
            return null;
        }
        
        final User user = new User();
        user.setEmail(request.getEmail());
        user.setName(request.getFirstName() + " " + request.getLastName());
        
        // Encrypt password
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        
        return user;
    }
    
    /**
     * Maps a User entity to a UserDetailedResponse DTO (admin view)
     * 
     * @param entity The User entity
     * @return UserDetailedResponse DTO
     */
    public UserDetailedResponse toDetailedDto(User entity) {
        if (entity == null) {
            return null;
        }
        
        return UserDetailedResponse.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .displayName(entity.getName())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .deletedAt(entity.getDeletedAt())
                .build();
    }
    
    /**
     * Converts a User entity to CustomUserDetails
     * 
     * @param user the user entity
     * @return CustomUserDetails
     */
    public CustomUserDetails toUserDetails(User user) {
        if (user == null) {
            return null;
        }
        
        final List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
        
        return new CustomUserDetails(user, authorities);
    }
    
    /**
     * Updates a User entity from a UserUpdateRequest DTO
     * 
     * @param request The UserUpdateRequest DTO
     * @param entity The User entity to update
     * @return Updated User entity
     */
    public User updateFromDto(UserUpdateRequest request, User entity) {
        if (request == null || entity == null) {
            return entity;
        }
        
        if (StringUtils.isNotBlank(request.getDisplayName())) {
            entity.setName(request.getDisplayName());
        }
        
        if (StringUtils.isNotBlank(request.getPassword())) {
            entity.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        
        return entity;
    }
}