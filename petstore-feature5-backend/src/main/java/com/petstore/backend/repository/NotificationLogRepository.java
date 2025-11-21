package com.petstore.backend.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.petstore.backend.entity.NotificationConsent;
import com.petstore.backend.entity.NotificationLog;

@Repository
public interface NotificationLogRepository extends JpaRepository<NotificationLog, Long> {
    
    @Query("SELECT COUNT(nl) FROM NotificationLog nl WHERE nl.user.id = :userId " +
           "AND nl.notificationType = :type AND nl.sentAt >= :startTime")
    Long countNotificationsSentSince(
            @Param("userId") Long userId, 
            @Param("type") NotificationConsent.NotificationType type,
            @Param("startTime") LocalDateTime startTime);
    
    @Query("SELECT nl FROM NotificationLog nl WHERE nl.promotion.id = :promotionId " +
           "ORDER BY nl.sentAt DESC")
    List<NotificationLog> findByPromotionId(@Param("promotionId") Long promotionId);
    
    @Query("SELECT nl FROM NotificationLog nl WHERE nl.user.id = :userId " +
           "AND nl.status = :status ORDER BY nl.sentAt DESC")
    List<NotificationLog> findByUserIdAndStatus(
            @Param("userId") Long userId, 
            @Param("status") NotificationLog.NotificationStatus status);
    
    @Query("SELECT nl FROM NotificationLog nl WHERE nl.status = 'FAILED' " +
           "AND nl.sentAt >= :since ORDER BY nl.sentAt DESC")
    List<NotificationLog> findFailedNotificationsSince(@Param("since") LocalDateTime since);
    
    @Query("SELECT COUNT(DISTINCT nl.user.id) FROM NotificationLog nl WHERE nl.promotion.id = :promotionId " +
           "AND nl.status IN ('SENT', 'DELIVERED')")
    Long countUniqueRecipientsForPromotion(@Param("promotionId") Long promotionId);
}
