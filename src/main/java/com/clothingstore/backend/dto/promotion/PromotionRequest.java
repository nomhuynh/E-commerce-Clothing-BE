package com.clothingstore.backend.dto.promotion;

import com.clothingstore.backend.entity.enums.DiscountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PromotionRequest {

    @NotBlank
    private String name;

    private String description;

    @NotNull
    private DiscountType discountType;

    @NotNull
    private BigDecimal discountValue;

    @NotNull
    private LocalDateTime startDate;

    @NotNull
    private LocalDateTime endDate;

    private Boolean isActive;
}
