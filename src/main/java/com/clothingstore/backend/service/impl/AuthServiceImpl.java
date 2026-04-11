package com.clothingstore.backend.service.impl;

import com.clothingstore.backend.dto.auth.*;
import com.clothingstore.backend.dto.user.UserResponse;
import com.clothingstore.backend.entity.User;
import com.clothingstore.backend.entity.enums.AuthProvider;
import com.clothingstore.backend.entity.enums.Role;
import com.clothingstore.backend.entity.enums.UserStatus;
import com.clothingstore.backend.repository.UserRepository;
import com.clothingstore.backend.service.AuthService;
import com.clothingstore.backend.service.GoogleOAuthTokenVerifier;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final GoogleOAuthTokenVerifier googleOAuthTokenVerifier;

    @Override
    public User register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new RuntimeException("Phone number already exists");
        }

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .role(Role.CUSTOMER)
                .authProvider(AuthProvider.LOCAL)
                .status(UserStatus.ACTIVE)
                .build();

        return userRepository.save(user);
    }

    @Override
    public AuthLoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (user.getPasswordHash() == null || !passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid email or password");
        }

        return AuthLoginResponse.builder()
                .accessToken(generateToken(user))
                .user(toResponse(user))
                .build();
    }

    @Override
    public void changePassword(ChangePasswordRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getPasswordHash() == null || !passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Old password is incorrect");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email not found"));

        user.setResetPasswordToken(UUID.randomUUID().toString());
        userRepository.save(user);
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findAll().stream()
                .filter(u -> request.getToken().equals(u.getResetPasswordToken()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        user.setResetPasswordToken(null);
        userRepository.save(user);
    }

    @Override
    public AuthLoginResponse loginWithGoogle(GoogleLoginRequest request) {
        GoogleUserClaims claims = googleOAuthTokenVerifier.verify(request.getToken());

        User user = userRepository
                .findByAuthProviderAndAuthProviderId(AuthProvider.GOOGLE, claims.subject())
                .orElseGet(() -> handleGoogleUserByEmail(claims));

        return AuthLoginResponse.builder()
                .accessToken(generateToken(user))
                .user(toResponse(user))
                .build();
    }

    /** Link or create user after Google token is verified. */
    private User handleGoogleUserByEmail(GoogleUserClaims claims) {
        return userRepository.findByEmail(claims.email()).map(existing -> {
            if (existing.getAuthProvider() == AuthProvider.LOCAL) {
                throw new RuntimeException(
                        "This email is already registered with a password. Please sign in with email and password.");
            }
            if (existing.getAuthProvider() == AuthProvider.GOOGLE) {
                if (existing.getAuthProviderId() == null || existing.getAuthProviderId().isBlank()) {
                    existing.setAuthProviderId(claims.subject());
                }
                if (claims.pictureUrl() != null && !claims.pictureUrl().isBlank()) {
                    existing.setAvatarUrl(claims.pictureUrl());
                }
                return userRepository.save(existing);
            }
            throw new RuntimeException("Cannot sign in with Google for this account.");
        }).orElseGet(() -> userRepository.save(User.builder()
                .firstName(claims.firstName())
                .lastName(claims.lastName())
                .email(claims.email())
                .phoneNumber(randomUniquePhonePlaceholder())
                .authProvider(AuthProvider.GOOGLE)
                .authProviderId(claims.subject())
                .role(Role.CUSTOMER)
                .status(UserStatus.ACTIVE)
                .isEmailVerified(true)
                .avatarUrl(claims.pictureUrl())
                .build()));
    }

    private String randomUniquePhonePlaceholder() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 12);
    }

    private String generateToken(User user) {
        return "mock-token-" + user.getId();
    }

    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .authProvider(user.getAuthProvider())
                .authProviderId(user.getAuthProviderId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .avatarUrl(user.getAvatarUrl())
                .gender(user.getGender())
                .dateOfBirth(user.getDateOfBirth())
                .preferences(user.getPreferences())
                .loyaltyPoints(user.getLoyaltyPoints())
                .tierLevel(user.getTierLevel())
                .role(user.getRole())
                .status(user.getStatus())
                .isEmailVerified(user.getIsEmailVerified())
                .lastLoginAt(user.getLastLoginAt())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
