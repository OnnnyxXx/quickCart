package com.quickcart.quickCart.order;

import com.quickcart.quickCart.product.OrderProduct;
import com.quickcart.quickCart.product.dto.ProductWithQuantityDTO;
import jakarta.persistence.*;
import com.quickcart.quickCart.product.Product;
import com.quickcart.quickCart.store.Store;
import com.quickcart.quickCart.user.User;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Setter
@Getter
@NoArgsConstructor
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

    @OneToMany
    private List<OrderProduct> products;

    @Enumerated(EnumType.STRING)
    private OrderStatus status; // Статус заказа (например, "В обработке", "Доставлен")
    private LocalDateTime orderDate;
    private String deliveryAddress;
    private String paymentMethod; // Метод оплаты (например, "Кредитная карта", "Наличные")

    public enum OrderStatus {
        PENDING,
        ASSEMBLED,
        PAID,
        DELIVERED,
        COMPLETED
    }
}
