package com.petstore.backend.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.petstore.backend.entity.NotificationConsent;
import com.petstore.backend.entity.NotificationConsent.NotificationType;
import com.petstore.backend.entity.NotificationLog;
import com.petstore.backend.entity.NotificationRateLimit;
import com.petstore.backend.entity.Promotion;
import com.petstore.backend.entity.Status;
import com.petstore.backend.entity.User;
import com.petstore.backend.repository.NotificationConsentRepository;
import com.petstore.backend.repository.NotificationLogRepository;
import com.petstore.backend.repository.NotificationRateLimitRepository;
import com.petstore.backend.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationConsentRepository consentRepository;

    @Mock
    private NotificationLogRepository logRepository;

    @Mock
    private NotificationRateLimitRepository rateLimitRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private NotificationService notificationService;

    private User user;
    private Promotion promotion;
    private NotificationConsent consent;

    @BeforeEach
    void setUp() {
        // Setup user
        user = new User();
        user.setUserId(1);
        user.setEmail("test@example.com");
        user.setUserName("Test User");

        // Setup promotion
        promotion = new Promotion();
        promotion.setPromotionId(1);
        promotion.setPromotionName("Test Promotion");
        promotion.setDescription("Test Description");
        promotion.setDiscountValue(25.0);
        promotion.setStartDate(LocalDate.of(2025, 11, 20));
        promotion.setEndDate(LocalDate.of(2025, 12, 31));

        Status status = new Status();
        status.setStatusId(1);
        status.setStatusName("ACTIVE");
        promotion.setStatus(status);

        // Setup consent
        consent = new NotificationConsent();
        consent.setId(1L);
        consent.setUser(user);
        consent.setNotificationType(NotificationType.EMAIL_PROMOTION);
        consent.setConsentGiven(true);
        
        // Configurar valores de rate limit usando ReflectionTestUtils
        ReflectionTestUtils.setField(notificationService, "emailRateLimitPerHour", 10);
        ReflectionTestUtils.setField(notificationService, "rateLimitWindowHours", 1);
        ReflectionTestUtils.setField(notificationService, "frontendUrl", "https://test-frontend.com");
    }

    @Test
    @DisplayName("Should send promotion notification to user successfully")
    void shouldSendPromotionNotificationToUserSuccessfully() {
        // Given
        when(consentRepository.hasActiveConsent(eq(1L), eq(NotificationType.EMAIL_PROMOTION)))
            .thenReturn(true);
        when(logRepository.countNotificationsSentSince(eq(1L), eq(NotificationType.EMAIL_PROMOTION), any(LocalDateTime.class)))
            .thenReturn(0L); // No previous notifications
        when(emailService.sendEmail(anyString(), anyString(), anyString()))
            .thenReturn(true);

        // When
        notificationService.sendPromotionNotificationToUser(promotion, user);

        // Then
        verify(consentRepository).hasActiveConsent(eq(1L), eq(NotificationType.EMAIL_PROMOTION));
        verify(emailService).sendEmail(eq("test@example.com"), anyString(), anyString());
        verify(logRepository).save(any(NotificationLog.class));
        verify(rateLimitRepository).save(any(NotificationRateLimit.class));
    }

    @Test
    @DisplayName("Should not send notification when user has no consent")
    void shouldNotSendNotificationWhenUserHasNoConsent() {
        // Given
        when(consentRepository.hasActiveConsent(eq(1L), eq(NotificationType.EMAIL_PROMOTION)))
            .thenReturn(false);

        // When
        notificationService.sendPromotionNotificationToUser(promotion, user);

        // Then
        verify(consentRepository).hasActiveConsent(eq(1L), eq(NotificationType.EMAIL_PROMOTION));
        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
        verify(logRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should handle email service failure")
    void shouldHandleEmailServiceFailure() {
        // Given
        when(consentRepository.hasActiveConsent(eq(1L), eq(NotificationType.EMAIL_PROMOTION)))
            .thenReturn(true);
        when(logRepository.countNotificationsSentSince(eq(1L), eq(NotificationType.EMAIL_PROMOTION), any(LocalDateTime.class)))
            .thenReturn(0L);
        when(emailService.sendEmail(anyString(), anyString(), anyString()))
            .thenReturn(false);

        // When
        notificationService.sendPromotionNotificationToUser(promotion, user);

        // Then
        verify(emailService).sendEmail(eq("test@example.com"), anyString(), anyString());
        verify(logRepository).save(any(NotificationLog.class));
        verify(rateLimitRepository).save(any(NotificationRateLimit.class));
    }

    @Test
    @DisplayName("Should send notifications to multiple users")
    void shouldSendNotificationsToMultipleUsers() {
        // Given
        User user1 = new User();
        user1.setUserId(1);
        user1.setEmail("user1@test.com");

        User user2 = new User();
        user2.setUserId(2);
        user2.setEmail("user2@test.com");

        List<User> users = Arrays.asList(user1, user2);
        when(consentRepository.findUsersWithActiveConsent(NotificationType.EMAIL_PROMOTION))
            .thenReturn(users);
        when(logRepository.countNotificationsSentSince(anyLong(), eq(NotificationType.EMAIL_PROMOTION), any(LocalDateTime.class)))
            .thenReturn(0L);
        when(emailService.sendEmail(anyString(), anyString(), anyString()))
            .thenReturn(true);

        // When
        notificationService.sendPromotionNotification(promotion);

        // Then
        verify(consentRepository).findUsersWithActiveConsent(NotificationType.EMAIL_PROMOTION);
        verify(emailService, times(2)).sendEmail(anyString(), anyString(), anyString());
        verify(logRepository, times(2)).save(any(NotificationLog.class));
        verify(rateLimitRepository, times(2)).save(any(NotificationRateLimit.class));
    }

    @Test
    @DisplayName("Should update notification consent")
    void shouldUpdateNotificationConsent() {
        // Given
        when(consentRepository.findByUserIdAndNotificationType(1L, NotificationType.EMAIL_PROMOTION))
            .thenReturn(Optional.of(consent));
        when(consentRepository.save(any(NotificationConsent.class)))
            .thenReturn(consent);

        // When
        notificationService.updateNotificationConsent(
            1, NotificationType.EMAIL_PROMOTION, true, "Test", "127.0.0.1"
        );

        // Then
        verify(consentRepository).findByUserIdAndNotificationType(1L, NotificationType.EMAIL_PROMOTION);
        verify(consentRepository).save(any(NotificationConsent.class));
    }

    @Test
    @DisplayName("Should create new consent when none exists")
    void shouldCreateNewConsentWhenNoneExists() {
        // Given
        when(consentRepository.findByUserIdAndNotificationType(1L, NotificationType.EMAIL_PROMOTION))
            .thenReturn(Optional.empty());
        when(userRepository.findById(1))
            .thenReturn(Optional.of(user));
        when(consentRepository.save(any(NotificationConsent.class)))
            .thenReturn(consent);

        // When
        notificationService.updateNotificationConsent(
            1, NotificationType.EMAIL_PROMOTION, true, "Test", "127.0.0.1"
        );

        // Then
        verify(consentRepository).findByUserIdAndNotificationType(1L, NotificationType.EMAIL_PROMOTION);
        verify(userRepository).findById(1);
        verify(consentRepository).save(any(NotificationConsent.class));
    }

    @Test
    @DisplayName("Should check if user has active consent")
    void shouldCheckIfUserHasActiveConsent() {
        // Given
        when(consentRepository.hasActiveConsent(1L, NotificationType.EMAIL_PROMOTION))
            .thenReturn(true);

        // When
        boolean hasConsent = notificationService.hasActiveConsent(1, NotificationType.EMAIL_PROMOTION);

        // Then
        assertTrue(hasConsent);
        verify(consentRepository).hasActiveConsent(1L, NotificationType.EMAIL_PROMOTION);
    }

    @Test
    @DisplayName("Should format discount value correctly for integer")
    void shouldFormatDiscountValueCorrectlyForInteger() {
        // Given - promotion with integer discount
        promotion.setDiscountValue(25.0);
        when(consentRepository.hasActiveConsent(eq(1L), eq(NotificationType.EMAIL_PROMOTION)))
            .thenReturn(true);
        when(logRepository.countNotificationsSentSince(eq(1L), eq(NotificationType.EMAIL_PROMOTION), any(LocalDateTime.class)))
            .thenReturn(0L);
        when(emailService.sendEmail(anyString(), anyString(), anyString()))
            .thenReturn(true);

        // When
        notificationService.sendPromotionNotificationToUser(promotion, user);

        // Then
        verify(emailService).sendEmail(anyString(), anyString(), argThat(content -> 
            content.contains("¡25% DE DESCUENTO!")
        ));
        verify(rateLimitRepository).save(any(NotificationRateLimit.class));
    }

    @Test
    @DisplayName("Should format discount value correctly for decimal")
    void shouldFormatDiscountValueCorrectlyForDecimal() {
        // Given - promotion with decimal discount
        promotion.setDiscountValue(1.5);
        when(consentRepository.hasActiveConsent(eq(1L), eq(NotificationType.EMAIL_PROMOTION)))
            .thenReturn(true);
        when(logRepository.countNotificationsSentSince(eq(1L), eq(NotificationType.EMAIL_PROMOTION), any(LocalDateTime.class)))
            .thenReturn(0L);
        when(emailService.sendEmail(anyString(), anyString(), anyString()))
            .thenReturn(true);

        // When
        notificationService.sendPromotionNotificationToUser(promotion, user);

        // Then - Acepta tanto formato español (1,5%) como formato inglés (1.5%)
        verify(emailService).sendEmail(anyString(), anyString(), argThat(content -> 
            content.contains("¡1,5% DE DESCUENTO!") || content.contains("¡1.5% DE DESCUENTO!")
        ));
        verify(rateLimitRepository).save(any(NotificationRateLimit.class));
    }

    @Test
    @DisplayName("Should handle null discount value")
    void shouldHandleNullDiscountValue() {
        // Given
        promotion.setDiscountValue(null);
        when(consentRepository.hasActiveConsent(eq(1L), eq(NotificationType.EMAIL_PROMOTION)))
            .thenReturn(true);
        when(logRepository.countNotificationsSentSince(eq(1L), eq(NotificationType.EMAIL_PROMOTION), any(LocalDateTime.class)))
            .thenReturn(0L);
        when(emailService.sendEmail(anyString(), anyString(), anyString()))
            .thenReturn(true);

        // When
        notificationService.sendPromotionNotificationToUser(promotion, user);

        // Then
        verify(emailService).sendEmail(anyString(), anyString(), argThat(content -> 
            content.contains("¡0% DE DESCUENTO!")
        ));
        verify(rateLimitRepository).save(any(NotificationRateLimit.class));
    }

    @Test
    @DisplayName("Should respect rate limiting")
    void shouldRespectRateLimiting() {
        // Given
        when(consentRepository.hasActiveConsent(eq(1L), eq(NotificationType.EMAIL_PROMOTION)))
            .thenReturn(true);
        when(logRepository.countNotificationsSentSince(eq(1L), eq(NotificationType.EMAIL_PROMOTION), any(LocalDateTime.class)))
            .thenReturn(15L); // Limit exceeded (limit is 10 per hour)

        // When
        notificationService.sendPromotionNotificationToUser(promotion, user);

        // Then
        verify(consentRepository).hasActiveConsent(eq(1L), eq(NotificationType.EMAIL_PROMOTION));
        verify(logRepository).countNotificationsSentSince(eq(1L), eq(NotificationType.EMAIL_PROMOTION), any(LocalDateTime.class));
        // Should not send email due to rate limiting
        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
        verify(logRepository).save(any(NotificationLog.class));
    }
}
