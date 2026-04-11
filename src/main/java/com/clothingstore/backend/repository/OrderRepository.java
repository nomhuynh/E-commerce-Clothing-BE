package com.clothingstore.backend.repository;

import com.clothingstore.backend.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.clothingstore.backend.entity.enums.OrderStatus;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, String>, JpaSpecificationExecutor<Order> {
    List<Order> findByUserIdOrderByCreatedAtDesc(String userId);

    /** Same ownership rule as {@link #findByIdAndUserIdWithItems}; explicit JPQL avoids derived-query ambiguity. */
    @Query("SELECT o FROM Order o WHERE o.id = :orderId AND o.user.id = :userId")
    Optional<Order> findByIdAndUserId(@Param("orderId") String orderId, @Param("userId") String userId);

    @Query("SELECT DISTINCT o FROM Order o "
            + "LEFT JOIN FETCH o.items i "
            + "LEFT JOIN FETCH i.variant v "
            + "LEFT JOIN FETCH v.product p "
            + "WHERE o.id = :orderId AND o.user.id = :userId")
    Optional<Order> findByIdAndUserIdWithItems(@Param("orderId") String orderId, @Param("userId") String userId);

    @Query("SELECT DISTINCT o FROM Order o "
            + "LEFT JOIN FETCH o.items i "
            + "LEFT JOIN FETCH i.variant v "
            + "LEFT JOIN FETCH v.product p "
            + "WHERE o.id = :orderId")
    Optional<Order> findByIdWithItems(@Param("orderId") String orderId);

    List<Order> findByUserIdAndOrderStatusOrderByCreatedAtDesc(String userId, OrderStatus status);
}

