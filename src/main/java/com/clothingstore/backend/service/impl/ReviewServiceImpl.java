package com.clothingstore.backend.service.impl;

import com.clothingstore.backend.dto.review.ReviewRequest;
import com.clothingstore.backend.dto.review.ReviewResponse;
import com.clothingstore.backend.dto.review.ReviewUpdateRequest;
import com.clothingstore.backend.entity.Product;
import com.clothingstore.backend.entity.Review;
import com.clothingstore.backend.entity.User;
import com.clothingstore.backend.entity.enums.ReviewStatus;
import com.clothingstore.backend.repository.ProductRepository;
import com.clothingstore.backend.repository.ReviewRepository;
import com.clothingstore.backend.repository.UserRepository;
import com.clothingstore.backend.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    public ReviewResponse create(ReviewRequest request) {
        reviewRepository.findByUserIdAndProductId(request.getUserId(), request.getProductId())
                .ifPresent(r -> { throw new RuntimeException("User already reviewed this product"); });

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Review review = Review.builder()
                .user(user)
                .product(product)
                .rating(request.getRating())
                .title(request.getTitle())
                .content(request.getContent())
                .status(ReviewStatus.PENDING)
                .build();

        return toResponse(reviewRepository.save(review));
    }

    @Override
    public ReviewResponse update(String id, ReviewUpdateRequest request) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        if (request.getRating() != null) {
            review.setRating(request.getRating());
        }
        if (request.getTitle() != null) {
            review.setTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            review.setContent(request.getContent());
        }
        return toResponse(reviewRepository.save(review));
    }

    @Override
    public List<ReviewResponse> getByProduct(String productId) {
        return reviewRepository.findByProductIdAndStatus(productId, ReviewStatus.APPROVED)
                .stream().map(this::toResponse).toList();
    }

    @Override
    public Optional<ReviewResponse> getByUserAndProduct(String userId, String productId) {
        return reviewRepository.findByUserIdAndProductId(userId, productId).map(this::toResponse);
    }

    @Override
    public void delete(String id) {
        reviewRepository.deleteById(id);
    }

    private ReviewResponse toResponse(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .userId(review.getUser().getId())
                .productId(review.getProduct().getId())
                .rating(review.getRating())
                .title(review.getTitle())
                .content(review.getContent())
                .status(review.getStatus())
                .build();
    }
}
