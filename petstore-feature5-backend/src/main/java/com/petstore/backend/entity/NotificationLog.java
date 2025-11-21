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
@Table(name = "notification_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_id")
    private Promotion promotion;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false)
    private NotificationConsent.NotificationType notificationType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private NotificationStatus status;
    
    @Column(name = "recipient", nullable = false)
    private String recipient; // Email o número de teléfono
    
    @Column(name = "subject")
    private String subject;
    
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;
    
    @Column(name = "sent_at")
    private LocalDateTime sentAt;
    
    @Column(name = "error_message")
    private String errorMessage;
    
    @Column(name = "external_id") // ID del proveedor externo (SendGrid, Twilio, etc.)
    private String externalId;
    
    public enum NotificationStatus {
        PENDING,
        SENT,
        DELIVERED,
        FAILED,
        BOUNCED,
        RATE_LIMITED
    }
}
