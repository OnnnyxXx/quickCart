package com.quickcart.quickCart.product;

import com.quickcart.quickCart.store.Store;
import com.quickcart.quickCart.store.StoreService;

import com.quickcart.quickCart.user.UserService;
import jakarta.transaction.Transactional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class ProductService {
    final private ProductRepository productRepository;
    final private UserService userService;
    final private StoreService storeService;

    public ProductService(ProductRepository productRepository, UserService userService, StoreService storeService) {
        super();
        this.productRepository = productRepository;
        this.userService = userService;
        this.storeService = storeService;
    }

    public ResponseEntity<Product> createProduct(Product product, Long storeId) {
        Store store = storeService.getStoreById(storeId);
        if(store != null) product.setStore(store);
        else return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        Product savedProduct = productRepository.save(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
    }

    public ResponseEntity<List<Product>> getProductsByStoreId(Long storeId) {
        Store store = storeService.getStoreById(storeId);
        if(store == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        return ResponseEntity.ok(store.getProducts());
    }

    public ResponseEntity<Product> getProductById(Long id) {
        return productRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build()
                );
    }

    @Transactional //may be not necessary
    public ResponseEntity<Product> updateProductById(Long id, Product updateProduct) {
        if (!productRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        updateProduct.setId(id);
        Product savedProduct = productRepository.save(updateProduct);
        return ResponseEntity.ok(savedProduct);
    }
}