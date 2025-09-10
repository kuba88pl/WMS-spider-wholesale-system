package com.WMS_spiders_wholesale_system;

import com.WMS_spiders_wholesale_system.entity.Customer;
import com.WMS_spiders_wholesale_system.exception.CustomerAlreadyExistsException;
import com.WMS_spiders_wholesale_system.exception.CustomerNotFoundException;
import com.WMS_spiders_wholesale_system.exception.InvalidCustomerDataException;
import com.WMS_spiders_wholesale_system.repository.CustomerRepository;
import com.WMS_spiders_wholesale_system.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    @Test
    void shouldAddCustomerSuccessfully() {
        // given
        Customer customer = new Customer("Jan", "Kowalski", "123456789", "jan@example.com", "ul. Testowa 1", "PL123");
        when(customerRepository.existsByEmail(customer.getEmail())).thenReturn(false);
        when(customerRepository.save(customer)).thenReturn(customer);

        // when
        Customer saved = customerService.addCustomer(customer);

        // then
        assertEquals("Jan", saved.getFirstName());
        verify(customerRepository).save(customer);
    }

    @Test
    void shouldThrowExceptionWhenCustomerAlreadyExists() {
        // given
        Customer customer = new Customer("Anna", "Nowak", "987654321", "anna@example.com", "ul. Przykładowa 2", "PL456");
        when(customerRepository.existsByEmail(customer.getEmail())).thenReturn(true);

        // then
        assertThrows(CustomerAlreadyExistsException.class, () -> customerService.addCustomer(customer));
    }

    @Test
    void shouldUpdateCustomerSuccessfully() {
        // given
        UUID id = UUID.randomUUID();
        Customer customer = new Customer("Piotr", "Zieliński", "111222333", "piotr@example.com", "ul. Zielona 3", "PL789");
        customer.setId(id);
        when(customerRepository.existsById(id)).thenReturn(true);
        when(customerRepository.save(customer)).thenReturn(customer);

        // when
        Customer updated = customerService.updateCustomer(customer);

        // then
        assertEquals(id, updated.getId());
        verify(customerRepository).save(customer);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonexistentCustomer() {
        // given
        UUID id = UUID.randomUUID();
        Customer customer = new Customer();
        customer.setId(id);
        when(customerRepository.existsById(id)).thenReturn(false);

        // then
        assertThrows(CustomerNotFoundException.class, () -> customerService.updateCustomer(customer));
    }

    @Test
    void shouldDeleteCustomerSuccessfully() {
        // given
        UUID id = UUID.randomUUID();
        when(customerRepository.existsById(id)).thenReturn(true);

        // when
        customerService.deleteCustomer(id);

        // then
        verify(customerRepository).deleteById(id);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonexistentCustomer() {
        // given
        UUID id = UUID.randomUUID();
        when(customerRepository.existsById(id)).thenReturn(false);

        // then
        assertThrows(CustomerNotFoundException.class, () -> customerService.deleteCustomer(id));
    }

    @Test
    void shouldReturnCustomerById() {
        // given
        UUID id = UUID.randomUUID();
        Customer customer = new Customer();
        customer.setId(id);
        when(customerRepository.findById(id)).thenReturn(Optional.of(customer));

        // when
        Customer result = customerService.getCustomerById(id);

        // then
        assertEquals(id, result.getId());
    }

    @Test
    void shouldThrowExceptionWhenCustomerNotFoundById() {
        // given
        UUID id = UUID.randomUUID();
        when(customerRepository.findById(id)).thenReturn(Optional.empty());

        // then
        assertThrows(CustomerNotFoundException.class, () -> customerService.getCustomerById(id));
    }

    @Test
    void shouldReturnCustomersByLastName() {
        // given
        String lastName = "Kowalski";
        List<Customer> customers = List.of(new Customer(), new Customer());
        when(customerRepository.getCustomerByLastName(lastName)).thenReturn(customers);

        // when
        List<Customer> result = customerService.getCustomerByLastName(lastName);

        // then
        assertEquals(2, result.size());
    }

    @Test
    void shouldThrowExceptionWhenNoCustomerWithLastNameFound() {
        // given
        String lastName = "Nieistniejący";
        when(customerRepository.getCustomerByLastName(lastName)).thenReturn(Collections.emptyList());

        // then
        assertThrows(CustomerNotFoundException.class, () -> customerService.getCustomerByLastName(lastName));
    }

    @Test
    void shouldValidateCorrectCustomer() {
        // given
        Customer customer = new Customer("Adam", "Testowy", "123456789", "adam@example.com", "ul. Testowa", "PL000");

        // then
        assertDoesNotThrow(() -> {
            // używamy refleksji, bo metoda jest prywatna
            var method = CustomerService.class.getDeclaredMethod("validateCustomer", Customer.class);
            method.setAccessible(true);
            method.invoke(customerService, customer);
        });
    }

    @Test
    void shouldThrowExceptionForInvalidCustomer() {
        // given
        Customer customer = new Customer();
        customer.setFirstName(""); // invalid

        // then
        assertThrows(InvalidCustomerDataException.class, () -> {
            var method = CustomerService.class.getDeclaredMethod("validateCustomer", Customer.class);
            method.setAccessible(true);
            method.invoke(customerService, customer);
        });
    }
}
