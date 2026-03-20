package com.clothingstore.backend.controller;

import com.clothingstore.backend.dto.ApiResponse;
import com.clothingstore.backend.dto.wishlist.WishlistRequest;
import com.clothingstore.backend.dto.wishlist.WishlistResponse;
import com.clothingstore.backend.service.WishlistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @PostMapping
    public ResponseEntity<ApiResponse<WishlistResponse>> add(@Valid @RequestBody WishlistRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Added to wishlist", wishlistService.add(request)));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> remove(@RequestParam String userId, @RequestParam String productId) {
        wishlistService.remove(userId, productId);
        return ResponseEntity.ok(ApiResponse.success("Removed from wishlist", null));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<List<WishlistResponse>>> getByUser(@PathVariable String userId) {
        return ResponseEntity.ok(ApiResponse.success("Wishlist fetched", wishlistService.getByUser(userId)));
    }
}
