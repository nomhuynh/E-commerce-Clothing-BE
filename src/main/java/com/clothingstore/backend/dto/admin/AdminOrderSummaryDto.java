package com.clothingstore.backend.dto.admin;

import com.clothingstore.backend.entity.enums.OrderStatus;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AdminOrderSummaryDto {
    private String orderId;
    private String orderCode;
    private String customerName;
    private LocalDateTime createdAt;
    private BigDecimal totalAmount;
    private OrderStatus orderStatus;
    private long itemCount;
}
