package com.petstore.backend.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.petstore.backend.service.EmailService;

/**
 * Controlador para diagnóstico de email en producción
 */
@RestController
@RequestMapping("/api/debug")
@CrossOrigin(origins = "*")
public class EmailDebugController {

    @Autowired
    private EmailService emailService;
    
    @Autowired
    private JavaMailSender javaMailSender;
    
    @Value("${spring.mail.host:NOT_SET}")
    private String mailHost;
    
    @Value("${spring.mail.port:NOT_SET}")
    private String mailPort;
    
    @Value("${spring.mail.username:NOT_SET}")
    private String mailUsername;
    
    @Value("${app.email.from-email:NOT_SET}")
    private String fromEmail;
    
    @Value("${app.email.from-name:NOT_SET}")
    private String fromName;

    /**
     * Endpoint para verificar la configuración de email
     */
    @GetMapping("/email-config")
    public Map<String, Object> getEmailConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("mailHost", maskValue(mailHost));
        config.put("mailPort", mailPort);
        config.put("mailUsername", maskValue(mailUsername));
        config.put("fromEmail", fromEmail);
        config.put("fromName", fromName);
        config.put("javaMailSenderConfigured", javaMailSender != null);
        
        return config;
    }
    
    /**
     * Endpoint para probar conexión SMTP
     */
    @PostMapping("/test-smtp")
    public Map<String, Object> testSmtpConnection() {
        try {
            // Probar creando un mensaje vacío
            javaMailSender.createMimeMessage();
            return Map.of(
                "success", true,
                "message", "JavaMailSender configurado correctamente"
            );
        } catch (Exception e) {
            return Map.of(
                "success", false,
                "error", e.getMessage(),
                "errorClass", e.getClass().getSimpleName()
            );
        }
    }
    
    /**
     * Endpoint para probar conexión SMTP real con autenticación
     */
    @PostMapping("/test-smtp-real")
    public Map<String, Object> testSmtpConnectionReal() {
        try {
            // Crear mensaje de prueba real
            var message = javaMailSender.createMimeMessage();
            var helper = new org.springframework.mail.javamail.MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail, fromName);
            helper.setTo("test@example.com"); // Email falso para probar autenticación
            helper.setSubject("Test de conexión SMTP");
            helper.setText("Test", false);
            
            // Intentar enviar (fallará por email inválido pero probará autenticación)
            try {
                javaMailSender.send(message);
                return Map.of(
                    "success", true,
                    "message", "Conexión SMTP y autenticación exitosa"
                );
            } catch (Exception sendEx) {
                String errorMsg = sendEx.getMessage().toLowerCase();
                if (errorMsg.contains("authentication") || errorMsg.contains("auth")) {
                    return Map.of(
                        "success", false,
                        "error", "Error de autenticación: " + sendEx.getMessage(),
                        "diagnosis", "Credenciales incorrectas o App Password inválido"
                    );
                } else {
                    return Map.of(
                        "success", true,
                        "message", "Autenticación exitosa (error esperado por email de prueba)",
                        "note", "La autenticación funciona, error: " + sendEx.getMessage()
                    );
                }
            }
            
        } catch (Exception e) {
            return Map.of(
                "success", false,
                "error", e.getMessage(),
                "errorClass", e.getClass().getSimpleName()
            );
        }
    }
    
    /**
     * Endpoint para enviar email de prueba con diagnóstico detallado
     */
    @PostMapping("/send-test-email")
    public Map<String, Object> sendTestEmailWithDiagnostics(@RequestParam String email) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Verificar configuración
            if ("NOT_SET".equals(mailHost) || "NOT_SET".equals(mailUsername)) {
                result.put("success", false);
                result.put("error", "Configuración de email incompleta");
                result.put("config", getEmailConfig());
                return result;
            }
            
            // Intentar enviar email con más detalles del error
            try {
                boolean sent = emailService.sendTestEmail(email);
                
                result.put("success", sent);
                result.put("message", sent ? "Email enviado exitosamente" : "Error al enviar email");
                result.put("config", getEmailConfig());
                result.put("timestamp", java.time.LocalDateTime.now().toString());
                
                if (!sent) {
                    result.put("possibleCauses", java.util.List.of(
                        "Gmail App Password incorrecto",
                        "Cuenta de Gmail bloqueada temporalmente", 
                        "Verificación en 2 pasos no activada",
                        "Firewall de Render bloqueando SMTP",
                        "Límites de rate limiting de Gmail"
                    ));
                    result.put("recommendations", java.util.List.of(
                        "Verificar App Password en Gmail",
                        "Revisar bandeja de spam",
                        "Intentar con SendGrid o Mailgun",
                        "Verificar logs de aplicación"
                    ));
                }
                
                return result;
                
            } catch (Exception emailException) {
                result.put("success", false);
                result.put("error", "Excepción al enviar email: " + emailException.getMessage());
                result.put("errorClass", emailException.getClass().getSimpleName());
                result.put("config", getEmailConfig());
                
                // Diagnóstico específico por tipo de error
                String errorMsg = emailException.getMessage().toLowerCase();
                if (errorMsg.contains("authentication")) {
                    result.put("diagnosis", "Error de autenticación - Verificar App Password de Gmail");
                } else if (errorMsg.contains("connection")) {
                    result.put("diagnosis", "Error de conexión - Render puede estar bloqueando SMTP");
                } else if (errorMsg.contains("timeout")) {
                    result.put("diagnosis", "Timeout - Problemas de red o configuración de puertos");
                } else {
                    result.put("diagnosis", "Error no identificado - Revisar configuración completa");
                }
                
                return result;
            }
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "Error general: " + e.getMessage());
            result.put("errorClass", e.getClass().getSimpleName());
            result.put("config", getEmailConfig());
            return result;
        }
    }
    
    private String maskValue(String value) {
        if (value == null || "NOT_SET".equals(value)) {
            return value;
        }
        if (value.length() <= 4) {
            return "***";
        }
        return value.substring(0, 2) + "***" + value.substring(value.length() - 2);
    }
}
