package com.quickcart.quickCart.product;

import com.quickcart.quickCart.product.dto.ProductDTO;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/store/{storeId}/product")
    @PreAuthorize("@storeSecurityService.isOwner(#storeId, authentication.name)")
    public ResponseEntity<Product> createProduct(@ModelAttribute @Valid ProductDTO product,
                                                 @PathVariable("storeId") Long storeId) {
        productService.createProduct(product, storeId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/get/all/products")
    public List<ProductDTO> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/store/{storeId}/products")
    public ResponseEntity<List<ProductDTO>> getProductsByStore(@PathVariable("storeId") Long storeId) {

        List<ProductDTO> listDTO = productService.getProductsByStoreId(storeId);
        if (listDTO == null) ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        return ResponseEntity.ok(listDTO);
    }

    @GetMapping("/product/productImage/{imageName:.+}")
    public ResponseEntity<Resource> getLogo(@PathVariable String imageName,
                                            @RequestParam(required = false, defaultValue = "false")
                                            boolean download) {
        return productService.getImage(imageName, download);
    }

    @GetMapping("/product/{id}")
    public ResponseEntity<ProductDTO> getProduct(@PathVariable("id") Long id) {
        ProductDTO productDTO = productService.getProductById(id);
        if (productDTO == null) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        return ResponseEntity.ok(productDTO);
    }

    @PatchMapping("/product/{id}")
    @PreAuthorize("@productSecurityService.isOwnerProduct(#id, authentication.name)")
    public ResponseEntity<HashMap<String, String>> updateProduct(@PathVariable("id") Long id,
                                                                 @ModelAttribute @Valid ProductDTO productDTO,
                                                                 @RequestParam(required = false) MultipartFile image) {
        HashMap<String, String> result = productService.updateProductById(id, productDTO, image);
        return ResponseEntity.ok(result);
    }
}
