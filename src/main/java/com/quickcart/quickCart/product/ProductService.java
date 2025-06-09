package com.quickcart.quickCart.product;

import com.quickcart.quickCart.product.dto.ProductWithQuantityDTO;
import com.quickcart.quickCart.store.Store;
import com.quickcart.quickCart.store.StoreService;

import com.quickcart.quickCart.product.dto.ProductDTO;
import com.quickcart.quickCart.user.User;
import com.quickcart.quickCart.user.UserService;
import jakarta.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
@EnableCaching
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(StoreService.class);

    final private ProductRepository productRepository;

    final private OrderProductRepository orderProductRepository;
    final private UserService userService;
    final private StoreService storeService;

    public ProductService(OrderProductRepository orderProductRepository, ProductRepository productRepository, UserService userService, StoreService storeService) {
        super();
        this.productRepository = productRepository;
        this.orderProductRepository = orderProductRepository;
        this.userService = userService;
        this.storeService = storeService;
    }

    @Transactional
    public ResponseEntity<Product> createProduct(ProductDTO productDTO, Long storeId) {
        Store store = storeService.getStoreById(storeId);
        Product product = getProduct(productDTO);
        if(store != null) product.setStore(store);
        else return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        Product savedProduct = productRepository.save(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
    }
    public Product getProduct(ProductDTO productDTO) {
        Product product = new Product();

        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setCategory(productDTO.getCategory());
        product.setPrice(new BigDecimal(productDTO.getPrice()));
        product.setStock(productDTO.getStock());
        MultipartFile image = productDTO.getImage();
        if (image != null && !image.isEmpty()) {
            String imageUrl = saveImageAsWebP(image);
            product.setImageUrl(imageUrl);
        }
        return product;
    }

    public Product getProduct(Long id){
        return productRepository.findById(id).get();
    }
    public static ProductDTO getProductDTO(Product product){
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(product.getId());
        productDTO.setStoreId(product.getStore().getId());
        productDTO.setName(product.getName());
        productDTO.setDescription(product.getDescription());
        productDTO.setCategory(product.getCategory());
        productDTO.setPrice(product.getPrice().toString());
        productDTO.setStock(product.getStock());
        productDTO.setImageUrl(product.getImageUrl());
        return productDTO;
    }

    public static ProductWithQuantityDTO getProductWithQuantityDTO(OrderProduct orderProduct){
        Product product = orderProduct.getProduct();
        ProductWithQuantityDTO productDTO = new ProductWithQuantityDTO();
        productDTO.setId(product.getId());
        productDTO.setStoreId(product.getStore().getId());
        productDTO.setName(product.getName());
        productDTO.setDescription(product.getDescription());
        productDTO.setCategory(product.getCategory());
        productDTO.setPrice(product.getPrice().toString());
        productDTO.setStock(product.getStock());
        productDTO.setImageUrl(product.getImageUrl());
        productDTO.setQuantity(orderProduct.getQuantity());
        return productDTO;
    }



    @Cacheable(value = "products", key = "#storeId")
    public List<ProductDTO> getProductsByStoreId(Long storeId) {
        Store store = storeService.getStoreById(storeId);
        if(store == null) return null;
        List<Product> productList = store.getProducts();
        List<ProductDTO> productDTOList = new ArrayList<>();
        for(Product product: productList){
            productDTOList.add(getProductDTO(product));
        }
        return productDTOList;
    }

    @Cacheable(value = "product", key = "#id")
    public ProductDTO getProductById(Long id) {
        Optional<Product> product = productRepository.findById(id);
        if(product == null) return null;
        ProductDTO productDTO = getProductDTO(product.get());
        return productDTO;
    }
    @CacheEvict(value = "product", key = "#id")
    @Transactional
    public HashMap<String, String> updateProductById(Long id, ProductDTO updateProduct, MultipartFile image) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Товар с id " + id + " не найден."));

        HashMap<String, String> updatedFields = new HashMap<>();
        if (updateProduct.getName() != null) {
            product.setName(updateProduct.getName());
            updatedFields.put("name", updateProduct.getName());
        }

        if (updateProduct.getDescription() != null) {
            product.setDescription(updateProduct.getDescription());
            updatedFields.put("description", updateProduct.getDescription());
        }
        if (updateProduct.getCategory() != null) {
            product.setCategory(updateProduct.getCategory());
            updatedFields.put("category", updateProduct.getCategory());
        }

        if (updateProduct.getPrice() != null) {
            product.setPrice(new BigDecimal(updateProduct.getPrice()));
            updatedFields.put("price", updateProduct.getPrice().toString());
        }

        if (updateProduct.getStock() > 0) {
            product.setStock(updateProduct.getStock());
            updatedFields.put("stock", "" + updateProduct.getStock());
        }

        if (image != null && !image.isEmpty()) {
            try {
                String productImageUrl = saveImageAsWebP(image);
                product.setImageUrl(productImageUrl);
                updatedFields.put("imageUrl", productImageUrl);
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("Error saving image: {}", e.getMessage());
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Ошибка обновления изображения товара");
            }

        }

        productRepository.save(product);
        logger.info("Обновлен продукт с id: {}", id);

        return updatedFields;
    }

    public String saveImageAsWebP(MultipartFile image) {
        try {
            String formatName = "webp"; // webp, png, jpg без .
            String contentType = image.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new IllegalArgumentException("Файл должен быть изображением.");
            }

            BufferedImage bufferedImage = ImageIO.read(image.getInputStream());
            if (bufferedImage == null) {
                throw new IllegalArgumentException("Невозможно прочитать изображение");
            }

            String originalFileName = image.getOriginalFilename();
            String sanitizedFileName = originalFileName.replaceAll("[^a-zA-Z0-9.]", "_");
            String fileName = sanitizedFileName + "_" + UUID.randomUUID() + "." + formatName;

            File outputDir = new File("src/main/resources/static/productImage/");
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }

            File outputFile = new File(outputDir, fileName);
            ImageIO.write(bufferedImage, formatName, outputFile);

            return fileName;

        } catch (IOException e) {
            logger.error("Ошибка при сохранении логотипа в формате WebP", e);
            throw new RuntimeException("Ошибка при сохранении логотипа в формате WebP", e);
        } catch (IllegalArgumentException e) {
            logger.error("Ошибка при обработке изображения: {}", e.getMessage());
            throw e;
        }
    }

    public ResponseEntity<Resource> getImage(String imageName, boolean download) {
        try {
            Path filePath = Paths.get("src/main/resources/static/productImage/" + imageName);
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            String contentDisposition = download
                    ? "attachment; filename=\"" + resource.getFile() + "\""
                    : "inline; filename=\"" + resource.getFilename() + "\"";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Transactional
    public OrderProduct createOrderProduct(OrderProduct orderProduct){
        OrderProduct savedProduct = orderProductRepository.save(orderProduct);
        return savedProduct;
    }
}