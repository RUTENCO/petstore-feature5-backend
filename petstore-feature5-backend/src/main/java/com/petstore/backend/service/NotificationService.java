package com.petstore.backend.service;

import com.petstore.backend.entity.*;
import com.petstore.backend.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class NotificationService {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    
    @Autowired
    private NotificationConsentRepository consentRepository;
    
    @Autowired
    private NotificationLogRepository logRepository;
    
    @Autowired
    private NotificationRateLimitRepository rateLimitRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private EmailService emailService;
    
    // Configuración desde application.properties
    @Value("${notification.rate-limit.email.max-per-hour:10}")
    private int emailRateLimitPerHour;
    
    @Value("${notification.rate-limit.window-hours:1}")
    private int rateLimitWindowHours;
    
    @Value("${app.frontend.url:https://fluffy-deals-hub.vercel.app}")
    private String frontendUrl;
    
    /**
     * Envía notificación de nueva promoción a todos los usuarios con consentimiento
     */
    public void sendPromotionNotification(Promotion promotion) {
        logger.info("Enviando notificaciones de promoción: {}", promotion.getPromotionName());
        
        // Obtener usuarios con consentimiento para notificaciones de promociones por email
        List<User> emailUsers = consentRepository.findUsersWithActiveConsent(
                NotificationConsent.NotificationType.EMAIL_PROMOTION);
        
        // Enviar notificaciones por email
        for (User user : emailUsers) {
            if (checkRateLimit(user.getUserId(), NotificationConsent.NotificationType.EMAIL_PROMOTION)) {
                sendPromotionEmail(user, promotion);
                updateRateLimit(user.getUserId(), NotificationConsent.NotificationType.EMAIL_PROMOTION);
            } else {
                logRateLimitedNotification(user, promotion, NotificationConsent.NotificationType.EMAIL_PROMOTION);
            }
        }
        
        logger.info("Notificaciones enviadas por email: {}", emailUsers.size());
    }
    
    /**
     * Envía notificación de promoción a un usuario específico
     * Método optimizado para eventos automáticos
     */
    @Transactional
    public void sendPromotionNotificationToUser(Promotion promotion, User user) {
        logger.info("Enviando notificación de promoción '{}' a usuario: {}", 
                   promotion.getPromotionName(), user.getEmail());
        
        try {
            // Verificar que el usuario tenga consentimiento activo
            if (!hasActiveConsent(user.getUserId(), NotificationConsent.NotificationType.EMAIL_PROMOTION)) {
                logger.warn("Usuario {} no tiene consentimiento activo para EMAIL_PROMOTION", user.getEmail());
                return;
            }
            
            // Verificar rate limit
            if (!checkRateLimit(user.getUserId(), NotificationConsent.NotificationType.EMAIL_PROMOTION)) {
                logRateLimitedNotification(user, promotion, NotificationConsent.NotificationType.EMAIL_PROMOTION);
                return;
            }
            
            // Enviar email
            sendPromotionEmail(user, promotion);
            updateRateLimit(user.getUserId(), NotificationConsent.NotificationType.EMAIL_PROMOTION);
            
            logger.info("✅ Notificación enviada exitosamente a: {}", user.getEmail());
            
        } catch (Exception e) {
            logger.error("❌ Error enviando notificación a {}: {}", user.getEmail(), e.getMessage());
        }
    }
    
    /**
     * Envía email de promoción a un usuario específico
     */
    private void sendPromotionEmail(User user, Promotion promotion) {
        try {
            String subject = "🎉 Nueva Promoción Disponible: " + promotion.getPromotionName();
            String content = buildPromotionEmailContent(user, promotion);
            
            NotificationLog log = new NotificationLog();
            log.setUser(user);
            log.setPromotion(promotion);
            log.setNotificationType(NotificationConsent.NotificationType.EMAIL_PROMOTION);
            log.setRecipient(user.getEmail());
            log.setSubject(subject);
            log.setContent(content);
            log.setSentAt(LocalDateTime.now());
            log.setStatus(NotificationLog.NotificationStatus.PENDING);
            
            // Intentar enviar email
            boolean sent = emailService.sendEmail(user.getEmail(), subject, content);
            
            if (sent) {
                log.setStatus(NotificationLog.NotificationStatus.SENT);
                logger.info("Email enviado exitosamente a: {}", user.getEmail());
            } else {
                log.setStatus(NotificationLog.NotificationStatus.FAILED);
                log.setErrorMessage("Error al enviar email");
                logger.error("Error enviando email a: {}", user.getEmail());
            }
            
            logRepository.save(log);
            
        } catch (Exception e) {
            logger.error("Error enviando email promocional a {}: {}", user.getEmail(), e.getMessage());
            
            NotificationLog log = new NotificationLog();
            log.setUser(user);
            log.setPromotion(promotion);
            log.setNotificationType(NotificationConsent.NotificationType.EMAIL_PROMOTION);
            log.setRecipient(user.getEmail());
            log.setStatus(NotificationLog.NotificationStatus.FAILED);
            log.setErrorMessage(e.getMessage());
            log.setSentAt(LocalDateTime.now());
            logRepository.save(log);
        }
    }
    
    /**
     * Formatea el valor del descuento para mostrar decimales solo cuando sea necesario
     */
    private String formatDiscountValue(Double discountValue) {
        if (discountValue == null) return "0";
        
        // Si es un número entero, mostrar sin decimales
        if (discountValue == Math.floor(discountValue)) {
            return String.format("%.0f", discountValue);
        } else {
            // Si tiene decimales, mostrar hasta 1 decimal
            return String.format("%.1f", discountValue);
        }
    }

    /**
     * Construye el contenido HTML del email promocional
     */
    private String buildPromotionEmailContent(User user, Promotion promotion) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        return String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Nueva Promoción - PetStore</title>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 20px; background-color: #f4f4f4; }
                    .container { max-width: 600px; margin: 0 auto; background: #f4f4f4; padding: 20px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
                    .header { text-align: center; background: #E9C295; color: #f4f4f4; padding: 20px; border-radius: 10px 10px 0 0; margin: -20px -20px 20px -20px; }
                    .logo { font-size: 24px; font-weight: bold; margin-bottom: 5px; }
                    .promo-title { font-size: 28px; color: #e74c3c; font-weight: bold; text-align: center; margin: 20px 0; }
                    .discount { font-size: 36px; color: #27ae60; font-weight: bold; text-align: center; background: #ecf0f1; padding: 15px; border-radius: 8px; margin: 15px 0; }
                    .details { background: #f8f9fa; padding: 15px; border-radius: 8px; margin: 15px 0; }
                    .cta-button { display: inline-block; background: #E9C295; color: #f4f4f4; padding: 15px 30px; text-decoration: none; border-radius: 5px; font-weight: bold; margin: 20px 0; text-align: center; width: 200px; }
                    .footer { text-align: center; margin-top: 30px; padding-top: 20px; border-top: 1px solid #ecf0f1; color: #7f8c8d; font-size: 12px; }
                    @media (max-width: 600px) { .container { margin: 10px; padding: 15px; } .promo-title { font-size: 24px; } .discount { font-size: 28px; } }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <div class="logo">🐾 PetStore</div>
                        <p>Tu tienda de mascotas favorita</p>
                    </div>
                    
                    <h2>¡Hola %s!</h2>
                    
                    <div class="promo-title">🎉 %s</div>
                    
                    <div class="discount">
                        ¡%s%% DE DESCUENTO!
                    </div>
                    
                    <div class="details">
                        <h3>📅 Período de Promoción:</h3>
                        <p><strong>Desde:</strong> %s</p>
                        <p><strong>Hasta:</strong> %s</p>
                        
                        <h3>✨ ¿Cómo aprovecharla?</h3>
                        <ul>
                            <li>Visita nuestra tienda online</li>
                            <li>Selecciona los productos que desees</li>
                            <li>El descuento se aplicará automáticamente</li>
                        </ul>
                    </div>
                    
                    <div style="text-align: center;">
                        <a href="%s" class="cta-button">¡COMPRAR AHORA!</a>
                    </div>
                    
                    <div class="footer">
                        <p>© 2025 PetStore. Todos los derechos reservados.</p>
                        <p>Has recibido este email porque tienes activadas las notificaciones de promociones.</p>
                        <p><a href="#">Gestionar preferencias de notificación</a> | <a href="#">Darse de baja</a></p>
                    </div>
                </div>
            </body>
            </html>
            """,
            user.getUserName(),
            promotion.getPromotionName(),
            formatDiscountValue(promotion.getDiscountValue()),
            promotion.getStartDate().format(formatter),
            promotion.getEndDate().format(formatter),
            frontendUrl
        );
    }
    
    /**
     * Verifica si el usuario ha alcanzado el límite de notificaciones
     */
    private boolean checkRateLimit(Integer userId, NotificationConsent.NotificationType type) {
        LocalDateTime windowStart = LocalDateTime.now().minusHours(rateLimitWindowHours);
        
        Long count = logRepository.countNotificationsSentSince(Long.valueOf(userId), type, windowStart);
        int maxAllowed = getMaxNotificationsPerWindow(type);
        
        return count < maxAllowed;
    }
    
    private int getMaxNotificationsPerWindow(NotificationConsent.NotificationType type) {
        return switch (type) {
            case EMAIL_PROMOTION, EMAIL_GENERAL -> emailRateLimitPerHour;
        };
    }
    
    /**
     * Actualiza el contador de rate limiting
     */
    private void updateRateLimit(Integer userId, NotificationConsent.NotificationType type) {
        Optional<NotificationRateLimit> existing = rateLimitRepository
                .findByUserIdAndNotificationType(Long.valueOf(userId), type);
        
        NotificationRateLimit rateLimit;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime windowStart = now.minusHours(rateLimitWindowHours);
        
        if (existing.isPresent()) {
            rateLimit = existing.get();
            
            // Si la ventana de tiempo ha expirado, reiniciar contador
            if (rateLimit.getTimeWindowStart().isBefore(windowStart)) {
                rateLimit.setTimeWindowStart(now);
                rateLimit.setNotificationCount(1);
                rateLimit.setLastReset(now);
            } else {
                rateLimit.setNotificationCount(rateLimit.getNotificationCount() + 1);
            }
        } else {
            // Crear nuevo registro de rate limit
            User user = userRepository.findById(userId).orElse(null);
            rateLimit = new NotificationRateLimit();
            rateLimit.setUser(user);
            rateLimit.setNotificationType(type);
            rateLimit.setNotificationCount(1);
            rateLimit.setTimeWindowStart(now);
            rateLimit.setLastReset(now);
        }
        
        rateLimitRepository.save(rateLimit);
    }
    
    /**
     * Registra notificación bloqueada por rate limiting
     */
    private void logRateLimitedNotification(User user, Promotion promotion, 
                                          NotificationConsent.NotificationType type) {
        NotificationLog log = new NotificationLog();
        log.setUser(user);
        log.setPromotion(promotion);
        log.setNotificationType(type);
        log.setRecipient(user.getEmail());
        log.setStatus(NotificationLog.NotificationStatus.RATE_LIMITED);
        log.setErrorMessage("Rate limit exceeded");
        log.setSentAt(LocalDateTime.now());
        
        logRepository.save(log);
        
        logger.warn("Notificación bloqueada por rate limit para usuario: {} - tipo: {}", 
                   user.getUserId(), type);
    }
    
    /**
     * Gestiona el consentimiento de notificaciones
     */
    @Transactional
    public void updateNotificationConsent(Integer userId, NotificationConsent.NotificationType type, 
                                        boolean consent, String ipAddress, String userAgent) {
        Optional<NotificationConsent> existing = consentRepository
                .findByUserIdAndNotificationType(Long.valueOf(userId), type);
        
        NotificationConsent notificationConsent;
        if (existing.isPresent()) {
            notificationConsent = existing.get();
        } else {
            User user = userRepository.findById(userId).orElse(null);
            notificationConsent = new NotificationConsent();
            notificationConsent.setUser(user);
            notificationConsent.setNotificationType(type);
        }
        
        notificationConsent.setConsentGiven(consent);
        notificationConsent.setConsentDate(LocalDateTime.now());
        notificationConsent.setLastModified(LocalDateTime.now());
        notificationConsent.setIpAddress(ipAddress);
        notificationConsent.setUserAgent(userAgent);
        
        consentRepository.save(notificationConsent);
        
        logger.info("Consentimiento actualizado - Usuario: {}, Tipo: {}, Consentimiento: {}", 
                   userId, type, consent);
    }
    
    /**
     * Verifica si el usuario tiene consentimiento activo
     */
    public boolean hasActiveConsent(Integer userId, NotificationConsent.NotificationType type) {
        return consentRepository.hasActiveConsent(Long.valueOf(userId), type);
    }
    
    /**
     * Getter para EmailService (para testing y endpoints de prueba)
     */
    public EmailService getEmailService() {
        return emailService;
    }
}
