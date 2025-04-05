package com.spacedlearning.security;

import java.util.Collection;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.spacedlearning.entity.User;
import com.spacedlearning.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Custom implementation of Spring Security's UserDetailsService
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        log.debug("Loading user details for username or email: {}", usernameOrEmail);

        // Use findByUsernameOrEmailWithRoles to avoid N+1 query problem
        final User user = userRepository.findByUsernameOrEmailWithRoles(StringUtils.trim(usernameOrEmail)).orElseThrow(
                () -> {
                    log.error("User not found with username or email: {}", usernameOrEmail);
                    return new UsernameNotFoundException("User not found with username or email: " + usernameOrEmail);
                });

        return buildUserDetails(user);
    }

    /**
     * Build UserDetails from User entity
     *
     * @param user The user entity
     * @return Spring Security UserDetails
     */
    private UserDetails buildUserDetails(User user) {
        // Map roles to authorities
        final Collection<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());

        // Additional log to help with debugging
        log.debug("Built UserDetails for user: {}, with authorities: {}", user.getUsername(), authorities);

        return new CustomUserDetails(user, authorities);
    }
}