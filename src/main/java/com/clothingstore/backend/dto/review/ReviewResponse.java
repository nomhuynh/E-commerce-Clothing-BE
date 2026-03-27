package com.clothingstore.backend.dto.review;

import com.clothingstore.backend.entity.enums.ReviewStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReviewResponse {
    private String id;
    private String userId;
    private String productId;
    private Integer rating;
    private String title;
    private String content;
    private ReviewStatus status;
}
