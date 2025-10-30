package com.petstore.backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class ProfileAwareRunner implements CommandLineRunner {

    @Autowired
    private Environment environment;

    @Override
    public void run(String... args) {
        String[] activeProfiles = environment.getActiveProfiles();
        String[] defaultProfiles = environment.getDefaultProfiles();
        
        // Debug logging
        System.out.println("DEBUG - Active profiles: " + java.util.Arrays.toString(activeProfiles));
        System.out.println("DEBUG - Default profiles: " + java.util.Arrays.toString(defaultProfiles));
        
        System.out.println("============================================");
        System.out.println("PETSTORE BACKEND - PERFIL DE SEGURIDAD");
        System.out.println("============================================");
        
        if (activeProfiles.length == 0) {
            System.out.println(" Perfiles activos: " + Arrays.toString(defaultProfiles) + " (default)");
            System.out.println(" Modo: DESARROLLO");
            System.out.println(" GraphiQL: http://localhost:8080/graphiql");
            System.out.println(" GraphQL: http://localhost:8080/graphql (PÚBLICO)");
            System.out.println(" H2 Console: Habilitado");
            System.out.println(" Actuator: Todos los endpoints");
        } else {
            System.out.println(" Perfiles activos: " + Arrays.toString(activeProfiles));
            
            boolean isDev = Arrays.asList(activeProfiles).contains("dev");
            boolean isProd = Arrays.asList(activeProfiles).contains("prod");
            
            if (isDev) {
                System.out.println(" Modo: DESARROLLO");
                System.out.println(" GraphiQL: http://localhost:8080/graphiql");
                System.out.println(" GraphQL: http://localhost:8080/graphql (PÚBLICO)");
                System.out.println(" H2 Console: Habilitado");
                System.out.println(" Actuator: Todos los endpoints");
            } else if (isProd) {
                System.out.println(" Modo: PRODUCCIÓN");
                System.out.println(" GraphiQL: DESHABILITADO");
                System.out.println(" GraphQL: http://localhost:8080/graphql (REQUIERE JWT)");
                System.out.println(" H2 Console: DESHABILITADO");
                System.out.println(" Actuator: Solo /health público");
            } else {
                System.out.println(" Modo: DESARROLLO (perfil custom)");
            }
        }
        
        System.out.println(" ============================================");
        System.out.println(" Para usar GraphQL en producción:");
        System.out.println("   1. POST /api/auth/login");
        System.out.println("   2. Usar token en header: Authorization: Bearer <token>");
        System.out.println(" ============================================");
    }
}
