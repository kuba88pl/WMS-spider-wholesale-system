package com.WMS_spiders_wholesale_system.service;

import com.WMS_spiders_wholesale_system.dto.OrderDTO;
import com.WMS_spiders_wholesale_system.dto.OrderedSpiderDTO;
import com.WMS_spiders_wholesale_system.entity.*;
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
    public Order createOrder(OrderDTO orderDTO) {
        if (orderDTO.getCustomerId() == null) {
            throw new InvalidOrderDataException("Customer ID cannot be null");
        }
        Customer customer = customerRepository.findById(orderDTO.getCustomerId())
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found!"));

        List<OrderedSpiderDTO> orderedSpidersDTO = orderDTO.getOrderedSpiders();
        if (orderedSpidersDTO == null || orderedSpidersDTO.isEmpty()) {
            throw new InvalidOrderDataException("Order must contain at least one spider");
        }

        List<OrderedSpider> orderedSpidersList = new ArrayList<>();
        double totalPrice = 0.0;

        for (OrderedSpiderDTO spiderDTO : orderedSpidersDTO) {
            Spider spider = spiderRepository.findById(spiderDTO.getSpiderId())
                    .orElseThrow(() -> new SpiderNotFoundException("Spider with ID: " + spiderDTO.getSpiderId() + " not found!"));

            if (spiderDTO.getQuantity() > spider.getQuantity()) {
                throw new InvalidOrderDataException("Not enough spiders in stock for species: " + spider.getSpeciesName());
            }

            int newQuantity = spider.getQuantity() - spiderDTO.getQuantity();
            spider.setQuantity(newQuantity);

            OrderedSpider orderedSpider = new OrderedSpider();
            orderedSpider.setSpider(spider);
            orderedSpider.setQuantity(spiderDTO.getQuantity());

            orderedSpidersList.add(orderedSpider);
            totalPrice += spider.getPrice() * spiderDTO.getQuantity();
        }

        Order newOrder = new Order();
        newOrder.setCustomer(customer);
        newOrder.setDate(LocalDate.now());
        newOrder.setStatus(OrderStatus.NEW);
        newOrder.setPrice(totalPrice);
        newOrder.setOrderedSpiders(orderedSpidersList);

        orderedSpidersList.forEach(os -> os.setOrder(newOrder));

        Order finalOrder = orderRepository.save(newOrder);

        spiderRepository.saveAll(orderedSpidersList.stream()
                .map(OrderedSpider::getSpider)
                .collect(Collectors.toList()));

        logger.info("Created new order: {}", finalOrder.getOrderId());
        return finalOrder;
    }



    @Transactional
    public Order updateStatus(UUID orderId, OrderStatus orderStatus) {
        Order existingOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderId));
        existingOrder.setStatus(orderStatus);
        logger.info("Updated order: " + existingOrder.getOrderId());
        return orderRepository.save(existingOrder);
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
}