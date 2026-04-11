package com.clothingstore.backend.controller;

import com.clothingstore.backend.dto.ApiResponse;
import com.clothingstore.backend.dto.review.ReviewRequest;
import com.clothingstore.backend.dto.review.ReviewResponse;
import com.clothingstore.backend.dto.review.ReviewUpdateRequest;
import com.clothingstore.backend.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ApiResponse<ReviewResponse>> create(@Valid @RequestBody ReviewRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Review created", reviewService.create(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ReviewResponse>> update(
            @PathVariable String id,
            @Valid @RequestBody ReviewUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Review updated", reviewService.update(id, request)));
    }

    @GetMapping("/my-review/{productId}")
    public ResponseEntity<ApiResponse<ReviewResponse>> getMyReview(
            @PathVariable String productId,
            Authentication authentication) {
        String userId = requireUserId(authentication);
        return reviewService.getByUserAndProduct(userId, productId)
                .map(r -> ResponseEntity.ok(ApiResponse.success("Review fetched", r)))
                .orElseGet(() -> ResponseEntity.ok(ApiResponse.success("No review", null)));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getByProduct(@PathVariable String productId) {
        return ResponseEntity.ok(ApiResponse.success("Reviews fetched", reviewService.getByProduct(productId)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        reviewService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Review deleted", null));
    }

    private static String requireUserId(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof String)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        return (String) authentication.getPrincipal();
    }
}
