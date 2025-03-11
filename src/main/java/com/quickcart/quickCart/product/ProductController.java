package com.quickcart.quickCart.product;

import com.quickcart.quickCart.order.Order;
import com.quickcart.quickCart.order.OrderService;
import com.quickcart.quickCart.user.User;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ProductController {

    @Autowired
    ProductService productService;

    @PostMapping("/stores/{storeId}/products")
    public ResponseEntity<Product> createProduct(@Valid @RequestBody Product product, @PathVariable("storeId") Long storeId,/* CurrentUser*/ User user){
        return productService.createProduct(product, storeId);
    }

    @GetMapping("/stores/{storeId}/products")
    public List<Product> getProductsByStore(@PathVariable("storeId") Long storeId){
        return productService.getProductsByStoreId(storeId);
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable("id") Long id){
        return productService.getProductById(id);
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<Product> updateProduct(@Valid @RequestBody Product product, @PathVariable("id") Long id){
        ResponseEntity<Product> productResponse = productService.getProductById(id);
        if(productResponse.getStatusCode() == HttpStatus.NOT_FOUND) return productResponse;
        return productService.updateProductById(id, product);
    }

}
