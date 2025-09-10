package com.WMS_spiders_wholesale_system.service;

import com.WMS_spiders_wholesale_system.entity.Order;
import com.WMS_spiders_wholesale_system.entity.OrderStatus;
import com.WMS_spiders_wholesale_system.entity.Spider;
import com.WMS_spiders_wholesale_system.exception.InvalidOrderDataException;
import com.WMS_spiders_wholesale_system.exception.OrderNotFoundException;
import com.WMS_spiders_wholesale_system.repository.CustomerRepository;
import com.WMS_spiders_wholesale_system.repository.OrderRepository;
import com.WMS_spiders_wholesale_system.repository.SpiderRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final SpiderRepository spiderRepository;
    private final CustomerRepository customerRepository;
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    public OrderService(OrderRepository orderRepository, SpiderRepository spiderRepository, CustomerRepository customerRepository) {
        this.orderRepository = orderRepository;
        this.spiderRepository = spiderRepository;
        this.customerRepository = customerRepository;
    }

    @Transactional
    public Order createOrder(Order order) {
        if (order.getOrderedSpiders() != null) {
            for (Spider spider : order.getOrderedSpiders()) {
                spider.setOrder(order);
            }
            double totalPrice = order.getOrderedSpiders().stream()
                    .mapToDouble(spider -> spider.getPrice() * spider.getQuantity())
                    .sum();
            order.setPrice(totalPrice);
        }
        validateOrder(order);
        logger.info("Created new order: {}", order.getOrderId());
        return orderRepository.save(order);
    }

    @Transactional
    public Order updateStatus(Order order, OrderStatus orderStatus) {
        if (!orderRepository.existsById(order.getOrderId())) {
            throw new OrderNotFoundException("Order not found: " + order.getOrderId());
        }
        order.setStatus(orderStatus);
        logger.info("Updated order: " + order.getOrderId());
        return orderRepository.save(order);
    }

    @Transactional
    public void deleteOrder(UUID orderId) {
        if (!orderRepository.existsById(orderId)) {
            throw new OrderNotFoundException("Order not found: " + orderId);
        }
        logger.info("Deleted order: " + orderId);
        orderRepository.deleteById(orderId);
    }

    public Order getOrderById(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderId));
    }

    public Page<Order> getAllOrders(int size, int page, Sort sort) {
        Pageable pageable = PaginationHelper.createPageable(size, page, sort, "status");
        return orderRepository.findAll(pageable);
    }

    public void validateOrder(Order order) {
        if (order == null) {
            throw new InvalidOrderDataException("Order cannot be null");
        }
        if (order.getDate() == null) {
            throw new InvalidOrderDataException("Order date cannot be null");
        }
        if (order.getCustomer() == null || order.getCustomer().getId() == null) {
            throw new InvalidOrderDataException("orderedSpiders cannot be null");
        }
        logger.info("Validating order: " + order.getOrderId());
    }
}
