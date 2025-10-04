package com.quickcart.quickCart.moderation;

import com.quickcart.quickCart.store.Store;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import com.quickcart.quickCart.user.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class ModerationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // Пользователь, который подал заявку

    @NotBlank(message = "Store name is required")
    private String storeName;

    private String logoUrl;

    @NotBlank(message = "Location is required")
    private String location;

    @NotBlank(message = "Description is required")
    @Size(max = 255)
    private String description;

//    @NotBlank(message = "Phone is required")
//    @Size(max = 255)
    private String phone;

    @Column(updatable = false)
    private LocalDateTime requestDate = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private Store.StoreStatus status; // Статус заявки

    public ModerationRequest() {
    }

    public ModerationRequest(Long id, User user, String storeName, String logoUrl, String location, String description, String phone, LocalDateTime requestDate, Store.StoreStatus status) {
        this.id = id;
        this.user = user;
        this.storeName = storeName;
        this.logoUrl = logoUrl;
        this.location = location;
        this.description = description;
        this.phone = phone;
        this.requestDate = requestDate;
        this.status = status;
    }

}
