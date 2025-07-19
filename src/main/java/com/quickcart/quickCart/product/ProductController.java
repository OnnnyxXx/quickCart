package com.quickcart.quickCart.product;

import com.quickcart.quickCart.order.Order;
import com.quickcart.quickCart.order.OrderService;
import com.quickcart.quickCart.product.dto.ProductDTO;
import com.quickcart.quickCart.store.dto.StoreDtoUpdate;
import com.quickcart.quickCart.user.User;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ProductController {

    @Autowired
    ProductService productService;

    @PostMapping("/store/{storeId}/product")
    public ResponseEntity<?> createProduct(@ModelAttribute @Valid ProductDTO product, @PathVariable("storeId") Long storeId){
        productService.createProduct(product, storeId);
        return new ResponseEntity<>("Продукт успешно создан", HttpStatus.CREATED);
    }

    @GetMapping("/store/{storeId}/products")
    public ResponseEntity<List<ProductDTO>> getProductsByStore(@PathVariable("storeId") Long storeId){
        List<ProductDTO> listDTO = productService.getProductsByStoreId(storeId);
        if(listDTO == null) ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        return ResponseEntity.ok(listDTO);
    }

    @GetMapping("/product/productImage/{imageName:.+}")
    public ResponseEntity<Resource> getLogo(@PathVariable String imageName,
                                            @RequestParam(required = false, defaultValue = "false")
                                            boolean download) {
        return productService.getImage(imageName, download);
    }

    @GetMapping("/product/{id}")
    public ResponseEntity<ProductDTO> getProduct(@PathVariable("id") Long id){
        ProductDTO productDTO = productService.getProductById(id);
        if(productDTO == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        return ResponseEntity.ok(productDTO);
    }

    @PatchMapping("/product/{id}")
    public ResponseEntity<HashMap<String, String>> updateProduct(@PathVariable("id") Long id,
                                                                 @ModelAttribute @Valid ProductDTO productDTO,
                                                                 @RequestParam(required = false) MultipartFile image){
        return ResponseEntity.ok(productService.updateProductById(id, productDTO, image));
    }
}
