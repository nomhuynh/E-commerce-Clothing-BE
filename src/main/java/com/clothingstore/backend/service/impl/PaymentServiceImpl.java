package com.clothingstore.backend.service.impl;

import com.clothingstore.backend.dto.payment.PaymentResponse;
import com.clothingstore.backend.entity.Order;
import com.clothingstore.backend.entity.Payment;
import com.clothingstore.backend.entity.enums.OrderStatus;
import com.clothingstore.backend.entity.enums.PaymentStatus;
import com.clothingstore.backend.repository.OrderRepository;
import com.clothingstore.backend.repository.PaymentRepository;
import com.clothingstore.backend.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Override
    public PaymentResponse getByOrder(String orderId) {
        Payment p = paymentRepository.findByOrderId(orderId).orElseThrow(() -> new RuntimeException("Payment not found"));
        return toResponse(p);
    }

    @Override
    public PaymentResponse markSuccess(String orderId, String transactionCode) {
        Payment p = paymentRepository.findByOrderId(orderId).orElseThrow(() -> new RuntimeException("Payment not found"));
        boolean wasPending = p.getPaymentStatus() == PaymentStatus.PENDING;
        p.setPaymentStatus(PaymentStatus.SUCCESS);
        p.setTransactionCode(transactionCode);
        p.setPaymentTime(LocalDateTime.now());
        paymentRepository.save(p);

        // VNPay (and other gateways): sau khi thanh toán thành công, xác nhận đơn (PENDING → CONFIRMED).
        if (wasPending) {
            Order order = p.getOrder();
            if (order != null && order.getOrderStatus() == OrderStatus.PENDING) {
                order.setOrderStatus(OrderStatus.CONFIRMED);
                orderRepository.save(order);
            }
        }

        return toResponse(p);
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
