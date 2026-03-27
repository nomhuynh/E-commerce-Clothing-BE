package com.clothingstore.backend.dto.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReviewRequest {
    @NotBlank
    private String userId;
    @NotBlank
    private String productId;
    @Min(1)
    @Max(5)
    private Integer rating;
    private String title;
    private String content;
}
