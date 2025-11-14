package com.petstore.backend.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Pruebas para PromotionController usando MockMvc
 * Solo incluye endpoints que realmente existen en el controlador
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class PromotionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // Tests para endpoints GET existentes

    @Test
    void testGetAllActivePromotions() throws Exception {
        // GET /api/promotions
        mockMvc.perform(get("/api/promotions"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testGetAllPromotions() throws Exception {
        // GET /api/promotions/all
        mockMvc.perform(get("/api/promotions/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testGetPromotionsByCategory() throws Exception {
        // GET /api/promotions/category/{categoryId}
        mockMvc.perform(get("/api/promotions/category/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testGetPromotionsByCategory_InvalidId() throws Exception {
        // Test con ID de categoría inválido
        mockMvc.perform(get("/api/promotions/category/invalid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetValidPromotions() throws Exception {
        // GET /api/promotions/valid
        mockMvc.perform(get("/api/promotions/valid"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testGetStatus() throws Exception {
        // GET /api/promotions/status
        mockMvc.perform(get("/api/promotions/status"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testGetDeletedPromotions() throws Exception {
        // GET /api/promotions/trash
        mockMvc.perform(get("/api/promotions/trash"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testGetDeletedPromotionsByUser() throws Exception {
        // GET /api/promotions/trash/user/{userId}
        mockMvc.perform(get("/api/promotions/trash/user/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testGetDeletedPromotionsByUser_InvalidId() throws Exception {
        // Test con ID de usuario inválido
        mockMvc.perform(get("/api/promotions/trash/user/invalid"))
                .andExpect(status().isBadRequest());
    }

    // Tests para endpoints DELETE existentes

    @Test
    void testSoftDeletePromotion_WithoutUserId() throws Exception {
        // DELETE /api/promotions/{id} sin userId
        mockMvc.perform(delete("/api/promotions/1"))
                .andExpect(status().isNotFound()); // El controlador devuelve 404
    }

    @Test
    void testSoftDeletePromotion_WithUserId() throws Exception {
        // DELETE /api/promotions/{id} con userId
        mockMvc.perform(delete("/api/promotions/1").param("userId", "1"))
                .andExpect(status().isNotFound()); // El controlador devuelve 404
    }

    @Test
    void testSoftDeletePromotion_InvalidId() throws Exception {
        // Test eliminación con ID inválido
        mockMvc.perform(delete("/api/promotions/invalid").param("userId", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testPermanentDeletePromotion() throws Exception {
        // DELETE /api/promotions/permanent/{id}
        mockMvc.perform(delete("/api/promotions/permanent/1").param("userId", "1"))
                .andExpect(status().isBadRequest()); // El controlador devuelve 400
    }

    @Test
    void testPermanentDeletePromotion_InvalidId() throws Exception {
        // Test eliminación permanente con ID inválido
        mockMvc.perform(delete("/api/promotions/permanent/invalid").param("userId", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testPermanentDeletePromotion_WithoutUserId() throws Exception {
        // Test eliminación permanente sin userId
        mockMvc.perform(delete("/api/promotions/permanent/1"))
                .andExpect(status().isBadRequest()); // Falta userId requerido
    }

    @Test
    void testRemoveProductsFromPromotion() throws Exception {
        // DELETE /api/promotions/{promotionId}/products
        mockMvc.perform(delete("/api/promotions/1/products")
                .param("productIds", "1", "2"))
                .andExpect(status().isBadRequest()); // El controlador devuelve 400
    }

    @Test
    void testRemoveProductsFromPromotion_InvalidId() throws Exception {
        // Test remoción con ID inválido
        mockMvc.perform(delete("/api/promotions/invalid/products")
                .param("productIds", "1", "2"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRemoveProductsFromPromotion_WithoutProductIds() throws Exception {
        // Test remoción sin productIds
        mockMvc.perform(delete("/api/promotions/1/products"))
                .andExpect(status().isBadRequest()); // Falta productIds requerido
    }

    // Tests para endpoints POST existentes

    @Test
    void testRestorePromotion() throws Exception {
        // POST /api/promotions/{id}/restore
        mockMvc.perform(post("/api/promotions/1/restore").param("userId", "1"))
                .andExpect(status().isBadRequest()); // El controlador devuelve 400
    }

    @Test
    void testRestorePromotion_InvalidId() throws Exception {
        // Test restauración con ID inválido
        mockMvc.perform(post("/api/promotions/invalid/restore").param("userId", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRestorePromotion_WithoutUserId() throws Exception {
        // Test restauración sin userId
        mockMvc.perform(post("/api/promotions/1/restore"))
                .andExpect(status().isBadRequest()); // Falta userId requerido
    }

    @Test
    void testAssociateProductsToPromotion() throws Exception {
        // POST /api/promotions/{promotionId}/products
        mockMvc.perform(post("/api/promotions/1/products")
                .param("productIds", "1", "2", "3"))
                .andExpect(status().isBadRequest()); // El controlador devuelve 400
    }

    @Test
    void testAssociateProductsToPromotion_InvalidId() throws Exception {
        // Test asociación con ID inválido
        mockMvc.perform(post("/api/promotions/invalid/products")
                .param("productIds", "1", "2", "3"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAssociateProductsToPromotion_WithoutProductIds() throws Exception {
        // Test asociación sin productIds
        mockMvc.perform(post("/api/promotions/1/products"))
                .andExpect(status().isBadRequest()); // Falta productIds requerido
    }

    // Tests adicionales para casos extremos

    @Test
    void testGetPromotionsByCategory_NonExistentCategory() throws Exception {
        // Test con categoría que no existe
        mockMvc.perform(get("/api/promotions/category/999"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testGetDeletedPromotionsByUser_NonExistentUser() throws Exception {
        // Test con usuario que no existe
        mockMvc.perform(get("/api/promotions/trash/user/999"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void testSoftDeletePromotion_NonExistentPromotion() throws Exception {
        // Test eliminación de promoción que no existe
        mockMvc.perform(delete("/api/promotions/999").param("userId", "1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testPermanentDeletePromotion_NonExistentPromotion() throws Exception {
        // Test eliminación permanente de promoción que no existe
        mockMvc.perform(delete("/api/promotions/permanent/999").param("userId", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRestorePromotion_NonExistentPromotion() throws Exception {
        // Test restauración de promoción que no existe
        mockMvc.perform(post("/api/promotions/999/restore").param("userId", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAssociateProductsToPromotion_NonExistentPromotion() throws Exception {
        // Test asociación con promoción que no existe
        mockMvc.perform(post("/api/promotions/999/products")
                .param("productIds", "1", "2"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRemoveProductsFromPromotion_NonExistentPromotion() throws Exception {
        // Test remoción con promoción que no existe
        mockMvc.perform(delete("/api/promotions/999/products")
                .param("productIds", "1", "2"))
                .andExpect(status().isBadRequest());
    }

    // Tests para validación de parámetros

    @Test
    void testAssociateProductsToPromotion_EmptyProductIds() throws Exception {
        // Test asociación con lista vacía de productos
        mockMvc.perform(post("/api/promotions/1/products")
                .param("productIds", ""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRemoveProductsFromPromotion_EmptyProductIds() throws Exception {
        // Test remoción con lista vacía de productos
        mockMvc.perform(delete("/api/promotions/1/products")
                .param("productIds", ""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAssociateProductsToPromotion_InvalidProductIds() throws Exception {
        // Test asociación con IDs de productos inválidos
        mockMvc.perform(post("/api/promotions/1/products")
                .param("productIds", "invalid", "also-invalid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRemoveProductsFromPromotion_InvalidProductIds() throws Exception {
        // Test remoción con IDs de productos inválidos
        mockMvc.perform(delete("/api/promotions/1/products")
                .param("productIds", "invalid", "also-invalid"))
                .andExpect(status().isBadRequest());
    }

    // Tests adicionales para cobertura

    @Test
    void testRestorePromotion_InvalidUserId() throws Exception {
        // Test restauración con userId inválido
        mockMvc.perform(post("/api/promotions/1/restore").param("userId", "invalid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testPermanentDeletePromotion_InvalidUserId() throws Exception {
        // Test eliminación permanente con userId inválido
        mockMvc.perform(delete("/api/promotions/permanent/1").param("userId", "invalid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSoftDeletePromotion_InvalidUserId() throws Exception {
        // Test eliminación temporal con userId inválido
        mockMvc.perform(delete("/api/promotions/1").param("userId", "invalid"))
                .andExpect(status().isBadRequest());
    }
}
