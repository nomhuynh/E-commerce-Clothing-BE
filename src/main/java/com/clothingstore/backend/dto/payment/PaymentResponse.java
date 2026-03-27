package com.clothingstore.backend.dto.payment;

import com.clothingstore.backend.entity.enums.PaymentMethod;
import com.clothingstore.backend.entity.enums.PaymentStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentResponse {
    private String paymentId;
    private String orderId;
    private PaymentMethod method;
    private PaymentStatus status;
    private String transactionCode;
}
