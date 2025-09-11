package com.WMS_spiders_wholesale_system.dto;

import com.WMS_spiders_wholesale_system.entity.Spider;
import org.springframework.data.domain.Page;

public class SpiderMapper {

    public static Spider toEntity(SpiderDTO dto) {
        Spider entity = new Spider();
        entity.setId(dto.getId());
        entity.setTypeName(dto.getTypeName());
        entity.setSpeciesName(dto.getSpeciesName());
        entity.setQuantity(dto.getQuantity());
        entity.setSize(dto.getSize());
        entity.setPrice(dto.getPrice());
        entity.setCites(dto.isCites());
        return entity;
    }

    public static SpiderDTO toDTO(Spider spider) {
        SpiderDTO dto = new SpiderDTO();
        dto.setId(spider.getId());
        dto.setTypeName(spider.getTypeName());
        dto.setSpeciesName(spider.getSpeciesName());
        dto.setQuantity(spider.getQuantity());
        dto.setSize(spider.getSize());
        dto.setPrice(spider.getPrice());
        dto.setCites(spider.isCites());
        return dto;
    }

    public static Page<SpiderDTO> toDTOPage(Page<Spider> spiderPage) {
        return spiderPage.map(SpiderMapper::toDTO);
    }
}