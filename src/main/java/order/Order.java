package order;

import jakarta.persistence.*;
import product.Product;
import store.Store;
import user.User;

import java.time.LocalDateTime;
import java.util.List;

@Entity
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

    @OneToMany(mappedBy = "order")
    private List<Product> products;
    private String status; // Статус заказа (например, "В обработке", "Доставлен")
    private LocalDateTime orderDate;
    private String deliveryAddress;
    private String paymentMethod; // Метод оплаты (например, "Кредитная карта", "Наличные")
}
