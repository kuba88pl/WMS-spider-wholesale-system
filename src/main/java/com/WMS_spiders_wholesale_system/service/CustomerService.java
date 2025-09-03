package com.WMS_spiders_wholesale_system.service;

import com.WMS_spiders_wholesale_system.entity.Customer;
import com.WMS_spiders_wholesale_system.exception.CustomerAlreadyExistsException;
import com.WMS_spiders_wholesale_system.exception.InvalidCustomerDataException;
import com.WMS_spiders_wholesale_system.repository.CustomerRepository;
import com.WMS_spiders_wholesale_system.exception.CustomerNotFoundException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;

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
        validateCustomer(customer);
        if (customerRepository.existsByEmail(customer.getEmail())) {
            throw new CustomerAlreadyExistsException("Customer already exists" + customer);
        }
        logger.info("Created new customer with ID: " + customer.getId());
        return customerRepository.save(customer);
    }

    public Customer updateCustomer(Customer customer) {
        if (!customerRepository.existsById(customer.getId())) {
            throw new CustomerNotFoundException("Customer with id " + customer.getId() + " not found");
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
        if (sort == null || sort.isUnsorted()) {
            sort = Sort.by(Sort.Direction.ASC, "lastName");
        }
        Pageable pageable = PageRequest.of(page, size, sort);
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

    private void validateCustomer(Customer customer) {
        if (customer == null) {
            throw new InvalidCustomerDataException("Customer cannot be null");
        }
        if (customer.getFirstName() == null && customer.getLastName() == null) {
            throw new InvalidCustomerDataException("First name or last name cannot be null");
        }
        if (customer.getEmail() == null || customer.getEmail().isEmpty()) {
            throw new InvalidCustomerDataException("Email cannot be empty");
        }
        if (customer.getTelephone() == null || customer.getTelephone().isEmpty()) {
            throw new InvalidCustomerDataException("Telephone cannot be empty");
        }
        logger.info("Validating customer with ID: " + customer.getId());
    }
}