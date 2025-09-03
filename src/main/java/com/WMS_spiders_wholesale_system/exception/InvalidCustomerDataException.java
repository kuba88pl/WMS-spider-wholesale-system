package com.WMS_spiders_wholesale_system.exception;

import com.WMS_spiders_wholesale_system.repository.CustomerRepository;

public class InvalidCustomerDataException extends RuntimeException {
    public InvalidCustomerDataException(String message) {
        super(message);
    }
}
