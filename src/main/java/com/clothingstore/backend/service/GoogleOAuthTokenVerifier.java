package com.clothingstore.backend.service;

import com.clothingstore.backend.dto.auth.GoogleUserClaims;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

/**
 * Verifies Google Identity Services (GIS) ID tokens using Google's public keys.
 * Client secret is not required for ID token verification — only the Web client ID (audience).
 */
@Service
public class GoogleOAuthTokenVerifier {

    @Value("${google.client.id}")
    private String googleWebClientId;

    public GoogleUserClaims verify(String idTokenJwt) {
        if (idTokenJwt == null || idTokenJwt.isBlank()) {
            throw new RuntimeException("Google ID token is required");
        }
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(),
                GsonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList(googleWebClientId))
                .build();
        try {
            GoogleIdToken idToken = verifier.verify(idTokenJwt);
            if (idToken == null) {
                throw new RuntimeException("Invalid Google ID token");
            }
            GoogleIdToken.Payload p = idToken.getPayload();
            String email = p.getEmail();
            if (email == null || email.isBlank()) {
                throw new RuntimeException("Google account has no email");
            }
            if (!Boolean.TRUE.equals(p.getEmailVerified())) {
                throw new RuntimeException("Google email is not verified");
            }
            String sub = p.getSubject();
            String given = (String) p.get("given_name");
            String family = (String) p.get("family_name");
            if ((given == null || given.isBlank()) && (family == null || family.isBlank())) {
                String name = (String) p.get("name");
                if (name != null && !name.isBlank()) {
                    String[] parts = name.trim().split("\\s+", 2);
                    given = parts[0];
                    family = parts.length > 1 ? parts[1] : "User";
                } else {
                    given = "Google";
                    family = "User";
                }
            }
            if (given == null || given.isBlank()) {
                given = "Google";
            }
            if (family == null || family.isBlank()) {
                family = "User";
            }
            String picture = (String) p.get("picture");
            return new GoogleUserClaims(sub, email, given, family, picture);
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException("Google token verification failed: " + e.getMessage());
        }
    }
}
