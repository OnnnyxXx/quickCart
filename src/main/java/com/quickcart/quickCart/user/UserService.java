package com.quickcart.quickCart.user;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quickcart.quickCart.auth.dto.UserDtoInfo;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.Optional;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public UserService(UserRepository userRepository, ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    public Optional<UserDtoInfo> profileUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();
        return userRepository.findInfoByEmail(currentUserEmail);
    }

    public Optional<UserDtoInfo> getUserByEmail(String email) {
        return Optional.ofNullable(userRepository.findInfoByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден")));
    }

    @Cacheable(value = "myProfile", key = "#id")
    public Optional<UserDtoInfo> getInfoById(Long id) {
        return Optional.ofNullable(userRepository.findInfoById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден")));
    }

    @Transactional
    @CacheEvict(value = "myProfile", key = "#id")
    public User patch(Long id, JsonNode patchNode) throws IOException {
        User user = userRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь c id `%s` не найден".formatted(id)));

        objectMapper.readerForUpdating(user).readValue(patchNode);

        return userRepository.save(user);
    }

    public ResponseEntity<User> delete(Long id) {
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
