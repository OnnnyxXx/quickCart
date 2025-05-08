package com.quickcart.quickCart.user;

import com.quickcart.quickCart.auth.dto.UserDto;
import com.quickcart.quickCart.auth.dto.UserDtoInfo;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<UserDtoInfo> profileUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();
        return userRepository.findInfoByEmail(currentUserEmail);
    }

    // Для /update/{id}
    public ResponseEntity<UserDto> getUserById(Long id) {
        return userRepository.findById(id)
                .map(user -> {
                    UserDto userDTO = new UserDto();
                    userDTO.setId(user.getId());
                    userDTO.setUsername(user.getUsername());
                    userDTO.setEmail(user.getEmail());
                    userDTO.setLocation(user.getLocation());
                    return ResponseEntity.ok(userDTO);
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    public Optional<UserDtoInfo> getUserByEmail(String email) {
        return Optional.ofNullable(userRepository.findInfoByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден")));
    }

    public Optional<UserDtoInfo> getInfoById(Long id) {
        return Optional.ofNullable(userRepository.findInfoById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден")));
    }

    @Transactional
    public void updateUser(UserDto userDTO) {
        User user = userRepository.findById(userDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден"));

        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setLocation(userDTO.getLocation());

        if (userDTO.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }
        userRepository.save(user);
    }

    public ResponseEntity<User> delete(Long id) {
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
