package com.quickcart.quickCart.securityService;

import com.quickcart.quickCart.product.Product;
import com.quickcart.quickCart.product.ProductRepository;
import com.quickcart.quickCart.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProductSecurityService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    public boolean isOwnerProduct(Long productId, String userEmail) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return product.getStore().getUser().getEmail().equals(userEmail);
    }
}
