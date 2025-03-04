package com.quickcart.quickCart.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {

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

    public UserDTO() {
    }

    public UserDTO(Long id, String username, String password, String email, String location) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.location = location;
    }
}
