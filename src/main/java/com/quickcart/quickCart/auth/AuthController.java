package com.quickcart.quickCart.auth;

import com.quickcart.quickCart.auth.dto.SignupRequest;
import com.quickcart.quickCart.user.UserRepository;
import com.quickcart.quickCart.auth.dto.LoginRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth", description = "The Auth API")
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final AuthService authService;

    public AuthController(UserRepository userRepository, AuthService authService) {
        this.userRepository = userRepository;
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest,
                                        HttpServletRequest request, HttpServletResponse response) {
        return authService.login(loginRequest, request, response);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
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
