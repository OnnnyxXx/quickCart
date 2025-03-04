package com.quickcart.quickCart.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable("id") Long id) {
        return userService.getUserById(id);
    }

    @PostMapping("/create")
    public ResponseEntity<User> create(@RequestBody User user) {
        return userService.createUser(user);

    }

    @GetMapping("email/{email}")
    public ResponseEntity<UserDTO> getUserByEmail(@PathVariable("email") String email) {
        return userService.getUserByEmail(email);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable("id") Long id,
                                                      @RequestParam(required = false) String name,
                                                      @RequestParam(required = false) String email,
                                                      @RequestParam(required = false) String password) {
        UserDTO userDTO = userService.getUserById(id).getBody();
        Map<String, Object> response = new HashMap<>();

        if (userDTO == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        if (name != null) {
            userDTO.setUsername(name);
            response.put("name", userDTO.getUsername());
        }
        if (email != null) {
            userDTO.setEmail(email);
            response.put("email", userDTO.getEmail());
        }
        if (password != null) {
            userDTO.setPassword(password);
//            response.put("password", userDTO.getPassword()); не надо его возвращать
        }

        userService.updateUser(userDTO);
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<User> delete(@PathVariable("id") Long id) {
        return userService.delete(id);
    }

}
