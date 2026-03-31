package com.clothingstore.backend.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GoogleLoginRequest {
    @NotBlank
    private String token;

    @NotBlank
    @Email
    private String email;

    private String firstName;
    private String lastName;
}
