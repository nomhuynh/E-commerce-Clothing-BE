package com.clothingstore.backend.service;

import com.clothingstore.backend.dto.order.OrderRequest;
import com.clothingstore.backend.dto.order.OrderResponse;

import java.util.List;

public interface OrderService {
    OrderResponse create(OrderRequest request);
    List<OrderResponse> getByUser(String userId);
    OrderResponse updateStatus(String orderId, String status);
}
