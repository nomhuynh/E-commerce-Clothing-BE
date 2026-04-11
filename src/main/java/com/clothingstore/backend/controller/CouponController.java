package com.clothingstore.backend.controller;

import com.clothingstore.backend.dto.ApiResponse;
import com.clothingstore.backend.dto.coupon.CouponRequest;
import com.clothingstore.backend.dto.coupon.CouponResponse;
import com.clothingstore.backend.dto.coupon.CouponValidateRequest;
import com.clothingstore.backend.entity.Coupon;
import com.clothingstore.backend.entity.enums.DiscountType;
import com.clothingstore.backend.service.CouponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @PostMapping("/validate")
    public ResponseEntity<ApiResponse<Map<String, Object>>> validate(@Valid @RequestBody CouponValidateRequest req) {
        Coupon c;
        try {
            c = couponService.getByCode(req.getCode().trim());
        } catch (RuntimeException ex) {
            return ResponseEntity.ok(ApiResponse.success("Invalid coupon", invalidCouponResponse()));
        }
        LocalDateTime now = LocalDateTime.now();
        if (Boolean.FALSE.equals(c.getIsActive()) || now.isBefore(c.getStartDate()) || now.isAfter(c.getEndDate())) {
            return ResponseEntity.ok(ApiResponse.success("Coupon not active", invalidCouponResponse()));
        }
        BigDecimal subtotal = req.getTotalAmount() != null ? req.getTotalAmount() : BigDecimal.ZERO;
        BigDecimal minOrder = c.getMinOrderValue() != null ? c.getMinOrderValue() : BigDecimal.ZERO;
        if (subtotal.compareTo(minOrder) < 0) {
            Map<String, Object> m = new HashMap<>(invalidCouponResponse());
            m.put("message", "Order total below minimum for this coupon");
            return ResponseEntity.ok(ApiResponse.success("Below minimum", m));
        }
        BigDecimal discount;
        if (c.getDiscountType() == DiscountType.percentage) {
            discount = subtotal.multiply(c.getDiscountValue()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        } else {
            discount = c.getDiscountValue();
        }
        if (c.getMaxDiscountAmount() != null && discount.compareTo(c.getMaxDiscountAmount()) > 0) {
            discount = c.getMaxDiscountAmount();
        }
        if (discount.compareTo(subtotal) > 0) {
            discount = subtotal;
        }
        Map<String, Object> ok = new HashMap<>();
        ok.put("valid", true);
        ok.put("discount_amount", discount);
        ok.put("coupon", toResponse(c));
        return ResponseEntity.ok(ApiResponse.success("Coupon applicable", ok));
    }

    private static Map<String, Object> invalidCouponResponse() {
        Map<String, Object> m = new HashMap<>();
        m.put("valid", false);
        m.put("discount_amount", BigDecimal.ZERO);
        return m;
    }

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
