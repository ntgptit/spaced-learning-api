package com.spacedlearning.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.spacedlearning.entity.User;
import com.spacedlearning.entity.enums.UserStatus;

/**
 * Repository for User entity with enhanced queries.
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

	/**
	 * Checks if a user exists with the given email.
	 *
	 * @param email The email to check
	 * @return true if a user exists, false otherwise
	 */
	boolean existsByEmail(String email);

	/**
	 * Checks if a user exists with the given username.
	 *
	 * @param username The username to check
	 * @return true if a user exists, false otherwise
	 */
	boolean existsByUsername(String username);

	/**
	 * Finds a user by email.
	 *
	 * @param email The email to search for
	 * @return An Optional containing the user if found
	 */
	Optional<User> findByEmail(String email);

	/**
	 * Finds a user by username.
	 *
	 * @param username The username to search for
	 * @return An Optional containing the user if found
	 */
	Optional<User> findByUsername(String username);

	/**
	 * Finds a user by username or email.
	 *
	 * @param usernameOrEmail The username or email to search for
	 * @return An Optional containing the user if found
	 */
	@Query("SELECT u FROM User u WHERE (u.username = :usernameOrEmail OR u.email = :usernameOrEmail) AND u.deletedAt IS NULL")
	Optional<User> findByUsernameOrEmail(@Param("usernameOrEmail") String usernameOrEmail);

	/**
	 * Finds a user by email, including roles, with a single query.
	 *
	 * @param email The email to search for
	 * @return An Optional containing the user if found
	 */
	@Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.email = :email AND u.deletedAt IS NULL")
	Optional<User> findByEmailWithRoles(@Param("email") String email);

	/**
	 * Finds a user by username, including roles, with a single query.
	 *
	 * @param username The username to search for
	 * @return An Optional containing the user if found
	 */
	@Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.username = :username AND u.deletedAt IS NULL")
	Optional<User> findByUsernameWithRoles(@Param("username") String username);

	/**
	 * Finds a user by username or email, including roles, with a single query.
	 *
	 * @param usernameOrEmail The username or email to search for
	 * @return An Optional containing the user if found
	 */
	@Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE (u.username = :usernameOrEmail OR u.email = :usernameOrEmail) AND u.deletedAt IS NULL")
	Optional<User> findByUsernameOrEmailWithRoles(@Param("usernameOrEmail") String usernameOrEmail);

	/**
	 * Find users by status
	 * 
	 * @param status The user status
	 * @return List of users with the specified status
	 */
	List<User> findByStatus(UserStatus status);
}