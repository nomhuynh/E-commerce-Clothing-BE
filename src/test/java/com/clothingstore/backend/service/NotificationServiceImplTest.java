package com.clothingstore.backend.service;

import com.clothingstore.backend.entity.Notification;
import com.clothingstore.backend.repository.NotificationRepository;
import com.clothingstore.backend.service.impl.NotificationServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Test
    void markAsRead_ShouldUpdateFlag() {
        Notification n = new Notification();
        n.setId("n1");
        n.setIsRead(false);

        when(notificationRepository.findById("n1")).thenReturn(Optional.of(n));

        notificationService.markAsRead("n1");

        verify(notificationRepository, times(1)).save(n);
    }
}
