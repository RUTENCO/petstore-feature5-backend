package com.petstore.backend.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

/**
 * Tests simplificados para EmailService usando Resend API.
 * Estos tests verifican la funcionalidad básica sin mocks complejos.
 */
class EmailServiceTest {

    private EmailService emailService;

    private String toEmail;
    private String subject;
    private String content;

    @BeforeEach
    void setUp() {
        toEmail = "test@example.com";
        subject = "Test Subject";
        content = "<html><body><h1>Test HTML Content</h1></body></html>";
        
        // Create EmailService with test values que fallarán en el envío real
        // pero nos permitirán testear la lógica sin configuración real
        emailService = new EmailService(
            "invalid-test-api-key",
            "test@resend.dev", 
            "Test Sender",
            "default@test.com"
        );
    }

    @Test
    @DisplayName("Should handle invalid API key gracefully")
    void shouldHandleInvalidApiKeyGracefully() {
        // When - usando API key inválida debería retornar false
        boolean result = emailService.sendEmail(toEmail, subject, content);

        // Then - debería fallar gracefully y retornar false
        assertFalse(result);
    }

    @Test
    @DisplayName("Should handle null parameters gracefully")
    void shouldHandleNullParametersGracefully() {
        // When & Then - parámetros null deberían retornar false sin crash
        assertFalse(emailService.sendEmail(null, subject, content));
        assertFalse(emailService.sendEmail(toEmail, null, content));
        assertFalse(emailService.sendEmail(toEmail, subject, null));
        assertFalse(emailService.sendEmail(null, null, null));
    }

    @Test
    @DisplayName("Should handle empty parameters gracefully")
    void shouldHandleEmptyParametersGracefully() {
        // When & Then - parámetros vacíos deberían fallar
        // (con API key inválida siempre retornará false)
        assertFalse(emailService.sendEmail("", "", ""));
        assertFalse(emailService.sendEmail("", subject, content));
        assertFalse(emailService.sendEmail(toEmail, "", content));
        assertFalse(emailService.sendEmail(toEmail, subject, ""));
    }

    @Test
    @DisplayName("Should handle HTML content without crashing")
    void shouldHandleHtmlContentWithoutCrashing() {
        // Given
        String htmlContent = "<html><body><h1>Hello World</h1><p>This is a test</p></body></html>";

        // When & Then - no debería crashear con contenido HTML
        assertDoesNotThrow(() -> {
            emailService.sendEmail(toEmail, subject, htmlContent);
        });
    }

    @Test
    @DisplayName("Should handle very long content without crashing")
    void shouldHandleVeryLongContentWithoutCrashing() {
        // Given
        StringBuilder longContent = new StringBuilder("<html><body>");
        for (int i = 0; i < 100; i++) {
            longContent.append("<p>This is line ").append(i).append(" of very long content.</p>");
        }
        longContent.append("</body></html>");

        // When & Then - no debería crashear con contenido muy largo
        assertDoesNotThrow(() -> {
            emailService.sendEmail(toEmail, subject, longContent.toString());
        });
    }

    @Test
    @DisplayName("Should handle special characters without crashing")
    void shouldHandleSpecialCharactersWithoutCrashing() {
        // Given
        String specialSubject = "Test with special chars: áéíóú ñ ç 中文 🎉";
        String specialContent = "<html><body><h1>Título con acentos: áéíóú</h1><p>Emoji: 🚀 Unicode: 中文</p></body></html>";
        
        // When & Then - no debería crashear con caracteres especiales
        assertDoesNotThrow(() -> {
            emailService.sendEmail(toEmail, specialSubject, specialContent);
        });
    }

    @Test
    @DisplayName("Should verify EmailService configuration methods work")
    void shouldVerifyConfigurationMethodsWork() {
        // When & Then - métodos de configuración deberían funcionar
        assertTrue(emailService.isConfigured());
        assertNotNull(emailService.getFromEmail());
        assertNotNull(emailService.getFromName());
        assertNotNull(emailService.getDefaultToEmail());
        
        assertEquals("test@resend.dev", emailService.getFromEmail());
        assertEquals("Test Sender", emailService.getFromName());
        assertEquals("default@test.com", emailService.getDefaultToEmail());
    }

    @Test
    @DisplayName("Should send test email method work")
    void shouldSendTestEmailMethodWork() {
        // When & Then - método sendTestEmail no debería crashear
        assertDoesNotThrow(() -> {
            boolean result = emailService.sendTestEmail(toEmail);
            // Con API key inválida, debería retornar false
            assertFalse(result);
        });
    }

    @Test
    @DisplayName("Should send promotion email method work")
    void shouldSendPromotionEmailMethodWork() {
        // When & Then - método sendPromotionEmail no debería crashear
        assertDoesNotThrow(() -> {
            boolean result = emailService.sendPromotionEmail(
                toEmail,
                "Test Promotion",
                "This is a test promotion",
                "50% OFF"
            );
            // Con API key inválida, debería retornar false
            assertFalse(result);
        });
    }
}
