package com.clothingstore.backend.controller;

import com.clothingstore.backend.dto.ApiResponse;
import com.clothingstore.backend.dto.payment.PaymentResponse;
import com.clothingstore.backend.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * FE expects a payment URL (e.g. VNPay). Wire gateway integration here; placeholder until then.
     */
    @PostMapping("/create-url")
    public ResponseEntity<ApiResponse<Map<String, String>>> createPaymentUrl(@RequestBody Map<String, Object> body) {
        String orderId = body.get("orderId") != null ? String.valueOf(body.get("orderId")) : "";
        Map<String, String> data = new HashMap<>();
        data.put("paymentUrl", "");
        data.put("payUrl", "");
        data.put("orderId", orderId);
        return ResponseEntity.ok(ApiResponse.success("Payment URL placeholder — integrate VNPay/MoMo here", data));
    }

    /** Alias for FE; returns current payment row for the order (same as getPaymentStatus). */
    @GetMapping("/verify/{orderId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> verify(@PathVariable String orderId) {
        return ResponseEntity.ok(ApiResponse.success("Payment fetched", paymentService.getByOrder(orderId)));
    }

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
