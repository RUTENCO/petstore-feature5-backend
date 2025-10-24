package com.petstore.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
public class SecurityConfig {

    private final CorsConfigurationSource corsConfigurationSource;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final Environment environment;

    @Value("${app.security.whitelist:}")
    private String[] whitelistEndpoints;

    public SecurityConfig(CorsConfigurationSource corsConfigurationSource, 
                         JwtAuthenticationFilter jwtAuthenticationFilter,
                         Environment environment) {
        this.corsConfigurationSource = corsConfigurationSource;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.environment = environment;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                // Configurar CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                
                // Deshabilitar CSRF para API REST/GraphQL
                .csrf(AbstractHttpConfigurer::disable)
                
                // Configurar headers de seguridad
                .headers(headers -> headers
                    .frameOptions(frameOptions -> frameOptions.deny())
                    .contentTypeOptions(contentTypeOptions -> {})
                    .xssProtection(xss -> {})
                    .referrerPolicy(referrer -> referrer
                        .policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN))
                    .httpStrictTransportSecurity(hstsConfig -> hstsConfig
                        .maxAgeInSeconds(31536000)
                        .includeSubDomains(true))
                )
                
                // Configurar gestión de sesiones (stateless para API)
                .sessionManagement(session -> 
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                
                // Agregar filtro JWT antes del filtro de autenticación de usuario/contraseña
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(session -> 
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                
                // Configurar autorización de endpoints
                .authorizeHttpRequests(authz -> {
                    // Endpoints básicos siempre públicos
                    authz.requestMatchers("/api/auth/login", "/api/auth/register").permitAll();
                    authz.requestMatchers("/actuator/health").permitAll();
                    
                    // Verificar si estamos en modo desarrollo o producción
                    String[] activeProfiles = environment.getActiveProfiles();
                    boolean isProduction = java.util.Arrays.asList(activeProfiles).contains("prod") ||
                                         java.util.Arrays.asList(activeProfiles).contains("production");
                    boolean isDevelopment = java.util.Arrays.asList(activeProfiles).contains("dev") ||
                                          java.util.Arrays.asList(activeProfiles).contains("development") ||
                                          activeProfiles.length == 0; // Por defecto desarrollo
                    
                    // Log del modo detectado
                    System.out.println("🔍 Security Mode Detection:");
                    System.out.println("   Active Profiles: " + java.util.Arrays.toString(activeProfiles));
                    System.out.println("   Is Production: " + isProduction);
                    System.out.println("   Is Development: " + isDevelopment);
                    
                    // GraphiQL y GraphQL SIEMPRE PÚBLICOS (tanto dev como prod)
                    authz.requestMatchers("/graphiql", "/graphiql/**").permitAll();
                    authz.requestMatchers("/graphql", "/graphql/**").permitAll();
                    
                    if (isDevelopment) {
                        // 🔓 MODO DESARROLLO: Más permisivo
                        authz.requestMatchers("/h2-console/**").permitAll(); // H2 Console para dev
                        authz.requestMatchers("/actuator/**").permitAll(); // Actuator para dev
                        authz.requestMatchers("/test", "/graphql-test").permitAll(); // Test endpoints
                        
                        // Productos públicos para testing en desarrollo
                        authz.requestMatchers("GET", "/api/products/**").permitAll();
                        authz.requestMatchers("POST", "/api/products/**").authenticated(); // Crear requiere auth
                        authz.requestMatchers("PUT", "/api/products/**").authenticated(); // Actualizar requiere auth
                        authz.requestMatchers("DELETE", "/api/products/**").authenticated(); // Eliminar requiere auth
                        
                    } else {
                        // 🔒 MODO PRODUCCIÓN: Más restrictivo
                        authz.requestMatchers("/h2-console/**").denyAll(); // ❌ No H2 en producción
                        authz.requestMatchers("/test", "/graphql-test").denyAll(); // ❌ No test endpoints
                        
                        // Solo actuator health público en producción
                        authz.requestMatchers("/actuator/**").authenticated();
                        
                        // Productos: solo lectura pública, modificaciones requieren auth
                        authz.requestMatchers("GET", "/api/products", "/api/products/category/*").permitAll();
                        authz.requestMatchers("/api/products/**").authenticated();
                    }
                    
                    // Promociones siempre requieren autenticación (excepto algunas lecturas públicas)
                    authz.requestMatchers("GET", "/api/promotions", "/api/promotions/status").permitAll(); // Lectura pública
                    authz.requestMatchers("/api/promotions/**").authenticated(); // El resto requiere auth
                    
                    // Categorías: permitir lectura, auth para modificaciones
                    authz.requestMatchers("GET", "/api/categories", "/api/categories/*", "/api/categories/info").permitAll();
                    authz.requestMatchers("/api/categories/**").authenticated();
                    
                    // Perfil de usuario siempre requiere autenticación
                    authz.requestMatchers("/api/auth/me", "/api/auth/verify").authenticated();
                    
                    // Whitelist adicional si está configurada
                    if (whitelistEndpoints != null && whitelistEndpoints.length > 0) {
                        authz.requestMatchers(whitelistEndpoints).permitAll();
                    }
                    
                    // Todo lo demás requiere autenticación
                    authz.anyRequest().authenticated();
                })
                
                .build();
    }
}