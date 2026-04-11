package com.clothingstore.backend.dto.cart;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class CartResponse {
    private String cartId;
    private String userId;
    private String couponId;
    /** Sum of line totals before coupon. */
    private BigDecimal subtotal;
    private BigDecimal discount;
    private BigDecimal total;
    private CouponSummary coupon;
    private List<CartItemResponse> items;

    @Data
    @Builder
    public static class CouponSummary {
        private String couponId;
        private String code;
        /** "PERCENTAGE" | "FIXED" for storefront */
        private String discountType;
        private BigDecimal discountValue;
    }

    @Data
    @Builder
    public static class CartItemResponse {
        private String variantId;
        private String sku;
        private Integer quantity;
        private String productName;
        /** Unit price used for line total (variant price or product base price). */
        private BigDecimal unitPrice;
        private String colorName;
        private String sizeName;
        /** Variant image, else first product image URL. */
        private String imageUrl;
    }
}
