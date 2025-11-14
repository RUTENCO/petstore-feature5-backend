package com.petstore.backend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final String PRODUCTAPIPATTERN = "/api/products/**";
    Logger loggerMessage = LoggerFactory.getLogger(getClass());


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
                // CSRF protection is disabled because this backend uses JWT (Bearer token) authentication.
                // Requests are stateless and do not rely on session cookies, making CSRF attacks not applicable.
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
                                          java.util.Arrays.asList(activeProfiles).contains("test") ||
                                          activeProfiles.length == 0; // Por defecto desarrollo
                    
                    // Implementación de verificación condicional para satisfacer el analizador estricto:
                    if (loggerMessage.isInfoEnabled()) {
                        // Log del modo detectado
                        loggerMessage.info("   Security Mode Detection:");
                        loggerMessage.info("   Active Profiles: {} " , java.util.Arrays.toString(activeProfiles));
                        loggerMessage.info("   Is Production: {}" , isProduction);
                        loggerMessage.info("   Is Development: {}" , isDevelopment);
                    }
                    
                    // GraphiQL y GraphQL SIEMPRE PÚBLICOS (tanto dev como prod)
                    authz.requestMatchers("/graphiql", "/graphiql/**").permitAll();
                    authz.requestMatchers("/graphql", "/graphql/**").permitAll();
                    
                    // Swagger/OpenAPI endpoints públicos
                    authz.requestMatchers("/swagger-ui/**", "/swagger-ui.html").permitAll();
                    authz.requestMatchers("/api-docs/**", "/api-docs").permitAll();
                    authz.requestMatchers("/v3/api-docs/**", "/v3/api-docs").permitAll();
                    
                    if (isDevelopment) {
                        // MODO DESARROLLO: Más permisivo
                        authz.requestMatchers("/h2-console/**").permitAll(); // H2 Console para dev
                        authz.requestMatchers("/actuator/**").permitAll(); // Actuator para dev
                        authz.requestMatchers("/test", "/graphql-test").permitAll(); // Test endpoints
                        
                        // Productos públicos para testing en desarrollo
                        authz.requestMatchers("GET", PRODUCTAPIPATTERN).permitAll();
                        authz.requestMatchers("POST", PRODUCTAPIPATTERN).authenticated(); // Crear requiere auth
                        authz.requestMatchers("PUT", PRODUCTAPIPATTERN).authenticated(); // Actualizar requiere auth
                        authz.requestMatchers("DELETE", PRODUCTAPIPATTERN).authenticated(); // Eliminar requiere auth
                        
                    } else {
                        // MODO PRODUCCIÓN: Más restrictivo
                        authz.requestMatchers("/h2-console/**").denyAll(); //  No H2 en producción
                        authz.requestMatchers("/test", "/graphql-test").denyAll(); //  No test endpoints
                        
                        // Solo actuator health público en producción
                        authz.requestMatchers("/actuator/**").authenticated();
                        
                        // Productos: solo lectura pública, modificaciones requieren auth
                        authz.requestMatchers("GET", "/api/products", "/api/products/category/*").permitAll();
                        authz.requestMatchers(PRODUCTAPIPATTERN).authenticated();
                    }
                    
                    // Whitelist adicional si está configurada (debe ir ANTES de las reglas específicas)
                    if (whitelistEndpoints != null && whitelistEndpoints.length > 0) {
                        authz.requestMatchers(whitelistEndpoints).permitAll();
                    }
                    
                    // Promociones siempre requieren autenticación (excepto algunas lecturas públicas)
                    authz.requestMatchers("GET", "/api/promotions", "/api/promotions/status").permitAll(); // Lectura pública
                    authz.requestMatchers("/api/promotions/**").authenticated(); // El resto requiere auth
                    
                    // Categorías: permitir lectura, auth para modificaciones
                    authz.requestMatchers("GET", "/api/categories", "/api/categories/*", "/api/categories/info").permitAll();
                    authz.requestMatchers("/api/categories/**").authenticated();
                    
                    // Perfil de usuario siempre requiere autenticación
                    authz.requestMatchers("/api/auth/me", "/api/auth/verify").authenticated();
                    
                    // lo demás requiere autenticación
                    authz.anyRequest().authenticated();
                })
                
                .build();
    }
}