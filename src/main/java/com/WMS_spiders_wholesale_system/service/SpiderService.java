package com.WMS_spiders_wholesale_system.service;

import com.WMS_spiders_wholesale_system.entity.Spider;
import com.WMS_spiders_wholesale_system.exception.InvalidSpiderDataException;
import com.WMS_spiders_wholesale_system.exception.SpiderNotFoundException;
import com.WMS_spiders_wholesale_system.repository.SpiderRepository;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


import java.util.UUID;

import org.slf4j.Logger;

@Service
public class SpiderService {

    private final SpiderRepository spiderRepository;
    private static final Logger logger = LoggerFactory.getLogger(SpiderService.class);

    @Autowired
    public SpiderService(SpiderRepository spiderRepository) {
        this.spiderRepository = spiderRepository;
    }

    public Spider addSpider(Spider spider) {
        validateSpider(spider);
        logger.info("Added spider: " + spider.getId() + " " + spider.getSpeciesName() + " " + spider.getTypeName());
        return spiderRepository.save(spider);
    }

    public Spider updateSpider(Spider spider) {
        Spider existingSpider = spiderRepository.findById(spider.getId())
                .orElseThrow(() -> new SpiderNotFoundException("Spider with id " + spider.getId() + " not found"));
        existingSpider.setTypeName(spider.getTypeName());
        existingSpider.setSpeciesName(spider.getSpeciesName());
        existingSpider.setQuantity(spider.getQuantity());
        existingSpider.setSize(spider.getSize());
        existingSpider.setPrice(spider.getPrice());
        logger.info("Updated spider " + existingSpider.getId());
        return spiderRepository.save(existingSpider);
    }

    public Spider getSpiderById(UUID id) {
        return spiderRepository.findById(id)
                .orElseThrow(() -> new SpiderNotFoundException("Spider with id " + id + " not found"));
    }

    public void removeSpider(UUID id) {
        if (!spiderRepository.existsById(id)) {
            throw new SpiderNotFoundException("Spider with ID " + id + " not found");
        }
        spiderRepository.deleteById(id);
    }

    public Page<Spider> getAllSpiders(int page, int size, Sort sort) {
        Pageable pageable = PaginationHelper.createPageable(page, size, sort, "speciesName");
        return spiderRepository.findAll(pageable);
    }

    private void validateSpider(Spider spider) {
        if (spider == null) {
            throw new InvalidSpiderDataException("Spider cannot be null");
        }

        if ((spider.getSpeciesName() == null) || spider.getSpeciesName().isEmpty()
                || (spider.getTypeName() == null) || spider.getTypeName().isEmpty()) {
            throw new InvalidSpiderDataException("Spider species name or type name cannot be null or empty");
        }
        if (spider.getSize() == null) {
            throw new InvalidSpiderDataException("Spider size cannot be null");
        }
        if (spider.getQuantity() < 0) {
            throw new InvalidSpiderDataException("Spider quantity cannot smaller than 0");
        }
        logger.info("Validating spider: " + spider.getId());
    }

}
