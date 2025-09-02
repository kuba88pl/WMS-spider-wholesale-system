package com.coolspiders.API.controller;


import com.coolspiders.API.entity.Customer;
import com.coolspiders.API.service.CustomerService;
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
