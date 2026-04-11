package com.clothingstore.backend.dto.cart;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ApplyCouponRequest {
    /** Optional when Authorization bearer token identifies the user. */
    private String userId;

    @NotBlank
    private String code;
}
