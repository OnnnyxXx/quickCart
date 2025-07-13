package com.quickcart.quickCart.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {

    @NotBlank(message = "Требуется указать почту")
    @Email(message = "Email должен быть корректным")
    private String email;

    @NotBlank(message = "Требуется указать пароль")
    private String password;
}
