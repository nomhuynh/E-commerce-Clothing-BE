package com.clothingstore.backend.controller;

import com.clothingstore.backend.dto.ApiResponse;
import com.clothingstore.backend.repository.OrderRepository;
import com.clothingstore.backend.repository.PaymentRepository;
import com.clothingstore.backend.repository.ProductRepository;
import com.clothingstore.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @GetMapping("/overview")
    public ResponseEntity<ApiResponse<Map<String, Object>>> overview() {
        Map<String, Object> data = Map.of(
                "totalOrders", orderRepository.count(),
                "totalPayments", paymentRepository.count(),
                "totalUsers", userRepository.count(),
                "totalProducts", productRepository.count()
        );
        return ResponseEntity.ok(ApiResponse.success("Dashboard overview", data));
    }
}
