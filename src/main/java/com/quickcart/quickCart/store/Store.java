package com.quickcart.quickCart.store;

import com.quickcart.quickCart.user.User;
import jakarta.persistence.*;
import com.quickcart.quickCart.product.Product;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@Entity
public class Store {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 35)
    private String name;

    @NotBlank(message = "Location is required")
    private String location;

    @NotBlank(message = "Description is required")
    @Size(min = 3, max = 255)
    private String description;

    private String workingHours;

    private int rating;

    private boolean deleted = false;

    @Enumerated(EnumType.STRING)
    private StoreStatus status = StoreStatus.PENDING;// Статус магазина (например, "активен", "на модерации", "заблокирован")

    private String logoUrl;

    @OneToMany(mappedBy = "store")
    private List<Product> products;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id")
    private User user;

    public enum StoreStatus {
        ACTIVE,
        PENDING,
        BLOCKED
    }



}
