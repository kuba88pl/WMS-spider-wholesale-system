package com.WMS_spiders_wholesale_system.repository;

import com.WMS_spiders_wholesale_system.entity.Spider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SpiderRepository extends JpaRepository<Spider, UUID> {

}
