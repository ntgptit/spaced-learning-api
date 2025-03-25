package com.spacedlearning.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

import com.spacedlearning.entity.User;
import com.spacedlearning.entity.enums.UserStatus;

import lombok.Getter;

/**
 * Custom UserDetails implementation that holds a reference to the original User
 * entity.
 */
@Getter
public class CustomUserDetails extends org.springframework.security.core.userdetails.User {

    private static final long serialVersionUID = 1L;

    /**
     * Determines if the user is active based on their status.
     *
     * @param user The user to check
     * @return true if the user status is ACTIVE, false otherwise
     */
    private static boolean isUserActive(User user) {
        return user != null && UserStatus.ACTIVE.equals(user.getStatus());
    }

    private transient User user;

	/**
     * Constructs a CustomUserDetails from a User entity
     *
     * @param user        The original User entity
     * @param authorities The authorities granted to the user
     */
    public CustomUserDetails(User user, Collection<? extends GrantedAuthority> authorities) {
        super(
            user.getEmail(),
            user.getPassword(),
            isUserActive(user),
            true, // accountNonExpired
            true, // credentialsNonExpired
            true, // accountNonLocked
            authorities);
        this.user = user;
    }
}