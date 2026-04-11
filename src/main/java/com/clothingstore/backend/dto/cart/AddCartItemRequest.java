package com.clothingstore.backend.dto.cart;

import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddCartItemRequest {
    /** Optional when Authorization bearer token identifies the user. */
    private String userId;

    @NotBlank
    @JsonAlias({"variant_id", "variantId"})
    private String variantId;

    @Min(1)
    private Integer quantity = 1;
}
