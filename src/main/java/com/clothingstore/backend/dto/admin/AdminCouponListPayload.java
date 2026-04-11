package com.clothingstore.backend.dto.admin;

import com.clothingstore.backend.dto.coupon.CouponResponse;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AdminCouponListPayload {
    private List<CouponResponse> coupons;
    private long total;
    private int page;
    private int totalPages;
    private int limit;
}
