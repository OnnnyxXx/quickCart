package com.quickcart.quickCart.store.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class StoreDtoUpdate {
    private Long userId;

    @Size(min = 2, max = 35, message = "Размер от 2 до 35")
    private String storeName;

    private String storeLocation;

    @Size(min = 3, max = 255)
    private String storeDescription;

    private String storePhone;

    private String storeWorkingHours;
    private MultipartFile logo;
    private String storeUrlLogo;

    public StoreDtoUpdate() {
    }

    public StoreDtoUpdate(Long userId, String storeName, String storeLocation, String storeDescription, String storePhone, String storeWorkingHours, MultipartFile logo, String storeUrlLogo) {
        this.userId = userId;
        this.storeName = storeName;
        this.storeLocation = storeLocation;
        this.storeDescription = storeDescription;
        this.storePhone = storePhone;
        this.storeWorkingHours = storeWorkingHours;
        this.logo = logo;
        this.storeUrlLogo = storeUrlLogo;
    }
}
