package com.clothingstore.backend.controller;

import com.clothingstore.backend.dto.ApiResponse;
import com.clothingstore.backend.dto.payment.PaymentResponse;
import com.clothingstore.backend.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getByOrder(@PathVariable String orderId) {
        return ResponseEntity.ok(ApiResponse.success("Payment fetched", paymentService.getByOrder(orderId)));
    }

    @PostMapping("/{orderId}/success")
    public ResponseEntity<ApiResponse<PaymentResponse>> markSuccess(@PathVariable String orderId,
                                                                     @RequestParam String transactionCode) {
        return ResponseEntity.ok(ApiResponse.success("Payment updated", paymentService.markSuccess(orderId, transactionCode)));
    }
}
