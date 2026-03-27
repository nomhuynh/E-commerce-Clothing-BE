package com.clothingstore.backend.repository;

import com.clothingstore.backend.entity.Review;
import com.clothingstore.backend.entity.enums.ReviewStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, String> {
    Optional<Review> findByUserIdAndProductId(String userId, String productId);
    List<Review> findByProductIdAndStatus(String productId, ReviewStatus status);
}
