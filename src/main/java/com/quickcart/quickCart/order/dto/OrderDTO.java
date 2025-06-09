package com.quickcart.quickCart.order.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.quickcart.quickCart.order.Order;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import com.quickcart.quickCart.product.dto.ProductDTO;

import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderDTO {
    private Long id;

    //@NotBlank(message = "Покупка анонимными пользователями временно невозможна")
    private Long userId;
    private Long storeId;

    @NotEmpty(message = "Список продуктов не может быть пуст")
    private String products;

    @NotBlank(message = "необходимо указать адрес доставки")
    @Size(min = 3, max = 255)
    private String deliveryAddress;

    @NotBlank(message = "необходимо указать метод оплаты")
    private String paymentMethod;

    private String orderDate;

    private String status;

    public OrderDTO(){}

    public OrderDTO(Long userId, String deliveryAddress, String paymentMethod, String products){
        this.userId = userId;
        this.deliveryAddress = deliveryAddress;
        this.paymentMethod = paymentMethod;
        this.products = products;

    }

    public OrderDTO(Long id, Long userId, String deliveryAddress, String paymentMethod, String orderDate, String status){
        this.id = id;
        this.userId = userId;
        this.deliveryAddress = deliveryAddress;
        this.paymentMethod = paymentMethod;
        this.orderDate = orderDate;
        this.status = status;
    }

    public OrderDTO( Order order){
        this.id = order.getId();
        this.userId = order.getUser().getId();
        this.storeId = order.getStore().getId();
        this.orderDate = order.getOrderDate().toString();
        this.deliveryAddress = order.getDeliveryAddress();
        this.paymentMethod = order.getPaymentMethod();
    }

}
