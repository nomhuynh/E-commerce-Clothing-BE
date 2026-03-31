package com.clothingstore.backend.service.impl;

import com.clothingstore.backend.dto.cart.*;
import com.clothingstore.backend.entity.*;
import com.clothingstore.backend.repository.*;
import com.clothingstore.backend.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductVariantRepository productVariantRepository;
    private final CouponRepository couponRepository;

    @Override
    public CartResponse getCart(String userId) {
        Cart cart = cartRepository.findByUserId(userId).orElseGet(() -> {
            User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
            return cartRepository.save(Cart.builder().user(user).build());
        });
        return toResponse(cart);
    }

    @Override
    public CartResponse addItem(AddCartItemRequest request) {
        Cart cart = cartRepository.findByUserId(request.getUserId())
                .orElseGet(() -> cartRepository.save(Cart.builder()
                        .user(userRepository.findById(request.getUserId()).orElseThrow(() -> new RuntimeException("User not found")))
                        .build()));

        ProductVariant variant = productVariantRepository.findById(request.getVariantId())
                .orElseThrow(() -> new RuntimeException("Variant not found"));

        CartItem item = cartItemRepository.findByCartIdAndVariantId(cart.getId(), variant.getId())
                .orElseGet(() -> CartItem.builder().cart(cart).variant(variant).quantity(0).build());

        item.setQuantity(item.getQuantity() + request.getQuantity());
        cartItemRepository.save(item);

        return getCart(request.getUserId());
    }

    @Override
    public CartResponse updateItem(String userId, String variantId, UpdateCartItemRequest request) {
        Cart cart = cartRepository.findByUserId(userId).orElseThrow(() -> new RuntimeException("Cart not found"));
        CartItem item = cartItemRepository.findByCartIdAndVariantId(cart.getId(), variantId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        item.setQuantity(request.getQuantity());
        cartItemRepository.save(item);
        return getCart(userId);
    }

    @Override
    public CartResponse removeItem(String userId, String variantId) {
        Cart cart = cartRepository.findByUserId(userId).orElseThrow(() -> new RuntimeException("Cart not found"));
        CartItem item = cartItemRepository.findByCartIdAndVariantId(cart.getId(), variantId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        cartItemRepository.delete(item);
        return getCart(userId);
    }

    @Override
    public CartResponse applyCoupon(ApplyCouponRequest request) {
        Cart cart = cartRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        Coupon coupon = couponRepository.findByCode(request.getCode())
                .orElseThrow(() -> new RuntimeException("Coupon not found"));

        if (!Boolean.TRUE.equals(coupon.getIsActive()) || coupon.getEndDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Coupon is not valid");
        }

        cart.setCoupon(coupon);
        cartRepository.save(cart);
        return getCart(request.getUserId());
    }

    @Override
    public CartResponse removeCoupon(String userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        cart.setCoupon(null);
        cartRepository.save(cart);
        return getCart(userId);
    }

    @Override
    public CartResponse clearCart(String userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        cartItemRepository.deleteByCartId(cart.getId());
        return getCart(userId);
    }

    private CartResponse toResponse(Cart cart) {
        return CartResponse.builder()
                .cartId(cart.getId())
                .userId(cart.getUser() != null ? cart.getUser().getId() : null)
                .couponId(cart.getCoupon() != null ? cart.getCoupon().getId() : null)
                .items(cart.getItems() == null ? new ArrayList<>() : cart.getItems().stream().map(i -> CartResponse.CartItemResponse.builder()
                        .variantId(i.getVariant().getId())
                        .sku(i.getVariant().getSku())
                        .quantity(i.getQuantity())
                        .build()).toList())
                .build();
    }
}
