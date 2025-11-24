package com.petstore.backend.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.petstore.backend.service.PromotionService;

/**
 * Tareas programadas para el sistema de promociones
 */
@Component
public class PromotionScheduler {

    private static final Logger logger = LoggerFactory.getLogger(PromotionScheduler.class);

    private final PromotionService promotionService;

    public PromotionScheduler(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    /**
     * Actualiza automáticamente los estados de las promociones cada día a las 00:01
     * Esto asegura que las promociones cambien de estado automáticamente sin intervención manual
     */
    @Scheduled(cron = "0 1 0 * * *") // Cada día a las 00:01 AM
    public void updatePromotionStatusesDaily() {
        logger.info("⏰ Iniciando tarea programada: Actualización diaria de estados de promociones");
        
        try {
            int updatedCount = promotionService.updateAllPromotionStatuses();
            
            if (updatedCount > 0) {
                logger.info("✅ Tarea completada: {} promociones actualizadas automáticamente", updatedCount);
            } else {
                logger.info("ℹ️ Tarea completada: No se requirieron actualizaciones de estado");
            }
            
        } catch (Exception e) {
            logger.error("❌ Error en tarea programada de actualización de estados: {}", e.getMessage(), e);
        }
    }

    /**
     * Actualiza los estados cada hora durante horas laborales (opcional)
     * Comentado por defecto, se puede habilitar si se requiere mayor frecuencia
     */
    // @Scheduled(cron = "0 0 8-18 * * MON-FRI") // Cada hora de 8 AM a 6 PM, lunes a viernes
    public void updatePromotionStatusesHourly() {
        logger.info("⏰ Iniciando tarea programada: Actualización horaria de estados de promociones");
        
        try {
            int updatedCount = promotionService.updateAllPromotionStatuses();
            logger.info("✅ Actualización horaria completada: {} promociones actualizadas", updatedCount);
            
        } catch (Exception e) {
            logger.error("❌ Error en tarea horaria de actualización: {}", e.getMessage(), e);
        }
    }
}
