package com.clothingstore.backend.service;

import com.clothingstore.backend.dto.admin.AdminOrderDetailPayload;
import com.clothingstore.backend.dto.admin.AdminOrderListPayload;
import com.clothingstore.backend.dto.order.OrderResponse;

public interface AdminOrderService {

    AdminOrderListPayload listOrders(int page, int limit, String status, String startDate, String endDate);

    AdminOrderDetailPayload getOrderDetail(String orderId);

    OrderResponse updateStatus(String orderId, String status);
}
