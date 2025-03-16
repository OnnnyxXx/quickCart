package com.quickcart.quickCart.user;

import com.quickcart.quickCart.user.auth.dto.UserDTO;
import com.quickcart.quickCart.user.auth.dto.UserDtoInfo;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Для /update/{id}
    public ResponseEntity<UserDTO> getUserById(Long id) {
        return userRepository.findById(id)
                .map(user -> {
                    UserDTO userDTO = new UserDTO();
                    userDTO.setId(user.getId());
                    userDTO.setUsername(user.getUsername());
                    userDTO.setEmail(user.getEmail());
                    userDTO.setLocation(user.getLocation());
                    return ResponseEntity.ok(userDTO);
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    public Optional<UserDtoInfo> getUserByEmail(String email) {
        return userRepository.findInfoByEmail(email);
    }

    public Optional<UserDtoInfo> getInfoById(Long id){
        return userRepository.findInfoById(id);
    }


    @Transactional
    public void updateUser(UserDTO userDTO) {
        User user = userRepository.findById(userDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException("User  not found"));

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
