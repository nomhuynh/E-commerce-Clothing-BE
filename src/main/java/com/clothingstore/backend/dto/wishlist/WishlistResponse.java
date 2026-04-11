package com.clothingstore.backend.dto.wishlist;

import com.clothingstore.backend.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WishlistResponse {
    private String id;
    private String userId;
    private String productId;
    private String productName;
    /** Full product graph for storefront (images, variants, {@code active_promotion}). */
    private Product product;
}
