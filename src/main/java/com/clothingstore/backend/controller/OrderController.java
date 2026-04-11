package com.clothingstore.backend.controller;

import com.clothingstore.backend.dto.ApiResponse;
import com.clothingstore.backend.dto.order.OrderRequest;
import com.clothingstore.backend.dto.order.OrderResponse;
import com.clothingstore.backend.dto.order.OrderReturnRequest;
import com.clothingstore.backend.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Order endpoints")
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "Create order")
    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponse>> create(
            Authentication authentication,
            @Valid @RequestBody OrderRequest request) {
        // Always bind order to the authenticated user (token), not client-supplied userId — avoids mismatch with VNPay create-url.
        request.setUserId(requireUserId(authentication));
        return ResponseEntity.ok(ApiResponse.success("Order created", orderService.create(request)));
    }

    private static String requireUserId(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof String)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        return (String) authentication.getPrincipal();
    }

    @Operation(summary = "Get my orders")
    @GetMapping("/my-orders")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getMyOrders(@RequestParam String userId) {
        return ResponseEntity.ok(ApiResponse.success("Orders fetched", orderService.getByUser(userId)));
    }

    @Operation(summary = "Get my order detail")
    @GetMapping("/my-orders/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponse>> getMyOrderDetail(
            @PathVariable String orderId,
            @RequestParam String userId) {
        return ResponseEntity.ok(ApiResponse.success("Order fetched", orderService.getByIdForUser(orderId, userId)));
    }

    @Operation(summary = "Cancel my order")
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(
            @PathVariable String orderId,
            @RequestParam String userId) {
        return ResponseEntity.ok(ApiResponse.success("Order cancelled successfully", orderService.cancelOrder(orderId, userId)));
    }

    @Operation(summary = "Request order return")
    @PostMapping("/{orderId}/return-request")
    public ResponseEntity<ApiResponse<OrderResponse>> requestReturn(
            @PathVariable String orderId,
            @RequestParam String userId,
            @Valid @RequestBody(required = false) OrderReturnRequest request) {
        String reason = request != null ? request.getReason() : null;
        return ResponseEntity.ok(ApiResponse.success("Return requested", orderService.requestReturn(orderId, userId, reason)));
    }
}
