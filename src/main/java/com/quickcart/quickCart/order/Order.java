package com.quickcart.quickCart.order;

import jakarta.persistence.*;
import com.quickcart.quickCart.product.Product;
import com.quickcart.quickCart.store.Store;
import com.quickcart.quickCart.user.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;

    @OneToMany(mappedBy = "orderReference")
    private List<Product> products;

    private String status; // Статус заказа (например, "В обработке", "Доставлен")
    private LocalDateTime orderDate;
    private String deliveryAddress;
    private String paymentMethod; // Метод оплаты (например, "Кредитная карта", "Наличные")
}
