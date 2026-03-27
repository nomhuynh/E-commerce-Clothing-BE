package com.clothingstore.backend.dto.cart;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CartResponse {
    private String cartId;
    private String userId;
    private String couponId;
    private List<CartItemResponse> items;

    @Data
    @Builder
    public static class CartItemResponse {
        private String variantId;
        private String sku;
        private Integer quantity;
    }
}
