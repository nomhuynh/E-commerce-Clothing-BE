package com.clothingstore.backend.dto.admin;

import com.clothingstore.backend.dto.user.UserResponse;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AdminUserListPayload {
    private List<UserResponse> users;
    private long total;
    private int page;
    private int limit;
}
