package com.clothingstore.backend.service;

import com.clothingstore.backend.dto.payment.PaymentResponse;

public interface PaymentService {
    PaymentResponse getByOrder(String orderId);
    PaymentResponse markSuccess(String orderId, String transactionCode);
}
