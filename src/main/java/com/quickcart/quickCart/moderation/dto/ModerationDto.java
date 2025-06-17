package com.quickcart.quickCart.moderation.dto;

import com.quickcart.quickCart.store.Store;
import com.quickcart.quickCart.auth.dto.UserDtoInfo;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ModerationDto {
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
}
