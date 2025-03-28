package com.quickcart.quickCart.moderation.dto;

import com.quickcart.quickCart.store.Store;
import com.quickcart.quickCart.user.auth.dto.UserDtoInfo;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ModerationDTO {
    private Long storeId;
    private String storeName;
    private String logoUrl;
    private String storeLocation;
    private String storeDescription;
    private String storeWorkingHours;
    private int storeRating;

    @Enumerated(EnumType.STRING)
    private Store.StoreStatus storeStatus;

    private UserDtoInfo userDtoInfo;

    public ModerationDTO() {
    }

    public ModerationDTO(Long storeId, String storeName, String logoUrl, String storeLocation, String storeDescription, String storeWorkingHours, int storeRating, Store.StoreStatus storeStatus, UserDtoInfo userDtoInfo) {
        this.storeId = storeId;
        this.storeName = storeName;
        this.logoUrl = logoUrl;
        this.storeLocation = storeLocation;
        this.storeDescription = storeDescription;
        this.storeWorkingHours = storeWorkingHours;
        this.storeRating = storeRating;
        this.storeStatus = storeStatus;
        this.userDtoInfo = userDtoInfo;
    }
}
