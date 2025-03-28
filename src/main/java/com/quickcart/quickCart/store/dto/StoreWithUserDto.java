package com.quickcart.quickCart.store.dto;

import com.quickcart.quickCart.user.auth.dto.UserDtoInfo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoreWithUserDto {
    private Long storeId;
    private String storeName;
    private String storeLocation;
    private String storeDescription;
    private String storeWorkingHours;
    private int storeRating;
    private String logoUrl;
    private UserDtoInfo userDtoInfo;

    public StoreWithUserDto(Long storeId, String storeName, String storeLocation, String storeDescription, String storeWorkingHours, int storeRating, String logoUrl, UserDtoInfo userDtoInfo) {
        this.storeId = storeId;
        this.storeName = storeName;
        this.storeLocation = storeLocation;
        this.storeDescription = storeDescription;
        this.storeWorkingHours = storeWorkingHours;
        this.storeRating = storeRating;
        this.logoUrl = logoUrl;
        this.userDtoInfo = userDtoInfo;
    }

    public StoreWithUserDto() {
    }
}