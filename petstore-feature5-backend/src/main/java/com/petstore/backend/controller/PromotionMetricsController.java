package com.petstore.backend.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.petstore.backend.dto.ProductMetricsDTO;
import com.petstore.backend.dto.PromotionPerformanceDTO;
import com.petstore.backend.service.PromotionMetricsService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Métricas de Promociones", description = "API para obtener métricas de rendimiento de promociones")
@RestController
@RequestMapping("/api/promotions/metrics")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080", "https://petstore-feature5-backend.onrender.com", "https://fluffy-deals-hub.vercel.app"})
public class PromotionMetricsController {

    private final PromotionMetricsService promotionMetricsService;

    public PromotionMetricsController(PromotionMetricsService promotionMetricsService) {
        this.promotionMetricsService = promotionMetricsService;
    }

    @Operation(
        summary = "Obtener métricas de rendimiento de una promoción",
        description = "Retorna métricas completas de rendimiento incluyendo variación de inventario, ventas e ingresos para una promoción específica"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Métricas de promoción obtenidas exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = PromotionPerformanceDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Promoción no encontrada",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "204", 
            description = "Promoción encontrada pero sin métricas registradas",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Error interno del servidor",
            content = @Content
        )
    })
    @GetMapping("/{promotionId}")
    public ResponseEntity<PromotionPerformanceDTO> getPromotionMetrics(
            @Parameter(description = "ID de la promoción", example = "1", required = true)
            @PathVariable Integer promotionId) {
        try {
            Optional<PromotionPerformanceDTO> metrics = promotionMetricsService.getPromotionPerformance(promotionId);
            
            if (metrics.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            PromotionPerformanceDTO performance = metrics.get();
            
            // Si no hay métricas registradas (todos los valores en 0)
            if (performance.getTotalProducts() == 0) {
                return ResponseEntity.noContent().build();
            }
            
            return ResponseEntity.ok(performance);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(
        summary = "Obtener métricas detalladas por producto",
        description = "Retorna métricas específicas de cada producto asociado a la promoción"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Métricas por producto obtenidas exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(type = "array", implementation = ProductMetricsDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Promoción no encontrada o sin productos",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Error interno del servidor",
            content = @Content
        )
    })
    @GetMapping("/{promotionId}/products")
    public ResponseEntity<List<ProductMetricsDTO>> getProductMetrics(
            @Parameter(description = "ID de la promoción", example = "1", required = true)
            @PathVariable Integer promotionId) {
        try {
            List<ProductMetricsDTO> productMetrics = promotionMetricsService.getProductMetricsByPromotionId(promotionId);
            
            if (productMetrics.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok(productMetrics);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(
        summary = "Verificar si una promoción tiene métricas",
        description = "Endpoint para verificar rápidamente si existen datos de métricas para una promoción"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Verificación exitosa",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(type = "object", example = "{\"hasMetrics\": true, \"promotionId\": 1}")
            )
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Error interno del servidor",
            content = @Content
        )
    })
    @GetMapping("/{promotionId}/exists")
    public ResponseEntity<Map<String, Object>> checkMetricsExist(
            @Parameter(description = "ID de la promoción", example = "1", required = true)
            @PathVariable Integer promotionId) {
        try {
            boolean hasMetrics = promotionMetricsService.hasMetrics(promotionId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("promotionId", promotionId);
            response.put("hasMetrics", hasMetrics);
            response.put("message", hasMetrics ? "Métricas disponibles" : "No hay datos disponibles para esta promoción");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Endpoints administrativos (requieren autenticación)
    
    @Operation(
        summary = "Inicializar métricas para una promoción",
        description = "Crea métricas iniciales para todos los productos asociados a la promoción. Solo para administradores."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Métricas inicializadas exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(type = "object", example = "{\"success\": true, \"message\": \"Métricas inicializadas\"}")
            )
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "No autorizado - Token requerido",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Error interno del servidor",
            content = @Content
        )
    })
    @PostMapping("/{promotionId}/initialize")
    public ResponseEntity<Map<String, Object>> initializeMetrics(
            @Parameter(description = "ID de la promoción", example = "1", required = true)
            @PathVariable Integer promotionId) {
        try {
            promotionMetricsService.initializeMetricsForPromotion(promotionId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("promotionId", promotionId);
            response.put("message", "Métricas inicializadas exitosamente");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al inicializar métricas: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @Operation(
        summary = "Simular actualización de métricas",
        description = "Endpoint para testing que simula ventas y actualiza métricas. Solo para administradores."
    )
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Métricas actualizadas exitosamente",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(type = "object")
            )
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Datos inválidos",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "No autorizado - Token requerido",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Error interno del servidor",
            content = @Content
        )
    })
    @PostMapping("/{promotionId}/simulate")
    public ResponseEntity<Map<String, Object>> simulateMetricsUpdate(
            @Parameter(description = "ID de la promoción", example = "1", required = true)
            @PathVariable Integer promotionId,
            @Parameter(description = "Datos de simulación", required = true)
            @RequestBody SimulationRequest request) {
        try {
            if (request.getProductId() == null || request.getUnitsSold() == null || request.getUnitsSold() < 0) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "productId y unitsSold son requeridos y unitsSold debe ser >= 0");
                
                return ResponseEntity.badRequest().body(errorResponse);
            }

            promotionMetricsService.simulateMetricsUpdate(promotionId, request.getProductId(), request.getUnitsSold());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("promotionId", promotionId);
            response.put("productId", request.getProductId());
            response.put("unitsSoldAdded", request.getUnitsSold());
            response.put("message", "Métricas actualizadas exitosamente");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al actualizar métricas: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    // Clase interna para el request de simulación
    @Schema(description = "Request para simular actualización de métricas")
    public static class SimulationRequest {
        
        @Schema(description = "ID del producto", example = "1", required = true)
        private Integer productId;
        
        @Schema(description = "Número de unidades vendidas a simular", example = "5", required = true)
        private Integer unitsSold;

        public SimulationRequest() {}

        public Integer getProductId() {
            return productId;
        }

        public void setProductId(Integer productId) {
            this.productId = productId;
        }

        public Integer getUnitsSold() {
            return unitsSold;
        }

        public void setUnitsSold(Integer unitsSold) {
            this.unitsSold = unitsSold;
        }
    }
}
