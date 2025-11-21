package com.petstore.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

/**
 * Servicio para envío de emails usando Spring Boot Mail.
 * Soporta proveedores como Gmail, Mailtrap y otros servicios SMTP.
 */
@Service
public class EmailService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    
    @Autowired
    private JavaMailSender javaMailSender;
    
    @Value("${app.email.from-name:PetStore}")
    private String fromName;
    
    @Value("${app.email.from-email:noreply@petstore.com}")
    private String fromEmail;

    /**
     * Envía un email HTML.
     * 
     * @param to Dirección de email del destinatario
     * @param subject Asunto del email
     * @param htmlContent Contenido HTML del email
     * @return true si el email se envió exitosamente, false en caso contrario
     */
    public boolean sendEmail(String to, String subject, String htmlContent) {
        try {
            logger.info("Enviando email a: {}", to);
            
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail, fromName);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true = HTML content
            
            javaMailSender.send(message);
            
            logger.info("Email enviado exitosamente a: {}", to);
            return true;
            
        } catch (Exception e) {
            logger.error("Error enviando email a {}: {}", to, e.getMessage());
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
     * Construye el contenido HTML para emails de promoción.
     */
    private String buildPromotionEmailContent(String title, String description, String discount) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Promoción PetStore</title>
                <style>
                    body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 0; }
                    .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px; text-align: center; border-radius: 8px 8px 0 0; }
                    .content { background: #ffffff; padding: 30px; border: 1px solid #ddd; }
                    .highlight { background: #f8f9fa; border-left: 4px solid #667eea; padding: 15px; margin: 20px 0; }
                    .discount { font-size: 1.2em; font-weight: bold; color: #e74c3c; text-align: center; margin: 20px 0; }
                    .button { display: inline-block; background: #667eea; color: white; padding: 12px 24px; text-decoration: none; border-radius: 5px; margin: 20px 0; }
                    .footer { background: #f8f9fa; padding: 20px; text-align: center; font-size: 0.9em; color: #666; border-radius: 0 0 8px 8px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🐾 PetStore</h1>
                        <h2>¡Nueva Promoción Disponible!</h2>
                    </div>
                    
                    <div class="content">
                        <h3>""" + title + """
                        </h3>
                        
                        <div class="highlight">
                            <p>""" + description + """
                            </p>
                        </div>
                        
                        <div class="discount">
                            💰 Descuento: """ + discount + """
                        </div>
                        
                        <p>¡No dejes pasar esta oportunidad! Visita nuestra tienda y aprovecha esta increíble promoción para tu mascota.</p>
                        
                        <div style="text-align: center;">
                            <a href="#" class="button">Ver Promoción</a>
                        </div>
                        
                        <p><strong>Términos y condiciones:</strong></p>
                        <ul>
                            <li>Promoción válida hasta agotar existencias</li>
                            <li>No acumulable con otras promociones</li>
                            <li>Válida solo en productos seleccionados</li>
                        </ul>
                    </div>
                    
                    <div class="footer">
                        <p>¿No quieres recibir más emails? 
                           <a href="#">Darse de baja</a>
                        </p>
                        <p>© 2025 PetStore. Todos los derechos reservados.</p>
                    </div>
                </div>
            </body>
            </html>
            """;
    }

    /**
     * Envía un email de prueba para verificar la configuración.
     * 
     * @param to Email del destinatario
     * @return true si se envió exitosamente, false en caso contrario
     */
    public boolean sendTestEmail(String to) {
        try {
            String subject = "✅ Email de Prueba - PetStore";
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
                        <h1>🐾 PetStore - Email de Prueba</h1>
                    </div>
                    
                    <div style="background: #ffffff; padding: 20px; border: 1px solid #ddd; border-radius: 0 0 8px 8px;">
                        <h2>¡Configuración de Email Exitosa! ✅</h2>
                        
                        <p>Este es un email de prueba para verificar que la configuración de emails está funcionando correctamente.</p>
                        
                        <div style="background: #f0f8ff; border-left: 4px solid #4CAF50; padding: 15px; margin: 20px 0;">
                            <p><strong>Sistema de notificaciones:</strong> Operativo ✨</p>
                            <p><strong>Envío de emails:</strong> Funcional 🚀</p>
                            <p><strong>Fecha/Hora:</strong> """ + java.time.LocalDateTime.now() + """
                            </p>
                        </div>
                        
                        <p>Si recibiste este email, significa que el sistema de notificaciones de PetStore está listo para enviar promociones y alertas.</p>
                        
                        <hr style="margin: 20px 0; border: none; border-top: 1px solid #eee;">
                        <p style="font-size: 0.9em; color: #666;">
                            Este es un email automatizado de prueba del sistema PetStore.
                        </p>
                    </div>
                </body>
                </html>
                """;
            
            return sendEmail(to, subject, htmlContent);
            
        } catch (Exception e) {
            logger.error("Error enviando email de prueba a {}: {}", to, e.getMessage());
            return false;
        }
    }
}
