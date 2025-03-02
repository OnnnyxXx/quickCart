package product;

import jakarta.persistence.*;
import store.Store;

import java.math.BigDecimal;

@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private int stock; // Количество на складе
    private String imageUrl;
    private String category;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;
}
