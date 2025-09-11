package com.WMS_spiders_wholesale_system.repository;

import com.WMS_spiders_wholesale_system.entity.Order;
import com.WMS_spiders_wholesale_system.entity.OrderStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    // Metoda dostarczona przez JpaRepository (wcześniej findByOrderId).
    // Użyj standardowej metody, która jest ulepszona przez @EntityGraph poniżej.
    @Override
    @EntityGraph(attributePaths = {"customer", "orderedSpiders"})
    Optional<Order> findById(UUID id);

    // Znajdź wszystkie zamówienia o danym statusie.
    List<Order> findByStatus(OrderStatus orderStatus);

    // Zaktualizuj status zamówienia na podstawie jego ID.
    // Zmieniono 'o.orderId' na 'o.id'
    @Modifying
    @Transactional
    @Query("UPDATE Order o SET o.status = :newStatus WHERE o.id = :orderId")
    void updateOrderStatus(@Param("orderId") UUID orderId, @Param("newStatus") OrderStatus newStatus);

    // Ta metoda jest zbędna, ponieważ nadpisana metoda findAll z @EntityGraph
    // zapewnia już to samo zachowanie.
    // @Query("SELECT o FROM Order o JOIN FETCH o.customer LEFT JOIN FETCH o.orderedSpiders")
    // Page<Order> findAllWithDetails(Pageable pageable);

    // Nadpisuje standardową metodę findAll, aby zoptymalizować zapytania.
    // Ładuje powiązane encje (klienta i zamówione pająki) w jednym zapytaniu.
    @Override
    @EntityGraph(attributePaths = {"customer", "orderedSpiders"})
    Page<Order> findAll(Pageable pageable);
}