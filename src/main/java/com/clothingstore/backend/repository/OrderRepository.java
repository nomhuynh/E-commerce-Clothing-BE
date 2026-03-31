package com.clothingstore.backend.repository;

import com.clothingstore.backend.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import com.clothingstore.backend.entity.enums.OrderStatus;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, String> {
    List<Order> findByUserIdOrderByCreatedAtDesc(String userId);
    Optional<Order> findByIdAndUserId(String orderId, String userId);
    List<Order> findByUserIdAndOrderStatusOrderByCreatedAtDesc(String userId, OrderStatus status);
}
