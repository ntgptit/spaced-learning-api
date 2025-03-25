package com.spacedlearning.entity;

import java.util.HashSet;
import java.util.Set;

import com.spacedlearning.entity.enums.UserStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

	@Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private UserStatus status = UserStatus.ACTIVE;

	@OneToMany(mappedBy = "user")
    private Set<ModuleProgress> moduleProgresses = new HashSet<>();
}