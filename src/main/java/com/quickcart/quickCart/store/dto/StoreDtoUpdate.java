package com.quickcart.quickCart.store.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoreDtoUpdate {
    private Long userId;
    private String storeName;
    private String storeLocation;
    private String storeDescription;
    private String storeUrlLogo;

    public StoreDtoUpdate() {
    }

    public StoreDtoUpdate(Long userId, String storeName, String storeLocation, String storeDescription, String storeUrlLogo) {
        this.userId = userId;
        this.storeName = storeName;
        this.storeLocation = storeLocation;
        this.storeDescription = storeDescription;
        this.storeUrlLogo = storeUrlLogo;
    }
}
