package com.quickcart.quickCart.order;

import com.quickcart.quickCart.user.User;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    @Autowired
    OrderService orderService;

    @PostMapping("/")
    public ResponseEntity<Order> createOrder(@Valid @RequestBody Order order,/* @CurrentUser*/ User user){
        return orderService.createOrder(order, user);
    }
    @GetMapping("/user/{userId}")
    public List<Order> getListOrders(@PathVariable("userId") Long userId){
        return orderService.getOrdersByUserId(userId);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable("id") Long id){
        return orderService.getOrderById(id);
    }
}
