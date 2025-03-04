package com.quickcart.quickCart.order;

import com.quickcart.quickCart.user.User;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    @GetMapping("/{userId}")
    public List<Order> getListOrders(@PathVariable("userId") Long userId){
        List<Order> listOrders = orderService.getOrderByUserId(userId);
        return listOrders;
    }
}
