package com.WMS_spiders_wholesale_system.controller;

import com.WMS_spiders_wholesale_system.entity.Spider;
import com.WMS_spiders_wholesale_system.exception.InvalidSpiderDataException;
import com.WMS_spiders_wholesale_system.exception.SpiderNotFoundException;
import com.WMS_spiders_wholesale_system.service.SpiderService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping("api/spiders")
public class SpiderController {
    private static final Logger logger = LoggerFactory.getLogger(SpiderController.class);
    private final SpiderService spiderService;

    @Autowired
    public SpiderController(SpiderService spiderService) {
        this.spiderService = spiderService;
    }

//    @GetMapping("/")
//    public String hello() {
//        return "WSZYSTKO OK";
//    }

    @GetMapping("/{id}")
    public ResponseEntity<Spider> getSpiderById(@PathVariable UUID id) {
        try {
            Spider spider = spiderService.getSpiderById(id);
            return ResponseEntity.ok(spider);
        } catch (SpiderNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping
    public ResponseEntity<Spider> addSpider(@RequestBody Spider spider) {
        try {
            Spider newSpider = spiderService.addSpider(spider);
            return ResponseEntity.status(HttpStatus.CREATED).body(newSpider);
        } catch (InvalidSpiderDataException e) {
            logger.error("Invalid spider data: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            logger.error("An unexpected error occured while adding spider.", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping
    public ResponseEntity<Spider> updateSpider(@RequestBody Spider spider) {
        try {
            Spider updatedSpider = spiderService.updateSpider(spider);
            return ResponseEntity.ok(updatedSpider);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Spider> deleteSpider(@PathVariable UUID id) {
        try {
            spiderService.removeSpider(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllSpiders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "speciesName") String sortBy) {
        try {
            var spiderSPage = spiderService.getAllSpiders(page, size, org.springframework.data.domain.Sort.by(sortBy));
            return ResponseEntity.ok(spiderSPage);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

    }
}



