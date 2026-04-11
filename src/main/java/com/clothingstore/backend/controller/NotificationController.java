package com.clothingstore.backend.controller;

import com.clothingstore.backend.dto.ApiResponse;
import com.clothingstore.backend.dto.wishlist.NotificationResponse;
import com.clothingstore.backend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getByUser(@PathVariable String userId) {
        return ResponseEntity.ok(ApiResponse.success("Notifications fetched", notificationService.getByUser(userId)));
    }

    @GetMapping("/user/{userId}/unread-count")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getUnreadCount(@PathVariable String userId) {
        long count = notificationService.getUnreadCount(userId);
        return ResponseEntity.ok(ApiResponse.success("Unread count", Map.of("unreadCount", count)));
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable String notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok(ApiResponse.success("Notification marked as read", null));
    }

    @PatchMapping("/user/{userId}/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllRead(
            @PathVariable String userId,
            Authentication authentication) {
        requireSameUser(authentication, userId);
        notificationService.markAllReadForUser(userId);
        return ResponseEntity.ok(ApiResponse.success("All notifications marked read", null));
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable String notificationId,
            Authentication authentication) {
        String uid = requireUserId(authentication);
        notificationService.deleteForUser(notificationId, uid);
        return ResponseEntity.ok(ApiResponse.success("Notification deleted", null));
    }

    private static String requireUserId(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof String)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        return (String) authentication.getPrincipal();
    }

    private static void requireSameUser(Authentication authentication, String userId) {
        String uid = requireUserId(authentication);
        if (!uid.equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot modify another user's notifications");
        }
    }
}
