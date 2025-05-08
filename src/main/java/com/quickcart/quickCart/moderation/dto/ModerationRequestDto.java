package com.quickcart.quickCart.moderation.dto;

import com.quickcart.quickCart.store.Store;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModerationRequestDto {

    @Enumerated(EnumType.STRING)
    private Store.StoreStatus status;

    public ModerationRequestDto() {}

    public ModerationRequestDto(Store.StoreStatus status) {
        this.status = status;
    }
}
