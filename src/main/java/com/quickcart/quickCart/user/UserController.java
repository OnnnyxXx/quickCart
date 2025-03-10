package com.quickcart.quickCart.user;

import com.quickcart.quickCart.user.auth.dto.UserDTO;
import com.quickcart.quickCart.user.auth.dto.UserDtoInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/{id}")
    public Optional<UserDtoInfo> getUserById(@PathVariable("id") Long id) {
        return userService.getInfoById(id);
    }

    @GetMapping("email/{email}")
    public Optional<UserDtoInfo> getUserByEmail(@PathVariable("email") String email) {
        return userService.getUserByEmail(email);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable("id") Long id,
                                                      @RequestParam(required = false) String name,
                                                      @RequestParam(required = false) String email,
                                                      @RequestParam(required = false) String password,
                                                      @RequestParam(required = false) String location) {
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

        if (location != null) {
            userDTO.setLocation(location);
            response.put("location", userDTO.getLocation());
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
