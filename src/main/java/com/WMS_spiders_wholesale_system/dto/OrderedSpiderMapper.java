package com.WMS_spiders_wholesale_system.dto;

import com.WMS_spiders_wholesale_system.entity.OrderedSpider;

public class OrderedSpiderMapper {
    public static OrderedSpiderDTO toDTO(OrderedSpider orderedSpider) {
        OrderedSpiderDTO dto = new OrderedSpiderDTO();
        dto.setSpiderId(orderedSpider.getSpider().getId());
        dto.setQuantity(orderedSpider.getQuantity());
        return dto;
    }

    public static OrderedSpider toEntity(OrderedSpiderDTO dto) {
        OrderedSpider entity = new OrderedSpider();
        entity.setQuantity(dto.getQuantity());
        return entity;
    }
}