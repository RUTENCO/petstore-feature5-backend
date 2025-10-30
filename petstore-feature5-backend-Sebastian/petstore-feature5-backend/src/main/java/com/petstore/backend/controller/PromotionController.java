package com.petstore.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

@RestController
@RequestMapping("/api/promotions")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001", "http://localhost:5173", "http://127.0.0.1:5500", "http://localhost:5500"})
public class PromotionController {

    @Autowired
    private PromotionService promotionService;

    /**
     * Obtiene todas las promociones activas y vigentes
     * GET /api/promotions
     */
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
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<PromotionDTO>> getPromotionsByCategory(@PathVariable Integer categoryId) {
        try {
            List<PromotionDTO> promotions = promotionService.getPromotionsByCategory(categoryId);
            return ResponseEntity.ok(promotions);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Obtiene promociones vigentes para hoy
     * GET /api/promotions/valid
     */
    @GetMapping("/valid")
    public ResponseEntity<List<PromotionDTO>> getValidPromotions() {
        try {
            List<PromotionDTO> promotions = promotionService.getValidPromotions();
            return ResponseEntity.ok(promotions);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Endpoint de estado para verificar que el servicio funciona
     * GET /api/promotions/status
     */
    @GetMapping("/status")
    public ResponseEntity<?> getStatus() {
        return ResponseEntity.ok().body(java.util.Map.of(
            "status", "OK",
            "message", "Promotion service is running",
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
    
    /**
     * Elimina una promoción y la guarda en papelera temporal
     * DELETE /api/promotions/{id}?userId={userId}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePromotion(
            @PathVariable Integer id,
            @RequestParam(required = false) Integer userId) {
        
        try {
            boolean deleted = promotionService.deletePromotion(id, userId);
            
            if (deleted) {
                return ResponseEntity.ok().body(java.util.Map.of(
                    "success", true,
                    "message", "Promoción eliminada exitosamente y movida a papelera temporal (30 días)",
                    "promotionId", id
                ));
            } else {
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(java.util.Map.of(
                "success", false,
                "message", "Error interno del servidor: " + e.getMessage()
            ));
        }
    }
    
    /**
     * Obtiene promociones en la papelera temporal
     * GET /api/promotions/trash
     */
    @GetMapping("/trash")
    public ResponseEntity<List<PromotionDeletedDTO>> getDeletedPromotions() {
        try {
            List<PromotionDeletedDTO> deletedPromotions = promotionService.getDeletedPromotions();
            return ResponseEntity.ok(deletedPromotions);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Obtiene promociones eliminadas por un usuario específico
     * GET /api/promotions/trash/user/{userId}
     */
    @GetMapping("/trash/user/{userId}")
    public ResponseEntity<List<PromotionDeletedDTO>> getDeletedPromotionsByUser(@PathVariable Integer userId) {
        try {
            List<PromotionDeletedDTO> deletedPromotions = promotionService.getDeletedPromotionsByUser(userId);
            return ResponseEntity.ok(deletedPromotions);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Restaura una promoción de la papelera temporal
     * POST /api/promotions/{id}/restore?userId={userId}
     */
    @PostMapping("/{id}/restore")
    public ResponseEntity<?> restorePromotion(
            @PathVariable Integer id,
            @RequestParam Integer userId) {
        
        try {
            boolean restored = promotionService.restorePromotion(id, userId);
            
            if (restored) {
                return ResponseEntity.ok().body(java.util.Map.of(
                    "success", true,
                    "message", "Promoción restaurada exitosamente desde la papelera temporal",
                    "promotionId", id
                ));
            } else {
                return ResponseEntity.badRequest().body(java.util.Map.of(
                    "success", false,
                    "message", "No se pudo restaurar la promoción. Puede que no exista o hayan pasado más de 30 días"
                ));
            }
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(java.util.Map.of(
                "success", false,
                "message", "Error interno del servidor: " + e.getMessage()
            ));
        }
    }
}
