package com.WMS_spiders_wholesale_system.service;

import com.WMS_spiders_wholesale_system.entity.Customer;
import com.WMS_spiders_wholesale_system.exception.CustomerAlreadyExistsException;
import com.WMS_spiders_wholesale_system.repository.CustomerRepository;
import com.WMS_spiders_wholesale_system.exception.CustomerNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer addCustomer(Customer customer) {
        if(customerRepository.existsByEmail(customer.getEmail())) {
            throw new CustomerAlreadyExistsException("Customer already exists" + customer);
        }
        return customerRepository.save(customer);
    }

    public Customer updateCustomer(Customer customer) {
        if (!customerRepository.existsById(customer.getId())) {
            throw new CustomerNotFoundException("Customer with id " + customer.getId() + " not found");
        }
        return customerRepository.save(customer);
    }

    public void deleteCustomer(UUID id) {
        if (!customerRepository.existsById(id)) {
            throw new CustomerNotFoundException("Customer with id " + id + " not found");
        }
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
}