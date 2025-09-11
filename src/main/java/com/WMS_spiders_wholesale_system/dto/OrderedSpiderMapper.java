package com.WMS_spiders_wholesale_system.dto;

import com.WMS_spiders_wholesale_system.entity.OrderedSpider;
import com.WMS_spiders_wholesale_system.entity.Spider;

public class OrderedSpiderMapper {

    public static OrderedSpiderDTO toDTO(OrderedSpider orderedSpider) {
        OrderedSpiderDTO dto = new OrderedSpiderDTO();
        dto.setSpiderId(orderedSpider.getSpider().getId());
        dto.setQuantity(orderedSpider.getQuantity());

        // Uzupełniamy dane pająka dla wygody frontendu
        Spider spider = orderedSpider.getSpider();
        if (spider != null) {
            dto.setTypeName(spider.getTypeName());
            dto.setSpeciesName(spider.getSpeciesName());
            dto.setSize(spider.getSize());
            dto.setPrice(spider.getPrice());
        }

        return dto;
    }

    public static OrderedSpider toEntity(OrderedSpiderDTO dto) {
        OrderedSpider entity = new OrderedSpider();
        entity.setQuantity(dto.getQuantity());

        // Uwaga: Spider jest ustawiany w serwisie na podstawie ID
        // więc tutaj go nie przypisujemy, żeby uniknąć błędów
        return entity;
    }
}
