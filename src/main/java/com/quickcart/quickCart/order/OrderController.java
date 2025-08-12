package com.quickcart.quickCart.order;

import com.quickcart.quickCart.order.dto.OrderAnswerDTO;
import com.quickcart.quickCart.order.dto.OrderDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name = "Order", description = "The Order API")
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/")
    public ResponseEntity<List<OrderDTO>> createOrder(@ModelAttribute @Valid OrderDTO orderDTO){
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(orderDTO));
    }
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderDTO>> getUserOrders(@PathVariable("userId") Long userId){
        return ResponseEntity.ok(orderService.getOrdersByUserId(userId));
    }
    @GetMapping("/store/{storeId}")
    public ResponseEntity<List<OrderDTO>> getStoreOrders(@PathVariable("storeId") Long storeId){
        return ResponseEntity.ok(orderService.getOrdersByStoreId(storeId));
    }
    @GetMapping("/{id}")
    public ResponseEntity<OrderAnswerDTO> getOrder(@PathVariable("id") Long id){
        OrderAnswerDTO order = orderService.getOrderById(id);
        if(order == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(order);
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<String> updateOrderStatus(@PathVariable("id") Long id,
                                                    @RequestParam Order.OrderStatus status){
        return ResponseEntity.ok(orderService.updateOrderStatus(id, status));
    }
}
