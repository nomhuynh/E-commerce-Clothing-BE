package com.clothingstore.backend.service.impl;

import com.clothingstore.backend.dto.auth.*;
import com.clothingstore.backend.dto.user.UserResponse;
import com.clothingstore.backend.entity.User;
import com.clothingstore.backend.entity.enums.AuthProvider;
import com.clothingstore.backend.entity.enums.Role;
import com.clothingstore.backend.entity.enums.UserStatus;
import com.clothingstore.backend.repository.UserRepository;
import com.clothingstore.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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
        User user = userRepository.findByEmail(request.getEmail()).orElseGet(() -> {
            User newUser = User.builder()
                    .firstName(request.getFirstName() != null ? request.getFirstName() : "Google")
                    .lastName(request.getLastName() != null ? request.getLastName() : "User")
                    .email(request.getEmail())
                    .phoneNumber(UUID.randomUUID().toString().substring(0, 12))
                    .authProvider(AuthProvider.GOOGLE)
                    .authProviderId(request.getToken())
                    .role(Role.CUSTOMER)
                    .status(UserStatus.ACTIVE)
                    .isEmailVerified(true)
                    .build();
            return userRepository.save(newUser);
        });

        return AuthLoginResponse.builder()
                .accessToken(generateToken(user))
                .user(toResponse(user))
                .build();
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
