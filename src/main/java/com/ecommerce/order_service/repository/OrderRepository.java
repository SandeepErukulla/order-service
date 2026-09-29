package com.ecommerce.order_service.repository;

import com.ecommerce.order_service.entity.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // Fetch order + items in 1 query using JOIN FETCH
    @Query("SELECT DISTINCT o FROM Order o JOIN FETCH o.items WHERE o.orderNumber = :orderNumber")
    Optional<Order> findByOrderNumberWithItems(@Param("orderNumber") String orderNumber);

    // Fetch orders + items in 1 query using @EntityGraph with optional item name filtering
    @EntityGraph(attributePaths = {"items"})
    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN o.items i WHERE :itemName IS NULL OR i.skuCode LIKE %:itemName%")
    List<Order> findAllWithItems(@Param("itemName") String itemName);
}