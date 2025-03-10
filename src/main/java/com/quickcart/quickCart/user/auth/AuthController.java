package com.quickcart.quickCart.user.auth;

import com.quickcart.quickCart.user.UserRepository;
import com.quickcart.quickCart.user.auth.dto.LoginRequest;
import com.quickcart.quickCart.user.auth.dto.UserDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
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

    private final SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();


    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        return authService.login(loginRequest, request);
    }


    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody UserDTO signupRequest) {
        // проверка что мыла нет в базе
        if (userRepository.findByEmail(signupRequest.getEmail()).isPresent()) {
            return new ResponseEntity<>("Email already in use.", HttpStatus.CONFLICT);
        }

        authService.registerUser(signupRequest);
        return new ResponseEntity<>("User  registered successfully", HttpStatus.CREATED);
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        logoutHandler.logout(request, response, authentication);

        request.getSession().removeAttribute("SPRING_SECURITY_CONTEXT");
        request.getSession().invalidate();
        SecurityContextHolder.clearContext();
        return new ResponseEntity<>("Logged out successfully", HttpStatus.OK);

    }

}
