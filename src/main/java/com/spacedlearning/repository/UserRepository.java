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
	 * Finds a user by email.
	 *
	 * @param email The email to search for
	 * @return An Optional containing the user if found
	 */
	Optional<User> findByEmail(String email);

	/**
	 * Finds a user by email, including roles, with a single query. Uses a custom
	 * query to avoid N+1 issues.
	 *
	 * @param email The email to search for
	 * @return An Optional containing the user if found
	 */
	@Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.email = :email")
	Optional<User> findByEmailWithRoles(String email);

	/**
	 * Find users by status
	 * 
	 * @param status The user status
	 * @return List of users with the specified status
	 */
	List<User> findByStatus(UserStatus status);

	/**
	 * Find users by status ordered by last active date
	 * 
	 * @param status The user status
	 * @return List of users with the specified status ordered by last active date
	 */
	List<User> findAllByStatusOrderByLastActiveDate(UserStatus status);

	/**
	 * Get active users with the most modules in progress
	 * 
	 * @param limit Maximum number of users to return
	 * @return List of active users with the most modules in progress
	 */
	@Query(value = """
			SELECT u.* FROM spaced_learning.users u
			JOIN (
			    SELECT mp.user_id, COUNT(mp.id) as module_count
			    FROM spaced_learning.module_progress mp
			    WHERE mp.deleted_at IS NULL
			    AND mp.percent_complete < 100
			    GROUP BY mp.user_id
			    ORDER BY module_count DESC
			    LIMIT :limit
			) mc ON u.id = mc.user_id
			WHERE u.status = 'ACTIVE'
			AND u.deleted_at IS NULL
			""", nativeQuery = true)
	List<User> findActiveUsersWithMostModulesInProgress(@Param("limit") int limit);
}