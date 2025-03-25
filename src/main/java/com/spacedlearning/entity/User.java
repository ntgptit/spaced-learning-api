package com.spacedlearning.entity;

import java.util.HashSet;
import java.util.Set;

import com.spacedlearning.entity.enums.UserStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity representing a user in the system.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users", schema = "spaced_learning")
public class User extends BaseEntity {

	@NotBlank
	@Size(max = 100)
	@Column(name = "name", length = 100)
	private String name;

	@Email
	@NotBlank
	@Size(max = 100)
	@Column(name = "email", length = 100, unique = true)
	private String email;

	@NotBlank
	@Size(min = 8, max = 120)
	@Column(name = "password", length = 120, nullable = false)
	private String password;

	@Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private UserStatus status = UserStatus.ACTIVE;

	@OneToMany(mappedBy = "user")
    private Set<ModuleProgress> moduleProgresses = new HashSet<>();

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles = new HashSet<>();

	/**
	 * Add a role to this user
	 *
	 * @param role The role to add
	 */
	public void addRole(Role role) {
		if (roles == null) {
			roles = new HashSet<>();
		}
		roles.add(role);
	}

	/**
	 * Check if the user has a specific role
	 *
	 * @param roleName The role name to check
	 * @return true if the user has the role, false otherwise
	 */
	public boolean hasRole(String roleName) {
		if (roles == null) {
			return false;
		}
		return roles.stream().anyMatch(role -> role.getName().equals(roleName));
	}

	/**
	 * Remove a role from this user
	 *
	 * @param role The role to remove
	 * @return true if the role was removed, false if not found
	 */
	public boolean removeRole(Role role) {
		if (roles == null) {
			return false;
		}
		return roles.remove(role);
	}
}