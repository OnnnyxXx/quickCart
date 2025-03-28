package com.quickcart.quickCart.securityService;

import com.quickcart.quickCart.store.StoreRepository;
import com.quickcart.quickCart.user.User;
import com.quickcart.quickCart.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserSecurityService {
    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private UserRepository userRepository;

    public boolean isYou(Long userId, String userEmail) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return user.getEmail().equals(userEmail);
    }
}
