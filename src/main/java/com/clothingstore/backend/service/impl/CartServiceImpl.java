package com.clothingstore.backend.service.impl;

import com.clothingstore.backend.dto.cart.*;
import com.clothingstore.backend.entity.*;
import com.clothingstore.backend.entity.enums.DiscountType;
import com.clothingstore.backend.repository.*;
import com.clothingstore.backend.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductVariantRepository productVariantRepository;
    private final CouponRepository couponRepository;

    @Override
    @Transactional(readOnly = true)
    public CartResponse getCart(String userId) {
        Cart cart = cartRepository.findByUserId(userId).orElseGet(() -> {
            User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
            return cartRepository.save(Cart.builder().user(user).build());
        });
        cart.getItems().size();
        return toResponse(cart);
    }

    @Override
    @Transactional
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
    @Transactional
    public CartResponse updateItem(String userId, String variantId, UpdateCartItemRequest request) {
        Cart cart = cartRepository.findByUserId(userId).orElseThrow(() -> new RuntimeException("Cart not found"));
        CartItem item = cartItemRepository.findByCartIdAndVariantId(cart.getId(), variantId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        item.setQuantity(request.getQuantity());
        cartItemRepository.save(item);
        return getCart(userId);
    }

    @Override
    @Transactional
    public CartResponse removeItem(String userId, String variantId) {
        Cart cart = cartRepository.findByUserId(userId).orElseThrow(() -> new RuntimeException("Cart not found"));
        CartItem item = cartItemRepository.findByCartIdAndVariantId(cart.getId(), variantId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        cartItemRepository.delete(item);
        return getCart(userId);
    }

    @Override
    @Transactional
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
    @Transactional
    public CartResponse removeCoupon(String userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        cart.setCoupon(null);
        cartRepository.save(cart);
        return getCart(userId);
    }

    @Override
    @Transactional
    public CartResponse clearCart(String userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        cartItemRepository.deleteByCartId(cart.getId());
        return getCart(userId);
    }

    private CartResponse toResponse(Cart cart) {
        List<CartItem> rows = cart.getItems() == null ? List.of() : cart.getItems();
        List<CartResponse.CartItemResponse> itemResponses = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;

        for (CartItem ci : rows) {
            ProductVariant v = ci.getVariant();
            if (v == null) {
                continue;
            }
            Product p = v.getProduct();
            String productName = p != null ? p.getName() : "Sản phẩm";
            BigDecimal unit = unitPrice(v);
            String colorName = v.getColor() != null ? v.getColor().getName() : "";
            String sizeName = v.getSize() != null ? v.getSize().getName() : "";
            String imageUrl = resolveImageUrl(v);

            itemResponses.add(CartResponse.CartItemResponse.builder()
                    .variantId(v.getId())
                    .sku(v.getSku())
                    .quantity(ci.getQuantity())
                    .productName(productName)
                    .unitPrice(unit)
                    .colorName(colorName)
                    .sizeName(sizeName)
                    .imageUrl(imageUrl)
                    .build());

            subtotal = subtotal.add(unit.multiply(BigDecimal.valueOf(ci.getQuantity())));
        }

        BigDecimal discountAmt = BigDecimal.ZERO;
        CartResponse.CouponSummary couponSummary = null;
        Coupon coupon = cart.getCoupon();
        if (coupon != null) {
            discountAmt = computeDiscount(subtotal, coupon);
            couponSummary = CartResponse.CouponSummary.builder()
                    .couponId(coupon.getId())
                    .code(coupon.getCode())
                    .discountType(mapDiscountType(coupon.getDiscountType()))
                    .discountValue(coupon.getDiscountValue())
                    .build();
        }

        BigDecimal total = subtotal.subtract(discountAmt);
        if (total.compareTo(BigDecimal.ZERO) < 0) {
            total = BigDecimal.ZERO;
        }

        return CartResponse.builder()
                .cartId(cart.getId())
                .userId(cart.getUser() != null ? cart.getUser().getId() : null)
                .couponId(coupon != null ? coupon.getId() : null)
                .subtotal(subtotal)
                .discount(discountAmt)
                .total(total)
                .coupon(couponSummary)
                .items(itemResponses)
                .build();
    }

    private static BigDecimal unitPrice(ProductVariant v) {
        if (v.getPrice() != null && v.getPrice().compareTo(BigDecimal.ZERO) > 0) {
            return v.getPrice();
        }
        if (v.getProduct() != null && v.getProduct().getBasePrice() != null) {
            return v.getProduct().getBasePrice();
        }
        return BigDecimal.ZERO;
    }

    private static String resolveImageUrl(ProductVariant v) {
        if (v.getImage() != null && !v.getImage().isBlank()) {
            return v.getImage();
        }
        Product p = v.getProduct();
        if (p == null || p.getImages() == null || p.getImages().isEmpty()) {
            return null;
        }
        List<ProductImage> imgs = p.getImages();
        return imgs.stream()
                .filter(img -> Boolean.TRUE.equals(img.getIsThumbnail()))
                .min(Comparator.comparing(img -> img.getSortOrder() != null ? img.getSortOrder() : 0))
                .map(ProductImage::getImageUrl)
                .orElseGet(() -> imgs.stream()
                        .min(Comparator.comparing(img -> img.getSortOrder() != null ? img.getSortOrder() : 0))
                        .map(ProductImage::getImageUrl)
                        .orElse(null));
    }

    private static BigDecimal computeDiscount(BigDecimal subtotal, Coupon c) {
        if (subtotal.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }
        if (c.getMinOrderValue() != null && subtotal.compareTo(c.getMinOrderValue()) < 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal d;
        if (c.getDiscountType() == DiscountType.percentage) {
            d = subtotal.multiply(c.getDiscountValue()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            if (c.getMaxDiscountAmount() != null && d.compareTo(c.getMaxDiscountAmount()) > 0) {
                d = c.getMaxDiscountAmount();
            }
        } else {
            d = c.getDiscountValue();
        }
        if (d.compareTo(subtotal) > 0) {
            d = subtotal;
        }
        return d;
    }

    private static String mapDiscountType(DiscountType t) {
        if (t == null) {
            return "FIXED";
        }
        return t == DiscountType.percentage ? "PERCENTAGE" : "FIXED";
    }
}
