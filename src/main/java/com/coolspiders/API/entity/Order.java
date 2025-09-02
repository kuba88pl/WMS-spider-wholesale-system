package com.coolspiders.API.entity;

import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private UUID orderId;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private OrderStatus status;
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
    @OneToMany(mappedBy = "order")
    private List<Spider> orderedSpiders;

    public Order() {
    }

    public Order(Customer customer, OrderStatus status) {
        this.customer = customer;
        this.status = status;

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
