package com.quickcart.quickCart.moderation.dto;

import com.quickcart.quickCart.store.Store;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModerationRequestDTO {

    @Enumerated(EnumType.STRING)
    private Store.StoreStatus status;

    public ModerationRequestDTO() {}

    public ModerationRequestDTO(Store.StoreStatus status) {
        this.status = status;
    }
}
