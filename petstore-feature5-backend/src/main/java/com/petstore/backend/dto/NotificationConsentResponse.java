package com.petstore.backend.dto;

import com.petstore.backend.entity.NotificationConsent;

/**
 * DTO para respuestas de GraphQL relacionadas con notificaciones
 */
public class NotificationConsentResponse {
    
    private Boolean success;
    private String message;
    private NotificationConsent consent;
    
    // Constructores
    public NotificationConsentResponse() {}
    
    public NotificationConsentResponse(Boolean success, String message, NotificationConsent consent) {
        this.success = success;
        this.message = message;
        this.consent = consent;
    }
    
    public static NotificationConsentResponse success(String message, NotificationConsent consent) {
        return new NotificationConsentResponse(true, message, consent);
    }
    
    public static NotificationConsentResponse error(String message) {
        return new NotificationConsentResponse(false, message, null);
    }
    
    // Getters y Setters
    public Boolean getSuccess() {
        return success;
    }
    
    public void setSuccess(Boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public NotificationConsent getConsent() {
        return consent;
    }
    
    public void setConsent(NotificationConsent consent) {
        this.consent = consent;
    }
}
