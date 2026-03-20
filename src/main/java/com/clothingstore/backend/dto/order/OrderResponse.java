package com.clothingstore.backend.dto.order;

import com.clothingstore.backend.entity.enums.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class OrderResponse {
    private String id;
    private String orderCode;
    private String userId;
    private BigDecimal totalAmount;
    private OrderStatus status;
}
