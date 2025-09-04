package com.WMS_spiders_wholesale_system.service;

import com.WMS_spiders_wholesale_system.entity.Order;
import com.WMS_spiders_wholesale_system.entity.OrderStatus;
import com.WMS_spiders_wholesale_system.entity.Spider;
import com.WMS_spiders_wholesale_system.exception.OrderNotFoundException;
import com.WMS_spiders_wholesale_system.repository.CustomerRepository;
import com.WMS_spiders_wholesale_system.repository.OrderRepository;
import com.WMS_spiders_wholesale_system.repository.SpiderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    public Order createOrder(Order order) {
        Order newOrder = new Order();
        newOrder.setCustomer(order.getCustomer());
        newOrder.setStatus(order.getStatus());
        List<UUID> spiderIds = order.getOrderedSpiders().stream()
                .map(Spider::getId)
                .collect(Collectors.toList());
        List<Spider> orderedSpiders = spiderRepository.findAllById(spiderIds);
        newOrder.setOrderedSpiders(orderedSpiders);
        logger.info("Created new order: " + order.getOrderId());
        return orderRepository.save(newOrder);
    }

    public Order updateStatus(Order order, OrderStatus orderStatus) {
        if (!orderRepository.existsById(order.getOrderId())) {
            throw new OrderNotFoundException("Order not found" + order.getOrderId());
        }
        order.setStatus(orderStatus);
        logger.info("Updated order: " + order.getOrderId());
        return orderRepository.save(order);
    }


}
