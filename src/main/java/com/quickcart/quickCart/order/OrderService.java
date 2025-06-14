package com.quickcart.quickCart.order;

import com.quickcart.quickCart.order.dto.OrderAnswerDTO;
import com.quickcart.quickCart.order.dto.OrderDTO;
import com.quickcart.quickCart.product.*;
import com.quickcart.quickCart.product.dto.ProductDTO;
import com.quickcart.quickCart.product.dto.ProductWithQuantityDTO;
import com.quickcart.quickCart.store.Store;
import com.quickcart.quickCart.store.StoreService;
import com.quickcart.quickCart.user.User;
import com.quickcart.quickCart.user.UserRepository;
import com.quickcart.quickCart.user.UserService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class OrderService {

    OrderRepository orderRepository;
    UserService userService;
    UserRepository userRepository;
    ProductService productService;

    OrderProductRepository orderProductRepository;
    StoreService storeService;

    public OrderService(OrderProductRepository productRepository, OrderRepository orderRepository, UserService userService, UserRepository userRepository, ProductService productService, StoreService storeService){
        super();
        this.orderRepository = orderRepository;
        this.userService = userService;
        this.userRepository = userRepository;
        this.productService = productService;
        this.orderProductRepository = productRepository;
        this.storeService = storeService;
    }

    public List<ProductWithQuantityDTO> parseProducts(String productsString){
        return new Gson().fromJson(productsString, new TypeToken<List<ProductWithQuantityDTO>>() {}.getType());

    }

    @Transactional
    public List<OrderDTO> createOrder(@Valid OrderDTO orderDTO) {
        long userId = orderDTO.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь с id " + userId + " не найден."));
        String productsString = orderDTO.getProducts();
        List<ProductWithQuantityDTO> productDTOList = parseProducts(productsString);

        HashMap<Long, List<ProductWithQuantityDTO>> mapProducts = new HashMap<>();
        for(ProductWithQuantityDTO product: productDTOList){
            long storeId = product.getStoreId();
            List<ProductWithQuantityDTO> listProduct;
            if(mapProducts.containsKey(storeId)){
                listProduct = mapProducts.get(storeId);
                listProduct.add(product);
            }
            else {
                listProduct = new ArrayList<>();
                listProduct.add(product);
                mapProducts.put(storeId, listProduct);
            }
        }
        List<OrderDTO> orderDTOList = new ArrayList<>();
        for(Long key: mapProducts.keySet()){
            Order order = new Order();
            Store store = storeService.getStoreById(key);
            order.setStore(store);
            order.setOrderDate(LocalDateTime.now());
            order.setUser(user);
            List<OrderProduct> orderProducts = mapProducts.get(key).stream().map((item) -> {
                OrderProduct orderProduct = new OrderProduct();
                Product product = productService.getProduct(item.getId());
                orderProduct.setProduct(product);
                orderProduct.setQuantity(item.getQuantity());
                orderProduct = productService.createOrderProduct(orderProduct);
                return orderProduct;
            }).toList();
            order.setProducts(orderProducts);
            order.setDeliveryAddress(orderDTO.getDeliveryAddress());
            order.setPaymentMethod(orderDTO.getPaymentMethod());
            order.setStatus(Order.OrderStatus.PENDING);
            Order savedOrder = orderRepository.save(order);
            orderDTOList.add(new OrderDTO(savedOrder));
        }
        return orderDTOList;
    }

    public List<OrderDTO> getOrdersByUserId(Long userId) {
        List<Order> orderList = orderRepository.findAllByUserId(userId);
        List<OrderDTO> dtoList = new ArrayList<>();
        OrderDTO currentDTO;
        for(Order order: orderList){
            currentDTO = new OrderDTO(
                    order.getId(),
                    order.getUser().getId(),
                    order.getStore().getId(),
                    order.getDeliveryAddress(),
                    order.getPaymentMethod(),
                    order.getOrderDate().toString(),
                    order.getStatus().toString()
            );
            dtoList.add(currentDTO);
        }
        return dtoList;
    }

    public OrderAnswerDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Заказ с " + id + " не найден."));
        return new OrderAnswerDTO(order);
    }

    public List<Order> getOrdersByStoreId(Long storeId) {
        return orderRepository.findByStoreId(storeId);
    }
}
