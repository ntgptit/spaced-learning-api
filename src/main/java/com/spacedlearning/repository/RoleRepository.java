package com.spacedlearning.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.spacedlearning.entity.Role;

/**
 * Repository for Role entity.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Checks if a role exists with the given name.
     *
     * @param name The role name
     * @return true if a role exists, false otherwise
     */
    boolean existsByName(String name);

    /**
     * Finds a role by name.
     *
     * @param name The role name
     * @return An Optional containing the role if found
     */
    Optional<Role> findByName(String name);
}