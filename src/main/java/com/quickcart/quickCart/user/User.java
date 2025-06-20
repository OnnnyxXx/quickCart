package com.quickcart.quickCart.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.quickcart.quickCart.moderation.ModerationRequest;
import com.quickcart.quickCart.store.Store;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Требуется указать имя пользователя")
    @Size(min = 3, max = 20, message = "Имя пользователя должно быть длиной от 3 до 20 символов")
    private String username;

    @NotBlank(message = "Требуется указать пароль")
    @Size(min = 6, max = 100, message = "Пароль должен быть длиной от 6 до 100 символов")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @NotBlank(message = "Требуется указать email")
    @Email(message = "Email должен быть корректным")
    private String email;

    private String location;

    private int rating;

    @Enumerated(EnumType.STRING)
    private Role role;

    // Добавляем связь с заявками модерации
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<ModerationRequest> moderationRequests = new ArrayList<>();

    // Добавляем связь с магазинами
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Store> stores = new ArrayList<>();

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
        BUYER,
        MODER,
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