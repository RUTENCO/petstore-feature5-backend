package com.petstore.backend.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import com.petstore.backend.entity.NotificationConsent.NotificationType;
import com.petstore.backend.entity.NotificationLog.NotificationStatus;

class NotificationLogTest {

    private NotificationLog notificationLog;
    private User user;
    private Promotion promotion;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId(1);
        user.setEmail("test@example.com");
        user.setUserName("Test User");

        promotion = new Promotion();
        promotion.setPromotionId(1);
        promotion.setPromotionName("Test Promotion");

        notificationLog = new NotificationLog();
        notificationLog.setId(1L);
        notificationLog.setUser(user);
        notificationLog.setPromotion(promotion);
        notificationLog.setNotificationType(NotificationType.EMAIL_PROMOTION);
        notificationLog.setStatus(NotificationStatus.SENT);
        notificationLog.setRecipient("test@example.com");
        notificationLog.setSubject("Test Subject");
        notificationLog.setContent("Test Content");
        notificationLog.setSentAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should create NotificationLog with all fields")
    void shouldCreateNotificationLogWithAllFields() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        NotificationLog newLog = new NotificationLog();

        // When
        newLog.setId(2L);
        newLog.setUser(user);
        newLog.setPromotion(promotion);
        newLog.setNotificationType(NotificationType.EMAIL_GENERAL);
        newLog.setStatus(NotificationStatus.DELIVERED);
        newLog.setRecipient("newuser@example.com");
        newLog.setSubject("New Subject");
        newLog.setContent("New Content");
        newLog.setSentAt(now);
        newLog.setErrorMessage(null);
        newLog.setExternalId("ext123");

        // Then
        assertEquals(2L, newLog.getId());
        assertEquals(user, newLog.getUser());
        assertEquals(promotion, newLog.getPromotion());
        assertEquals(NotificationType.EMAIL_GENERAL, newLog.getNotificationType());
        assertEquals(NotificationStatus.DELIVERED, newLog.getStatus());
        assertEquals("newuser@example.com", newLog.getRecipient());
        assertEquals("New Subject", newLog.getSubject());
        assertEquals("New Content", newLog.getContent());
        assertEquals(now, newLog.getSentAt());
        assertNull(newLog.getErrorMessage());
        assertEquals("ext123", newLog.getExternalId());
    }

    @Test
    @DisplayName("Should handle null values gracefully")
    void shouldHandleNullValuesGracefully() {
        // Given
        NotificationLog nullLog = new NotificationLog();

        // When & Then
        assertNull(nullLog.getId());
        assertNull(nullLog.getUser());
        assertNull(nullLog.getPromotion());
        assertNull(nullLog.getNotificationType());
        assertNull(nullLog.getStatus());
        assertNull(nullLog.getRecipient());
        assertNull(nullLog.getSubject());
        assertNull(nullLog.getContent());
        assertNull(nullLog.getSentAt());
        assertNull(nullLog.getErrorMessage());
        assertNull(nullLog.getExternalId());
    }

    @Test
    @DisplayName("Should test notification status enum values")
    void shouldTestNotificationStatusEnumValues() {
        // Then
        assertEquals(6, NotificationStatus.values().length);
        assertEquals(NotificationStatus.PENDING, NotificationStatus.valueOf("PENDING"));
        assertEquals(NotificationStatus.SENT, NotificationStatus.valueOf("SENT"));
        assertEquals(NotificationStatus.DELIVERED, NotificationStatus.valueOf("DELIVERED"));
        assertEquals(NotificationStatus.FAILED, NotificationStatus.valueOf("FAILED"));
        assertEquals(NotificationStatus.BOUNCED, NotificationStatus.valueOf("BOUNCED"));
        assertEquals(NotificationStatus.RATE_LIMITED, NotificationStatus.valueOf("RATE_LIMITED"));
    }

    @Test
    @DisplayName("Should update status from PENDING to SENT")
    void shouldUpdateStatusFromPendingToSent() {
        // Given
        notificationLog.setStatus(NotificationStatus.PENDING);
        notificationLog.setSentAt(null);

        // When
        notificationLog.setStatus(NotificationStatus.SENT);
        notificationLog.setSentAt(LocalDateTime.now());

        // Then
        assertEquals(NotificationStatus.SENT, notificationLog.getStatus());
        assertNotNull(notificationLog.getSentAt());
    }

    @Test
    @DisplayName("Should handle failed notification with error message")
    void shouldHandleFailedNotificationWithErrorMessage() {
        // Given
        String errorMessage = "SMTP connection failed";

        // When
        notificationLog.setStatus(NotificationStatus.FAILED);
        notificationLog.setErrorMessage(errorMessage);

        // Then
        assertEquals(NotificationStatus.FAILED, notificationLog.getStatus());
        assertEquals(errorMessage, notificationLog.getErrorMessage());
    }

    @Test
    @DisplayName("Should set and get promotion correctly")
    void shouldSetAndGetPromotionCorrectly() {
        // Given
        Promotion newPromotion = new Promotion();
        newPromotion.setPromotionId(2);
        newPromotion.setPromotionName("New Promotion");

        // When
        notificationLog.setPromotion(newPromotion);

        // Then
        assertEquals(newPromotion, notificationLog.getPromotion());
        assertEquals(2, notificationLog.getPromotion().getPromotionId());
        assertEquals("New Promotion", notificationLog.getPromotion().getPromotionName());
    }

    @Test
    @DisplayName("Should handle external ID for tracking")
    void shouldHandleExternalIdForTracking() {
        // Given
        String externalId = "gmail-msg-12345";

        // When
        notificationLog.setExternalId(externalId);

        // Then
        assertEquals(externalId, notificationLog.getExternalId());
    }

    @Test
    @DisplayName("Should validate recipient email format")
    void shouldValidateRecipientEmailFormat() {
        // Given
        String validEmail = "user@domain.com";
        String anotherValidEmail = "test.email+tag@example.org";

        // When & Then
        notificationLog.setRecipient(validEmail);
        assertEquals(validEmail, notificationLog.getRecipient());

        notificationLog.setRecipient(anotherValidEmail);
        assertEquals(anotherValidEmail, notificationLog.getRecipient());
    }

    @Test
    @DisplayName("Should handle long content text")
    void shouldHandleLongContentText() {
        // Given
        String longContent = "Lorem ipsum ".repeat(100) + "dolor sit amet.";

        // When
        notificationLog.setContent(longContent);

        // Then
        assertEquals(longContent, notificationLog.getContent());
        assertTrue(notificationLog.getContent().length() > 1000);
    }

    @Test
    @DisplayName("Should handle promotion-less notification")
    void shouldHandlePromotionLessNotification() {
        // Given
        NotificationLog generalLog = new NotificationLog();

        // When
        generalLog.setUser(user);
        generalLog.setPromotion(null); // No promotion for general notifications
        generalLog.setNotificationType(NotificationType.EMAIL_GENERAL);
        generalLog.setStatus(NotificationStatus.SENT);
        generalLog.setRecipient("test@example.com");
        generalLog.setSubject("General Notification");

        // Then
        assertNotNull(generalLog.getUser());
        assertNull(generalLog.getPromotion());
        assertEquals(NotificationType.EMAIL_GENERAL, generalLog.getNotificationType());
        assertEquals(NotificationStatus.SENT, generalLog.getStatus());
    }
}
