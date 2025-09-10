package com.WMS_spiders_wholesale_system.service;

import com.WMS_spiders_wholesale_system.entity.Customer;
import com.WMS_spiders_wholesale_system.entity.Order;
import com.WMS_spiders_wholesale_system.entity.OrderStatus;
import com.WMS_spiders_wholesale_system.entity.Spider;
import com.WMS_spiders_wholesale_system.exception.InvalidOrderDataException;
import com.WMS_spiders_wholesale_system.exception.OrderNotFoundException;
import com.WMS_spiders_wholesale_system.repository.CustomerRepository;
import com.WMS_spiders_wholesale_system.repository.OrderRepository;
import com.WMS_spiders_wholesale_system.repository.SpiderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private SpiderRepository spiderRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    void shouldCreateOrderWithCorrectTotalPrice() {
        // given
        Customer customer = new Customer();
        customer.setId(UUID.randomUUID());

        Spider spider1 = new Spider();
        spider1.setPrice(150.0);
        spider1.setQuantity(2);

        Spider spider2 = new Spider();
        spider2.setPrice(300.0);
        spider2.setQuantity(1);

        Order order = new Order();
        order.setCustomer(customer);
        order.setDate(LocalDate.now());
        order.setStatus(OrderStatus.NEW);
        order.setOrderedSpiders(List.of(spider1, spider2));

        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        Order savedOrder = orderService.createOrder(order);

        // then
        assertEquals(600.0, savedOrder.getPrice());
        assertEquals(order, spider1.getOrder());
        assertEquals(order, spider2.getOrder());
        verify(orderRepository).save(order);
    }

    @Test
    void shouldUpdateOrderStatus() {
        // given
        UUID orderId = UUID.randomUUID();
        Order order = new Order();
        order.setOrderId(orderId);
        order.setStatus(OrderStatus.NEW);

        when(orderRepository.existsById(orderId)).thenReturn(true);
        when(orderRepository.save(order)).thenReturn(order);

        // when
        Order updatedOrder = orderService.updateStatus(order, OrderStatus.COMPLETED);

        // then
        assertEquals(OrderStatus.COMPLETED, updatedOrder.getStatus());
        verify(orderRepository).save(order);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonexistentOrder() {
        // given
        UUID orderId = UUID.randomUUID();
        Order order = new Order();
        order.setOrderId(orderId);

        when(orderRepository.existsById(orderId)).thenReturn(false);

        // then
        assertThrows(OrderNotFoundException.class, () -> orderService.updateStatus(order, OrderStatus.CANCELLED));
    }

    @Test
    void shouldDeleteOrderById() {
        // given
        UUID orderId = UUID.randomUUID();
        when(orderRepository.existsById(orderId)).thenReturn(true);

        // when
        orderService.deleteOrder(orderId);

        // then
        verify(orderRepository).deleteById(orderId);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonexistentOrder() {
        // given
        UUID orderId = UUID.randomUUID();
        when(orderRepository.existsById(orderId)).thenReturn(false);

        // then
        assertThrows(OrderNotFoundException.class, () -> orderService.deleteOrder(orderId));
    }

    @Test
    void shouldReturnOrderById() {
        // given
        UUID orderId = UUID.randomUUID();
        Order order = new Order();
        order.setOrderId(orderId);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        // when
        Order result = orderService.getOrderById(orderId);

        // then
        assertEquals(orderId, result.getOrderId());
    }

    @Test
    void shouldThrowExceptionWhenOrderNotFoundById() {
        // given
        UUID orderId = UUID.randomUUID();
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // then
        assertThrows(OrderNotFoundException.class, () -> orderService.getOrderById(orderId));
    }

    @Test
    void shouldReturnPagedOrders() {
        // given
        Pageable pageable = PageRequest.of(0, 10, Sort.by("status"));
        Page<Order> page = new PageImpl<>(List.of(new Order()));
        when(orderRepository.findAll(pageable)).thenReturn(page);

        // when
        Page<Order> result = orderService.getAllOrders(10, 0, Sort.by("status"));

        // then
        assertEquals(1, result.getContent().size());
    }

    @Test
    void shouldValidateCorrectOrder() {
        // given
        Customer customer = new Customer();
        customer.setId(UUID.randomUUID());

        Spider spider = new Spider();
        spider.setPrice(100.0);
        spider.setQuantity(1);

        Order order = new Order();
        order.setCustomer(customer);
        order.setDate(LocalDate.now());
        order.setOrderedSpiders(List.of(spider));

        // then
        assertDoesNotThrow(() -> orderService.validateOrder(order));
    }

    @Test
    void shouldThrowExceptionForInvalidOrder() {
        // given
        Order order = new Order(); // missing customer, date, spiders

        // then
        assertThrows(InvalidOrderDataException.class, () -> orderService.validateOrder(order));
    }
}
