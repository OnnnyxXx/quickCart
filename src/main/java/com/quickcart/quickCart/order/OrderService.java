package com.quickcart.quickCart.order;

import com.quickcart.quickCart.order.dto.OrderAnswerDTO;
import com.quickcart.quickCart.order.dto.OrderDTO;
import com.quickcart.quickCart.product.*;
import com.quickcart.quickCart.product.dto.ProductWithQuantityDTO;
import com.quickcart.quickCart.store.Store;
import com.quickcart.quickCart.store.StoreService;
import com.quickcart.quickCart.user.User;
import com.quickcart.quickCart.user.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;

@Service
@EnableCaching
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductService productService;
    private final StoreService storeService;

    public OrderService(
            OrderRepository orderRepository,
            UserRepository userRepository,
            ProductService productService,
            StoreService storeService) {
        super();
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productService = productService;
        this.storeService = storeService;
    }

    public List<ProductWithQuantityDTO> parseProducts(String productsString) {
        return new Gson().fromJson(productsString, new TypeToken<List<ProductWithQuantityDTO>>() {
        }.getType());

    }

    @Transactional
    public List<OrderDTO> createOrder(@Valid OrderDTO orderDTO) {
        long userId = orderDTO.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь с id " + userId + " не найден."));
        String productsString = orderDTO.getProducts();
        List<ProductWithQuantityDTO> productDTOList = parseProducts(productsString);

        HashMap<Long, List<ProductWithQuantityDTO>> mapProducts = new HashMap<>();
        for (ProductWithQuantityDTO product : productDTOList) {
            long storeId = product.getStoreId();
            List<ProductWithQuantityDTO> listProduct;
            if (mapProducts.containsKey(storeId)) {
                listProduct = mapProducts.get(storeId);
                listProduct.add(product);
            } else {
                listProduct = new ArrayList<>();
                listProduct.add(product);
                mapProducts.put(storeId, listProduct);
            }
        }
        List<OrderDTO> orderDTOList = new ArrayList<>();
        for (Long key : mapProducts.keySet()) {
            Order order = new Order();
            Store store = storeService.getStoreById(key);
            order.setStore(store);
            order.setOrderDate(LocalDateTime.now());
            order.setUser(user);
            List<OrderProduct> orderProducts = mapProducts.get(key).stream().map((item) -> {
                OrderProduct orderProduct = new OrderProduct();
                Product product = productService.getProduct(item.getId());
                orderProduct.setProduct(product);
                if (product.getStock() >= item.getQuantity()) {
                    product.setStock(product.getStock() - item.getQuantity());
                    orderProduct.setQuantity(item.getQuantity());
                } else {
                    orderProduct.setQuantity(product.getStock());
                    product.setStock(0);
                }
                productService.setProduct(product.getId(), product);

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
        return getOrdersDTO(orderList);
    }

    public List<OrderDTO> getOrdersDTO(List<Order> orderList) {
        List<OrderDTO> dtoList = new ArrayList<>();
        OrderDTO currentDTO;
        for (Order order : orderList) {
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

    @Cacheable(value = "order", key = "#id")
    public OrderAnswerDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Заказ с " + id + " не найден."));
        return new OrderAnswerDTO(order);
    }

    @Cacheable(value = "orderByStore", key = "#storeId")
    public List<OrderDTO> getOrdersByStoreId(Long storeId) {
        List<Order> orderList = orderRepository.findByStoreId(storeId);
        return getOrdersDTO(orderList);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "orderByStore", allEntries = true),
            @CacheEvict(value = "order", key = "#id")
    })
    public String updateOrderStatus(Long id, Order.OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Заказ с " + id + " не найден."));
        order.setStatus(status);
        orderRepository.save(order);
        logger.info("Updating order with id: {}", id);
        return status.toString();
    }

}
