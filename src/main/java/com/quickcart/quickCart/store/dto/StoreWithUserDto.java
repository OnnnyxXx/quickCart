package com.quickcart.quickCart.store.dto;

import com.quickcart.quickCart.user.auth.dto.UserDtoInfo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
    private UserDtoInfo userDtoInfo;

    public StoreWithUserDto(Long storeId, String storeName, String storeLocation, String storeDescription, String storeWorkingHours, int storeRating, UserDtoInfo userDtoInfo) {
        this.storeId = storeId;
        this.storeName = storeName;
        this.storeLocation = storeLocation;
        this.storeDescription = storeDescription;
        this.storeWorkingHours = storeWorkingHours;
        this.storeRating = storeRating;
        this.userDtoInfo = userDtoInfo;
    }

    public StoreWithUserDto() {
    }
}