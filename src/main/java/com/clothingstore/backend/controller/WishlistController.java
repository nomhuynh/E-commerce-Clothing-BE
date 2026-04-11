package com.clothingstore.backend.controller;

import com.clothingstore.backend.dto.ApiResponse;
import com.clothingstore.backend.dto.wishlist.WishlistRequest;
import com.clothingstore.backend.dto.wishlist.WishlistResponse;
import com.clothingstore.backend.service.WishlistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @PostMapping
    public ResponseEntity<ApiResponse<WishlistResponse>> add(
            @Valid @RequestBody WishlistRequest request,
            Authentication authentication) {
        String uid = requireUserId(authentication);
        request.setUserId(uid);
        return ResponseEntity.ok(ApiResponse.success("Added to wishlist", wishlistService.add(request)));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> remove(
            @RequestParam String userId,
            @RequestParam String productId,
            Authentication authentication) {
        String uid = requireUserId(authentication);
        if (!uid.equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        }
        wishlistService.remove(userId, productId);
        return ResponseEntity.ok(ApiResponse.success("Removed from wishlist", null));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<List<WishlistResponse>>> getByUser(
            @PathVariable String userId,
            Authentication authentication) {
        String uid = requireUserId(authentication);
        if (!uid.equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        }
        return ResponseEntity.ok(ApiResponse.success("Wishlist fetched", wishlistService.getByUser(userId)));
    }

    @GetMapping("/check/{productId}")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> check(
            @PathVariable String productId,
            Authentication authentication) {
        String userId = requireUserId(authentication);
        boolean inWishlist = wishlistService.isInWishlist(userId, productId);
        return ResponseEntity.ok(ApiResponse.success("OK", Map.of("inWishlist", inWishlist)));
    }

    private static String requireUserId(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof String)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        return (String) authentication.getPrincipal();
    }
}
