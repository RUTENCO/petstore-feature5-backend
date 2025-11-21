package com.petstore.backend.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import com.petstore.backend.entity.NotificationConsent.NotificationType;
import com.petstore.backend.entity.Role;

class NotificationConsentTest {

    private NotificationConsent consent;
    private User user;

    @BeforeEach
    void setUp() {
        // Create Role first
        Role role = new Role();
        role.setRoleId(1);
        role.setRoleName("USER");
        
        user = new User();
        user.setUserId(1);
        user.setEmail("test@example.com");
        user.setUserName("Test User");
        user.setRole(role);

        consent = new NotificationConsent();
        consent.setId(1L);
        consent.setUser(user);
        consent.setNotificationType(NotificationType.EMAIL_PROMOTION);
        consent.setConsentGiven(true);
        consent.setConsentDate(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should create NotificationConsent with all fields")
    void shouldCreateNotificationConsentWithAllFields() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        NotificationConsent newConsent = new NotificationConsent();

        // When
        newConsent.setId(2L);
        newConsent.setUser(user);
        newConsent.setNotificationType(NotificationType.EMAIL_GENERAL);
        newConsent.setConsentGiven(false);
        newConsent.setConsentDate(now);
        newConsent.setLastModified(now);
        newConsent.setIpAddress("192.168.1.1");
        newConsent.setUserAgent("Test Agent");

        // Then
        assertEquals(2L, newConsent.getId());
        assertEquals(user, newConsent.getUser());
        assertEquals(NotificationType.EMAIL_GENERAL, newConsent.getNotificationType());
        assertFalse(newConsent.getConsentGiven());
        assertEquals(now, newConsent.getConsentDate());
        assertEquals(now, newConsent.getLastModified());
        assertEquals("192.168.1.1", newConsent.getIpAddress());
        assertEquals("Test Agent", newConsent.getUserAgent());
    }

    @Test
    @DisplayName("Should handle null values gracefully")
    void shouldHandleNullValuesGracefully() {
        // Given
        NotificationConsent nullConsent = new NotificationConsent();

        // When & Then
        assertNull(nullConsent.getId());
        assertNull(nullConsent.getUser());
        assertNull(nullConsent.getNotificationType());
        assertEquals(false, nullConsent.getConsentGiven()); // Default value is false
        assertNull(nullConsent.getConsentDate());
        assertNull(nullConsent.getLastModified());
        assertNull(nullConsent.getIpAddress());
        assertNull(nullConsent.getUserAgent());
    }

    @Test
    @DisplayName("Should test notification type enum values")
    void shouldTestNotificationTypeEnumValues() {
        // Then
        assertEquals(2, NotificationType.values().length);
        assertEquals(NotificationType.EMAIL_PROMOTION, NotificationType.valueOf("EMAIL_PROMOTION"));
        assertEquals(NotificationType.EMAIL_GENERAL, NotificationType.valueOf("EMAIL_GENERAL"));
    }

    @Test
    @DisplayName("Should update consent properly")
    void shouldUpdateConsentProperly() {
        // Given
        assertTrue(consent.getConsentGiven());
        assertEquals(NotificationType.EMAIL_PROMOTION, consent.getNotificationType());

        // When
        consent.setConsentGiven(false);
        consent.setNotificationType(NotificationType.EMAIL_GENERAL);
        consent.setLastModified(LocalDateTime.now());

        // Then
        assertFalse(consent.getConsentGiven());
        assertEquals(NotificationType.EMAIL_GENERAL, consent.getNotificationType());
        assertNotNull(consent.getLastModified());
    }

    @Test
    @DisplayName("Should set and get user correctly")
    void shouldSetAndGetUserCorrectly() {
        // Given
        User newUser = new User();
        newUser.setUserId(2);
        newUser.setEmail("newuser@example.com");

        // When
        consent.setUser(newUser);

        // Then
        assertEquals(newUser, consent.getUser());
        assertEquals(2, consent.getUser().getUserId());
        assertEquals("newuser@example.com", consent.getUser().getEmail());
    }

    @Test
    @DisplayName("Should handle IP address validation")
    void shouldHandleIpAddressValidation() {
        // Given
        String validIpv4 = "192.168.1.1";
        String validIpv6 = "2001:0db8:85a3:0000:0000:8a2e:0370:7334";
        String localhost = "127.0.0.1";

        // When & Then
        consent.setIpAddress(validIpv4);
        assertEquals(validIpv4, consent.getIpAddress());

        consent.setIpAddress(validIpv6);
        assertEquals(validIpv6, consent.getIpAddress());

        consent.setIpAddress(localhost);
        assertEquals(localhost, consent.getIpAddress());
    }

    @Test
    @DisplayName("Should handle user agent")
    void shouldHandleUserAgent() {
        // Given
        String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36";

        // When
        consent.setUserAgent(userAgent);

        // Then
        assertEquals(userAgent, consent.getUserAgent());
    }
}
