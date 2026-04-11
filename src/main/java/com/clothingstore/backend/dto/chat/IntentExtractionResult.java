package com.clothingstore.backend.dto.chat;

import com.clothingstore.backend.entity.enums.AgeGroup;
import com.clothingstore.backend.entity.enums.ProductGender;

import java.math.BigDecimal;
import java.util.List;

/**
 * @param genderFilter  Lọc theo giới — khớp SP cùng giới hoặc {@link ProductGender#UNISEX}; null = không lọc.
 * @param ageGroupFilter Lọc theo độ tuổi; null = không lọc.
 */
public record IntentExtractionResult(
        List<String> keywords,
        List<String> colors,
        BigDecimal maxPrice,
        ProductGender genderFilter,
        AgeGroup ageGroupFilter) {

    public static IntentExtractionResult empty() {
        return new IntentExtractionResult(List.of(), List.of(), null, null, null);
    }
}
