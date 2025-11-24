package com.petstore.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;

/**
 * Servicio para envío de emails usando Resend API.
 * Resend es más confiable que SMTP para servicios en la nube como Render.
 */
@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final Resend resend;
    private final String fromEmail;
    private final String fromName;
    private final String defaultToEmail;

    public EmailService(
            @Value("${resend.api-key}") String apiKey,
            @Value("${resend.from}") String fromEmail,
            @Value("${resend.from-name}") String fromName,
            @Value("${resend.to}") String defaultToEmail) {
        
        this.resend = new Resend(apiKey);
        this.fromEmail = fromEmail;
        this.fromName = fromName;
        this.defaultToEmail = defaultToEmail;
        
        logger.info("EmailService inicializado con Resend API");
        logger.info("From: {} <{}>, Default To: {}", fromName, fromEmail, defaultToEmail);
    }

    /**
     * Método principal para enviar emails usando Resend API.
     * En plan gratuito, solo se puede enviar al email configurado.
     * 
     * @param to Email del destinatario (se usará defaultToEmail por limitaciones del plan gratuito)
     * @param subject Asunto del email
     * @param htmlContent Contenido HTML del email
     * @return true si el email se envió exitosamente, false en caso contrario
     */
    public boolean sendEmail(String to, String subject, String htmlContent) {
        try {
            logger.info("Enviando email a: {} desde: {} usando Resend", to, fromEmail);
            
            // En plan gratuito de Resend, usar el email configurado
            String recipientEmail = defaultToEmail; // Forzar al email permitido
            
            // Usar SDK de Resend para envío real
            CreateEmailOptions emailOptions = CreateEmailOptions.builder()
                .from(fromName + " <" + fromEmail + ">")
                .to(recipientEmail)
                .subject(subject)
                .html(htmlContent)
                .build();
            
            CreateEmailResponse response = resend.emails().send(emailOptions);
            
            logger.info("✅ Email enviado exitosamente con Resend. ID: {}", response.getId());
            return true;
            
        } catch (ResendException e) {
            logger.error("❌ Error de Resend API al enviar email: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            logger.error("❌ Error general enviando email a {}: {} - Tipo: {}", to, e.getMessage(), e.getClass().getSimpleName());
            logger.error("Stack trace completo:", e);
            return false;
        }
    }

    /**
     * Envía un email de prueba para verificar la configuración.
     * 
     * @param to Email del destinatario (se usará el email configurado en plan gratuito)
     * @return true si se envió exitosamente, false en caso contrario
     */
    public boolean sendTestEmail(String to) {
        try {
            String subject = "✅ Email de Prueba - PetStore con Resend";
            String htmlContent = """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Test Email</title>
                </head>
                <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px;">
                    <div style="background: #4CAF50; color: white; padding: 20px; text-align: center; border-radius: 8px 8px 0 0;">
                        <h1>🐾 PetStore - Email de Prueba con Resend</h1>
                    </div>
                    <div style="background: #ffffff; padding: 20px; border: 1px solid #ddd; border-radius: 0 0 8px 8px;">
                        <h2>¡Configuración de Email Exitosa! ✅</h2>
                        
                        <p>Este es un email de prueba para verificar que la configuración de emails con <strong>Resend</strong> está funcionando correctamente.</p>
                        
                        <div style="background: #f0f8ff; border-left: 4px solid #4CAF50; padding: 15px; margin: 20px 0;">
                            <p><strong>Sistema de notificaciones:</strong> Operativo ✨</p>
                            <p><strong>Servicio:</strong> Resend API 🚀</p>
                            <p><strong>Fecha/Hora:</strong> %s</p>
                        </div>
                        
                        <p>Si recibiste este email, significa que el sistema de notificaciones de PetStore está listo para enviar promociones y alertas usando Resend.</p>
                        
                        <hr style="margin: 20px 0; border: none; border-top: 1px solid #eee;">
                        <p style="font-size: 0.9em; color: #666;">
                            Este es un email automatizado de prueba del sistema PetStore con Resend API.
                        </p>
                    </div>
                </body>
                </html>
                """.formatted(java.time.LocalDateTime.now());
            
            return sendEmail(to, subject, htmlContent);
            
        } catch (Exception e) {
            logger.error("Error enviando email de prueba a {}: {}", to, e.getMessage());
            return false;
        }
    }

    /**
     * Envía un email de promoción con formato HTML prediseñado.
     * 
     * @param to Email del destinatario
     * @param promotionTitle Título de la promoción
     * @param promotionDescription Descripción de la promoción
     * @param promotionDiscount Descuento aplicable
     * @return true si se envió exitosamente, false en caso contrario
     */
    public boolean sendPromotionEmail(String to, String promotionTitle, String promotionDescription, String promotionDiscount) {
        try {
            String subject = "🎉 ¡Nueva Promoción en PetStore! - " + promotionTitle;
            String htmlContent = buildPromotionEmailContent(promotionTitle, promotionDescription, promotionDiscount);
            
            return sendEmail(to, subject, htmlContent);
            
        } catch (Exception e) {
            logger.error("Error enviando email de promoción a {}: {}", to, e.getMessage());
            return false;
        }
    }

    /**
     * Construye el contenido HTML del email de promoción.
     */
    private String buildPromotionEmailContent(String title, String description, String discount) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Nueva Promoción PetStore</title>
            </head>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px;">
                <div style="background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0;">
                    <h1 style="margin: 0; font-size: 28px;">🎉 ¡Nueva Promoción!</h1>
                    <p style="margin: 10px 0 0 0; font-size: 18px;">PetStore</p>
                </div>
                
                <div style="background: #ffffff; padding: 30px; border: 1px solid #ddd; border-radius: 0 0 10px 10px;">
                    <h2 style="color: #667eea; margin-top: 0;">%s</h2>
                    <p style="font-size: 16px; margin: 20px 0;">%s</p>
                    
                    <div style="background: #f8f9fa; border-left: 4px solid #667eea; padding: 20px; margin: 20px 0;">
                        <h3 style="margin: 0; color: #28a745;">💰 Descuento: %s</h3>
                    </div>
                    
                    <div style="text-align: center; margin: 30px 0;">
                        <a href="https://fluffy-deals-hub.vercel.app" 
                           style="background: #667eea; color: white; padding: 15px 30px; text-decoration: none; border-radius: 5px; font-weight: bold; display: inline-block;">
                            🛒 Ver Promoción
                        </a>
                    </div>
                    
                    <hr style="margin: 30px 0; border: none; border-top: 1px solid #eee;">
                    <p style="font-size: 14px; color: #666; text-align: center;">
                        Este es un email automático de PetStore. <br>
                        Si no deseas recibir más promociones, puedes darte de baja desde tu perfil.
                    </p>
                </div>
            </body>
            </html>
            """.formatted(title, description, discount);
    }

    /**
     * Verifica si el servicio está correctamente configurado.
     * 
     * @return true si está configurado, false en caso contrario
     */
    public boolean isConfigured() {
        return resend != null && fromEmail != null && !fromEmail.isEmpty() 
               && defaultToEmail != null && !defaultToEmail.isEmpty();
    }

    // Getters para acceso desde otros servicios
    public String getFromEmail() { return fromEmail; }
    public String getFromName() { return fromName; }
    public String getDefaultToEmail() { return defaultToEmail; }
}
