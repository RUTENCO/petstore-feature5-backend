package com.petstore.backend.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import com.petstore.backend.entity.NotificationConsent;
import com.petstore.backend.entity.User;
import com.petstore.backend.entity.NotificationConsent.NotificationType;
import com.petstore.backend.entity.Role;

@DataJpaTest
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.show-sql=false"
})
class NotificationConsentRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private NotificationConsentRepository notificationConsentRepository;

    private User testUser;
    private User anotherUser;

    @BeforeEach
    void setUp() {
        // Create Role first  
        Role role = new Role();
        role.setRoleName("USER");
        role = entityManager.persistAndFlush(role);
        
        // Create test users
        testUser = new User();
        testUser.setEmail("test@example.com");
        testUser.setUserName("Test User");
        testUser.setPassword("password");
        testUser.setRole(role);
        entityManager.persist(testUser);

        anotherUser = new User();
        anotherUser.setEmail("another@example.com");
        anotherUser.setUserName("Another User");
        anotherUser.setPassword("password");
        anotherUser.setRole(role);
        entityManager.persist(anotherUser);

        entityManager.flush();
    }

    @Test
    @DisplayName("Should find consent by user ID and notification type")
    void shouldFindConsentByUserIdAndNotificationType() {
        // Given
        NotificationConsent consent = createConsent(testUser, NotificationType.EMAIL_PROMOTION, true);
        entityManager.persistAndFlush(consent);

        // When
        Optional<NotificationConsent> result = notificationConsentRepository
            .findByUserIdAndNotificationType(testUser.getUserId().longValue(), NotificationType.EMAIL_PROMOTION);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testUser.getUserId(), result.get().getUser().getUserId());
        assertEquals(NotificationType.EMAIL_PROMOTION, result.get().getNotificationType());
        assertTrue(result.get().getConsentGiven());
    }

    @Test
    @DisplayName("Should return empty when consent not found")
    void shouldReturnEmptyWhenConsentNotFound() {
        // When
        Optional<NotificationConsent> result = notificationConsentRepository
            .findByUserIdAndNotificationType(999L, NotificationType.EMAIL_PROMOTION);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should find active consents by user ID")
    void shouldFindActiveConsentsByUserId() {
        // Given
        NotificationConsent activePromotion = createConsent(testUser, NotificationType.EMAIL_PROMOTION, true);
        NotificationConsent activeGeneral = createConsent(testUser, NotificationType.EMAIL_GENERAL, true);
        NotificationConsent inactiveConsent = createConsent(testUser, NotificationType.EMAIL_PROMOTION, false);
        
        entityManager.persist(activePromotion);
        entityManager.persist(activeGeneral);
        entityManager.persist(inactiveConsent);
        entityManager.flush();

        // When
        List<NotificationConsent> results = notificationConsentRepository
            .findActiveConsentsByUserId(testUser.getUserId().longValue());

        // Then
        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(consent -> consent.getConsentGiven()));
        assertTrue(results.stream().anyMatch(consent -> 
            consent.getNotificationType() == NotificationType.EMAIL_PROMOTION));
        assertTrue(results.stream().anyMatch(consent -> 
            consent.getNotificationType() == NotificationType.EMAIL_GENERAL));
    }

    @Test
    @DisplayName("Should find users with active consent for notification type")
    void shouldFindUsersWithActiveConsentForNotificationType() {
        // Given
        NotificationConsent activeConsent1 = createConsent(testUser, NotificationType.EMAIL_PROMOTION, true);
        NotificationConsent activeConsent2 = createConsent(anotherUser, NotificationType.EMAIL_PROMOTION, true);
        NotificationConsent inactiveConsent = createConsent(testUser, NotificationType.EMAIL_GENERAL, false);
        
        entityManager.persist(activeConsent1);
        entityManager.persist(activeConsent2);
        entityManager.persist(inactiveConsent);
        entityManager.flush();

        // When
        List<User> users = notificationConsentRepository
            .findUsersWithActiveConsent(NotificationType.EMAIL_PROMOTION);

        // Then
        assertEquals(2, users.size());
        assertTrue(users.stream().anyMatch(user -> user.getUserId().equals(testUser.getUserId())));
        assertTrue(users.stream().anyMatch(user -> user.getUserId().equals(anotherUser.getUserId())));
    }

    @Test
    @DisplayName("Should check if user has active consent")
    void shouldCheckIfUserHasActiveConsent() {
        // Given
        NotificationConsent activeConsent = createConsent(testUser, NotificationType.EMAIL_PROMOTION, true);
        NotificationConsent inactiveConsent = createConsent(anotherUser, NotificationType.EMAIL_PROMOTION, false);
        
        entityManager.persist(activeConsent);
        entityManager.persist(inactiveConsent);
        entityManager.flush();

        // When & Then
        assertTrue(notificationConsentRepository.hasActiveConsent(
            testUser.getUserId().longValue(), NotificationType.EMAIL_PROMOTION));
        
        assertFalse(notificationConsentRepository.hasActiveConsent(
            anotherUser.getUserId().longValue(), NotificationType.EMAIL_PROMOTION));
        
        assertFalse(notificationConsentRepository.hasActiveConsent(
            testUser.getUserId().longValue(), NotificationType.EMAIL_GENERAL));
    }

    @Test
    @DisplayName("Should find consents with user fetch join")
    void shouldFindConsentsWithUserFetchJoin() {
        // Given
        NotificationConsent consent1 = createConsent(testUser, NotificationType.EMAIL_PROMOTION, true);
        NotificationConsent consent2 = createConsent(anotherUser, NotificationType.EMAIL_PROMOTION, true);
        NotificationConsent inactiveConsent = createConsent(testUser, NotificationType.EMAIL_PROMOTION, false);
        
        entityManager.persist(consent1);
        entityManager.persist(consent2);
        entityManager.persist(inactiveConsent);
        entityManager.flush();
        entityManager.clear(); // Clear persistence context to test fetch join

        // When
        List<NotificationConsent> consents = notificationConsentRepository
            .findByNotificationTypeAndConsentGivenTrue(NotificationType.EMAIL_PROMOTION);

        // Then
        assertEquals(2, consents.size());
        
        // Verify that users are fetched (no lazy loading exception should occur)
        for (NotificationConsent consent : consents) {
            assertNotNull(consent.getUser());
            assertNotNull(consent.getUser().getEmail());
            assertTrue(consent.getConsentGiven());
        }
    }

    @Test
    @DisplayName("Should return empty list when no active consents exist")
    void shouldReturnEmptyListWhenNoActiveConsentsExist() {
        // Given
        NotificationConsent inactiveConsent = createConsent(testUser, NotificationType.EMAIL_PROMOTION, false);
        entityManager.persistAndFlush(inactiveConsent);

        // When
        List<NotificationConsent> activeConsents = notificationConsentRepository
            .findActiveConsentsByUserId(testUser.getUserId().longValue());
        
        List<User> usersWithConsent = notificationConsentRepository
            .findUsersWithActiveConsent(NotificationType.EMAIL_PROMOTION);

        // Then
        assertTrue(activeConsents.isEmpty());
        assertTrue(usersWithConsent.isEmpty());
    }

    @Test
    @DisplayName("Should handle multiple consent types for same user")
    void shouldHandleMultipleConsentTypesForSameUser() {
        // Given
        NotificationConsent promotionConsent = createConsent(testUser, NotificationType.EMAIL_PROMOTION, true);
        NotificationConsent generalConsent = createConsent(testUser, NotificationType.EMAIL_GENERAL, false);
        
        entityManager.persist(promotionConsent);
        entityManager.persist(generalConsent);
        entityManager.flush();

        // When
        boolean hasPromotionConsent = notificationConsentRepository
            .hasActiveConsent(testUser.getUserId().longValue(), NotificationType.EMAIL_PROMOTION);
        
        boolean hasGeneralConsent = notificationConsentRepository
            .hasActiveConsent(testUser.getUserId().longValue(), NotificationType.EMAIL_GENERAL);

        // Then
        assertTrue(hasPromotionConsent);
        assertFalse(hasGeneralConsent);
    }

    private NotificationConsent createConsent(User user, NotificationType type, boolean consentGiven) {
        NotificationConsent consent = new NotificationConsent();
        consent.setUser(user);
        consent.setNotificationType(type);
        consent.setConsentGiven(consentGiven);
        consent.setConsentDate(LocalDateTime.now());
        consent.setIpAddress("127.0.0.1");
        consent.setUserAgent("Test Agent");
        return consent;
    }
}
