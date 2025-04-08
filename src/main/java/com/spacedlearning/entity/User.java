package com.spacedlearning.entity;

import java.time.LocalDateTime;
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
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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

    @NotBlank
    @Size(min = 3, max = 50)
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Username can only contain letters, numbers, dots, underscores and hyphens")
    @Column(name = "username", length = 50, unique = true, nullable = false)
    private String username;

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

    // Removed: @OneToMany(mappedBy = "user") private Set<ModuleProgress> moduleProgresses

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles", schema = "spaced_learning", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    // Added: Many-to-Many relationship with Book
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_books", schema = "spaced_learning", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "book_id"))
    private Set<Book> books = new HashSet<>();

    @Column(name = "last_active_date")
    private LocalDateTime lastActiveDate;

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

    /**
     * Add a book to this user
     *
     * @param book The book to add
     */
    public void addBook(Book book) {
        if (books == null) {
            books = new HashSet<>();
        }
        books.add(book);
        book.getUsers().add(this);
    }

    /**
     * Remove a book from this user
     *
     * @param book The book to remove
     * @return true if the book was removed, false if not found
     */
    public boolean removeBook(Book book) {
        if (books == null) {
            return false;
        }
        if (books.remove(book)) {
            book.getUsers().remove(this);
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "User{" + "id=" + getId() + ", username='" + username + '\'' + ", email='" + email + '\'' + ", name='"
                + name + '\'' + ", status=" + status + ", createdAt=" + getCreatedAt() + '}';
    }
}