package com.quickcart.quickCart.securityService;

import com.quickcart.quickCart.store.Store;
import com.quickcart.quickCart.store.StoreRepository;
import com.quickcart.quickCart.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class StoreSecurityService {
    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private UserRepository userRepository;

    public boolean isOwner(Long storeId, String userEmail) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Store not found with id " + storeId));
        return store.getUser().getEmail().equals(userEmail);
    }
}
