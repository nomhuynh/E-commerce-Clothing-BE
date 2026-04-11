package com.clothingstore.backend.dto.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AdminCouponUsageRowDto {
    @JsonProperty("usage_id")
    private String id;
    private String couponId;
    private String userId;
    private String orderId;
    private BigDecimal discountAmount;
    private LocalDateTime usedAt;
    private AdminCouponUsageUserDto user;
}
