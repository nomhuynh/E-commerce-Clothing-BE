package com.clothingstore.backend.dto.shipping;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AssignShippingZoneRequest {
    @NotBlank
    private String zoneId;

    private BigDecimal fee;
    private Integer estimatedDays;
}
