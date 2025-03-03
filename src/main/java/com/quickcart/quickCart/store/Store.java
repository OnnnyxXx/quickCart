package com.quickcart.quickCart.store;

import jakarta.persistence.*;
import com.quickcart.quickCart.product.Product;

import java.util.List;

@Entity
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String location;
    private String description;
    private String workingHours;
    private int rating;
    private String status; // Статус магазина (например, "активен", "на модерации", "заблокирован")
    private String logoUrl;

    @OneToMany(mappedBy = "store")
    private List<Product> products;

}
