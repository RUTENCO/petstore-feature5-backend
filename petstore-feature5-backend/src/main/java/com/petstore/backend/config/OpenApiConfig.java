package com.petstore.backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuración de OpenAPI/Swagger para documentación de la API
 * Proporciona documentación interactiva para todos los endpoints REST
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private String serverPort;

    @Value("${spring.application.name:petstore-backend}")
    private String applicationName;

    @Bean
    public OpenAPI petStoreOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Pet Store Feature 5 Backend API")
                        .description("API REST completa para gestión de tienda de mascotas que incluye: " +
                                "gestión de productos, categorías, promociones, usuarios, pedidos, auditoría, " +
                                "autenticación JWT, GraphQL y funcionalidades avanzadas de e-commerce")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Equipo Pet Store")
                                .email("dev@petstore.com")
                                .url("https://github.com/RUTENCO/petstore-feature5-backend"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Servidor de Desarrollo Local"),
                        new Server()
                                .url("https://petstore-feature5-backend.onrender.com")
                                .description("Servidor de Producción en Render")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT token para autenticación. " +
                                        "Obtén el token usando POST /api/auth/login y úsalo como: 'Bearer {token}'")));
    }
}
