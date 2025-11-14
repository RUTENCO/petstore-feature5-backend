package com.petstore.backend.config;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import org.mockito.Mock;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.springframework.core.env.Environment;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ProfileAwareRunnerTest {

    @Mock
    private Environment environment;

    @Mock
    private Logger logger;

    private ProfileAwareRunner profileAwareRunner;

    @BeforeEach
    void setUp() {
        profileAwareRunner = new ProfileAwareRunner(environment);
        // Inject the mocked logger
        ReflectionTestUtils.setField(profileAwareRunner, "loggerMessage", logger);
        lenient().when(logger.isInfoEnabled()).thenReturn(true);
    }

    @Test
    void constructor_ShouldCreateProfileAwareRunner() {
        // Given
        Environment testEnvironment = mock(Environment.class);

        // When
        ProfileAwareRunner runner = new ProfileAwareRunner(testEnvironment);

        // Then
        assertNotNull(runner);
    }

    @Test
    void run_WithNoActiveProfiles_ShouldLogDevelopmentMode() throws Exception {
        // Given
        String[] activeProfiles = {};
        String[] defaultProfiles = {"default"};
        
        when(environment.getActiveProfiles()).thenReturn(activeProfiles);
        when(environment.getDefaultProfiles()).thenReturn(defaultProfiles);

        // When
        profileAwareRunner.run();

        // Then
        verify(logger).info("DEBUG - Active profiles: {}", java.util.Arrays.toString(activeProfiles));
        verify(logger).info("DEBUG - Default profiles: {}", java.util.Arrays.toString(defaultProfiles));
        verify(logger, times(2)).info("============================================");
        verify(logger).info("PETSTORE BACKEND - PERFIL DE SEGURIDAD");
        verify(logger, atLeastOnce()).info(contains("Modo: DESARROLLO"));
    }

    @Test
    void run_WithDevProfile_ShouldLogDevelopmentConfiguration() throws Exception {
        // Given
        String[] activeProfiles = {"dev"};
        String[] defaultProfiles = {"default"};
        
        when(environment.getActiveProfiles()).thenReturn(activeProfiles);
        when(environment.getDefaultProfiles()).thenReturn(defaultProfiles);

        // When
        profileAwareRunner.run();

        // Then
        verify(logger).info(" Perfiles activos: {}", java.util.Arrays.toString(activeProfiles));
        verify(logger, atLeastOnce()).info(contains("Modo: DESARROLLO"));
        verify(logger, atLeastOnce()).info(contains("GraphiQL: http://localhost:8080/graphiql"));
        verify(logger, atLeastOnce()).info(contains("GraphQL: http://localhost:8080/graphql (PÚBLICO)"));
        verify(logger, atLeastOnce()).info(contains("H2 Console: Habilitado"));
    }

    @Test
    void run_WithProdProfile_ShouldLogProductionConfiguration() throws Exception {
        // Given
        String[] activeProfiles = {"prod"};
        String[] defaultProfiles = {"default"};
        
        when(environment.getActiveProfiles()).thenReturn(activeProfiles);
        when(environment.getDefaultProfiles()).thenReturn(defaultProfiles);

        // When
        profileAwareRunner.run();

        // Then
        verify(logger).info(" Perfiles activos: {}", java.util.Arrays.toString(activeProfiles));
        verify(logger, atLeastOnce()).info(contains("Modo: PRODUCCIÓN"));
        verify(logger, atLeastOnce()).info(contains("GraphiQL: DESHABILITADO"));
        verify(logger, atLeastOnce()).info(contains("GraphQL: http://localhost:8080/graphql (REQUIERE JWT)"));
        verify(logger, atLeastOnce()).info(contains("H2 Console: DESHABILITADO"));
    }

    @Test
    void run_WithCustomProfile_ShouldLogCustomMode() throws Exception {
        // Given
        String[] activeProfiles = {"custom"};
        String[] defaultProfiles = {"default"};
        
        when(environment.getActiveProfiles()).thenReturn(activeProfiles);
        when(environment.getDefaultProfiles()).thenReturn(defaultProfiles);

        // When
        profileAwareRunner.run();

        // Then
        verify(logger).info(" Perfiles activos: {}", java.util.Arrays.toString(activeProfiles));
        verify(logger, atLeastOnce()).info(contains("Modo: DESARROLLO (perfil custom)"));
    }

    @Test
    void run_ShouldAlwaysLogJWTInstructions() throws Exception {
        // Given
        String[] activeProfiles = {"test"};
        String[] defaultProfiles = {"default"};
        
        when(environment.getActiveProfiles()).thenReturn(activeProfiles);
        when(environment.getDefaultProfiles()).thenReturn(defaultProfiles);

        // When
        profileAwareRunner.run();

        // Then
        verify(logger).info(" Para usar GraphQL en producción:");
        verify(logger).info("   1. POST /api/auth/login");
        verify(logger).info("   2. Usar token en header: Authorization: Bearer <token>");
        verify(logger, times(2)).info(" ============================================");
    }

    @Test
    void run_WithMultipleProfiles_ShouldHandleCorrectly() throws Exception {
        // Given
        String[] activeProfiles = {"dev", "local"};
        String[] defaultProfiles = {"default"};
        
        when(environment.getActiveProfiles()).thenReturn(activeProfiles);
        when(environment.getDefaultProfiles()).thenReturn(defaultProfiles);

        // When
        profileAwareRunner.run();

        // Then
        verify(logger).info(" Perfiles activos: {}", java.util.Arrays.toString(activeProfiles));
        verify(logger, atLeastOnce()).info(contains("Modo: DESARROLLO"));
    }

    @Test
    void run_WithLoggerDisabled_ShouldNotLog() throws Exception {
        // Given
        String[] activeProfiles = {"test"};
        String[] defaultProfiles = {"default"};
        
        when(environment.getActiveProfiles()).thenReturn(activeProfiles);
        when(environment.getDefaultProfiles()).thenReturn(defaultProfiles);
        when(logger.isInfoEnabled()).thenReturn(false);

        // When
        profileAwareRunner.run();

        // Then
        verify(logger).isInfoEnabled();
        verify(logger, never()).info(anyString());
        verify(logger, never()).info(anyString(), any(Object.class));
    }

    @Test
    void run_WithArgs_ShouldAcceptArguments() throws Exception {
        // Given
        String[] activeProfiles = {"test"};
        String[] defaultProfiles = {"default"};
        String[] args = {"arg1", "arg2"};
        
        when(environment.getActiveProfiles()).thenReturn(activeProfiles);
        when(environment.getDefaultProfiles()).thenReturn(defaultProfiles);

        // When & Then - Should not throw exception
        assertDoesNotThrow(() -> profileAwareRunner.run(args));
    }

    @Test
    void run_WithEmptyDefaultProfiles_ShouldHandleCorrectly() throws Exception {
        // Given
        String[] activeProfiles = {};
        String[] defaultProfiles = {};
        
        when(environment.getActiveProfiles()).thenReturn(activeProfiles);
        when(environment.getDefaultProfiles()).thenReturn(defaultProfiles);

        // When
        profileAwareRunner.run();

        // Then
        verify(logger).info("DEBUG - Active profiles: {}", java.util.Arrays.toString(activeProfiles));
        verify(logger).info("DEBUG - Default profiles: {}", java.util.Arrays.toString(defaultProfiles));
    }

    @Test
    void run_WithNullArgs_ShouldNotThrowException() throws Exception {
        // Given
        String[] activeProfiles = {"test"};
        String[] defaultProfiles = {"default"};
        
        when(environment.getActiveProfiles()).thenReturn(activeProfiles);
        when(environment.getDefaultProfiles()).thenReturn(defaultProfiles);

        // When & Then
        assertDoesNotThrow(() -> profileAwareRunner.run((String[]) null));
    }

    @Test
    void profileAwareRunner_ShouldImplementCommandLineRunner() {
        // Then
        assertTrue(profileAwareRunner instanceof org.springframework.boot.CommandLineRunner);
    }

    @Test
    void profileAwareRunner_ShouldHaveEnvironmentDependency() throws Exception {
        // Given
        Environment testEnvironment = mock(Environment.class);
        
        // When
        ProfileAwareRunner runner = new ProfileAwareRunner(testEnvironment);
        
        // Then
        assertNotNull(runner);
        // Verify that the environment was injected correctly by checking if it's used
        String[] profiles = {"test"};
        when(testEnvironment.getActiveProfiles()).thenReturn(profiles);
        when(testEnvironment.getDefaultProfiles()).thenReturn(profiles);
        
        // Inject logger for this test
        ReflectionTestUtils.setField(runner, "loggerMessage", logger);
        when(logger.isInfoEnabled()).thenReturn(true);
        
        runner.run();
        verify(testEnvironment).getActiveProfiles();
        verify(testEnvironment).getDefaultProfiles();
    }
}
