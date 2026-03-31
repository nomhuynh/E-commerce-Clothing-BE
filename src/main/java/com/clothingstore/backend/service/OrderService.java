package com.clothingstore.backend.service;

import com.clothingstore.backend.dto.order.OrderRequest;
import com.clothingstore.backend.dto.order.OrderResponse;

import java.util.List;

public interface OrderService {
    OrderResponse create(OrderRequest request);
    List<OrderResponse> getByUser(String userId);
    OrderResponse getByIdForUser(String orderId, String userId);
    OrderResponse cancelOrder(String orderId, String userId);
    OrderResponse requestReturn(String orderId, String userId, String reason);
    OrderResponse updateStatus(String orderId, String status);
}
