package com.quickcart.quickCart.auth.dto;

import com.quickcart.quickCart.user.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
    // Рейтинг???
    private Long id;

    @NotBlank(message = "Требуется указать имя пользователя")
    @Size(min = 3, max = 20, message = "Имя пользователя должно быть длиной от 3 до 20 символов")
    private String username;

    @NotBlank(message = "Требуется указать пароль")
    @Size(min = 6, max = 100, message = "Пароль должен быть длиной от 6 до 100 символов")
    private String password;

    @NotBlank(message = "Требуется указать email")
    @Email(message = "Email должен быть корректным")
    private String email;

    private String location;

    private User.Role role;

    public UserDto() {
    }

    public UserDto(Long id, String username, String email, String location, User.Role role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.location = location;
        this.role = role;
    }

}
