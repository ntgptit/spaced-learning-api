package com.spacedlearning.repository;

import com.spacedlearning.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Role entity.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Finds a role by name.
     *
     * @param name The role name
     * @return An Optional containing the role if found
     */
    Optional<Role> findByName(String name);
}