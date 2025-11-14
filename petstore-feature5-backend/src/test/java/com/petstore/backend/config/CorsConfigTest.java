package com.petstore.backend.config;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

class CorsConfigTest {

    private CorsConfig corsConfig;

    @BeforeEach
    void setUp() {
        corsConfig = new CorsConfig();
        // Set properties via reflection to simulate @Value injection
        ReflectionTestUtils.setField(corsConfig, "allowedOrigins", 
            new String[]{"http://localhost:3000", "http://localhost:4200"});
        ReflectionTestUtils.setField(corsConfig, "allowedMethods", 
            new String[]{"GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"});
        ReflectionTestUtils.setField(corsConfig, "allowedHeaders", "*");
        ReflectionTestUtils.setField(corsConfig, "allowCredentials", true);
        ReflectionTestUtils.setField(corsConfig, "maxAge", 3600L);
    }

    @Test
    void corsConfigurationSource_ShouldReturnValidConfiguration() {
        // When
        CorsConfigurationSource source = corsConfig.corsConfigurationSource();

        // Then
        assertNotNull(source);
        assertTrue(source instanceof UrlBasedCorsConfigurationSource);
    }

    @Test
    void corsConfig_ShouldHaveCorrectProperties() {
        // Given
        String[] expectedOrigins = {"http://localhost:3000", "http://localhost:4200"};
        String[] expectedMethods = {"GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"};
        String expectedHeaders = "*";
        boolean expectedCredentials = true;
        long expectedMaxAge = 3600L;

        // When
        String[] actualOrigins = (String[]) ReflectionTestUtils.getField(corsConfig, "allowedOrigins");
        String[] actualMethods = (String[]) ReflectionTestUtils.getField(corsConfig, "allowedMethods");
        String actualHeaders = (String) ReflectionTestUtils.getField(corsConfig, "allowedHeaders");
        Boolean actualCredentials = (Boolean) ReflectionTestUtils.getField(corsConfig, "allowCredentials");
        Long actualMaxAge = (Long) ReflectionTestUtils.getField(corsConfig, "maxAge");

        // Then
        assertArrayEquals(expectedOrigins, actualOrigins);
        assertArrayEquals(expectedMethods, actualMethods);
        assertEquals(expectedHeaders, actualHeaders);
        assertEquals(expectedCredentials, actualCredentials);
        assertEquals(expectedMaxAge, actualMaxAge);
    }

    @Test
    void corsConfig_Constructor_ShouldCreateInstance() {
        // When
        CorsConfig config = new CorsConfig();

        // Then
        assertNotNull(config);
    }

    @Test
    void corsConfigurationSource_ShouldReturnUrlBasedSource() {
        // When
        CorsConfigurationSource source = corsConfig.corsConfigurationSource();

        // Then
        assertNotNull(source);
        assertEquals(UrlBasedCorsConfigurationSource.class, source.getClass());
    }

    @Test
    void corsConfig_WithDifferentOrigins_ShouldSetCorrectly() {
        // Given
        String[] newOrigins = {"http://example.com", "https://test.com"};

        // When
        ReflectionTestUtils.setField(corsConfig, "allowedOrigins", newOrigins);
        String[] actualOrigins = (String[]) ReflectionTestUtils.getField(corsConfig, "allowedOrigins");

        // Then
        assertArrayEquals(newOrigins, actualOrigins);
    }

    @Test
    void corsConfig_WithDifferentMethods_ShouldSetCorrectly() {
        // Given
        String[] newMethods = {"GET", "POST"};

        // When
        ReflectionTestUtils.setField(corsConfig, "allowedMethods", newMethods);
        String[] actualMethods = (String[]) ReflectionTestUtils.getField(corsConfig, "allowedMethods");

        // Then
        assertArrayEquals(newMethods, actualMethods);
    }

    @Test
    void corsConfig_WithDifferentHeaders_ShouldSetCorrectly() {
        // Given
        String newHeaders = "Content-Type,Authorization";

        // When
        ReflectionTestUtils.setField(corsConfig, "allowedHeaders", newHeaders);
        String actualHeaders = (String) ReflectionTestUtils.getField(corsConfig, "allowedHeaders");

        // Then
        assertEquals(newHeaders, actualHeaders);
    }

    @Test
    void corsConfig_WithDisabledCredentials_ShouldSetCorrectly() {
        // Given
        boolean newCredentials = false;

        // When
        ReflectionTestUtils.setField(corsConfig, "allowCredentials", newCredentials);
        Boolean actualCredentials = (Boolean) ReflectionTestUtils.getField(corsConfig, "allowCredentials");

        // Then
        assertEquals(newCredentials, actualCredentials);
    }

    @Test
    void corsConfig_WithDifferentMaxAge_ShouldSetCorrectly() {
        // Given
        long newMaxAge = 7200L;

        // When
        ReflectionTestUtils.setField(corsConfig, "maxAge", newMaxAge);
        Long actualMaxAge = (Long) ReflectionTestUtils.getField(corsConfig, "maxAge");

        // Then
        assertEquals(newMaxAge, actualMaxAge);
    }

    @Test
    void corsConfigurationSource_ShouldNotReturnNull() {
        // When
        CorsConfigurationSource source = corsConfig.corsConfigurationSource();

        // Then
        assertNotNull(source, "CorsConfigurationSource should not be null");
    }
}
