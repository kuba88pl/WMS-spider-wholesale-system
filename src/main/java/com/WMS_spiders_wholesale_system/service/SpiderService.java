package com.WMS_spiders_wholesale_system.service;

import com.WMS_spiders_wholesale_system.entity.Spider;
import com.WMS_spiders_wholesale_system.exception.InvalidSpiderDataException;
import com.WMS_spiders_wholesale_system.exception.SpiderNotFoundException;
import com.WMS_spiders_wholesale_system.repository.SpiderRepository;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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
        return spiderRepository.save(spider);
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

    public List<Spider> getAllSpiders() {
        return spiderRepository.findAll();
    }

    private void validateSpider(Spider spider) {
        if (spider == null) {
            throw new InvalidSpiderDataException("Spider cannot be null");
        }

        if (spider.getSpeciesName() == null && spider.getTypeName() == null) {
            throw new InvalidSpiderDataException("Spider species name or type name cannot be null");
        }
        if (spider.getSize() == null) {
            throw new InvalidSpiderDataException("Spider size cannot be null");
        }
        if (spider.getQuantity() > 1) {
            throw new InvalidSpiderDataException("Spider quantity cannot smaller than 0");
        }
        logger.info("Validating spider: " + spider.getId());
    }

}
