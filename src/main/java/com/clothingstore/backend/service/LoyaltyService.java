package com.clothingstore.backend.service;

import com.clothingstore.backend.entity.Order;
import com.clothingstore.backend.entity.enums.OrderStatus;

public interface LoyaltyService {

    /**
     * Gọi sau khi đơn được lưu với trạng thái mới. Cộng điểm khi vào DELIVERED,
     * hoàn điểm khi rời DELIVERED (trả hàng, hủy sau giao, v.v.).
     */
    void onOrderStatusChanged(Order order, OrderStatus previousStatus, OrderStatus newStatus);
}
