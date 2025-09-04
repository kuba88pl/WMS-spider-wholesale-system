package com.WMS_spiders_wholesale_system.repository;

import com.WMS_spiders_wholesale_system.entity.Order;
import com.WMS_spiders_wholesale_system.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByStatus(OrderStatus orderStatus);

    Order updateStatus(Order order, OrderStatus orderStatus);
}
