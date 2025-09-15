package com.WMS_spiders_wholesale_system.service;

import com.WMS_spiders_wholesale_system.dto.OrderDTO;
import com.WMS_spiders_wholesale_system.entity.*;
import com.WMS_spiders_wholesale_system.exception.CustomerNotFoundException;
import com.WMS_spiders_wholesale_system.exception.OrderNotFoundException;
import com.WMS_spiders_wholesale_system.exception.SpiderNotFoundException;
import com.WMS_spiders_wholesale_system.repository.OrderRepository;
import com.WMS_spiders_wholesale_system.repository.SpiderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final SpiderRepository spiderRepository;
    private final CustomerService customerService;

    public OrderService(OrderRepository orderRepository, SpiderRepository spiderRepository, CustomerService customerService) {
        this.orderRepository = orderRepository;
        this.spiderRepository = spiderRepository;
        this.customerService = customerService;
    }

    @Transactional
    public Order createOrder(OrderDTO orderDTO) {
        Customer customer = customerService.getCustomerById(orderDTO.getCustomerId());
        if (customer == null) {
            throw new CustomerNotFoundException("Customer with id " + orderDTO.getCustomerId() + " not found.");
        }

        Order newOrder = new Order();
        newOrder.setCustomer(customer);
        newOrder.setDate(LocalDate.now());
        newOrder.setStatus(OrderStatus.NEW);
        newOrder.setShipmentNumber(orderDTO.getShipmentNumber());
        if (orderDTO.getSelfCollection() != null && orderDTO.getSelfCollection()) {
            newOrder.setSelfCollection(true);
            newOrder.setCourierCompany(CourierCompany.valueOf(orderDTO.getCourierCompany().toUpperCase()));
            newOrder.setShipmentNumber("Brak danych");
        } else {
//        if (orderDTO.getCourierCompany() != null && !orderDTO.getCourierCompany().isBlank()) {
//            try {
//                newOrder.setCourierCompany(CourierCompany.valueOf(orderDTO.getCourierCompany().toUpperCase()));
//                newOrder.setSelfCollection(false);
//            } catch (IllegalArgumentException e) {
//                throw new RuntimeException("Invalid courierCompany: " + orderDTO.getCourierCompany());
//            }
//        } else {
            newOrder.setCourierCompany(CourierCompany.DPD);
            newOrder.setSelfCollection(false);
        }


        List<OrderedSpider> orderedSpiders = orderDTO.getOrderedSpiders().stream()
                .map(itemDTO -> {
                    Spider spider = spiderRepository.findById(itemDTO.getSpiderId())
                            .orElseThrow(() -> new SpiderNotFoundException("Spider with id " + itemDTO.getSpiderId() + " not found."));
                    OrderedSpider orderedSpider = new OrderedSpider();
                    orderedSpider.setSpider(spider);
                    orderedSpider.setQuantity(itemDTO.getQuantity());
                    orderedSpider.setOrder(newOrder);
                    return orderedSpider;
                }).collect(Collectors.toList());

        orderedSpiders.forEach(orderedSpider -> {
            Spider spider = orderedSpider.getSpider();
            int orderedQuantity = orderedSpider.getQuantity();
            int currentQuantity = spider.getQuantity();

            if (currentQuantity < orderedQuantity) {
                throw new RuntimeException("Not enough stock for spider: " + spider.getSpeciesName());
            }
            spider.setQuantity(currentQuantity - orderedQuantity);
            spiderRepository.save(spider);
        });

        newOrder.setOrderedSpiders(orderedSpiders);

        double totalPrice = orderedSpiders.stream()
                .mapToDouble(os -> os.getSpider().getPrice() * os.getQuantity())
                .sum();
        newOrder.setPrice(totalPrice);

        Order savedOrder = orderRepository.save(newOrder);

        return savedOrder;
    }

    @Transactional
    public Order updateOrder(UUID id, OrderDTO orderDTO) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException("Order with id " + id + " not found."));

        if (orderDTO.getCustomerId() != null && !orderDTO.getCustomerId().equals(existingOrder.getCustomer().getId())) {
            Customer customer = customerService.getCustomerById(orderDTO.getCustomerId());
            if (customer == null) {
                throw new CustomerNotFoundException("Customer with id " + orderDTO.getCustomerId() + " not found.");
            }
            existingOrder.setCustomer(customer);
        }

        if (orderDTO.getStatus() != null) {
            existingOrder.setStatus(OrderStatus.valueOf(orderDTO.getStatus()));
        }

        if(orderDTO.getCourierCompany() != null && !orderDTO.getCourierCompany().isBlank()) {
            try{
                existingOrder.setCourierCompany(CourierCompany.valueOf(orderDTO.getCourierCompany().toUpperCase()));
                existingOrder.setSelfCollection(false);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid courierCompany: " + orderDTO.getCourierCompany());
            }
        } else {
            existingOrder.setCourierCompany(null);
            existingOrder.setSelfCollection(true);
        }

        if (orderDTO.getShipmentNumber() != null) {
            existingOrder.setShipmentNumber(orderDTO.getShipmentNumber());
        }

        if (orderDTO.getSelfCollection() != null) {
            existingOrder.setSelfCollection(orderDTO.getSelfCollection());
        }

        if (orderDTO.getOrderedSpiders() != null) {
            existingOrder.getOrderedSpiders().clear();

            List<OrderedSpider> updatedSpiders = orderDTO.getOrderedSpiders().stream()
                    .map(itemDTO -> {
                        Spider spider = spiderRepository.findById(itemDTO.getSpiderId())
                                .orElseThrow(() -> new SpiderNotFoundException("Spider with id " + itemDTO.getSpiderId() + " not found."));

                        OrderedSpider orderedSpider = new OrderedSpider();
                        orderedSpider.setSpider(spider);
                        orderedSpider.setQuantity(itemDTO.getQuantity());
                        orderedSpider.setOrder(existingOrder);
                        return orderedSpider;
                    })
                    .collect(Collectors.toList());

            existingOrder.getOrderedSpiders().addAll(updatedSpiders);
        }

        double totalPrice = existingOrder.getOrderedSpiders().stream()
                .mapToDouble(os -> os.getSpider().getPrice() * os.getQuantity())
                .sum();
        existingOrder.setPrice(totalPrice);

        return orderRepository.save(existingOrder);
    }

    public void deleteOrder(UUID id) {
        if (!orderRepository.existsById(id)) {
            throw new OrderNotFoundException("Order with id " + id + " not found.");
        }
        orderRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Page<Order> getAllOrders(int page, int size, Sort sort) {
        Pageable pageable = PageRequest.of(page, size, sort);
        return orderRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Optional<Order> findById(UUID id) {
        return orderRepository.findById(id);
    }
}