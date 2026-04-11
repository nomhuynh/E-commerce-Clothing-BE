package com.clothingstore.backend.controller;

import com.clothingstore.backend.dto.ApiResponse;
import com.clothingstore.backend.dto.payment.PaymentResponse;
import com.clothingstore.backend.service.PaymentService;
import com.clothingstore.backend.service.VnpayService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final VnpayService vnpayService;

    @Value("${app.front-end-url:http://localhost:5273}")
    private String frontendUrl;

    /**
     * Creates a gateway payment URL (VNPay). COD orders do not use this endpoint.
     */
    @PostMapping("/create-url")
    public ResponseEntity<ApiResponse<Map<String, String>>> createPaymentUrl(
            @RequestBody Map<String, Object> body,
            HttpServletRequest request) {
        Object rawOrderId = body.get("orderId") != null ? body.get("orderId") : body.get("order_id");
        String orderId = rawOrderId != null ? String.valueOf(rawOrderId).trim() : "";
        Object rawPm = body.get("paymentMethod") != null ? body.get("paymentMethod") : body.get("payment_method");
        String paymentMethod = rawPm != null ? String.valueOf(rawPm).trim() : "";

        if (!StringUtils.hasText(orderId)) {
            return ResponseEntity.badRequest().body(ApiResponse.error("orderId is required"));
        }

        Map<String, String> data = new HashMap<>();
        data.put("orderId", orderId);
        data.put("paymentUrl", "");
        data.put("payUrl", "");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof String)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("Sign in required"));
        }
        String userId = (String) auth.getPrincipal();

        if (!"VNPAY".equalsIgnoreCase(paymentMethod)) {
            return ResponseEntity.ok(ApiResponse.success("Not a VNPAY request — use COD or pass paymentMethod=VNPAY", data));
        }

        String ip = clientIp(request);
        String url = vnpayService.createPaymentUrl(orderId, ip, userId);
        data.put("paymentUrl", url);
        data.put("payUrl", url);
        return ResponseEntity.ok(ApiResponse.success("Payment URL created", data));
    }

    /**
     * VNPay redirects the browser here after payment (GET). Signature is verified; then redirect to SPA.
     */
    @GetMapping("/callback/vnpay")
    public RedirectView vnpayCallback(HttpServletRequest request) {
        Map<String, String> fields = new HashMap<>();
        request.getParameterMap().forEach((k, v) -> {
            if (v != null && v.length > 0) {
                fields.put(k, v[0]);
            }
        });

        String txnRef = fields.getOrDefault("vnp_TxnRef", "");
        String base = frontendUrl.replaceAll("/$", "") + "/orders/" + txnRef;

        if (!vnpayService.verifyReturnParams(fields)) {
            return new RedirectView(base + "?vnpay=invalid");
        }

        String responseCode = fields.get("vnp_ResponseCode");
        if ("00".equals(responseCode)) {
            String transNo = fields.get("vnp_TransactionNo");
            paymentService.markSuccess(txnRef, transNo != null ? transNo : "");
            return new RedirectView(base + "?vnpay=success");
        }
        String code = responseCode != null ? responseCode : "";
        return new RedirectView(base + "?vnpay=fail&code=" + code);
    }

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

    private static String clientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(xff)) {
            return xff.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
