package com.clothingstore.backend.dto.coupon;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CouponValidateRequest {
    @NotBlank
    private String code;

    @JsonProperty("total_amount")
    private BigDecimal totalAmount;
}
