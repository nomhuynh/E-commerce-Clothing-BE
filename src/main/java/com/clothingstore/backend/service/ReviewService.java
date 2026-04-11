package com.clothingstore.backend.service;

import com.clothingstore.backend.dto.review.ReviewRequest;
import com.clothingstore.backend.dto.review.ReviewResponse;
import com.clothingstore.backend.dto.review.ReviewUpdateRequest;

import java.util.List;
import java.util.Optional;

public interface ReviewService {
    ReviewResponse create(ReviewRequest request);

    ReviewResponse update(String id, ReviewUpdateRequest request);

    List<ReviewResponse> getByProduct(String productId);

    Optional<ReviewResponse> getByUserAndProduct(String userId, String productId);

    void delete(String id);
}
