package com.quickcart.quickCart.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/all")
    public List<UserDTO> getAllUsers() {
        System.out.println("getAllUsers called");
        return userService.dtoList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") Long id) {
        return userService.getUserById(id);
    }

    @PostMapping("/create")
    public ResponseEntity<User> create(@RequestBody User user) {
        return userService.createUser(user);

    }

    @GetMapping("email/{email}")
    public ResponseEntity<User> getUserByEmail(@PathVariable("email") String email) {
        return userService.getUserByEmail(email);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<User> update(@PathVariable("id") Long id,
                                       @RequestParam(required = false) String name,
                                       @RequestParam(required = false) String password) {

        ResponseEntity<User> userResponse = userService.getUserById(id);
        if (userResponse.getStatusCode() == HttpStatus.NOT_FOUND) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        }
        User existingUser = userResponse.getBody();
        if (existingUser != null) {
            if (name != null) {
                existingUser.setUsername(name);
            }
            if (password != null) {
                existingUser.setPassword(password);
            }
        }
        userService.updateUser(id, existingUser);
        return userService.updateUser(id, existingUser);

    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<User> delete(@PathVariable("id") Long id) {
        return userService.delete(id);
    }

}
