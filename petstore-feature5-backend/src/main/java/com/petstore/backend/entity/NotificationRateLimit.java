package com.petstore.backend.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notification_rate_limits")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRateLimit {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false)
    private NotificationConsent.NotificationType notificationType;
    
    @Column(name = "notification_count", nullable = false)
    private Integer notificationCount = 0;
    
    @Column(name = "time_window_start", nullable = false)
    private LocalDateTime timeWindowStart;
    
    @Column(name = "last_reset")
    private LocalDateTime lastReset;
}
