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
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "users", schema = "spaced_learning")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
@ToString(onlyExplicitlyIncluded = true)
public class User extends BaseEntity {

    @NotBlank
    @Size(max = 100)
    @Column(name = "name", length = 100)
    @ToString.Include
    private String name;

    @NotBlank
    @Size(min = 3, max = 50)
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Username can only contain letters, numbers, dots, underscores and hyphens")
    @Column(name = "username", length = 50, unique = true, nullable = false)
    @ToString.Include
    private String username;

    @Email
    @NotBlank
    @Size(max = 100)
    @Column(name = "email", length = 100, unique = true)
    @ToString.Include
    private String email;

    @NotBlank
    @Size(min = 8, max = 120)
    @Column(name = "password", length = 120, nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    @Builder.Default
    private UserStatus status = UserStatus.ACTIVE;

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles", schema = "spaced_learning", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_books", schema = "spaced_learning", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "book_id"))
    private Set<Book> books = new HashSet<>();

    @Column(name = "last_active_date")
    private LocalDateTime lastActiveDate;

    // Book methods
    public void addBook(Book book) {
        this.books.add(book);
        book.getUsers().add(this);
    }

    // Role methods
    public void addRole(Role role) {
        this.roles.add(role);
    }

    public boolean hasRole(String roleName) {
        return this.roles.stream().anyMatch(role -> role.getName().equals(roleName));
    }

    public boolean removeBook(Book book) {
        if (this.books.remove(book)) {
            book.getUsers().remove(this);
            return true;
        }
        return false;
    }

    public boolean removeRole(Role role) {
        return this.roles.remove(role);
    }
}
