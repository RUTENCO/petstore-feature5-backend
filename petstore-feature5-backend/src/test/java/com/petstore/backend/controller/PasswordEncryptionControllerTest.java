package com.petstore.backend.controller;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.petstore.backend.service.AuthService;

@ExtendWith(MockitoExtension.class)
@DisplayName("Password Encryption Controller Tests")
class PasswordEncryptionControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        // Setup can be added here if needed
    }

    @Test
    @DisplayName("Should encrypt password successfully")
    void testEncryptPassword_Success() {
        // Given
        String rawPassword = "admin123";
        String encryptedPassword = "$2a$10$abcdefghijklmnopqrstuvwxyz1234567890";
        
        when(authService.encryptPassword(rawPassword)).thenReturn(encryptedPassword);

        // When
        ResponseEntity<Map<String, String>> response = authController.encryptPassword(
            Map.of("password", rawPassword)
        );

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        Map<String, String> body = response.getBody();
        assertNotNull(body);
        assertEquals(rawPassword, body.get("rawPassword"));
        assertEquals(encryptedPassword, body.get("encryptedPassword"));
        assertEquals("Contraseña cifrada exitosamente", body.get("message"));
    }

    @Test
    @DisplayName("Should return error for empty password")
    void testEncryptPassword_EmptyPassword() {
        // When
        ResponseEntity<Map<String, String>> response = authController.encryptPassword(
            Map.of("password", "")
        );

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        
        Map<String, String> body = response.getBody();
        assertNotNull(body);
        assertEquals("La contraseña no puede estar vacía", body.get("error"));
    }

    @Test
    @DisplayName("Should return error for null password")
    void testEncryptPassword_NullPassword() {
        // When
        ResponseEntity<Map<String, String>> response = authController.encryptPassword(
            Map.of() // Empty map - no password field
        );

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        
        Map<String, String> body = response.getBody();
        assertNotNull(body);
        assertEquals("La contraseña no puede estar vacía", body.get("error"));
    }

    @Test
    @DisplayName("Should return error when encryption fails")
    void testEncryptPassword_EncryptionError() {
        // Given
        String rawPassword = "admin123";
        
        when(authService.encryptPassword(rawPassword))
            .thenThrow(new RuntimeException("Encryption failed"));

        // When
        ResponseEntity<Map<String, String>> response = authController.encryptPassword(
            Map.of("password", rawPassword)
        );

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        
        Map<String, String> body = response.getBody();
        assertNotNull(body);
        assertEquals("Error al cifrar la contraseña", body.get("error"));
    }

    @Test
    @DisplayName("Should encrypt complex password")
    void testEncryptPassword_ComplexPassword() {
        // Given
        String rawPassword = "MyComplexP@ssw0rd!2024";
        String encryptedPassword = "$2a$10$complex1234567890abcdefghijk";
        
        when(authService.encryptPassword(rawPassword)).thenReturn(encryptedPassword);

        // When
        ResponseEntity<Map<String, String>> response = authController.encryptPassword(
            Map.of("password", rawPassword)
        );

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        Map<String, String> body = response.getBody();
        assertNotNull(body);
        assertEquals(rawPassword, body.get("rawPassword"));
        assertEquals(encryptedPassword, body.get("encryptedPassword"));
        assertEquals("Contraseña cifrada exitosamente", body.get("message"));
    }

    @Test
    @DisplayName("Should handle whitespace-only password")
    void testEncryptPassword_WhitespacePassword() {
        // When
        ResponseEntity<Map<String, String>> response = authController.encryptPassword(
            Map.of("password", "   ")
        );

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        
        Map<String, String> body = response.getBody();
        assertNotNull(body);
        assertEquals("La contraseña no puede estar vacía", body.get("error"));
    }
}
