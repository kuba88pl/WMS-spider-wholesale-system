package com.WMS_spiders_wholesale_system.dto;

import com.WMS_spiders_wholesale_system.entity.Spider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;

public class SpiderMapper {
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

    public static Page<SpiderDTO> toDTOPage(Page<Spider> spidersPage) {
        List<SpiderDTO> dtoList = spidersPage.getContent().stream()
                .map(SpiderMapper::toDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(dtoList, spidersPage.getPageable(), spidersPage.getTotalElements());
    }
}