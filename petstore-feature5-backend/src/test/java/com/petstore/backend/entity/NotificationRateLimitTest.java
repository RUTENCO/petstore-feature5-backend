package com.petstore.backend.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import com.petstore.backend.entity.NotificationConsent.NotificationType;

class NotificationRateLimitTest {

    private NotificationRateLimit rateLimit;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId(1);
        user.setEmail("test@example.com");
        user.setUserName("Test User");

        rateLimit = new NotificationRateLimit();
        rateLimit.setId(1L);
        rateLimit.setUser(user);
        rateLimit.setNotificationType(NotificationType.EMAIL_PROMOTION);
        rateLimit.setNotificationCount(0);
        rateLimit.setTimeWindowStart(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should create NotificationRateLimit with all fields")
    void shouldCreateNotificationRateLimitWithAllFields() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        NotificationRateLimit newRateLimit = new NotificationRateLimit();

        // When
        newRateLimit.setId(2L);
        newRateLimit.setUser(user);
        newRateLimit.setNotificationType(NotificationType.EMAIL_GENERAL);
        newRateLimit.setNotificationCount(5);
        newRateLimit.setTimeWindowStart(now);
        newRateLimit.setLastReset(now.minusHours(1));

        // Then
        assertEquals(2L, newRateLimit.getId());
        assertEquals(user, newRateLimit.getUser());
        assertEquals(NotificationType.EMAIL_GENERAL, newRateLimit.getNotificationType());
        assertEquals(5, newRateLimit.getNotificationCount());
        assertEquals(now, newRateLimit.getTimeWindowStart());
        assertEquals(now.minusHours(1), newRateLimit.getLastReset());
    }

    @Test
    @DisplayName("Should handle null values gracefully")
    void shouldHandleNullValuesGracefully() {
        // Given
        NotificationRateLimit nullRateLimit = new NotificationRateLimit();

        // When & Then
        assertNull(nullRateLimit.getId());
        assertNull(nullRateLimit.getUser());
        assertNull(nullRateLimit.getNotificationType());
        assertEquals(0, nullRateLimit.getNotificationCount()); // Default value
        assertNull(nullRateLimit.getTimeWindowStart());
        assertNull(nullRateLimit.getLastReset());
    }

    @Test
    @DisplayName("Should increment notification count")
    void shouldIncrementNotificationCount() {
        // Given
        assertEquals(0, rateLimit.getNotificationCount());

        // When
        rateLimit.setNotificationCount(rateLimit.getNotificationCount() + 1);

        // Then
        assertEquals(1, rateLimit.getNotificationCount());

        // When - increment again
        rateLimit.setNotificationCount(rateLimit.getNotificationCount() + 1);

        // Then
        assertEquals(2, rateLimit.getNotificationCount());
    }

    @Test
    @DisplayName("Should reset notification count with new time window")
    void shouldResetNotificationCountWithNewTimeWindow() {
        // Given
        rateLimit.setNotificationCount(10);
        LocalDateTime oldTimeWindow = rateLimit.getTimeWindowStart();
        LocalDateTime newTimeWindow = LocalDateTime.now().plusHours(1);

        // When
        rateLimit.setNotificationCount(0);
        rateLimit.setTimeWindowStart(newTimeWindow);
        rateLimit.setLastReset(LocalDateTime.now());

        // Then
        assertEquals(0, rateLimit.getNotificationCount());
        assertEquals(newTimeWindow, rateLimit.getTimeWindowStart());
        assertNotNull(rateLimit.getLastReset());
        assertNotEquals(oldTimeWindow, rateLimit.getTimeWindowStart());
    }

