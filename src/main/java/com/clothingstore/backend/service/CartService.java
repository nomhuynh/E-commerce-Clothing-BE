package com.clothingstore.backend.service;

import com.clothingstore.backend.dto.cart.CartRequest;
import com.clothingstore.backend.dto.cart.CartResponse;

public interface CartService {
    CartResponse getCart(String userId);
    CartResponse addItem(CartRequest request);
    CartResponse updateItem(CartRequest request);
    CartResponse removeItem(String userId, String variantId);
}
