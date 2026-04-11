package com.clothingstore.backend.dto.admin;

import com.clothingstore.backend.entity.enums.OrderStatus;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AdminOrderDetailDto {
    private String orderId;
    private String orderCode;
    private String userId;
    private OrderStatus orderStatus;
    private String customerName;
    private String customerPhone;
    private String shippingAddress;
    private String note;
    private BigDecimal subtotalAmount;
    private BigDecimal shippingFee;
    private BigDecimal discountAmount;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    private List<AdminOrderItemRowDto> items;
    private List<AdminPaymentRowDto> payments;
}
