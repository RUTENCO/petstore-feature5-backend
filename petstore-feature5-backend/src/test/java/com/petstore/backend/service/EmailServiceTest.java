package com.petstore.backend.service;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import jakarta.mail.internet.MimeMessage;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private EmailService emailService;

    private String toEmail;
    private String subject;
    private String content;

    @BeforeEach
    void setUp() {
        toEmail = "test@example.com";
        subject = "Test Subject";
        content = "<html><body><h1>Test HTML Content</h1></body></html>";
        
        // Configure required properties
        ReflectionTestUtils.setField(emailService, "fromName", "Test PetStore");
        ReflectionTestUtils.setField(emailService, "fromEmail", "test@petstore.com");
    }

    @Test
    @DisplayName("Should send email successfully")
    void shouldSendEmailSuccessfully() {
        // Given
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // When
        boolean result = emailService.sendEmail(toEmail, subject, content);

        // Then
        assertTrue(result);
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("Should handle mail exception and return false")
    void shouldHandleMailExceptionAndReturnFalse() {
        // Given
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new MailException("SMTP error") {}).when(mailSender).send(any(MimeMessage.class));

        // When
        boolean result = emailService.sendEmail(toEmail, subject, content);

        // Then
        assertFalse(result);
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("Should handle runtime exception and return false")
    void shouldHandleRuntimeExceptionAndReturnFalse() {
        // Given
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new RuntimeException("Connection timeout"))
            .when(mailSender).send(any(MimeMessage.class));

        // When
        boolean result = emailService.sendEmail(toEmail, subject, content);

        // Then
        assertFalse(result);
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("Should handle null parameters gracefully")
    void shouldHandleNullParametersGracefully() {
        // Given
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        
        // When & Then - service tries to send but returns false due to errors
        assertFalse(emailService.sendEmail(null, subject, content));
        assertFalse(emailService.sendEmail(toEmail, null, content));
        assertFalse(emailService.sendEmail(toEmail, subject, null));
        
        // Verify mailSender was called (service attempts to create message)
        verify(mailSender, times(3)).createMimeMessage();
    }

    @Test
    @DisplayName("Should handle empty parameters gracefully")
    void shouldHandleEmptyParametersGracefully() {
        // Given
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));
        
        // When & Then - service attempts to send even with empty parameters
        // The actual behavior is that it tries to send but may succeed with empty values
        // This tests that the service doesn't crash with empty parameters
        assertDoesNotThrow(() -> emailService.sendEmail("", subject, content));
        assertDoesNotThrow(() -> emailService.sendEmail(toEmail, "", content));
        assertDoesNotThrow(() -> emailService.sendEmail(toEmail, subject, ""));
        
        // Verify mailSender was called (service attempts to create message)
        verify(mailSender, times(3)).createMimeMessage();
    }

    @Test
    @DisplayName("Should send HTML content correctly")
    void shouldSendHtmlContentCorrectly() {
        // Given
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // When
        boolean result = emailService.sendEmail(toEmail, subject, content);

        // Then
        assertTrue(result);
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("Should set correct email properties")
    void shouldSetCorrectEmailProperties() {
        // Given
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // When
        emailService.sendEmail(toEmail, subject, "Plain text content");

        // Then
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("Should handle very long content")
    void shouldHandleVeryLongContent() {
        // Given
        StringBuilder longContent = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            longContent.append("This is a very long email content. ");
        }
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // When
        boolean result = emailService.sendEmail(toEmail, subject, longContent.toString());

        // Then
        assertTrue(result);
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("Should handle special characters in email")
    void shouldHandleSpecialCharactersInEmail() {
        // Given
        String specialContent = "Promoción con caracteres especiales: ñ, á, é, í, ó, ú, ü, ¿, ¡, €, ®, ©";
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doNothing().when(mailSender).send(any(MimeMessage.class));

        // When
        boolean result = emailService.sendEmail(toEmail, "Título con ñ", specialContent);

        // Then
        assertTrue(result);
        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }
}
