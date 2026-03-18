package com.clothingstore.backend.dto.user;

import com.clothingstore.backend.entity.enums.AuthProvider;
import com.clothingstore.backend.entity.enums.Gender;
import com.clothingstore.backend.entity.enums.Role;
import com.clothingstore.backend.entity.enums.TierLevel;
import com.clothingstore.backend.entity.enums.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserRequest {

    @NotBlank
    @Email
    private String email;

    @Size(max = 15)
    private String phoneNumber;

    private String passwordHash;

    private AuthProvider authProvider;

    private String authProviderId;

    @NotBlank
    @Size(max = 50)
    private String firstName;

    @NotBlank
    @Size(max = 50)
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
}
