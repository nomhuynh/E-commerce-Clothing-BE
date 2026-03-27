package com.clothingstore.backend.service;

import com.clothingstore.backend.dto.review.ReviewRequest;
import com.clothingstore.backend.dto.review.ReviewResponse;

import java.util.List;

public interface ReviewService {
    ReviewResponse create(ReviewRequest request);
    ReviewResponse update(String id, ReviewRequest request);
    List<ReviewResponse> getByProduct(String productId);
    void delete(String id);
}
