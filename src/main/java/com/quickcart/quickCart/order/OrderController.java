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
    public ResponseEntity<Order> createOrder(@Valid @RequestBody Order order,/* @CurrentUser*/ Long userId){
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(order, userId));
    }
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getUserOrders(@PathVariable("userId") Long userId){
        return ResponseEntity.ok(orderService.getOrdersByUserId(userId));
    }
    @GetMapping("/store/{storeId}")
    public ResponseEntity<List<Order>> getStoreOrders(@PathVariable("storeId") Long storeId){
        return ResponseEntity.ok(orderService.getOrdersByStoreId(storeId));
    }
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable("id") Long id){
        Order order = orderService.getOrderById(id);
        if(order == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(order);
    }
}
