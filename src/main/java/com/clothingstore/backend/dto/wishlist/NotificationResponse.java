package com.clothingstore.backend.dto.wishlist;

import com.clothingstore.backend.entity.enums.NotificationType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationResponse {
    private String id;
    private String title;
    private String content;
    private NotificationType type;
    private Boolean isRead;
}
