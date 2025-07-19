package com.quickcart.quickCart.product.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductWithQuantityDTO {

    private Long storeId;
    private Long id;

    @NotBlank(message = "Необходимо указать имя")
    @Size(min = 2, max = 45)
    private String name;

    @NotBlank(message = "Необходимо добавить описание")
    @Size(min = 3, max = 255)
    private String description;

    @NotBlank
    private String price;

    @NotBlank(message = "Необходимо указать категорию")
    @Size(min = 3, max = 45)
    private String category;

    private int stock;

    @Min(1)
    private int quantity;

    private String imageUrl;

    private MultipartFile image;

    public ProductWithQuantityDTO() {}

    public ProductWithQuantityDTO(Long storeId, String name, String description, String category,
                      int stock, String price, String imageUrl, MultipartFile image) {
        this.storeId = storeId;
        this.name = name;
        this.description = description;
        this.stock = stock;
        this.category = category;
        this.price = price;
        this.imageUrl = imageUrl;
        this.image = image;
    }

    public ProductWithQuantityDTO(Long id, Long storeId, String name, String description, String category,
                      int stock, String price, String imageUrl, int quantity, MultipartFile image) {
        this.id = id;
        this.storeId = storeId;
        this.name = name;
        this.description = description;
        this.stock = stock;
        this.category = category;
        this.price = price;
        this.imageUrl = imageUrl;
        this.quantity = quantity;
        this.image = image;
    }
}