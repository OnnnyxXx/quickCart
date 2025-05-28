package com.quickcart.quickCart.auth;

import com.quickcart.quickCart.user.UserRepository;
import com.quickcart.quickCart.auth.dto.LoginRequest;
import com.quickcart.quickCart.auth.dto.UserDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth", description = "The Auth API")
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest,
                                        HttpServletRequest request, HttpServletResponse response) {
        return authService.login(loginRequest, request, response);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody UserDto signupRequest) {
        // проверка что мыла нет в базе
        if (userRepository.findByEmail(signupRequest.getEmail()).isPresent()) {
            return new ResponseEntity<>("Электронная почта уже используется.", HttpStatus.CONFLICT);
        }

        authService.registerUser(signupRequest);
        return new ResponseEntity<>("Пользователь успешно зарегистрирован", HttpStatus.CREATED);
    }

    @PostMapping("/logout")
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        authService.logout(request, response);
    }

}
