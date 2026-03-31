package com.clothingstore.backend.dto.cart;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ApplyCouponRequest {
    @NotBlank
    private String userId;

    @NotBlank
    private String code;
}
