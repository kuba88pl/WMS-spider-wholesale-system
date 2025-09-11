package com.WMS_spiders_wholesale_system.dto;

import com.WMS_spiders_wholesale_system.entity.Order;
import com.WMS_spiders_wholesale_system.entity.OrderedSpider;
import com.WMS_spiders_wholesale_system.service.SpiderService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

public class OrderMapper {

    // Przebudowana metoda toDTO
    public static OrderDTO toDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setOrderId(order.getOrderId());
        dto.setDate(order.getDate());
        dto.setPrice(order.getPrice());
        dto.setStatus(order.getStatus());
        if (order.getCustomer() != null) {
            dto.setCustomerId(order.getCustomer().getId());
        }

        // Zmieniona logika mapowania OrderedSpider
        if (order.getOrderedSpiders() != null) {
            List<OrderedSpiderDTO> orderedSpiders = order.getOrderedSpiders().stream()
                    .map(OrderedSpiderMapper::toDTO)
                    .collect(Collectors.toList());
            dto.setOrderedSpiders(orderedSpiders);
        }
        return dto;
    }

    public static Order toEntity(OrderDTO dto) {
        Order order = new Order();
        order.setOrderId(dto.getOrderId());
        order.setDate(dto.getDate());
        order.setPrice(dto.getPrice());
        order.setStatus(dto.getStatus());
        return order;
    }
}