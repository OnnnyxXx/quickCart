package com.quickcart.quickCart.product;

import com.quickcart.quickCart.order.Order;
import com.quickcart.quickCart.store.Store;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;

@Setter
@Getter
@Entity
@Table(name = "cart_products")
public class OrderProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    @Min(value = 1)
    @Column(columnDefinition = "int default 1")
    private int quantity;
//    @ManyToOne
//    @JoinColumn(name = "order_id")
//    private Order order;

    public OrderProduct(){

    }

    public OrderProduct(Product product, int qty){
        super();
        this.product = product;
        this.quantity = qty;
    }
}
