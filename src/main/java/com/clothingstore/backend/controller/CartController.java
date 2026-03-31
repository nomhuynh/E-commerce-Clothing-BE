package com.clothingstore.backend.controller;

import com.clothingstore.backend.dto.ApiResponse;
import com.clothingstore.backend.dto.cart.*;
import com.clothingstore.backend.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
@Tag(name = "Cart", description = "Shopping cart endpoints")
public class CartController {

    private final CartService cartService;

    @Operation(summary = "Get cart by user")
    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> getCart(@RequestParam String userId) {
        return ResponseEntity.ok(ApiResponse.success("Cart fetched", cartService.getCart(userId)));
    }

    @Operation(summary = "Add item to cart")
    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartResponse>> addItem(@Valid @RequestBody AddCartItemRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Item added", cartService.addItem(request)));
    }

    @Operation(summary = "Update quantity of a cart item")
    @PatchMapping("/items/{variantId}")
    public ResponseEntity<ApiResponse<CartResponse>> updateItem(
            @PathVariable String variantId,
            @RequestParam String userId,
            @Valid @RequestBody UpdateCartItemRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Item updated", cartService.updateItem(userId, variantId, request)));
    }

    @Operation(summary = "Remove item from cart")
    @DeleteMapping("/items/{variantId}")
    public ResponseEntity<ApiResponse<CartResponse>> removeItem(
            @PathVariable String variantId,
            @RequestParam String userId) {
        return ResponseEntity.ok(ApiResponse.success("Item removed", cartService.removeItem(userId, variantId)));
    }

    @Operation(summary = "Apply coupon to cart")
    @PostMapping("/coupon")
    public ResponseEntity<ApiResponse<CartResponse>> applyCoupon(@Valid @RequestBody ApplyCouponRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Coupon applied", cartService.applyCoupon(request)));
    }

    @Operation(summary = "Remove coupon from cart")
    @DeleteMapping("/coupon")
    public ResponseEntity<ApiResponse<CartResponse>> removeCoupon(@RequestParam String userId) {
        return ResponseEntity.ok(ApiResponse.success("Coupon removed", cartService.removeCoupon(userId)));
    }

    @Operation(summary = "Clear cart")
    @DeleteMapping
    public ResponseEntity<ApiResponse<CartResponse>> clearCart(@RequestParam String userId) {
        return ResponseEntity.ok(ApiResponse.success("Cart cleared", cartService.clearCart(userId)));
    }
}
