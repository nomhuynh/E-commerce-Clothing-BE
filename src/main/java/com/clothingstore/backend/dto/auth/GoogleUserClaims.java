package com.clothingstore.backend.dto.auth;

/**
 * Claims extracted from a verified Google ID token (GIS credential JWT).
 */
public record GoogleUserClaims(
        String subject,
        String email,
        String firstName,
        String lastName,
        String pictureUrl
) {}
