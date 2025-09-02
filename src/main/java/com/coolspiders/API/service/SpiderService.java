package com.coolspiders.API.service;

import com.coolspiders.API.entity.Spider;
import com.coolspiders.API.exception.SpiderNotFoundException;
import com.coolspiders.API.repository.SpiderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SpiderService {

    private final SpiderRepository spiderRepository;

    @Autowired
    public SpiderService(SpiderRepository spiderRepository) {
        this.spiderRepository = spiderRepository;
    }

    public Spider addSpider(Spider spider) {
        return spiderRepository.save(spider);
    }

    public Spider updateSpider(Spider spider) {
        if (!spiderRepository.existsById(spider.getId())) {
            throw new SpiderNotFoundException("Spider with id " + spider.getId() + " not found");
        }
        return spiderRepository.save(spider);
    }

    public void removeSpider(UUID id) {

        if (!spiderRepository.existsById(id)) {
            throw new SpiderNotFoundException("Spider with ID " + id + " not found");
        }
        spiderRepository.deleteById(id);
    }

    public void showAllSpiders() {
        List<Spider> spiders = spiderRepository.findAll();
    }


}
