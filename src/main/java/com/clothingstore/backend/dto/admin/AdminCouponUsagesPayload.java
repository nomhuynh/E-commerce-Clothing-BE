package com.clothingstore.backend.dto.admin;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AdminCouponUsagesPayload {
    private List<AdminCouponUsageRowDto> usages;
    private long total;
    private int page;
    private int totalPages;
    private int limit;
}
