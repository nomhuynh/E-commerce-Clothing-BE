package com.clothingstore.backend.dto.auth;

import com.clothingstore.backend.dto.user.UserResponse;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthLoginResponse {
    private String accessToken;
    private UserResponse user;
}
