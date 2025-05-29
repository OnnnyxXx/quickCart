package com.quickcart.quickCart.user;

import com.fasterxml.jackson.databind.JsonNode;
import com.quickcart.quickCart.securityService.UserSecurityService;
import com.quickcart.quickCart.auth.dto.UserDtoInfo;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Optional;

@Tag(name = "User", description = "The User API")
@RestController
@RequestMapping("api/v1/users")
public class UserController {

    @Autowired
    private UserSecurityService userSecurityService;

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile/user")
    public Optional<UserDtoInfo> profileUser(){
        return userService.profileUser();
    }

    @GetMapping("/{id}")
    public Optional<UserDtoInfo> getUserById(@PathVariable("id") Long id) {
        return userService.getInfoById(id);
    }

    @GetMapping("email/{email}")
    public Optional<UserDtoInfo> getUserByEmail(@PathVariable("email") String email) {
        return userService.getUserByEmail(email);
    }

    @PatchMapping("/update/{id}")
    @PreAuthorize("@userSecurityService.isYou(#id, authentication.name)")
    public User patch(@PathVariable Long id, @Valid @RequestBody JsonNode patchNode) throws IOException {
        return userService.patch(id, patchNode);
    }

//
//    @DeleteMapping("/delete/{id}")
//    public ResponseEntity<User> delete(@PathVariable("id") Long id) {
//        return userService.delete(id);
//    }

}
