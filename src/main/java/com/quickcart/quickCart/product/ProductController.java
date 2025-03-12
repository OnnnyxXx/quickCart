package com.quickcart.quickCart.product;

import com.quickcart.quickCart.order.Order;
import com.quickcart.quickCart.order.OrderService;
import com.quickcart.quickCart.user.User;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
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
    public ResponseEntity<List<Product>> getProductsByStore(@PathVariable("storeId") Long storeId){
        return productService.getProductsByStoreId(storeId);
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable("id") Long id){
        return productService.getProductById(id);
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable("id") Long id,
                                                 @RequestParam(required = false) String name,
                                                 @RequestParam(required = false) String description,
                                                 @RequestParam(required = false) BigDecimal price,
                                                 @RequestParam(required = false) Integer stock,
                                                 @RequestParam(required = false) String imageUrl,
                                                 @RequestParam(required = false) String category){

        Product product = productService.getProductById(id).getBody();
        if(product == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        if(name != null) product.setName(name);
        if(description != null) product.setDescription(description);
        if(price != null) product.setPrice(price);
        if(stock != null) product.setStock(stock);
        if(imageUrl != null) product.setImageUrl(imageUrl);
        if(category != null) product.setCategory(category);
        return productService.updateProductById(id, product);
    }
}
