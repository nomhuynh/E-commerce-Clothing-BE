package com.clothingstore.backend.dto.wishlist;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WishlistResponse {
    private String id;
    private String userId;
    private String productId;
    private String productName;
}
