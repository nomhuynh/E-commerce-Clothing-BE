package com.clothingstore.backend.dto.shipping;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ShippingCalculateRequest {
    @NotBlank
    private String methodId;

    @NotBlank
    private String zoneId;
}
