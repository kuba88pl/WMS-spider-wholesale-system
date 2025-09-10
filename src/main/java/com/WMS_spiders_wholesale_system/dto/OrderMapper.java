package com.WMS_spiders_wholesale_system.dto;

import com.WMS_spiders_wholesale_system.entity.Order;
import com.WMS_spiders_wholesale_system.entity.Spider;

import java.util.stream.Collectors;

public class OrderMapper {
    public static OrderDTO toDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setOrderId(order.getOrderId());
        dto.setDate(order.getDate());
        dto.setPrice(order.getPrice());
        dto.setStatus(order.getStatus());
        if (order.getCustomer() != null) {
            dto.setCustomerId(order.getCustomer().getId());
        }
        if (order.getOrderedSpiders() != null) {
            dto.setOrderedSpiders(order.getOrderedSpiders().stream()
                    .map(s -> {
                        OrderedSpiderDTO orderedSpiderDTO = new OrderedSpiderDTO();
                        orderedSpiderDTO.setSpiderId(s.getId());
                        orderedSpiderDTO.setTypeName(s.getTypeName());
                        orderedSpiderDTO.setSpeciesName(s.getSpeciesName());
                        orderedSpiderDTO.setQuantity(s.getQuantity());
                        orderedSpiderDTO.setSize(s.getSize());
                        orderedSpiderDTO.setPrice(s.getPrice());
                        return orderedSpiderDTO;
                    })
                    .collect(Collectors.toList()));
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