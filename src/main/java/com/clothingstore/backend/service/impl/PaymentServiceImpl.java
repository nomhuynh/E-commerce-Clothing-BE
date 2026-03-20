package com.clothingstore.backend.service.impl;

import com.clothingstore.backend.dto.payment.PaymentResponse;
import com.clothingstore.backend.entity.Payment;
import com.clothingstore.backend.entity.enums.PaymentStatus;
import com.clothingstore.backend.repository.PaymentRepository;
import com.clothingstore.backend.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    @Override
    public PaymentResponse getByOrder(String orderId) {
        Payment p = paymentRepository.findByOrderId(orderId).orElseThrow(() -> new RuntimeException("Payment not found"));
        return toResponse(p);
    }

    @Override
    public PaymentResponse markSuccess(String orderId, String transactionCode) {
        Payment p = paymentRepository.findByOrderId(orderId).orElseThrow(() -> new RuntimeException("Payment not found"));
        p.setPaymentStatus(PaymentStatus.SUCCESS);
        p.setTransactionCode(transactionCode);
        p.setPaymentTime(LocalDateTime.now());
        return toResponse(paymentRepository.save(p));
    }

    private PaymentResponse toResponse(Payment p) {
        return PaymentResponse.builder()
                .paymentId(p.getId())
                .orderId(p.getOrder().getId())
                .method(p.getPaymentMethod())
                .status(p.getPaymentStatus())
                .transactionCode(p.getTransactionCode())
                .build();
    }
}
