package com.clothingstore.backend.controller;

import com.clothingstore.backend.dto.ApiResponse;
import com.clothingstore.backend.dto.admin.AdminCouponListPayload;
import com.clothingstore.backend.dto.admin.AdminCouponUsageRowDto;
import com.clothingstore.backend.dto.admin.AdminCouponUsageUserDto;
import com.clothingstore.backend.dto.admin.AdminCouponUsagesPayload;
import com.clothingstore.backend.dto.coupon.CouponRequest;
import com.clothingstore.backend.dto.coupon.CouponResponse;
import com.clothingstore.backend.entity.Coupon;
import com.clothingstore.backend.entity.CouponUsage;
import com.clothingstore.backend.entity.User;
import com.clothingstore.backend.service.CouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/coupons")
@RequiredArgsConstructor
public class AdminCouponController {

    private final CouponService couponService;

    @PostMapping
    public ResponseEntity<ApiResponse<CouponResponse>> create(@Valid @RequestBody CouponRequest request) {
        Coupon created = couponService.create(toEntity(request));
        return ResponseEntity.ok(ApiResponse.success("Coupon created", toResponse(created)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CouponResponse>> update(@PathVariable String id, @Valid @RequestBody CouponRequest request) {
        Coupon existing = couponService.getById(id);
        Coupon entity = toEntity(request);
        entity.setId(id);
        if (request.getUsageCount() == null) {
            entity.setUsageCount(existing.getUsageCount());
        }
        Coupon updated = couponService.update(entity);
        return ResponseEntity.ok(ApiResponse.success("Coupon updated", toResponse(updated)));
    }

    /** FE gửi PATCH — cùng logic với PUT */
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<CouponResponse>> patch(@PathVariable String id, @Valid @RequestBody CouponRequest request) {
        return update(id, request);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CouponResponse>> getById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success("Coupon fetched", toResponse(couponService.getById(id))));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<AdminCouponListPayload>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) Boolean is_active) {
        Page<Coupon> pg = couponService.findPageForAdmin(page, limit, code, is_active);
        List<CouponResponse> rows = pg.getContent().stream().map(this::toResponse).toList();
        AdminCouponListPayload payload = AdminCouponListPayload.builder()
                .coupons(rows)
                .total(pg.getTotalElements())
                .page(page)
                .totalPages(Math.max(1, pg.getTotalPages()))
                .limit(limit)
                .build();
        return ResponseEntity.ok(ApiResponse.success("Coupons fetched", payload));
    }

    @GetMapping("/{id}/usages")
    public ResponseEntity<ApiResponse<AdminCouponUsagesPayload>> usages(
            @PathVariable String id,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        couponService.getById(id);
        Page<CouponUsage> pg = couponService.findUsagesForAdmin(id, page, limit);
        List<AdminCouponUsageRowDto> rows = pg.getContent().stream()
                .map((u) -> toUsageRow(u, id))
                .toList();
        AdminCouponUsagesPayload payload = AdminCouponUsagesPayload.builder()
                .usages(rows)
                .total(pg.getTotalElements())
                .page(page)
                .totalPages(Math.max(1, pg.getTotalPages()))
                .limit(limit)
                .build();
        return ResponseEntity.ok(ApiResponse.success("Coupon usages fetched", payload));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        couponService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Coupon deleted", null));
    }

    private AdminCouponUsageRowDto toUsageRow(CouponUsage u, String couponId) {
        User user = u.getUser();
        AdminCouponUsageUserDto userDto = AdminCouponUsageUserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
        return AdminCouponUsageRowDto.builder()
                .id(u.getId())
                .couponId(couponId)
                .userId(user.getId())
                .orderId(u.getOrderId())
                .discountAmount(u.getDiscountAmount())
                .usedAt(u.getUsedAt())
                .user(userDto)
                .build();
    }

    private Coupon toEntity(CouponRequest request) {
        Coupon coupon = new Coupon();
        coupon.setCode(request.getCode());
        coupon.setDescription(request.getDescription());
        coupon.setDiscountType(request.getDiscountType());
        coupon.setDiscountValue(request.getDiscountValue());
        coupon.setMinOrderValue(request.getMinOrderValue() != null ? request.getMinOrderValue() : BigDecimal.ZERO);
        coupon.setMaxDiscountAmount(request.getMaxDiscountAmount());
        coupon.setUsageLimit(request.getUsageLimit());
        coupon.setUsageCount(request.getUsageCount() != null ? request.getUsageCount() : 0);
        coupon.setPerUserLimit(request.getPerUserLimit() != null ? request.getPerUserLimit() : 1);
        coupon.setStartDate(request.getStartDate());
        coupon.setEndDate(request.getEndDate());
        coupon.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        return coupon;
    }

    private CouponResponse toResponse(Coupon coupon) {
        return CouponResponse.builder()
                .id(coupon.getId())
                .code(coupon.getCode())
                .description(coupon.getDescription())
                .discountType(coupon.getDiscountType())
                .discountValue(coupon.getDiscountValue())
                .minOrderValue(coupon.getMinOrderValue())
                .maxDiscountAmount(coupon.getMaxDiscountAmount())
                .usageLimit(coupon.getUsageLimit())
                .usageCount(coupon.getUsageCount())
                .perUserLimit(coupon.getPerUserLimit())
                .startDate(coupon.getStartDate())
                .endDate(coupon.getEndDate())
                .isActive(coupon.getIsActive())
                .createdAt(coupon.getCreatedAt())
                .updatedAt(coupon.getUpdatedAt())
                .build();
    }
}
