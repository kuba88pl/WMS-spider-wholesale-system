package com.WMS_spiders_wholesale_system.service;

import com.WMS_spiders_wholesale_system.entity.Customer;
import com.WMS_spiders_wholesale_system.exception.CustomerAlreadyExistsException;
import com.WMS_spiders_wholesale_system.exception.CustomerNotFoundException;
import com.WMS_spiders_wholesale_system.exception.InvalidCustomerDataException;
import com.WMS_spiders_wholesale_system.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);

    @Autowired
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer addCustomer(Customer customer) {
        validateCustomerForCreate(customer);
        if (customer.getEmail() != null && !customer.getEmail().isBlank() && customerRepository.existsByEmail(customer.getEmail())) {
            throw new CustomerAlreadyExistsException("Customer with email " + customer.getEmail() + " already exists.");
        }
        logger.info("Created new customer with ID: " + customer.getId());
        return customerRepository.save(customer);
    }

    public Customer updateCustomer(Customer customer) {
        if (!customerRepository.existsById(customer.getId())) {
            throw new CustomerNotFoundException("Customer with id " + customer.getId() + " not found");
        }
        validateCustomerForUpdate(customer);
        Customer existingCustomer = customerRepository.findById(customer.getId()).orElseThrow();
        if (customer.getEmail() != null && !customer.getEmail().isBlank() && !customer.getEmail().equals(existingCustomer.getEmail())) {
            if (customerRepository.existsByEmail(customer.getEmail())) {
                throw new CustomerAlreadyExistsException("Customer with email " + customer.getEmail() + " already exists.");
            }
        }
        logger.info("Updated customer with ID: " + customer.getId());
        return customerRepository.save(customer);
    }

    public void deleteCustomer(UUID id) {
        if (!customerRepository.existsById(id)) {
            throw new CustomerNotFoundException("Customer with id " + id + " not found");
        }
        logger.info("Deleted customer with ID: " + id);
        customerRepository.deleteById(id);
    }

    public Page<Customer> getAllCustomers(int page, int size, Sort sort) {
        Pageable pageable = PaginationHelper.createPageable(page, size, sort, "id");
        return customerRepository.findAll(pageable);
    }

    public Customer getCustomerById(UUID id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException("Customer with id " + id + " not found"));
    }

    public List<Customer> getCustomerByLastName(String lastName) throws CustomerNotFoundException {
        List<Customer> customers = customerRepository.getCustomerByLastName(lastName);
        if (customers.isEmpty()) {
            throw new CustomerNotFoundException("Customer with last name " + lastName + " not found");
        }
        return customers;
    }

    private void validateCustomerForCreate(Customer customer) {
        if (customer == null) {
            throw new InvalidCustomerDataException("Customer cannot be null");
        }
        if (customer.getFirstName() == null || customer.getFirstName().isBlank()) {
            throw new InvalidCustomerDataException("First name is required");
        }
        if (customer.getLastName() == null || customer.getLastName().isBlank()) {
            throw new InvalidCustomerDataException("Last name is required");
        }
    }

    private void validateCustomerForUpdate(Customer customer) {
        if (customer == null) {
            throw new InvalidCustomerDataException("Customer cannot be null");
        }
        if (customer.getFirstName() == null || customer.getFirstName().isBlank()) {
            throw new InvalidCustomerDataException("First name is required");
        }
        if (customer.getLastName() == null || customer.getLastName().isBlank()) {
            throw new InvalidCustomerDataException("Last name is required");
        }
    }
}