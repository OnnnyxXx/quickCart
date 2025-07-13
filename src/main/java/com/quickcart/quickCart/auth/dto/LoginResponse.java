package com.quickcart.quickCart.auth.dto;

import com.quickcart.quickCart.user.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse {

    private long id;
    private String username;
    private String email;
    private String location;
    private String role;

    public LoginResponse() {}

    public static LoginResponse convertUserToLoginResponse(User user) {
        LoginResponse response = new LoginResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setLocation(user.getLocation());
        response.setRole(user.getRole().name());
        return response;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", location='" + location + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}


