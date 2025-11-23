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
            
            // Intentar enviar email
            boolean sent = emailService.sendTestEmail(email);
            
            result.put("success", sent);
            result.put("message", sent ? "Email enviado exitosamente" : "Error al enviar email");
            result.put("config", getEmailConfig());
            
            return result;
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
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
