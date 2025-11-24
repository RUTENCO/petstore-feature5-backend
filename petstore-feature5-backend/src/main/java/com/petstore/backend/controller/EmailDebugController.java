package com.petstore.backend.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.petstore.backend.service.EmailService;

/**
 * Controlador para diagnóstico de email en producción usando Resend
 */
@RestController
@RequestMapping("/api/debug")
@CrossOrigin(origins = "*")
public class EmailDebugController {

    @Autowired
    private EmailService emailService;
    
    @Value("${resend.api-key:NOT_SET}")
    private String apiKey;
    
    @Value("${resend.from:NOT_SET}")
    private String fromEmail;
    
    @Value("${resend.to:NOT_SET}")
    private String toEmail;
    
    @Value("${resend.from-name:NOT_SET}")
    private String fromName;

    /**
     * Endpoint para verificar la configuración de Resend
     */
    @GetMapping("/email-config")
    public Map<String, Object> getEmailConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("service", "Resend API");
        config.put("apiKeyConfigured", !apiKey.equals("NOT_SET") && apiKey.length() > 10);
        config.put("fromEmail", fromEmail);
        config.put("toEmail", toEmail);
        config.put("fromName", fromName);
        config.put("emailServiceConfigured", emailService != null);
        
        return config;
    }
    
    /**
     * Endpoint para enviar email de prueba con Resend
     */
    @PostMapping("/send-test-email")
    public Map<String, Object> sendTestEmailWithDiagnostics(@RequestParam String email) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Verificar configuración
            if (apiKey.equals("NOT_SET")) {
                result.put("success", false);
                result.put("error", "Configuración de Resend incompleta");
                result.put("config", getEmailConfig());
                return result;
            }
            
            // Intentar enviar email con Resend
            boolean sent = emailService.sendTestEmail(email);
            
            result.put("success", sent);
            result.put("message", sent ? "Email enviado exitosamente via Resend" : "Error al enviar email via Resend");
            result.put("config", getEmailConfig());
            result.put("timestamp", java.time.LocalDateTime.now().toString());
            result.put("note", "En plan gratuito de Resend, el email se envía a: " + toEmail);
            
            if (!sent) {
                result.put("possibleCauses", java.util.List.of(
                    "API Key de Resend incorrecto o expirado",
                    "Límites de rate limiting de Resend excedidos", 
                    "Problemas de conectividad con Resend API",
                    "Email de origen no verificado en Resend"
                ));
                result.put("recommendations", java.util.List.of(
                    "Verificar API Key en dashboard de Resend",
                    "Verificar dominio/email de origen",
                    "Revisar logs de aplicación para más detalles",
                    "Verificar cuota de emails en Resend"
                ));
            }
            
            return result;
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "Error general: " + e.getMessage());
            result.put("errorClass", e.getClass().getSimpleName());
            result.put("config", getEmailConfig());
            return result;
        }
    }
}
