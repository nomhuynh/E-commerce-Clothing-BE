package com.clothingstore.backend.dto.wishlist;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class WishlistRequest {
    @NotBlank
    private String userId;
    @NotBlank
    private String productId;
}
