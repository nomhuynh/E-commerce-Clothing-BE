package com.clothingstore.backend.dto.user;

import com.clothingstore.backend.entity.enums.AuthProvider;
import com.clothingstore.backend.entity.enums.Gender;
import com.clothingstore.backend.entity.enums.Role;
import com.clothingstore.backend.entity.enums.TierLevel;
import com.clothingstore.backend.entity.enums.UserStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserResponse {
    @JsonProperty("user_id")
    private String id;
    private String email;
    private String phoneNumber;
    private AuthProvider authProvider;
    private String authProviderId;
    private String firstName;
    private String lastName;
    private String avatarUrl;
    private Gender gender;
    private LocalDate dateOfBirth;
    private String preferences;
    private Integer loyaltyPoints;
    private TierLevel tierLevel;
    private Role role;
    private UserStatus status;
    private Boolean isEmailVerified;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
