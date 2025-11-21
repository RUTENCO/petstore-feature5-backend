package com.petstore.backend.listener;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.petstore.backend.entity.NotificationConsent;
import com.petstore.backend.entity.NotificationConsent.NotificationType;
import com.petstore.backend.event.PromotionActivatedEvent;
import com.petstore.backend.entity.Promotion;
import com.petstore.backend.repository.NotificationConsentRepository;
import com.petstore.backend.service.NotificationService;

/**
 * Listener que se ejecuta automáticamente cuando una promoción se activa
 * Envía notificaciones inmediatas a usuarios con consentimiento
 */
@Component
public class PromotionActivatedListener {

    private static final Logger logger = LoggerFactory.getLogger(PromotionActivatedListener.class);

    private final NotificationService notificationService;
    private final NotificationConsentRepository notificationConsentRepository;

    public PromotionActivatedListener(NotificationService notificationService,
                                    NotificationConsentRepository notificationConsentRepository) {
        this.notificationService = notificationService;
        this.notificationConsentRepository = notificationConsentRepository;
    }

    /**
     * Se ejecuta automáticamente cuando una promoción cambia a estado ACTIVE
     */
    @EventListener
    @Async
    @Transactional
    public void handlePromotionActivated(PromotionActivatedEvent event) {
        Promotion promotion = event.getPromotion();
        if (promotion == null) {
            logger.warn("Evento recibido con promoción nula, ignorando");
            return;
        }
        
        logger.info("🔔 EVENTO RECIBIDO: Promoción activada - {} - Iniciando envío de notificaciones...", 
                   promotion.getPromotionName());

        try {
            // Obtener todos los usuarios con consentimiento para EMAIL_PROMOTION
            List<NotificationConsent> consents = notificationConsentRepository
                    .findByNotificationTypeAndConsentGivenTrue(NotificationType.EMAIL_PROMOTION);

            int emailsSent = 0;
            int emailsFailed = 0;

            // Enviar notificación a cada usuario con consentimiento
            for (NotificationConsent consent : consents) {
                try {
                    // Enviar notificación personalizada para este usuario específico
                    notificationService.sendPromotionNotificationToUser(
                        event.getPromotion(), 
                        consent.getUser()
                    );
                    emailsSent++;
                    
                    logger.debug("✅ Email enviado a: {}", consent.getUser().getEmail());
                    
                    // Pequeña pausa para no saturar el servidor SMTP
                    Thread.sleep(100);
                    
                } catch (Exception e) {
                    emailsFailed++;
                    logger.error("❌ Error enviando email a {}: {}", 
                               consent.getUser().getEmail(), e.getMessage());
                }
            }

            logger.info("📧 Notificaciones de promoción '{}' completadas: {} enviados, {} fallidos", 
                       event.getPromotion().getPromotionName(), emailsSent, emailsFailed);

        } catch (Exception e) {
            logger.error("❌ Error procesando promoción activada '{}': {}", 
                        event.getPromotion().getPromotionName(), e.getMessage());
            // No volver a lanzar la excepción para evitar reintentos automáticos
        }
    }
}
