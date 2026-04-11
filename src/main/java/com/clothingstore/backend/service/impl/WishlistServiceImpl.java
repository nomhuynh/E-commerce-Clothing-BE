package com.clothingstore.backend.service.impl;

import com.clothingstore.backend.dto.wishlist.WishlistRequest;
import com.clothingstore.backend.dto.wishlist.WishlistResponse;
import com.clothingstore.backend.entity.Product;
import com.clothingstore.backend.entity.User;
import com.clothingstore.backend.entity.Wishlist;
import com.clothingstore.backend.repository.ProductRepository;
import com.clothingstore.backend.repository.UserRepository;
import com.clothingstore.backend.repository.WishlistRepository;
import com.clothingstore.backend.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Override
    public WishlistResponse add(WishlistRequest request) {
        wishlistRepository.findByUserIdAndProductId(request.getUserId(), request.getProductId())
                .ifPresent(w -> { throw new RuntimeException("Product already in wishlist"); });

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Wishlist wishlist = Wishlist.builder().user(user).product(product).build();
        return toResponse(wishlistRepository.save(wishlist));
    }

    @Override
    public void remove(String userId, String productId) {
        Wishlist wishlist = wishlistRepository.findByUserIdAndProductId(userId, productId)
                .orElseThrow(() -> new RuntimeException("Wishlist item not found"));
        wishlistRepository.delete(wishlist);
    }

    @Override
    public List<WishlistResponse> getByUser(String userId) {
        return wishlistRepository.findByUserId(userId).stream().map(this::toResponse).toList();
    }

    @Override
    public boolean isInWishlist(String userId, String productId) {
        return wishlistRepository.findByUserIdAndProductId(userId, productId).isPresent();
    }

    private WishlistResponse toResponse(Wishlist wishlist) {
        return WishlistResponse.builder()
                .id(wishlist.getId())
                .userId(wishlist.getUser().getId())
                .productId(wishlist.getProduct().getId())
                .productName(wishlist.getProduct().getName())
                .build();
    }
}
