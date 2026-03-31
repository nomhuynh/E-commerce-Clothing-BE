package com.clothingstore.backend.service;

import com.clothingstore.backend.dto.cart.*;

public interface CartService {
    CartResponse getCart(String userId);
    CartResponse addItem(AddCartItemRequest request);
    CartResponse updateItem(String userId, String variantId, UpdateCartItemRequest request);
    CartResponse removeItem(String userId, String variantId);
    CartResponse applyCoupon(ApplyCouponRequest request);
    CartResponse removeCoupon(String userId);
    CartResponse clearCart(String userId);
}
