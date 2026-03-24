package com.clothingstore.backend.dto.product;

import com.clothingstore.backend.entity.enums.AgeGroup;
import com.clothingstore.backend.entity.enums.ProductGender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private String id;
    private String categoryId;
    private String materialId;
    private String name;
    private String description;
    private BigDecimal basePrice;
    private ProductGender gender;
    private AgeGroup ageGroup;
    private Instant createdAt;
    private Instant updatedAt;
    private List<ProductVariantResponse> variants;
    private List<ProductImageResponse> images;
}