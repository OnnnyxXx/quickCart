package com.quickcart.quickCart.store.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.quickcart.quickCart.store.Store;
import com.quickcart.quickCart.user.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StoreDTO {

    private Long userId;
    private User user;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 35)
    private String name;

    @NotBlank(message = "Location is required")
    private String location;

    @NotBlank(message = "Description is required")
    @Size(min = 3, max = 255)
    private String description;

    private String workingHours;

    @Min(0)
    @Max(5)
    private int rating;

    private Store.StoreStatus status;

    private String logoUrl;

    private MultipartFile logo;

    public StoreDTO() {}

    public StoreDTO(Long userId, User user, String name,
                    String location, String description, String workingHours,
                    int rating, Store.StoreStatus status, String logoUrl, MultipartFile logo) {
        this.userId = userId;
        this.user = user;
        this.name = name;
        this.location = location;
        this.description = description;
        this.workingHours = workingHours;
        this.rating = rating;
        this.status = status;
        this.logoUrl = logoUrl;
        this.logo = logo;
    }

    public StoreDTO(Long userId, String name, String location, String description,
                    String workingHours, int rating, Store.StoreStatus status, String logoUrl) {
        this.userId = userId;
        this.name = name;
        this.location = location;
        this.description = description;
        this.workingHours = workingHours;
        this.rating = rating;
        this.status = status;
        this.logoUrl = logoUrl;
    }


}
