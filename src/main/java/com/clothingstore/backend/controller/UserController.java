package com.clothingstore.backend.controller;

import com.clothingstore.backend.dto.ApiResponse;
import com.clothingstore.backend.dto.user.UpdateProfileRequest;
import com.clothingstore.backend.dto.user.UserRequest;
import com.clothingstore.backend.dto.user.UserResponse;
import com.clothingstore.backend.entity.User;
import com.clothingstore.backend.service.CloudinaryUploadService;
import com.clothingstore.backend.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private static final String AVATAR_FOLDER = "stylex/avatars";

    private final UserService userService;
    private final ObjectMapper objectMapper;
    private final CloudinaryUploadService cloudinaryUploadService;

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponse>> getProfile(Authentication authentication) {
        String userId = requireUserId(authentication);
        return ResponseEntity.ok(ApiResponse.success("Profile fetched", toResponse(userService.getById(userId))));
    }

    @PatchMapping("/profile")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            Authentication authentication,
            @RequestBody UpdateProfileRequest request) throws JsonProcessingException {
        String userId = requireUserId(authentication);
        User user = userService.getById(userId);
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getGender() != null) {
            user.setGender(request.getGender());
        }
        if (request.getDateOfBirth() != null && !request.getDateOfBirth().isBlank()) {
            try {
                user.setDateOfBirth(LocalDate.parse(request.getDateOfBirth(), DateTimeFormatter.ISO_LOCAL_DATE));
            } catch (DateTimeParseException ex) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid date_of_birth, use ISO-8601 (yyyy-MM-dd)");
            }
        }
        if (request.getPreferences() != null) {
            if (request.getPreferences() instanceof String s) {
                user.setPreferences(s);
            } else {
                user.setPreferences(objectMapper.writeValueAsString(request.getPreferences()));
            }
        }
        User updated = userService.update(user);
        return ResponseEntity.ok(ApiResponse.success("Profile updated", toResponse(updated)));
    }

    @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<UserResponse>> uploadAvatar(
            Authentication authentication,
            @RequestPart("avatar") MultipartFile file) {
        String userId = requireUserId(authentication);
        String url = cloudinaryUploadService.uploadImage(file, AVATAR_FOLDER);
        User user = userService.getById(userId);
        user.setAvatarUrl(url);
        User updated = userService.update(user);
        return ResponseEntity.ok(ApiResponse.success("Avatar updated", toResponse(updated)));
    }

    private String requireUserId(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof String)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        return (String) authentication.getPrincipal();
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> create(@Valid @RequestBody UserRequest request) {
        User created = userService.create(toEntity(request));
        return ResponseEntity.ok(ApiResponse.success("User created", toResponse(created)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> update(@PathVariable String id, @Valid @RequestBody UserRequest request) {
        User entity = toEntity(request);
        entity.setId(id);
        User updated = userService.update(entity);
        return ResponseEntity.ok(ApiResponse.success("User updated", toResponse(updated)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success("User fetched", toResponse(userService.getById(id))));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<UserResponse>> getByEmail(@PathVariable String email) {
        return ResponseEntity.ok(ApiResponse.success("User fetched", toResponse(userService.getByEmail(email))));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAll() {
        List<UserResponse> data = userService.getAll().stream().map(this::toResponse).toList();
        return ResponseEntity.ok(ApiResponse.success("Users fetched", data));
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
        user.setPasswordHash(request.getPasswordHash());
        if (request.getAuthProvider() != null) {
            user.setAuthProvider(request.getAuthProvider());
        }
        user.setAuthProviderId(request.getAuthProviderId());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setAvatarUrl(request.getAvatarUrl());
        user.setGender(request.getGender());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setPreferences(request.getPreferences());
        if (request.getLoyaltyPoints() != null) {
            user.setLoyaltyPoints(request.getLoyaltyPoints());
        }
        if (request.getTierLevel() != null) {
            user.setTierLevel(request.getTierLevel());
        }
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