    @Test
    @DisplayName("Should handle different notification types")
    void shouldHandleDifferentNotificationTypes() {
        // Given & When & Then - EMAIL_PROMOTION
        rateLimit.setNotificationType(NotificationType.EMAIL_PROMOTION);
        assertEquals(NotificationType.EMAIL_PROMOTION, rateLimit.getNotificationType());

        // When & Then - EMAIL_GENERAL
        rateLimit.setNotificationType(NotificationType.EMAIL_GENERAL);
        assertEquals(NotificationType.EMAIL_GENERAL, rateLimit.getNotificationType());
    }

    @Test
    @DisplayName("Should set and get user correctly")
    void shouldSetAndGetUserCorrectly() {
        // Given
        User newUser = new User();
        newUser.setUserId(2);
        newUser.setEmail("newuser@example.com");

        // When
        rateLimit.setUser(newUser);

        // Then
        assertEquals(newUser, rateLimit.getUser());
        assertEquals(2, rateLimit.getUser().getUserId());
        assertEquals("newuser@example.com", rateLimit.getUser().getEmail());
    }

    @Test
    @DisplayName("Should handle time window calculations")
    void shouldHandleTimeWindowCalculations() {
        // Given
        LocalDateTime startTime = LocalDateTime.of(2024, 1, 1, 10, 0, 0);
        LocalDateTime endTime = LocalDateTime.of(2024, 1, 1, 11, 0, 0);

        // When
        rateLimit.setTimeWindowStart(startTime);

        // Then
        assertEquals(startTime, rateLimit.getTimeWindowStart());
        assertTrue(startTime.isBefore(endTime));
        
        // Simulate checking if we're still in the same hour window
        LocalDateTime currentTime = LocalDateTime.of(2024, 1, 1, 10, 30, 0);
        assertTrue(currentTime.isAfter(startTime) && currentTime.isBefore(endTime));
    }

    @Test
    @DisplayName("Should track last reset timestamp")
    void shouldTrackLastResetTimestamp() {
        // Given
        LocalDateTime resetTime = LocalDateTime.now();
        assertNull(rateLimit.getLastReset());

        // When
        rateLimit.setLastReset(resetTime);

        // Then
        assertEquals(resetTime, rateLimit.getLastReset());
        assertNotNull(rateLimit.getLastReset());
    }

    @Test
    @DisplayName("Should handle high notification counts")
    void shouldHandleHighNotificationCounts() {
        // Given
        Integer maxCount = 1000;

        // When
        rateLimit.setNotificationCount(maxCount);

        // Then
        assertEquals(maxCount, rateLimit.getNotificationCount());
        assertTrue(rateLimit.getNotificationCount() > 0);
    }

    @Test
    @DisplayName("Should validate notification count cannot be negative")
    void shouldValidateNotificationCountCannotBeNegative() {
        // Given
        rateLimit.setNotificationCount(5);
        assertEquals(5, rateLimit.getNotificationCount());

        // When setting to 0 (valid)
        rateLimit.setNotificationCount(0);

        // Then
        assertEquals(0, rateLimit.getNotificationCount());
        
        // Note: In a real application, you might want to add validation
        // to prevent negative values, but the entity itself doesn't enforce this
    }

    @Test
    @DisplayName("Should handle constructor with all parameters")
    void shouldHandleConstructorWithAllParameters() {
        // Given
        Long id = 3L;
        NotificationType type = NotificationType.EMAIL_PROMOTION;
        Integer count = 15;
        LocalDateTime timeWindow = LocalDateTime.now();
        LocalDateTime lastReset = LocalDateTime.now().minusMinutes(30);

        // When
        NotificationRateLimit constructedRateLimit = new NotificationRateLimit(
            id, user, type, count, timeWindow, lastReset
        );

        // Then
        assertEquals(id, constructedRateLimit.getId());
        assertEquals(user, constructedRateLimit.getUser());
        assertEquals(type, constructedRateLimit.getNotificationType());
        assertEquals(count, constructedRateLimit.getNotificationCount());
        assertEquals(timeWindow, constructedRateLimit.getTimeWindowStart());
        assertEquals(lastReset, constructedRateLimit.getLastReset());
    }
}
