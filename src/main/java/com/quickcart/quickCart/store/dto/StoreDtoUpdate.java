package com.quickcart.quickCart.store.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class StoreDtoUpdate {
    private Long userId;
    private String storeName;
    private String storeLocation;
    private String storeDescription;
    private String storeWorkingHours;
    private MultipartFile logo;
    private String storeUrlLogo;

    public StoreDtoUpdate() {
    }

    public StoreDtoUpdate(Long userId, String storeName, String storeLocation, String storeDescription, String storeWorkingHours, MultipartFile logo, String storeUrlLogo) {
        this.userId = userId;
        this.storeName = storeName;
        this.storeLocation = storeLocation;
        this.storeDescription = storeDescription;
        this.storeWorkingHours = storeWorkingHours;
        this.logo = logo;
        this.storeUrlLogo = storeUrlLogo;
    }
}
