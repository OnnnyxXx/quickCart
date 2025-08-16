package com.quickcart.quickCart.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    // получаю продукты у ACTIVE магазинов
    @Query(value = "SELECT p FROM Product p " +
            "JOIN p.store s " +
            "WHERE s.status='ACTIVE'")
    List<Product> getAll();
}
