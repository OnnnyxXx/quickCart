package com.quickcart.quickCart.product.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;


@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductDTO {

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

    private String imageUrl;

    private MultipartFile image;

    public ProductDTO() {}

    public ProductDTO(Long storeId, String name, String description, String category,
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

    public ProductDTO(Long id, Long storeId, String name, String description, String category,
                      int stock, String price, String imageUrl, MultipartFile image) {
        this.id = id;
        this.storeId = storeId;
        this.name = name;
        this.description = description;
        this.stock = stock;
        this.category = category;
        this.price = price;
        this.imageUrl = imageUrl;
        this.image = image;
    }
}
