package com.petstore.backend.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.petstore.backend.entity.NotificationConsent;
import com.petstore.backend.entity.NotificationRateLimit;

@Repository
public interface NotificationRateLimitRepository extends JpaRepository<NotificationRateLimit, Long> {
    
    @Query("SELECT nrl FROM NotificationRateLimit nrl WHERE nrl.user.id = :userId " +
           "AND nrl.notificationType = :type")
    Optional<NotificationRateLimit> findByUserIdAndNotificationType(
            @Param("userId") Long userId, 
            @Param("type") NotificationConsent.NotificationType type);
    
    @Query("SELECT nrl FROM NotificationRateLimit nrl WHERE nrl.timeWindowStart <= :cutoffTime")
    void deleteExpiredRateLimits(@Param("cutoffTime") LocalDateTime cutoffTime);
}
