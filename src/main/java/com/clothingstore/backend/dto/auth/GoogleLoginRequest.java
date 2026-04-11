package com.clothingstore.backend.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * GIS returns an ID token (JWT) in the credential response. Email/name are taken from the
 * verified token on the server — do not trust client-supplied identity fields.
 */
@Data
public class GoogleLoginRequest {
    /** Google Identity Services credential (ID token JWT). */
    @NotBlank
    private String token;
}
