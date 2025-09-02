package com.coolspiders.API.service;

import com.coolspiders.API.entity.Order;
import com.coolspiders.API.entity.Spider;
import com.coolspiders.API.repository.CustomerRepository;
import com.coolspiders.API.repository.OrderRepository;
import com.coolspiders.API.repository.SpiderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final SpiderRepository spiderRepository;
    private final CustomerRepository customerRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository, SpiderRepository spiderRepository, CustomerRepository customerRepository) {
        this.orderRepository = orderRepository;
        this.spiderRepository = spiderRepository;
        this.customerRepository = customerRepository;
    }

    public Order createOrder(Order order) {
        Order newOrder = new Order();
        newOrder.setOrderId(order.getOrderId());
        newOrder.setCustomer(order.getCustomer());
        List<UUID> spiderIds = order.getOrderedSpiders().stream()
                .map(Spider::getId)
                .collect(Collectors.toList());
        List<Spider> orderedSpiders = spiderRepository.findAllById(spiderIds);
        newOrder.setOrderedSpiders(order.getOrderedSpiders());
        newOrder.setStatus(order.getStatus());

        return orderRepository.save(newOrder);
    }


}
