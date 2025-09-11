package com.WMS_spiders_wholesale_system.dto;

import com.WMS_spiders_wholesale_system.entity.Customer;
import com.WMS_spiders_wholesale_system.entity.Order;
import com.WMS_spiders_wholesale_system.entity.OrderedSpider;
import com.WMS_spiders_wholesale_system.entity.Spider;
import com.WMS_spiders_wholesale_system.entity.OrderStatus;
import com.WMS_spiders_wholesale_system.repository.CustomerRepository;
import com.WMS_spiders_wholesale_system.repository.SpiderRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.stream.Collectors;

@Component
public class OrderMapper {
    private final CustomerRepository customerRepository;
    private final SpiderRepository spiderRepository;

    public OrderMapper(CustomerRepository customerRepository, SpiderRepository spiderRepository) {
        this.customerRepository = customerRepository;
        this.spiderRepository = spiderRepository;
    }

    public static OrderDTO toDTO(Order order) {
        if (order == null) {
            return null;
        }
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        if (order.getCustomer() != null) {
            dto.setCustomerId(order.getCustomer().getId());
        }
        dto.setDate(order.getDate());
        dto.setPrice(order.getPrice());
        dto.setStatus(order.getStatus());
        dto.setOrderedSpiders(order.getOrderedSpiders().stream()
                .map(OrderedSpiderMapper::toDTO)
                .collect(Collectors.toList()));
        return dto;
    }

    public static Page<OrderDTO> toDTOPage(Page<Order> orderPage) {
        return orderPage.map(OrderMapper::toDTO);
    }

    public Order toEntity(OrderDTO orderDTO) {
        if (orderDTO == null) {
            return null;
        }
        Order order = new Order();
        if (orderDTO.getCustomerId() != null) {
            Customer customer = customerRepository.findById(orderDTO.getCustomerId()).orElse(null);
            order.setCustomer(customer);
        }
        if (orderDTO.getOrderedSpiders() != null) {
            orderDTO.getOrderedSpiders().forEach(orderedSpiderDTO -> {
                Spider spider = spiderRepository.findById(orderedSpiderDTO.getSpiderId()).orElse(null);
                if (spider != null) {
                    OrderedSpider orderedSpider = new OrderedSpider();
                    orderedSpider.setSpider(spider);
                    orderedSpider.setQuantity(orderedSpiderDTO.getQuantity());
                    orderedSpider.setOrder(order);
                    order.getOrderedSpiders().add(orderedSpider);
                }
            });
        }
        order.setDate(orderDTO.getDate() != null ? orderDTO.getDate() : LocalDate.now());
        order.setPrice(orderDTO.getPrice() > 0 ? orderDTO.getPrice() : 0.0);
        order.setStatus(orderDTO.getStatus() != null ? orderDTO.getStatus() : OrderStatus.PENDING);
        return order;
    }
}