package com.clothingstore.backend.service;

import com.clothingstore.backend.entity.Order;
import com.clothingstore.backend.entity.Payment;
import com.clothingstore.backend.entity.enums.PaymentMethod;
import com.clothingstore.backend.entity.enums.PaymentStatus;
import com.clothingstore.backend.repository.PaymentRepository;
import com.clothingstore.backend.service.impl.PaymentServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock private PaymentRepository paymentRepository;
    @InjectMocks private PaymentServiceImpl paymentService;

    @Test
    void getByOrder_ShouldReturnPayment() {
        Order order = new Order();
        order.setId("o1");

        Payment p = new Payment();
        p.setId("p1");
        p.setOrder(order);
        p.setPaymentMethod(PaymentMethod.COD);
        p.setPaymentStatus(PaymentStatus.SUCCESS);
        p.setAmount(BigDecimal.TEN);

        when(paymentRepository.findByOrderId("o1")).thenReturn(Optional.of(p));

        var result = paymentService.getByOrder("o1");
        assertEquals("p1", result.getPaymentId());
    }
}
