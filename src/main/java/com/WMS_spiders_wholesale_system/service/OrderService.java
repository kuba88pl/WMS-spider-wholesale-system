package com.WMS_spiders_wholesale_system.service;

import com.WMS_spiders_wholesale_system.entity.Customer;
import com.WMS_spiders_wholesale_system.entity.Order;
import com.WMS_spiders_wholesale_system.entity.OrderStatus;
import com.WMS_spiders_wholesale_system.entity.Spider;
import com.WMS_spiders_wholesale_system.exception.CustomerNotFoundException;
import com.WMS_spiders_wholesale_system.exception.InvalidOrderDataException;
import com.WMS_spiders_wholesale_system.exception.OrderNotFoundException;
import com.WMS_spiders_wholesale_system.exception.SpiderNotFoundException;
import com.WMS_spiders_wholesale_system.repository.CustomerRepository;
import com.WMS_spiders_wholesale_system.repository.OrderRepository;
import com.WMS_spiders_wholesale_system.repository.SpiderRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
        if (order.getCustomer() == null || order.getCustomer().getId() == null) {
            throw new InvalidOrderDataException("Customer or customer ID cannot be null");
        }
        Customer customer = customerRepository.findById(order.getCustomer().getId()).orElseThrow(() -> new CustomerNotFoundException("Customer not found!"));
        order.setCustomer(customer);

        if (order.getOrderedSpiders() != null && !order.getOrderedSpiders().isEmpty()) {
            List<Spider> orderedSpidersWithQuantity = new ArrayList<>();
            double totalPrice = 0.0;
            for (Spider spiderFromRequest : order.getOrderedSpiders()) {
                Spider fullSpider = spiderRepository.findById(spiderFromRequest.getId())
                        .orElseThrow(() -> new SpiderNotFoundException("Spider with ID: " + spiderFromRequest.getId() + " not found!"));
                fullSpider.setQuantity(spiderFromRequest.getQuantity());
                fullSpider.setOrder(order);
                orderedSpidersWithQuantity.add(fullSpider);
                totalPrice += fullSpider.getPrice() * fullSpider.getQuantity();
            }

            order.setOrderedSpiders(orderedSpidersWithQuantity);
            order.setPrice(totalPrice);
        } else {
            order.setPrice(0.0);
        }
        validateOrder(order);
        Order savedOrder = orderRepository.save(order);
        logger.info("Created new order: {}", order.getOrderId());
        return orderRepository.findById(savedOrder.getOrderId()).orElseThrow(() -> new OrderNotFoundException("Order with ID: " + savedOrder.getOrderId() + " not found!"));
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

    public Page<Order> getAllOrders(int page, int size, Sort sort) {
        return orderRepository.findAll(PageRequest.of(page, size, sort));
    }

    public List<Map<String, Object>> getMonthlyOrderStatistics() {
        List<Order> allOrders = orderRepository.findAll();

        Map<String, List<Order>> groupedByMonth = allOrders.stream()
                .collect(Collectors.groupingBy(order -> {
                    LocalDate date = order.getDate();
                    return date.getYear() + "-" + String.format("%02d", date.getMonthValue());
                }));

        List<Map<String, Object>> statsList = groupedByMonth.entrySet().stream()
                .map(entry -> {
                    String month = entry.getKey();
                    List<Order> ordersInMonth = entry.getValue();

                    int totalOrders = ordersInMonth.size();
                    double totalAmount = ordersInMonth.stream()
                            .mapToDouble(Order::getPrice)
                            .sum();

                    Map<String, Object> map = new java.util.HashMap<>();
                    map.put("month", month);
                    map.put("totalOrders", totalOrders);
                    map.put("totalAmount", totalAmount);
                    return map;
                })
                .collect(Collectors.toList());

        return statsList;
    }

    public void validateOrder(Order order) {
        if (order == null) {
            throw new InvalidOrderDataException("Order cannot be null");
        }
        if (order.getDate() == null) {
            throw new InvalidOrderDataException("Order date cannot be null");
        }
        if (order.getCustomer() == null || order.getCustomer().getId() == null) {
            throw new InvalidOrderDataException("Customer or customer ID cannot be null");
        }
        if (order.getOrderedSpiders() == null || order.getOrderedSpiders().isEmpty()) {
            throw new InvalidOrderDataException("Order must contain at least one spider");
        }
        logger.info("Validating order: " + order.getOrderId());
    }
}
