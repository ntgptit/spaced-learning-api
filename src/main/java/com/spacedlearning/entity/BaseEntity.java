package com.spacedlearning.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@SQLRestriction("deleted_at IS NULL")
@Getter
@Setter
public abstract class BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(name = "id", updatable = false)
	private UUID id;

	@CreatedDate
	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;

	@LastModifiedDate
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	/**
	 * When this field is set, the entity is considered deleted (soft delete).
	 * The @SQLRestriction annotation on the class ensures that entities with
	 * non-null deletedAt will not be returned in normal queries.
	 */
	@Column(name = "deleted_at")
	private LocalDateTime deletedAt;

	/**
	 * Check if the entity has been soft deleted
	 *
	 * @return true if the entity is deleted (deleted_at is not null)
	 */
	public boolean isDeleted() {
		return deletedAt != null;
	}

	/**
	 * Restore a soft-deleted entity
	 */
	public void restore() {
		deletedAt = null;
	}

	/**
	 * Mark this entity as soft deleted
	 */
	public void softDelete() {
		deletedAt = LocalDateTime.now();
	}
}