package com.clothingstore.backend.dto.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class ReviewUpdateRequest {
    @Min(1)
    @Max(5)
    private Integer rating;
    private String title;
    private String content;
}
