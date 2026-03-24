package com.clothingstore.backend.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageResponse {
    private String id;
    private String productId;
    private String imageUrl;
    private Integer displayOrder;
    private Boolean isThumbnail;
    private Instant createdAt;
    private Instant updatedAt;
}