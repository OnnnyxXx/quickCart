package com.quickcart.quickCart.order;

import com.quickcart.quickCart.product.ProductService;
import com.quickcart.quickCart.store.StoreService;
import com.quickcart.quickCart.user.User;
import com.quickcart.quickCart.user.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    OrderRepository orderRepository;
    UserService userService;
    ProductService productService;
    StoreService storeService;

    public OrderService( OrderRepository orderRepository, UserService userService, ProductService productService, StoreService storeService){
        super();
        this.orderRepository = orderRepository;
        this.userService = userService;
        this.productService = productService;
        this.storeService = storeService;
    }


    public ResponseEntity<Order> createOrder(@Valid Order order, User user) {
        order.setOrderDate(LocalDateTime.now());
        order.setUser(user);
        Order savedOrder = orderRepository.save(order);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedOrder);
    }

    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    public ResponseEntity<Order> getOrderById(Long id) {
        return orderRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build()
                );
    }
}
