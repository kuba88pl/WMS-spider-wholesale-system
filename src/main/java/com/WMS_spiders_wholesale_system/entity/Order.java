package com.WMS_spiders_wholesale_system.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", columnDefinition = "VARCHAR(36)")
    private UUID orderId;
    @Column(name = "date")
    private LocalDate date;
    @Column(name = "price", nullable = false)
    private double price;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;
    @ManyToOne
    @JoinColumn(name = "customer_id", columnDefinition = "VARCHAR(36)")
    private Customer customer;
    @OneToMany(mappedBy = "order")
    private List<Spider> orderedSpiders;

    public Order() {
    }

    public Order(Customer customer, LocalDate date, OrderStatus status, double price, List<Spider> orderedSpiders) {
        this.customer = customer;
        this.date = date;
        this.price = price;
        this.orderedSpiders = orderedSpiders;
        this.status = status;

    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public void setOrderedSpiders(List<Spider> orderedSpiders) {
        this.orderedSpiders = orderedSpiders;
    }

    public List<Spider> getOrderedSpiders() {
        return orderedSpiders;
    }
}
