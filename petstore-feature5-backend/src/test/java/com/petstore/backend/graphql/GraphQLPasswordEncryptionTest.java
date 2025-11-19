package com.petstore.backend.graphql;

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

import com.petstore.backend.repository.CategoryRepository;
import com.petstore.backend.repository.ProductRepository;
import com.petstore.backend.repository.PromotionRepository;
import com.petstore.backend.repository.UserRepository;
import com.petstore.backend.service.AuthService;
import com.petstore.backend.service.PromotionMetricsService;
import com.petstore.backend.service.PromotionService;

@ExtendWith(MockitoExtension.class)
@DisplayName("GraphQL Password Encryption Tests")
class GraphQLPasswordEncryptionTest {

    @Mock
    private PromotionService promotionService;

    @Mock
    private PromotionMetricsService promotionMetricsService;

    @Mock
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private PromotionRepository promotionRepository;

    @InjectMocks
    private GraphQLResolver graphQLResolver;

    @BeforeEach
    void setUp() {
        // Setup can be added here if needed
    }

    @Test
    @DisplayName("Should encrypt password successfully via GraphQL")
    void testEncryptPassword_Success() {
        // Given
        String rawPassword = "admin123";
        String encryptedPassword = "$2a$10$abcdefghijklmnopqrstuvwxyz1234567890";
        
        when(authService.encryptPassword(rawPassword)).thenReturn(encryptedPassword);

        // When
        Map<String, String> result = graphQLResolver.encryptPassword(rawPassword);

        // Then
        assertNotNull(result);
        assertEquals("true", result.get("success"));
        assertEquals(rawPassword, result.get("rawPassword"));
        assertEquals(encryptedPassword, result.get("encryptedPassword"));
        assertEquals("Contraseña cifrada exitosamente", result.get("message"));
    }

    @Test
    @DisplayName("Should return error for empty password via GraphQL")
    void testEncryptPassword_EmptyPassword() {
        // Given
        String rawPassword = "";

        // When
        Map<String, String> result = graphQLResolver.encryptPassword(rawPassword);

        // Then
        assertNotNull(result);
        assertEquals("false", result.get("success"));
        assertEquals("La contraseña no puede estar vacía", result.get("message"));
    }

    @Test
    @DisplayName("Should return error for null password via GraphQL")
    void testEncryptPassword_NullPassword() {
        // When
        Map<String, String> result = graphQLResolver.encryptPassword(null);

        // Then
        assertNotNull(result);
        assertEquals("false", result.get("success"));
        assertEquals("La contraseña no puede estar vacía", result.get("message"));
    }

    @Test
    @DisplayName("Should return error for whitespace password via GraphQL")
    void testEncryptPassword_WhitespacePassword() {
        // Given
        String rawPassword = "   ";

        // When
        Map<String, String> result = graphQLResolver.encryptPassword(rawPassword);

        // Then
        assertNotNull(result);
        assertEquals("false", result.get("success"));
        assertEquals("La contraseña no puede estar vacía", result.get("message"));
    }

    @Test
    @DisplayName("Should handle encryption error via GraphQL")
    void testEncryptPassword_EncryptionError() {
        // Given
        String rawPassword = "admin123";
        
        when(authService.encryptPassword(rawPassword))
            .thenThrow(new RuntimeException("Encryption failed"));

        // When
        Map<String, String> result = graphQLResolver.encryptPassword(rawPassword);

        // Then
        assertNotNull(result);
        assertEquals("false", result.get("success"));
        assertEquals("Error al cifrar la contraseña", result.get("message"));
    }

    @Test
    @DisplayName("Should encrypt complex password via GraphQL")
    void testEncryptPassword_ComplexPassword() {
        // Given
        String rawPassword = "MyComplexP@ssw0rd!2024";
        String encryptedPassword = "$2a$10$complex1234567890abcdefghijk";
        
        when(authService.encryptPassword(rawPassword)).thenReturn(encryptedPassword);

        // When
        Map<String, String> result = graphQLResolver.encryptPassword(rawPassword);

        // Then
        assertNotNull(result);
        assertEquals("true", result.get("success"));
        assertEquals(rawPassword, result.get("rawPassword"));
        assertEquals(encryptedPassword, result.get("encryptedPassword"));
        assertEquals("Contraseña cifrada exitosamente", result.get("message"));
    }
}
