package com.quickcart.quickCart.moderation;

import jakarta.persistence.*;
import com.quickcart.quickCart.user.User;

import java.time.LocalDateTime;

@Entity
public class ModerationRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user; // Пользователь, который подал заявку
    private String storeName; // Название магазина
    private String location; // Локация магазина
    private String description; // Описание магазина
    private LocalDateTime requestDate; // Дата подачи заявки
    private String status; // Статус заявки (например, "На рассмотрении", "Одобрена", "Отклонена")
}
