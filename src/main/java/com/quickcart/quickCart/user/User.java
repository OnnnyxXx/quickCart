package com.quickcart.quickCart.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "users", uniqueConstraints = {@UniqueConstraint(columnNames = "username"), @UniqueConstraint(columnNames = "email")})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 20)
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100)
    private String password;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    private String location;

    private int rating;

    @Enumerated(EnumType.STRING)
    private Role role;

    public User(Long id, String username, String password, String email, String location, int rating, Role role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.location = location;
        this.rating = rating;
        this.role = role;
    }

    public enum Role {
        ADMIN,
        SELLER,
        BUYER
    }

    @Override
    public String toString() {
        return "User {" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", location='" + location + '\'' +
                ", rating=" + rating +
                ", role='" + role + '\'' +
                '}';
    }
}
