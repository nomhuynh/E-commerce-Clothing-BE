package com.clothingstore.backend.dto.product;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ProductImageResponse {
    private String id;
    private String productId;
    private String imageUrl;
    private Integer displayOrder;
    private Boolean isThumbnail;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
