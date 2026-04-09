package com.clothingstore.backend.controller;

import com.clothingstore.backend.dto.ApiResponse;
import com.clothingstore.backend.dto.user.UserRequest;
import com.clothingstore.backend.dto.user.UserResponse;
import com.clothingstore.backend.entity.User;
import com.clothingstore.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAll() {
        List<UserResponse> data = userService.getAll().stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(ApiResponse.success("Users fetched", data));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success("User fetched", toResponse(userService.getById(id))));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> update(@PathVariable String id, @Valid @RequestBody UserRequest request) {
        User entity = toEntity(request);
        entity.setId(id);
        User updated = userService.update(entity);
        return ResponseEntity.ok(ApiResponse.success("User updated", toResponse(updated)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable String id) {
        userService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("User deleted", null));
    }

    private User toEntity(UserRequest request) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        if (request.getPasswordHash() != null) {
            user.setPasswordHash(request.getPasswordHash());
        }
        user.setAuthProvider(request.getAuthProvider());
        user.setAuthProviderId(request.getAuthProviderId());
        user.setAvatarUrl(request.getAvatarUrl());
        user.setGender(request.getGender());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setPreferences(request.getPreferences());
        user.setLoyaltyPoints(request.getLoyaltyPoints());
        user.setTierLevel(request.getTierLevel());
        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }
        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }
        if (request.getIsEmailVerified() != null) {
            user.setIsEmailVerified(request.getIsEmailVerified());
        }
        return user;
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