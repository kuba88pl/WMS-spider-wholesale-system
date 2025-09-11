// Poprawiona klasa OrderService
package com.WMS_spiders_wholesale_system.service;

import com.WMS_spiders_wholesale_system.dto.OrderDTO;
import com.WMS_spiders_wholesale_system.entity.Customer;
import com.WMS_spiders_wholesale_system.entity.Order;
import com.WMS_spiders_wholesale_system.entity.OrderedSpider;
import com.WMS_spiders_wholesale_system.entity.Spider;
import com.WMS_spiders_wholesale_system.exception.CustomerNotFoundException;
import com.WMS_spiders_wholesale_system.exception.OrderNotFoundException;
import com.WMS_spiders_wholesale_system.exception.SpiderNotFoundException;
import com.WMS_spiders_wholesale_system.repository.CustomerRepository;
import com.WMS_spiders_wholesale_system.repository.OrderRepository;
import com.WMS_spiders_wholesale_system.repository.SpiderRepository;
import com.WMS_spiders_wholesale_system.entity.OrderStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final SpiderRepository spiderRepository;

    public OrderService(OrderRepository orderRepository, CustomerRepository customerRepository, SpiderRepository spiderRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.spiderRepository = spiderRepository;
    }

    @Transactional
    public Order createOrder(OrderDTO orderDTO) throws CustomerNotFoundException, SpiderNotFoundException {
        Customer customer = customerRepository.findById(orderDTO.getCustomerId())
                .orElseThrow(() -> new CustomerNotFoundException("Customer with ID " + orderDTO.getCustomerId() + " not found."));

        Order newOrder = new Order();
        newOrder.setCustomer(customer);
        newOrder.setDate(LocalDate.now());
        newOrder.setStatus(OrderStatus.PENDING);

        double totalPrice = 0.0;

        for (var orderedSpiderDTO : orderDTO.getOrderedSpiders()) {
            Spider spider = spiderRepository.findById(orderedSpiderDTO.getSpiderId())
                    .orElseThrow(() -> new SpiderNotFoundException("Spider with ID " + orderedSpiderDTO.getSpiderId() + " not found."));

            if (spider.getQuantity() < orderedSpiderDTO.getQuantity()) {
                throw new IllegalStateException("Not enough spiders of species " + spider.getSpeciesName() + " in stock.");
            }

            OrderedSpider orderedSpider = new OrderedSpider();
            orderedSpider.setSpider(spider);
            orderedSpider.setQuantity(orderedSpiderDTO.getQuantity());

            newOrder.addOrderedSpider(orderedSpider);
            totalPrice += spider.getPrice() * orderedSpiderDTO.getQuantity();

            // WAŻNE: Po prostu ustaw nową ilość. Nie musisz zapisywać pająka oddzielnie.
            // Hibernate zrobi to za Ciebie w ramach transakcji.
            spider.setQuantity(spider.getQuantity() - orderedSpiderDTO.getQuantity());
        }

        newOrder.setPrice(totalPrice);

        // Zapisujemy obiekt Order, co automatycznie zapisze powiązane OrderedSpider
        // oraz zaktualizuje Spider, ponieważ są w tej samej transakcji.
        Order finalOrder = orderRepository.save(newOrder);

        logger.debug("Created new order: {}", finalOrder.getId());
        return finalOrder;
    }

    @Transactional
    public void deleteOrder(UUID id) throws OrderNotFoundException {
        if (!orderRepository.existsById(id)) {
            throw new OrderNotFoundException("Order with ID " + id + " not found.");
        }
        orderRepository.deleteById(id);
    }

    public Page<Order> getAllOrders(int page, int size, Sort sort) {
        return orderRepository.findAll(PageRequest.of(page, size, sort));
    }

    public Optional<Order> findById(UUID id) {
        return orderRepository.findById(id);
    }
}