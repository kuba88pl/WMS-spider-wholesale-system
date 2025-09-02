package com.WMS_spiders_wholesale_system.controller;


import com.WMS_spiders_wholesale_system.entity.Customer;
import com.WMS_spiders_wholesale_system.service.CustomerService;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public List<Customer> showAllCustomers() {
        return customerService.showAllCustomers();
    }
}
