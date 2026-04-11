package com.clothingstore.backend.dto.order;

import com.clothingstore.backend.entity.enums.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderResponse {
    private String id;
    private String orderCode;
    private String userId;
    private String customerName;
    private String customerPhone;
    private String shippingAddress;
    private String note;
    private BigDecimal subtotalAmount;
    private BigDecimal shippingFee;
    private BigDecimal discountAmount;
    private BigDecimal totalAmount;
    private OrderStatus status;
    /** Payment method code, e.g. COD, VNPAY */
    private String paymentMethod;
    private LocalDateTime createdAt;
    private List<OrderItemResponse> items;
}
