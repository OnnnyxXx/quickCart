package com.quickcart.quickCart.auth.dto;

import com.quickcart.quickCart.user.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Поля которые нужны для регистрации пользователя.
 */
@Setter
@Getter
public class SignupRequest {

    @Schema(description = "Username пользователя", example = "MyName")
    @NotBlank(message = "Имя пользователя обязательно")
    @Size(min = 3, max = 20, message = "Имя пользователя должно быть длиной от 3 до 20 символов")
    private String username;

    @Schema(description = "Email пользователя", example = "my_mail@example.com")
    @NotBlank(message = "Требуется указать email")
    @Email(message = "Email должен быть корректным")
    private String email;

    @NotBlank(message = "Пароль обязателен")
    @Size(min = 6, max = 100, message = "Пароль должен быть длиной от 6 до 100 символов")
    private String password;

    @Schema(description = "Роль пользователя", examples = {"BUYER", "SELLER"}, defaultValue = "BUYER")
    private User.Role role = User.Role.BUYER;

    /**
     * Дефолтный конструктор
     */
    public SignupRequest() {
    }

    /**
     * Конструктор с указанием роли.
     * Используется для создания объекта SignupRequest с заданными username, email, password и role.
     *
     * @param username имя пользователя
     * @param email    электронная почта пользователя
     * @param password пароль пользователя
     * @param role     роль пользователя (например, "BUYER" или "SELLER")
     */
    public SignupRequest(String username, String email, String password, User.Role role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    /**
     * Конструктор без указания роли.
     * Используется для создания объекта SignupRequest с заданными username, email и password.
     * Роль пользователя будет установлена на значение по умолчанию ("BUYER").
     *
     * @param username имя пользователя
     * @param email    электронная почта пользователя
     * @param password пароль пользователя
     */
    public SignupRequest(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
}
