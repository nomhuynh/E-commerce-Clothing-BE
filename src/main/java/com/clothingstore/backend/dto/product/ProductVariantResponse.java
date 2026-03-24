package com.clothingstore.backend.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariantResponse {
    private String id;
    private String productId;
    private String colorId;
    private String sizeId;
    private String sku;
    private BigDecimal price;
    private Integer stockQuantity;
    private Instant createdAt;
    private Instant updatedAt;
}