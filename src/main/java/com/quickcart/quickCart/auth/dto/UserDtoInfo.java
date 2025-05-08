package com.quickcart.quickCart.auth.dto;

import com.quickcart.quickCart.user.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDtoInfo {

    private Long id;

    private String username;

    private String email;

    private String location;

    private User.Role role;

    public UserDtoInfo() {
    }

    public UserDtoInfo(Long id, String username, String email, String location, User.Role role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.location = location;
        this.role = role;
    }
}
