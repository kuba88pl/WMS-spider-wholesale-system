package com.coolspiders.API.repository;

import com.coolspiders.API.entity.Spider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SpiderRepository extends JpaRepository<Spider, UUID> {

}
