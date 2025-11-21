package com.petstore.backend.config;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;

class AsyncConfigTest {

    private AsyncConfig asyncConfig;

    @BeforeEach
    void setUp() {
        asyncConfig = new AsyncConfig();
    }

    @Test
    @DisplayName("Should create AsyncConfig instance")
    void shouldCreateAsyncConfigInstance() {
        // Then
        assertNotNull(asyncConfig);
    }

    @Test
    @DisplayName("Should be annotated with Configuration and EnableAsync")
    void shouldBeAnnotatedWithConfigurationAndEnableAsync() {
        // Given
        Class<AsyncConfig> configClass = AsyncConfig.class;

        // Then
        assertTrue(configClass.isAnnotationPresent(org.springframework.context.annotation.Configuration.class));
        assertTrue(configClass.isAnnotationPresent(EnableAsync.class));
    }

    @Test
    @DisplayName("Should enable async processing in Spring context")
    void shouldEnableAsyncProcessingInSpringContext() {
        // Given
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        
        try {
            // When
            context.register(AsyncConfig.class);
            context.refresh();

            // Then
            assertNotNull(context.getBean(AsyncConfig.class));
            assertTrue(context.containsBean("asyncConfig"));
            
        } finally {
            context.close();
        }
    }

    @Test
    @DisplayName("Should be a valid Spring configuration class")
    void shouldBeValidSpringConfigurationClass() {
        // Given
        Class<AsyncConfig> configClass = AsyncConfig.class;

        // Then
        // Check if the class is not abstract and has a default constructor
        assertFalse(java.lang.reflect.Modifier.isAbstract(configClass.getModifiers()));
        
        // Check if it has a no-args constructor
        try {
            configClass.getDeclaredConstructor();
            assertTrue(true); // Constructor exists
        } catch (NoSuchMethodException e) {
            fail("AsyncConfig should have a default constructor");
        }
    }
}
