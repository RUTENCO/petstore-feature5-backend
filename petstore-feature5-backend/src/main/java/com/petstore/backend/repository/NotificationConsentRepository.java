package com.petstore.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.petstore.backend.entity.NotificationConsent;
import com.petstore.backend.entity.User;

@Repository
public interface NotificationConsentRepository extends JpaRepository<NotificationConsent, Long> {
    
    @Query("SELECT nc FROM NotificationConsent nc WHERE nc.user.id = :userId AND nc.notificationType = :type")
    Optional<NotificationConsent> findByUserIdAndNotificationType(
            @Param("userId") Long userId, 
            @Param("type") NotificationConsent.NotificationType type);
    
    @Query("SELECT nc FROM NotificationConsent nc WHERE nc.user.id = :userId AND nc.consentGiven = true")
    List<NotificationConsent> findActiveConsentsByUserId(@Param("userId") Long userId);
    
    @Query("SELECT nc.user FROM NotificationConsent nc WHERE nc.notificationType = :type AND nc.consentGiven = true")
    List<User> findUsersWithActiveConsent(@Param("type") NotificationConsent.NotificationType type);
    
    @Query("SELECT COUNT(nc) > 0 FROM NotificationConsent nc WHERE nc.user.id = :userId " +
           "AND nc.notificationType = :type AND nc.consentGiven = true")
    boolean hasActiveConsent(@Param("userId") Long userId, @Param("type") NotificationConsent.NotificationType type);
    
    @Query("SELECT nc FROM NotificationConsent nc JOIN FETCH nc.user WHERE nc.notificationType = :type AND nc.consentGiven = true")
    List<NotificationConsent> findByNotificationTypeAndConsentGivenTrue(@Param("type") NotificationConsent.NotificationType type);
}
