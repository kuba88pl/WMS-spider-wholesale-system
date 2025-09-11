package com.WMS_spiders_wholesale_system.dto;

import com.WMS_spiders_wholesale_system.entity.OrderStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class OrderDTO {
    private UUID id;
    private UUID customerId;
    private LocalDate date;
    private double price;
    private OrderStatus status;
    private List<OrderedSpiderDTO> orderedSpiders;

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public List<OrderedSpiderDTO> getOrderedSpiders() {
        return orderedSpiders;
    }

    public void setOrderedSpiders(List<OrderedSpiderDTO> orderedSpiders) {
        this.orderedSpiders = orderedSpiders;
    }
}