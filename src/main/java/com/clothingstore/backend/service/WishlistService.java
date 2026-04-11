package com.clothingstore.backend.service;

import com.clothingstore.backend.dto.wishlist.WishlistRequest;
import com.clothingstore.backend.dto.wishlist.WishlistResponse;

import java.util.List;

public interface WishlistService {
    WishlistResponse add(WishlistRequest request);

    void remove(String userId, String productId);

    List<WishlistResponse> getByUser(String userId);

    boolean isInWishlist(String userId, String productId);
}
