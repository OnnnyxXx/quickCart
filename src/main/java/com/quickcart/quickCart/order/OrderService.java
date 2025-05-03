package com.quickcart.quickCart.order;

import com.quickcart.quickCart.product.ProductService;
import com.quickcart.quickCart.store.StoreService;
import com.quickcart.quickCart.user.User;
import com.quickcart.quickCart.user.UserRepository;
import com.quickcart.quickCart.user.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    OrderRepository orderRepository;
    UserService userService;
    UserRepository userRepository;
    ProductService productService;
    StoreService storeService;

    public OrderService( OrderRepository orderRepository, UserService userService, UserRepository userRepository, ProductService productService, StoreService storeService){
        super();
        this.orderRepository = orderRepository;
        this.userService = userService;
        this.userRepository = userRepository;
        this.productService = productService;
        this.storeService = storeService;
    }


    public Order createOrder(@Valid Order order, Long userId) {
        order.setOrderDate(LocalDateTime.now());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь с id " + userId + " не найден."));
        order.setUser(user);
        Order savedOrder = orderRepository.save(order);
        return savedOrder;
    }

    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Заказ с " + id + " не найден."));
    }

    public List<Order> getOrdersByStoreId(Long storeId) {
        return orderRepository.findByStoreId(storeId);
    }
}
