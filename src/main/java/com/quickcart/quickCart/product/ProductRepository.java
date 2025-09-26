package com.quickcart.quickCart.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    // получаю продукты у ACTIVE магазинов
    @Query(value = "SELECT p FROM Product p " +
            "JOIN p.store s " +
            "WHERE s.status='ACTIVE' AND p.stock > 0")
    List<Product> getAll();
    @Modifying
    @Query(value = "DELETE FROM Product WHERE id = ?1", nativeQuery = true)
    void deleteById(long id);
}
