package com.quickcart.quickCart.order.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.quickcart.quickCart.order.Order;
import com.quickcart.quickCart.product.Product;
import com.quickcart.quickCart.product.ProductService;
import com.quickcart.quickCart.product.dto.ProductWithQuantityDTO;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import com.quickcart.quickCart.product.dto.ProductDTO;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderAnswerDTO {
    private Long id;

    //@NotBlank(message = "Покупка анонимными пользователями временно невозможна")
    private Long userId;
    private Long storeId;

    @NotNull(message = "Список продуктов не может быть пуст")
    private List<ProductWithQuantityDTO> products;

    @NotBlank(message = "необходимо указать адрес доставки")
    @Size(min = 3, max = 255)
    private String deliveryAddress;

    @NotBlank(message = "необходимо указать метод оплаты")
    private String paymentMethod;

    private String orderDate;

    private String status;

    public OrderAnswerDTO(){}

    public OrderAnswerDTO( Order order){
        this.id = order.getId();
        this.userId = order.getUser().getId();
        this.storeId = order.getStore().getId();
        this.orderDate = order.getOrderDate().toString();
        this.deliveryAddress = order.getDeliveryAddress();
        this.paymentMethod = order.getPaymentMethod();
        this.products = new ArrayList<>();
        order.getProducts().forEach((item) -> this.products.add(ProductService.getProductWithQuantityDTO(item)));
        this.status = order.getStatus().toString();
    }

}
