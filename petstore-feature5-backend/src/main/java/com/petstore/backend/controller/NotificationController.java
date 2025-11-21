package com.petstore.backend.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.petstore.backend.entity.NotificationConsent;
import com.petstore.backend.service.NotificationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notificaciones", description = "Gestión de consentimientos y preferencias de notificaciones")
@CrossOrigin(origins = "*")
public class NotificationController {
    
    @Autowired
    private NotificationService notificationService;
    
    @Operation(
            summary = "Actualizar consentimiento de notificaciones",
            description = "Permite al usuario gestionar sus preferencias de notificaciones de email para promociones"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", 
                    description = "Consentimiento actualizado exitosamente",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "400", 
                    description = "Datos de entrada inválidos",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404", 
                    description = "Usuario no encontrado",
                    content = @Content(mediaType = "application/json")
            )
    })
    @PostMapping("/consent")
    public ResponseEntity<Map<String, Object>> updateNotificationConsent(
            @RequestBody ConsentRequest request,
            HttpServletRequest httpRequest) {
        
        try {
            // Obtener información de la request para auditoría
            String ipAddress = getClientIpAddress(httpRequest);
            String userAgent = httpRequest.getHeader("User-Agent");
            
            // Validar datos de entrada
            if (request.getUserId() == null || request.getNotificationType() == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Usuario ID y tipo de notificación son requeridos"
                ));
            }
            
            // Actualizar consentimiento
            notificationService.updateNotificationConsent(
                request.getUserId(),
                request.getNotificationType(),
                request.getConsentGiven(),
                ipAddress,
                userAgent
            );
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Consentimiento actualizado exitosamente",
                "userId", request.getUserId(),
                "notificationType", request.getNotificationType().toString(),
                "consentGiven", request.getConsentGiven()
            ));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "Error actualizando consentimiento: " + e.getMessage()
            ));
        }
    }
    
    @Operation(
            summary = "Verificar consentimiento activo",
            description = "Verifica si un usuario tiene consentimiento activo para un tipo de notificación"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", 
                    description = "Estado de consentimiento obtenido exitosamente",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "400", 
                    description = "Parámetros inválidos",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping("/consent/{userId}/{notificationType}")
    public ResponseEntity<Map<String, Object>> checkNotificationConsent(
            @PathVariable Integer userId,
            @PathVariable String notificationType) {
        
        try {
            // Convertir string a enum
            NotificationConsent.NotificationType type;
            try {
                type = NotificationConsent.NotificationType.valueOf(notificationType.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Tipo de notificación inválido: " + notificationType
                ));
            }
            
            // Verificar consentimiento
            boolean hasConsent = notificationService.hasActiveConsent(userId, type);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "userId", userId,
                "notificationType", type.toString(),
                "hasActiveConsent", hasConsent
            ));
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "Error verificando consentimiento: " + e.getMessage()
            ));
        }
    }
    
    @Operation(
            summary = "Estado del servicio de notificaciones",
            description = "Retorna información sobre el estado del servicio de notificaciones"
    )
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getNotificationStatus() {
        return ResponseEntity.ok(Map.of(
            "service", "Notification Service",
            "status", "active",
            "timestamp", java.time.LocalDateTime.now().toString(),
            "supportedTypes", new String[]{
                "EMAIL_PROMOTION", "EMAIL_GENERAL"
            },
            "endpoints", new String[]{
                "POST /api/notifications/consent - Actualizar consentimiento",
                "GET /api/notifications/consent/{userId}/{type} - Verificar consentimiento",
                "GET /api/notifications/status - Estado del servicio"
            }
        ));
    }
    
    @Operation(
            summary = "Enviar email de prueba",
            description = "Envía un email de prueba para verificar la configuración del servicio de email"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", 
                    description = "Email de prueba enviado exitosamente",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "400", 
                    description = "Parámetros inválidos",
                    content = @Content(mediaType = "application/json")
            )
    })
    @PostMapping("/test")
    public ResponseEntity<Map<String, Object>> sendTestEmail(@RequestBody TestEmailRequest request) {
        try {
            String subject = "🧪 Email de Prueba - Petstore Notifications";
            String htmlContent = buildTestEmailContent(request.getMessage());
            
            boolean sent = notificationService.getEmailService().sendEmail(
                request.getEmail(), 
                subject, 
                htmlContent
            );
            
            if (sent) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Email de prueba enviado exitosamente",
                    "email", request.getEmail(),
                    "timestamp", java.time.LocalDateTime.now().toString()
                ));
            } else {
                return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "Error al enviar email de prueba",
                    "email", request.getEmail()
                ));
            }
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "Error enviando email de prueba: " + e.getMessage()
            ));
        }
    }
    
    /**
     * Construye el contenido HTML para el email de prueba
     */
    private String buildTestEmailContent(String message) {
        return """
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    .container { font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; }
                    .header { background: linear-gradient(45deg, #667eea 0%%, #764ba2 100%%); color: white; padding: 20px; text-align: center; border-radius: 8px 8px 0 0; }
                    .content { background: #f8f9fa; padding: 30px; border-radius: 0 0 8px 8px; border: 1px solid #dee2e6; }
                    .badge { background: #28a745; color: white; padding: 5px 15px; border-radius: 20px; font-size: 12px; font-weight: bold; }
                    .footer { text-align: center; color: #6c757d; font-size: 12px; margin-top: 20px; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>🧪 Email de Prueba</h1>
                        <p>Sistema de Notificaciones Petstore</p>
                    </div>
                    <div class="content">
                        <p><span class="badge">✅ FUNCIONANDO</span></p>
                        <h3>¡Configuración de Email Exitosa!</h3>
                        <p>Si recibes este email, significa que tu servicio de email está configurado correctamente.</p>
                        <blockquote style="border-left: 4px solid #667eea; padding-left: 15px; margin: 20px 0; font-style: italic; color: #495057;">
                            %s
                        </blockquote>
                        <p><strong>Información del sistema:</strong></p>
                        <ul>
                            <li>📧 Servicio: Activo</li>
                            <li>🕐 Timestamp: %s</li>
                            <li>🎯 Entorno: Desarrollo</li>
                        </ul>
                    </div>
                    <div class="footer">
                        <p>Petstore Backend - Sistema de Notificaciones</p>
                        <p>Este es un email automático de prueba</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(
                message != null ? message : "Mensaje de prueba del sistema de notificaciones",
                java.time.LocalDateTime.now().toString()
            );
    }
    
    /**
     * Obtiene la dirección IP real del cliente
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String[] headerNames = {
            "X-Forwarded-For",
            "X-Real-IP", 
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_CLIENT_IP",
            "HTTP_X_FORWARDED_FOR"
        };
        
        for (String headerName : headerNames) {
            String ip = request.getHeader(headerName);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                // En caso de múltiples IPs, tomar la primera
                return ip.split(",")[0].trim();
            }
        }
        
        return request.getRemoteAddr();
    }
    
    /**
     * DTO para request de consentimiento
     */
    public static class ConsentRequest {
        private Integer userId;
        private NotificationConsent.NotificationType notificationType;
        private Boolean consentGiven;
        
        // Constructores
        public ConsentRequest() {}
        
        public ConsentRequest(Integer userId, NotificationConsent.NotificationType notificationType, Boolean consentGiven) {
            this.userId = userId;
            this.notificationType = notificationType;
            this.consentGiven = consentGiven;
        }
        
        // Getters y Setters
        public Integer getUserId() {
            return userId;
        }
        
        public void setUserId(Integer userId) {
            this.userId = userId;
        }
        
        public NotificationConsent.NotificationType getNotificationType() {
            return notificationType;
        }
        
        public void setNotificationType(NotificationConsent.NotificationType notificationType) {
            this.notificationType = notificationType;
        }
        
        public Boolean getConsentGiven() {
            return consentGiven;
        }
        
        public void setConsentGiven(Boolean consentGiven) {
            this.consentGiven = consentGiven;
        }
    }
    
    /**
     * DTO para request de test de email
     */
    public static class TestEmailRequest {
        private String email;
        private String message;
        
        public String getEmail() {
            return email;
        }
        
        public void setEmail(String email) {
            this.email = email;
        }
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
    }
}
