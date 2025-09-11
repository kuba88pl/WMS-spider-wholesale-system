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
        // W tym przypadku nie ustawiamy obiektu Spider, ponieważ
        // jest to robione w serwisie, na podstawie ID
        entity.setQuantity(dto.getQuantity());
        return entity;
    }
}