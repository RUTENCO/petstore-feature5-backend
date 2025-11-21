package com.petstore.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Configuración para habilitar procesamiento asíncrono
 */
@Configuration
@EnableAsync
public class AsyncConfig {
    // Las configuraciones por defecto de Spring son suficientes para nuestro caso
    // Si necesitas personalizar el ThreadPoolTaskExecutor, puedes crear un @Bean aquí
}
