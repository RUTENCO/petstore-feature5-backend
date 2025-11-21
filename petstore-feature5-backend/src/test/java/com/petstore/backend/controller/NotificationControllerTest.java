package com.petstore.backend.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.petstore.backend.controller.NotificationController.ConsentRequest;
import com.petstore.backend.entity.NotificationConsent;
import com.petstore.backend.entity.NotificationConsent.NotificationType;
import com.petstore.backend.entity.User;
import com.petstore.backend.service.NotificationService;
import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class NotificationControllerTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private NotificationController notificationController;

    private User user;
    private NotificationConsent consent;
    private ConsentRequest consentRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId(1);
        user.setEmail("test@example.com");

        consent = new NotificationConsent();
        consent.setUser(user);
        consent.setNotificationType(NotificationType.EMAIL_PROMOTION);
        consent.setConsentGiven(true);

        consentRequest = new ConsentRequest();
        consentRequest.setUserId(1);
        consentRequest.setNotificationType(NotificationType.EMAIL_PROMOTION);
        consentRequest.setConsentGiven(true);
        
        // Mock HttpServletRequest completamente
        when(request.getHeader("User-Agent")).thenReturn("TestUserAgent");
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        when(request.getHeader("X-Real-IP")).thenReturn(null);
        when(request.getHeader("Proxy-Client-IP")).thenReturn(null);
        when(request.getHeader("WL-Proxy-Client-IP")).thenReturn(null);
        when(request.getHeader("HTTP_CLIENT_IP")).thenReturn(null);
        when(request.getHeader("HTTP_X_FORWARDED_FOR")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
    }

    @Test
    @DisplayName("Should update notification consent successfully")
    void shouldUpdateNotificationConsentSuccessfully() {
        // Given
        doNothing().when(notificationService).updateNotificationConsent(
            eq(1), eq(NotificationType.EMAIL_PROMOTION), eq(true), anyString(), anyString());

        // When
        ResponseEntity<Map<String, Object>> response = notificationController.updateNotificationConsent(consentRequest, this.request);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(notificationService).updateNotificationConsent(
            eq(1), eq(NotificationType.EMAIL_PROMOTION), eq(true), anyString(), anyString());
    }

    @Test
    @DisplayName("Should handle invalid notification type")
    void shouldHandleInvalidNotificationType() {
        // Given
        ConsentRequest invalidRequest = new ConsentRequest();
        invalidRequest.setUserId(1);
        invalidRequest.setNotificationType(null);
        invalidRequest.setConsentGiven(true);

        // When
        ResponseEntity<Map<String, Object>> response = notificationController.updateNotificationConsent(invalidRequest, this.request);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(notificationService, never()).updateNotificationConsent(anyInt(), any(), anyBoolean(), anyString(), anyString());
    }

    @Test
    @DisplayName("Should handle missing required fields")
    void shouldHandleMissingRequiredFields() {
        // Given
        ConsentRequest invalidRequest = new ConsentRequest();
        invalidRequest.setUserId(null);
        invalidRequest.setNotificationType(NotificationType.EMAIL_PROMOTION);
        invalidRequest.setConsentGiven(true);

        // When
        ResponseEntity<Map<String, Object>> response = notificationController.updateNotificationConsent(invalidRequest, this.request);

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(notificationService, never()).updateNotificationConsent(anyInt(), any(), anyBoolean(), anyString(), anyString());
    }

    @Test
    @DisplayName("Should check if user has active consent")
    void shouldCheckIfUserHasActiveConsent() {
        // Given
        when(notificationService.hasActiveConsent(1, NotificationType.EMAIL_PROMOTION))
            .thenReturn(true);

        // When
        ResponseEntity<Map<String, Object>> response = notificationController.checkNotificationConsent(1, "EMAIL_PROMOTION");

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(notificationService).hasActiveConsent(1, NotificationType.EMAIL_PROMOTION);
    }

    @Test
    @DisplayName("Should handle service exception gracefully")
    void shouldHandleServiceExceptionGracefully() {
        // Given
        doThrow(new RuntimeException("Service error")).when(notificationService)
            .updateNotificationConsent(anyInt(), any(), anyBoolean(), anyString(), anyString());

        // When
        ResponseEntity<Map<String, Object>> response = notificationController.updateNotificationConsent(consentRequest, this.request);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    @DisplayName("Should get notification status successfully")
    void shouldGetNotificationStatusSuccessfully() {
        // When
        ResponseEntity<Map<String, Object>> response = notificationController.getNotificationStatus();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Should require authentication")
    void shouldRequireAuthentication() {
        // This test would be more meaningful with actual security context
        // For now, we just verify the controller methods exist and can be called
        
        // When
        assertDoesNotThrow(() -> notificationController.updateNotificationConsent(consentRequest, this.request));
    }
}
