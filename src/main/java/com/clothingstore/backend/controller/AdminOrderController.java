package com.clothingstore.backend.controller;

import com.clothingstore.backend.dto.ApiResponse;
import com.clothingstore.backend.dto.admin.AdminOrderDetailPayload;
import com.clothingstore.backend.dto.admin.AdminOrderListPayload;
import com.clothingstore.backend.dto.admin.AdminOrderStatusRequest;
import com.clothingstore.backend.dto.order.OrderResponse;
import com.clothingstore.backend.service.AdminOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/orders")
@RequiredArgsConstructor
@Tag(name = "Admin Orders", description = "Quản lý đơn hàng (ADMIN)")
public class AdminOrderController {

    private final AdminOrderService adminOrderService;

    @Operation(summary = "Danh sách đơn (phân trang, lọc)")
    @GetMapping
    public ResponseEntity<ApiResponse<AdminOrderListPayload>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        AdminOrderListPayload data = adminOrderService.listOrders(page, limit, status, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("Orders fetched", data));
    }

    @Operation(summary = "Chi tiết đơn")
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<AdminOrderDetailPayload>> getDetail(@PathVariable String orderId) {
        return ResponseEntity.ok(ApiResponse.success("Order fetched", adminOrderService.getOrderDetail(orderId)));
    }

    @Operation(summary = "Cập nhật trạng thái đơn")
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateStatus(
            @PathVariable String orderId,
            @Valid @RequestBody AdminOrderStatusRequest request) {
        OrderResponse updated = adminOrderService.updateStatus(orderId, request.getStatus().trim());
        return ResponseEntity.ok(ApiResponse.success("Order status updated", updated));
    }
}
