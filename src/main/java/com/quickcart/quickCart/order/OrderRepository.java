package com.quickcart.quickCart.order;
import com.quickcart.quickCart.order.dto.OrderDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface OrderRepository extends JpaRepository<Order, Long> {
//    @Query("SELECT new com.quickcart.quickCart.order.dto.OrderDTO(" +
//            "o.id, u.id, o.deliveryAddress, o.paymentMethod, o.orderDate, o.status)" +
//            "FROM Order o JOIN o.user u WHERE u.id = :userId")
    List<Order> findAllByUserId(Long userId);

    List<Order> findByStoreId(Long storeId);
}
