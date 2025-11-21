package com.petstore.backend.listener;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.petstore.backend.entity.NotificationConsent;
import com.petstore.backend.entity.NotificationConsent.NotificationType;
import com.petstore.backend.entity.Promotion;
import com.petstore.backend.entity.Status;
import com.petstore.backend.entity.User;
import com.petstore.backend.event.PromotionActivatedEvent;
import com.petstore.backend.repository.NotificationConsentRepository;
import com.petstore.backend.service.NotificationService;

@ExtendWith(MockitoExtension.class)
class PromotionActivatedListenerTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private NotificationConsentRepository notificationConsentRepository;

    @InjectMocks
    private PromotionActivatedListener listener;

    private Promotion promotion;
    private User user1, user2;
    private NotificationConsent consent1, consent2;
    private PromotionActivatedEvent event;

    @BeforeEach
    void setUp() {
        // Setup promotion
        promotion = new Promotion();
        promotion.setPromotionId(1);
        promotion.setPromotionName("Test Promotion");
        promotion.setDescription("Test Description");
        
        Status status = new Status();
        status.setStatusId(1);
        status.setStatusName("ACTIVE");
        promotion.setStatus(status);

        // Setup users
        user1 = new User();
        user1.setUserId(1);
        user1.setEmail("user1@test.com");
        user1.setUserName("User 1");

        user2 = new User();
        user2.setUserId(2);
        user2.setEmail("user2@test.com");
        user2.setUserName("User 2");

        // Setup consents
        consent1 = new NotificationConsent();
        consent1.setId(1L);
        consent1.setUser(user1);
        consent1.setNotificationType(NotificationType.EMAIL_PROMOTION);
        consent1.setConsentGiven(true);

        consent2 = new NotificationConsent();
        consent2.setId(2L);
        consent2.setUser(user2);
        consent2.setNotificationType(NotificationType.EMAIL_PROMOTION);
        consent2.setConsentGiven(true);

        event = new PromotionActivatedEvent(this, promotion);
    }

    @Test
    @DisplayName("Should send notifications to all users with consent")
    void shouldSendNotificationsToUsersWithConsent() throws Exception {
        // Given
        List<NotificationConsent> consents = Arrays.asList(consent1, consent2);
        when(notificationConsentRepository.findByNotificationTypeAndConsentGivenTrue(NotificationType.EMAIL_PROMOTION))
            .thenReturn(consents);

        // When
        listener.handlePromotionActivated(event);

        // Wait a bit for async processing
        Thread.sleep(100);

        // Then
        verify(notificationConsentRepository).findByNotificationTypeAndConsentGivenTrue(NotificationType.EMAIL_PROMOTION);
        verify(notificationService, times(2)).sendPromotionNotificationToUser(eq(promotion), any(User.class));
        verify(notificationService).sendPromotionNotificationToUser(promotion, user1);
        verify(notificationService).sendPromotionNotificationToUser(promotion, user2);
    }

    @Test
    @DisplayName("Should handle empty consent list")
    void shouldHandleEmptyConsentList() throws Exception {
        // Given
        when(notificationConsentRepository.findByNotificationTypeAndConsentGivenTrue(NotificationType.EMAIL_PROMOTION))
            .thenReturn(Collections.emptyList());

        // When
        listener.handlePromotionActivated(event);

        // Wait a bit for async processing
        Thread.sleep(100);

        // Then
        verify(notificationConsentRepository).findByNotificationTypeAndConsentGivenTrue(NotificationType.EMAIL_PROMOTION);
        verify(notificationService, never()).sendPromotionNotificationToUser(any(), any());
    }

    @Test
    @DisplayName("Should handle notification service exception")
    void shouldHandleNotificationServiceException() throws Exception {
        // Given
        List<NotificationConsent> consents = Arrays.asList(consent1);
        when(notificationConsentRepository.findByNotificationTypeAndConsentGivenTrue(NotificationType.EMAIL_PROMOTION))
            .thenReturn(consents);
        doThrow(new RuntimeException("Email service error"))
            .when(notificationService).sendPromotionNotificationToUser(promotion, user1);

        // When & Then - Should not throw exception
        assertDoesNotThrow(() -> listener.handlePromotionActivated(event));

        // Wait a bit for async processing
        Thread.sleep(100);

        verify(notificationService).sendPromotionNotificationToUser(promotion, user1);
    }

    @Test
    @DisplayName("Should handle repository exception")
    void shouldHandleRepositoryException() throws Exception {
        // Given
        when(notificationConsentRepository.findByNotificationTypeAndConsentGivenTrue(NotificationType.EMAIL_PROMOTION))
            .thenThrow(new RuntimeException("Database error"));

        // When & Then - Should not throw exception
        assertDoesNotThrow(() -> listener.handlePromotionActivated(event));

        // Wait a bit for async processing
        Thread.sleep(100);

        verify(notificationService, never()).sendPromotionNotificationToUser(any(), any());
    }

    @Test
    @DisplayName("Should handle single user notification failure")
    void shouldHandleSingleUserNotificationFailure() throws Exception {
        // Given
        List<NotificationConsent> consents = Arrays.asList(consent1, consent2);
        when(notificationConsentRepository.findByNotificationTypeAndConsentGivenTrue(NotificationType.EMAIL_PROMOTION))
            .thenReturn(consents);
        
        // First user fails, second succeeds
        doThrow(new RuntimeException("Email failed for user1"))
            .when(notificationService).sendPromotionNotificationToUser(promotion, user1);
        doNothing().when(notificationService).sendPromotionNotificationToUser(promotion, user2);

        // When
        assertDoesNotThrow(() -> listener.handlePromotionActivated(event));

        // Wait a bit for async processing
        Thread.sleep(100);

        // Then - Both should be attempted
        verify(notificationService).sendPromotionNotificationToUser(promotion, user1);
        verify(notificationService).sendPromotionNotificationToUser(promotion, user2);
    }

    @Test
    @DisplayName("Should handle null promotion in event")
    void shouldHandleNullPromotionInEvent() throws Exception {
        // Given
        PromotionActivatedEvent nullEvent = new PromotionActivatedEvent(this, null);

        // When & Then - Should not throw exception
        assertDoesNotThrow(() -> listener.handlePromotionActivated(nullEvent));

        // Wait a bit for async processing
        Thread.sleep(100);

        // Should not call repository or service
        verify(notificationConsentRepository, never()).findByNotificationTypeAndConsentGivenTrue(any());
        verify(notificationService, never()).sendPromotionNotificationToUser(any(), any());
    }
}
