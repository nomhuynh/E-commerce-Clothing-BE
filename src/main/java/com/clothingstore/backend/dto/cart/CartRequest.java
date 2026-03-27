package com.clothingstore.backend.dto.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CartRequest {
    @NotBlank
    private String userId;
    @NotBlank
    private String variantId;
    @Min(1)
    private Integer quantity;
}
