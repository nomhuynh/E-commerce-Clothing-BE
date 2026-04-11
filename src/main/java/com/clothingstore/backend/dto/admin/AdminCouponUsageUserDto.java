package com.clothingstore.backend.dto.admin;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AdminCouponUsageUserDto {
    @JsonProperty("user_id")
    private String id;
    private String email;
    private String firstName;
    private String lastName;
}
