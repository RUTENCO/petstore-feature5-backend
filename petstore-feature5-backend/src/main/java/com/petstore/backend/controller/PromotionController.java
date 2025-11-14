package com.petstore.backend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.petstore.backend.dto.PromotionDTO;
import com.petstore.backend.dto.PromotionDeletedDTO;
import com.petstore.backend.service.PromotionService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/promotions")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080", "https://petstore-feature5-backend.onrender.com", "https://fluffy-deals-hub.vercel.app"})
@Tag(name = "Promociones", description = "API para gestión de promociones de la tienda")
public class PromotionController {

    private static final String MESSAGE_KEY = "message"; // Constante para key de mensaje en respuestas
    private static final String SUCCESS_STATUS = "success"; // Constante para estado de éxito

    private final PromotionService promotionService; // Inyección de dependencia del servicio de promociones

    public PromotionController(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    /**
     * Obtiene todas las promociones activas y vigentes
     * GET /api/promotions
     */
    @Operation(
        summary = "Obtener promociones activas",
        description = "Obtiene todas las promociones activas y vigentes disponibles para los clientes"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de promociones activas obtenida exitosamente",
                content = @Content(mediaType = "application/json", 
                          schema = @Schema(implementation = PromotionDTO.class))),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<List<PromotionDTO>> getAllActivePromotions() {
        try {
            List<PromotionDTO> promotions = promotionService.getAllActivePromotions();
            return ResponseEntity.ok(promotions);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Obtiene todas las promociones (para administración)
     * GET /api/promotions/all
     */
    @Operation(
        summary = "Obtener todas las promociones",
        description = "Obtiene todas las promociones del sistema (para administradores)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista completa de promociones obtenida exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/all")
    public ResponseEntity<List<PromotionDTO>> getAllPromotions() {
        try {
            List<PromotionDTO> promotions = promotionService.getAllPromotions();
            return ResponseEntity.ok(promotions);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Obtiene promociones por categoría
     * GET /api/promotions/category/{categoryId}
     */
    @Operation(
        summary = "Obtener promociones por categoría",
        description = "Obtiene todas las promociones activas de una categoría específica"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Promociones de la categoría obtenidas exitosamente"),
        @ApiResponse(responseCode = "400", description = "ID de categoría inválido"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<PromotionDTO>> getPromotionsByCategory(
            @Parameter(description = "ID de la categoría", required = true, example = "1")
            @PathVariable Integer categoryId) {
        try {
            List<PromotionDTO> promotions = promotionService.getPromotionsByCategory(categoryId);
            return ResponseEntity.ok(promotions);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(
            summary = "Obtener promociones vigentes",
            description = "Retorna todas las promociones que están activas y vigentes en la fecha actual"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", 
                    description = "Promociones vigentes obtenidas exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = PromotionDTO.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "500", 
                    description = "Error interno del servidor",
                    content = @Content
            )
    })
    @GetMapping("/valid")
    public ResponseEntity<List<PromotionDTO>> getValidPromotions() {
        try {
            List<PromotionDTO> promotions = promotionService.getValidPromotions();
            return ResponseEntity.ok(promotions);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(
            summary = "Estado del servicio de promociones",
            description = "Retorna información sobre el estado del servicio y endpoints disponibles"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", 
                    description = "Estado del servicio obtenido exitosamente",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        return ResponseEntity.ok().body(java.util.Map.of(
            "status", "OK",
            MESSAGE_KEY, "Promotion service is running",
            "timestamp", java.time.LocalDateTime.now(),
            "endpoints", java.util.List.of(
                "GET /api/promotions - Promociones activas y vigentes",
                "GET /api/promotions/all - Todas las promociones (admin)",
                "GET /api/promotions/category/{id} - Promociones por categoría",
                "GET /api/promotions/valid - Promociones vigentes hoy",
                "DELETE /api/promotions/{id}?userId={userId} - Eliminar promoción (papelera temporal)",
                "GET /api/promotions/trash - Ver papelera temporal",
                "POST /api/promotions/{id}/restore?userId={userId} - Restaurar promoción",
                "GET /api/promotions/status - Estado del servicio"
            )
        ));
    }
    
    @Operation(
            summary = "Eliminar promoción (papelera temporal)",
            description = "Elimina una promoción y la envía a la papelera temporal por 30 días antes de eliminación permanente"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", 
                    description = "Promoción eliminada exitosamente",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404", 
                    description = "Promoción no encontrada",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500", 
                    description = "Error interno del servidor",
                    content = @Content
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deletePromotion(
            @Parameter(description = "ID de la promoción a eliminar", example = "1", required = true)
            @PathVariable Integer id,
            @Parameter(description = "ID del usuario que realiza la eliminación", example = "1")
            @RequestParam(required = false) Integer userId) {
        
        try {
            boolean deleted = promotionService.deletePromotion(id, userId);
            
            if (deleted) {
                return ResponseEntity.ok().body(java.util.Map.of(
                    SUCCESS_STATUS, true,
                    MESSAGE_KEY, "Promoción eliminada exitosamente y movida a papelera temporal (30 días)",
                    "promotionId", id
                ));
            } else {
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(java.util.Map.of(
                SUCCESS_STATUS, false,
                MESSAGE_KEY, "Error interno del servidor: " + e.getMessage()
            ));
        }
    }
    
    @Operation(
            summary = "Ver papelera temporal de promociones",
            description = "Retorna todas las promociones eliminadas que están en la papelera temporal (30 días)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", 
                    description = "Papelera temporal obtenida exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = PromotionDeletedDTO.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "500", 
                    description = "Error interno del servidor",
                    content = @Content
            )
    })
    @GetMapping("/trash")
    public ResponseEntity<List<PromotionDeletedDTO>> getDeletedPromotions() {
        try {
            List<PromotionDeletedDTO> deletedPromotions = promotionService.getDeletedPromotions();
            return ResponseEntity.ok(deletedPromotions);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @Operation(
            summary = "Ver papelera temporal por usuario",
            description = "Retorna las promociones eliminadas por un usuario específico que están en la papelera temporal"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", 
                    description = "Promociones eliminadas por el usuario obtenidas exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = PromotionDeletedDTO.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "500", 
                    description = "Error interno del servidor",
                    content = @Content
            )
    })
    @GetMapping("/trash/user/{userId}")
    public ResponseEntity<List<PromotionDeletedDTO>> getDeletedPromotionsByUser(
            @Parameter(description = "ID del usuario", example = "1", required = true)
            @PathVariable Integer userId) {
        try {
            List<PromotionDeletedDTO> deletedPromotions = promotionService.getDeletedPromotionsByUser(userId);
            return ResponseEntity.ok(deletedPromotions);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    @Operation(
            summary = "Restaurar promoción desde papelera",
            description = "Restaura una promoción eliminada desde la papelera temporal y la vuelve a activar"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", 
                    description = "Promoción restaurada exitosamente",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "404", 
                    description = "Promoción no encontrada en la papelera",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500", 
                    description = "Error interno del servidor",
                    content = @Content
            )
    })
    @PostMapping("/{id}/restore")
    public ResponseEntity<Map<String, Object>> restorePromotion(
            @Parameter(description = "ID de la promoción a restaurar", example = "1", required = true)
            @PathVariable Integer id,
            @Parameter(description = "ID del usuario que realiza la restauración", example = "1", required = true)
            @RequestParam Integer userId) {
        
        try {
            boolean restored = promotionService.restorePromotion(id, userId);
            
            if (restored) {
                return ResponseEntity.ok().body(java.util.Map.of(
                    SUCCESS_STATUS, true,
                    MESSAGE_KEY, "Promoción restaurada exitosamente desde la papelera temporal",
                    "promotionId", id
                ));
            } else {
                return ResponseEntity.badRequest().body(java.util.Map.of(
                    SUCCESS_STATUS, false,
                    MESSAGE_KEY, "No se pudo restaurar la promoción. Puede que no exista o hayan pasado más de 30 días"
                ));
            }
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(java.util.Map.of(
                SUCCESS_STATUS, false,
                MESSAGE_KEY, "Error interno del servidor: " + e.getMessage()
            ));
        }
    }

    /**
     * Elimina permanentemente una promoción de la papelera temporal
     * DELETE /api/promotions/permanent/{id}
     */
    @DeleteMapping("/permanent/{id}")
    public ResponseEntity<Map<String, Object>> permanentDeletePromotion(
            @PathVariable Integer id,
            @RequestParam Integer userId) {
        try {
            boolean deleted = promotionService.permanentDeletePromotion(id, userId);
            
            if (deleted) {
                return ResponseEntity.ok(java.util.Map.of(
                    SUCCESS_STATUS, true,
                    MESSAGE_KEY, "Promoción eliminada permanentemente con éxito"
                ));
            } else {
                return ResponseEntity.badRequest().body(java.util.Map.of(
                    SUCCESS_STATUS, false,
                    MESSAGE_KEY, "No se pudo eliminar permanentemente la promoción. Puede que no exista en la papelera"
                ));
            }
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(java.util.Map.of(
                SUCCESS_STATUS, false,
                MESSAGE_KEY, "Error interno del servidor: " + e.getMessage()
            ));
        }
    }

    /**
     * Asocia productos a una promoción
     * POST /api/promotions/{promotionId}/products
     */
    @PostMapping("/{promotionId}/products")
    public ResponseEntity<Map<String, Object>> associateProductsToPromotion(
            @PathVariable Integer promotionId,
            @RequestParam List<Integer> productIds) {
        try {
            boolean associated = promotionService.associateProductsToPromotion(promotionId, productIds);
            
            if (associated) {
                return ResponseEntity.ok(java.util.Map.of(
                    SUCCESS_STATUS, true,
                    MESSAGE_KEY, "Productos asociados a la promoción con éxito"
                ));
            } else {
                return ResponseEntity.badRequest().body(java.util.Map.of(
                    SUCCESS_STATUS, false,
                    MESSAGE_KEY, "No se pudieron asociar los productos. Verifique que la promoción exista"
                ));
            }
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(java.util.Map.of(
                SUCCESS_STATUS, false,
                MESSAGE_KEY, "Error interno del servidor: " + e.getMessage()
            ));
        }
    }

    /**
     * Remueve productos de una promoción
     * DELETE /api/promotions/{promotionId}/products
     */
    @DeleteMapping("/{promotionId}/products")
    public ResponseEntity<Map<String, Object>> removeProductsFromPromotion(
            @PathVariable Integer promotionId,
            @RequestParam List<Integer> productIds) {
        try {
            boolean removed = promotionService.removeProductsFromPromotion(promotionId, productIds);
            
            if (removed) {
                return ResponseEntity.ok(java.util.Map.of(
                    SUCCESS_STATUS, true,
                    MESSAGE_KEY, "Productos removidos de la promoción con éxito"
                ));
            } else {
                return ResponseEntity.badRequest().body(java.util.Map.of(
                    SUCCESS_STATUS, false,
                    MESSAGE_KEY, "No se pudieron remover los productos. Verifique que la promoción exista"
                ));
            }
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(java.util.Map.of(
                SUCCESS_STATUS, false,
                MESSAGE_KEY, "Error interno del servidor: " + e.getMessage()
            ));
        }
    }
}
