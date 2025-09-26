package com.quickcart.quickCart.product;

import com.quickcart.quickCart.order.Order;
import com.quickcart.quickCart.store.Store;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;

    @NotNull
    @DecimalMin(value = "0.0")
    private BigDecimal price;

    @Min(-1)
    private int stock; // Количество на складе

    private String imageUrl;
    private String category;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order orderReference;
}
