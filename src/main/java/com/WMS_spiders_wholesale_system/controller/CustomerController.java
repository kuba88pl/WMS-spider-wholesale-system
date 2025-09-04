package com.WMS_spiders_wholesale_system.controller;

import com.WMS_spiders_wholesale_system.entity.Customer;
import com.WMS_spiders_wholesale_system.exception.CustomerNotFoundException;
import com.WMS_spiders_wholesale_system.exception.InvalidCustomerDataException;
import com.WMS_spiders_wholesale_system.service.CustomerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/customers")
public class CustomerController {
    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<Customer> addCustomer(@RequestBody Customer customer) {
        try {
            Customer newCustomer = customerService.addCustomer(customer);
            return ResponseEntity.status(HttpStatus.CREATED).body(newCustomer);
        } catch (InvalidCustomerDataException e) {
            logger.error("Invalid customer data: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            logger.error("An unexpected error occurred while adding customer", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping
    public ResponseEntity<Customer> updateCustomer(@RequestBody Customer customer) {
        try {
            Customer updatedCustomer = customerService.updateCustomer(customer);
            return ResponseEntity.ok(updatedCustomer);
        } catch (CustomerNotFoundException e) {
            logger.error("Customer not found with id {}", customer.getId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (InvalidCustomerDataException e) {
            logger.error("Invalid customer data: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            logger.error("An unexpected error occurred while updating customer", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable UUID id) {
        try {
            customerService.deleteCustomer(id);
            return ResponseEntity.noContent().build();
        } catch (CustomerNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping
    public ResponseEntity<Page<Customer>> getAllCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {

        var customersPage = customerService.getAllCustomers(page, size, org.springframework.data.domain.Sort.by(sortBy));
        return ResponseEntity.ok(customersPage);
    }

    @GetMapping("/lastName/{lastName}")

    public ResponseEntity<List<Customer>> getCustomerByLastName(@PathVariable String lastName) {
        try {
            List<Customer> customers = customerService.getCustomerByLastName(lastName);
            return ResponseEntity.ok(customers);
        } catch (CustomerNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
