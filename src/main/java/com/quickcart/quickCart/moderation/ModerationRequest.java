package com.quickcart.quickCart.moderation;

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

    @NotBlank(message = "Location is required")
    private String location;

    @NotBlank(message = "Description is required")
    @Size(max = 255)
    private String description;

    @Column(updatable = false)
    private LocalDateTime requestDate = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private ModerationRequestStatus status; // Статус заявки

    public ModerationRequest(User user, String storeName, String location, String description) {
        this.user = user;
        this.storeName = storeName;
        this.location = location;
        this.description = description;
        this.requestDate = LocalDateTime.now();
        this.status = ModerationRequestStatus.PENDING;
    }

    public ModerationRequest() {}
}
