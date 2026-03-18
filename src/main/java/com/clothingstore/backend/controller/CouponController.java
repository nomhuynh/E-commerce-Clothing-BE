package com.clothingstore.backend.controller;

import com.clothingstore.backend.dto.ApiResponse;
import com.clothingstore.backend.dto.coupon.CouponRequest;
import com.clothingstore.backend.dto.coupon.CouponResponse;
import com.clothingstore.backend.entity.Coupon;
import com.clothingstore.backend.service.CouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @PostMapping
    public ResponseEntity<ApiResponse<CouponResponse>> create(@Valid @RequestBody CouponRequest request) {
        Coupon created = couponService.create(toEntity(request));
        return ResponseEntity.ok(ApiResponse.success("Coupon created", toResponse(created)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CouponResponse>> update(@PathVariable String id, @Valid @RequestBody CouponRequest request) {
        Coupon entity = toEntity(request);
        entity.setId(id);
        Coupon updated = couponService.update(entity);
        return ResponseEntity.ok(ApiResponse.success("Coupon updated", toResponse(updated)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CouponResponse>> getById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success("Coupon fetched", toResponse(couponService.getById(id))));
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponse<CouponResponse>> getByCode(@PathVariable String code) {
        return ResponseEntity.ok(ApiResponse.success("Coupon fetched", toResponse(couponService.getByCode(code))));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CouponResponse>>> getAll() {
        List<CouponResponse> data = couponService.getAll().stream().map(this::toResponse).toList();
        return ResponseEntity.ok(ApiResponse.success("Coupons fetched", data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        couponService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Coupon deleted", null));
    }

    private Coupon toEntity(CouponRequest request) {
        Coupon coupon = new Coupon();
        coupon.setCode(request.getCode());
        coupon.setDescription(request.getDescription());
        coupon.setDiscountType(request.getDiscountType());
        coupon.setDiscountValue(request.getDiscountValue());
        if (request.getMinOrderValue() != null) {
            coupon.setMinOrderValue(request.getMinOrderValue());
        }
        coupon.setMaxDiscountAmount(request.getMaxDiscountAmount());
        if (request.getUsageLimit() != null) {
            coupon.setUsageLimit(request.getUsageLimit());
        }
        if (request.getUsageCount() != null) {
            coupon.setUsageCount(request.getUsageCount());
        }
        if (request.getPerUserLimit() != null) {
            coupon.setPerUserLimit(request.getPerUserLimit());
        }
        coupon.setStartDate(request.getStartDate());
        coupon.setEndDate(request.getEndDate());
        if (request.getIsActive() != null) {
            coupon.setIsActive(request.getIsActive());
        }
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
