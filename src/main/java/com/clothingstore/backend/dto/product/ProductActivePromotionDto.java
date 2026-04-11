package com.clothingstore.backend.dto.product;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Snapshot of the best active promotion for storefront JSON (matches {@link com.clothingstore.backend.entity.enums.DiscountType} names).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ProductActivePromotionDto {

    private String discountType;
    private BigDecimal discountValue;
}
