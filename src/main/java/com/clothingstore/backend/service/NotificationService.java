package com.clothingstore.backend.service;

import com.clothingstore.backend.dto.wishlist.NotificationResponse;

import java.util.List;

public interface NotificationService {
    List<NotificationResponse> getByUser(String userId);
    long getUnreadCount(String userId);
    void markAsRead(String notificationId);
}
