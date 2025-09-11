package com.WMS_spiders_wholesale_system.controller;

import com.WMS_spiders_wholesale_system.dto.OrderDTO;
import com.WMS_spiders_wholesale_system.dto.OrderMapper;
import com.WMS_spiders_wholesale_system.entity.Order;
import com.WMS_spiders_wholesale_system.exception.OrderNotFoundException;
import com.WMS_spiders_wholesale_system.service.OrderService;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"api/orders"})
public class OrderController {
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("")
    public ResponseEntity<OrderDTO> createOrder(@RequestBody OrderDTO orderDTO) {
        try {
            Order newOrder = this.orderService.createOrder(orderDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(OrderMapper.toDTO(newOrder));
        } catch (Exception e) {
            logger.error("An unexpected error occurred while creating order : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping({"/{id}"})
    public ResponseEntity<Void> deleteOrder(@PathVariable UUID id) {
        try {
            this.orderService.deleteOrder(id);
            return ResponseEntity.noContent().build();
        } catch (OrderNotFoundException var3) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping
    public ResponseEntity<Page<OrderDTO>> getAllOrders(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "date") String sortBy) {
        Page<Order> orderPage = this.orderService.getAllOrders(page, size, Sort.by(new String[]{sortBy}));
        return ResponseEntity.ok(OrderMapper.toDTOPage(orderPage));
    }
}