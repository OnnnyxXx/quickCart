package com.quickcart.quickCart.auth;

import com.quickcart.quickCart.user.User;
import com.quickcart.quickCart.user.UserRepository;
import com.quickcart.quickCart.auth.dto.LoginRequest;
import com.quickcart.quickCart.auth.dto.UserDto;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;


@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;


    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public void registerUser(UserDto userDTO) {
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setLocation(userDTO.getLocation());
        user.setRating(0);

        if (userDTO.getRole() == null) {
            user.setRole(User.Role.BUYER);
        } else {
            user.setRole(userDTO.getRole());
        }
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        userRepository.save(user);
    }

    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request,
                                        HttpServletResponse response) {
        try {
            Authentication authenticationRequest =
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());

            Authentication authenticationResponse = this.authenticationManager.authenticate(authenticationRequest);

            SecurityContextHolder.getContext().setAuthentication(authenticationResponse);

            HttpSession session = request.getSession();
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

            // Установка cookie
            Cookie cookie = new Cookie("sessionId", session.getId());
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            response.addCookie(cookie);

            return new ResponseEntity<>("Пользователь успешно вошел в систему!.", HttpStatus.OK);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>("Неверный адрес электронной почты или пароль.", HttpStatus.BAD_REQUEST);
        }
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        boolean isSecure = false;
        String contextPath = null;
        if (request != null) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            isSecure = request.isSecure();
            contextPath = request.getContextPath();
        }
        SecurityContext context = SecurityContextHolder.getContext();
        SecurityContextHolder.clearContext();
        context.setAuthentication(null);
        if (response != null) {
            Cookie cookie = new Cookie("sessionId", null);
            String cookiePath = StringUtils.hasText(contextPath) ? contextPath : "/";
            cookie.setPath(cookiePath);
            cookie.setMaxAge(0);
            cookie.setSecure(isSecure);
            response.addCookie(cookie);
        }

    }


}

