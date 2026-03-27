package com.clothingstore.backend.service;

import com.clothingstore.backend.dto.review.ReviewRequest;
import com.clothingstore.backend.entity.Product;
import com.clothingstore.backend.entity.User;
import com.clothingstore.backend.repository.ProductRepository;
import com.clothingstore.backend.repository.ReviewRepository;
import com.clothingstore.backend.repository.UserRepository;
import com.clothingstore.backend.service.impl.ReviewServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock private ReviewRepository reviewRepository;
    @Mock private ProductRepository productRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    @Test
    void create_ShouldThrow_WhenUserAlreadyReviewed() {
        ReviewRequest req = new ReviewRequest();
        req.setUserId("u1");
        req.setProductId("p1");
        req.setRating(5);

        when(reviewRepository.findByUserIdAndProductId("u1", "p1")).thenReturn(Optional.of(new com.clothingstore.backend.entity.Review()));

        assertThrows(RuntimeException.class, () -> reviewService.create(req));
    }
}
