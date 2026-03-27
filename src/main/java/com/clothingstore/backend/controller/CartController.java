package com.clothingstore.backend.controller;

import com.clothingstore.backend.dto.ApiResponse;
import com.clothingstore.backend.dto.cart.CartRequest;
import com.clothingstore.backend.dto.cart.CartResponse;
import com.clothingstore.backend.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<CartResponse>> getCart(@PathVariable String userId) {
        return ResponseEntity.ok(ApiResponse.success("Cart fetched", cartService.getCart(userId)));
    }

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartResponse>> addItem(@Valid @RequestBody CartRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Item added", cartService.addItem(request)));
    }

    @PutMapping("/items")
    public ResponseEntity<ApiResponse<CartResponse>> updateItem(@Valid @RequestBody CartRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Item updated", cartService.updateItem(request)));
    }

    @DeleteMapping("/items")
    public ResponseEntity<ApiResponse<CartResponse>> removeItem(@RequestParam String userId, @RequestParam String variantId) {
        return ResponseEntity.ok(ApiResponse.success("Item removed", cartService.removeItem(userId, variantId)));
    }
}
