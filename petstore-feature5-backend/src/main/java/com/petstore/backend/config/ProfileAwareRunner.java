package com.petstore.backend.config;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class ProfileAwareRunner implements CommandLineRunner {

    Logger loggerMessage = LoggerFactory.getLogger(getClass());

    
    private final Environment environment; // Inyección de dependencia para acceder a los perfiles activos
    public ProfileAwareRunner(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void run(String... args) {
    String[] activeProfiles = environment.getActiveProfiles();
    String[] defaultProfiles = environment.getDefaultProfiles();
    
    // Implementación de verificación condicional para satisfacer el analizador estricto:
    if (loggerMessage.isInfoEnabled()) {
        // Todas estas líneas están ahora dentro del bloque condicional
        loggerMessage.info("DEBUG - Active profiles: {}", java.util.Arrays.toString(activeProfiles));
        loggerMessage.info("DEBUG - Default profiles: {}", java.util.Arrays.toString(defaultProfiles));
        loggerMessage.info("============================================");
        loggerMessage.info("PETSTORE BACKEND - PERFIL DE SEGURIDAD");
        loggerMessage.info("============================================");
        
        if (activeProfiles.length == 0) {
            loggerMessage.info(" Perfiles activos: {}, {}" , Arrays.toString(defaultProfiles) , " (default)");
            loggerMessage.info(" Modo: DESARROLLO");
            loggerMessage.info(" GraphiQL: http://localhost:8080/graphiql");
            loggerMessage.info(" GraphQL: http://localhost:8080/graphql (PÚBLICO)");
            loggerMessage.info(" H2 Console: Habilitado");
            loggerMessage.info(" Actuator: Todos los endpoints");
        } else {
            loggerMessage.info(" Perfiles activos: {}", Arrays.toString(activeProfiles));
            
            boolean isDev = Arrays.asList(activeProfiles).contains("dev");
            boolean isProd = Arrays.asList(activeProfiles).contains("prod");
            
            if (isDev) {
                loggerMessage.info(" Modo: DESARROLLO");
                loggerMessage.info(" GraphiQL: http://localhost:8080/graphiql");
                loggerMessage.info(" GraphQL: http://localhost:8080/graphql (PÚBLICO)");
                loggerMessage.info(" H2 Console: Habilitado");
                loggerMessage.info(" Actuator: Todos los endpoints");
            } else if (isProd) {
                loggerMessage.info(" Modo: PRODUCCIÓN");
                loggerMessage.info(" GraphiQL: DESHABILITADO");
                loggerMessage.info(" GraphQL: http://localhost:8080/graphql (REQUIERE JWT)");
                loggerMessage.info(" H2 Console: DESHABILITADO");
                loggerMessage.info(" Actuator: Solo /health público");
            } else {
                loggerMessage.info(" Modo: DESARROLLO (perfil custom)");
            }
        }
        
        loggerMessage.info(" ============================================");
        loggerMessage.info(" Para usar GraphQL en producción:");
        loggerMessage.info("   1. POST /api/auth/login");
        loggerMessage.info("   2. Usar token en header: Authorization: Bearer <token>");
        loggerMessage.info(" ============================================");
        }
    }
}