package com.petstore.backend.service;

import com.petstore.backend.dto.CategoryDTO;
import com.petstore.backend.dto.PromotionDTO;
import com.petstore.backend.entity.Promotion;
import com.petstore.backend.entity.Status;
import com.petstore.backend.entity.User;
import com.petstore.backend.entity.Category;
import com.petstore.backend.repository.PromotionRepository;
import com.petstore.backend.repository.StatusRepository;
import com.petstore.backend.repository.UserRepository;
import com.petstore.backend.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PromotionService {

    @Autowired
    private PromotionRepository promotionRepository;
    
    @Autowired
    private StatusRepository statusRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;

    /**
     * Obtiene todas las promociones activas y vigentes
     */
    public List<PromotionDTO> getAllActivePromotions() {
        LocalDate today = LocalDate.now();
        
        // Buscar promociones activas
        List<Promotion> activePromotions = promotionRepository.findActivePromotions();
        
        // Filtrar las que están vigentes (fecha actual entre start y end)
        return activePromotions.stream()
                .filter(promotion -> !today.isBefore(promotion.getStartDate()) && !today.isAfter(promotion.getEndDate()))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todas las promociones (activas e inactivas) para administración
     */
    public List<PromotionDTO> getAllPromotions() {
        List<Promotion> allPromotions = promotionRepository.findAll();
        
        return allPromotions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene promociones por categoría
     */
    public List<PromotionDTO> getPromotionsByCategory(Integer categoryId) {
        List<Promotion> promotions = promotionRepository.findByCategoryCategoryId(categoryId);
        
        return promotions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene promociones vigentes para la fecha actual
     */
    public List<PromotionDTO> getValidPromotions() {
        LocalDate today = LocalDate.now();
        List<Promotion> promotions = promotionRepository.findValidPromotions(today);
        
        return promotions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convierte una entidad Promotion a PromotionDTO
     */
    private PromotionDTO convertToDTO(Promotion promotion) {
        PromotionDTO dto = new PromotionDTO();
        
        dto.setPromotionId(promotion.getPromotionId());
        dto.setPromotionName(promotion.getPromotionName());
        dto.setDescription(promotion.getDescription());
        
        // Convertir discount value de Double a BigDecimal
        if (promotion.getDiscountValue() != null) {
            dto.setDiscountPercentage(BigDecimal.valueOf(promotion.getDiscountValue()));
        }
        
        // Convertir LocalDate a LocalDateTime (agregando hora 00:00:00)
        if (promotion.getStartDate() != null) {
            dto.setStartDate(promotion.getStartDate().atStartOfDay());
        }
        if (promotion.getEndDate() != null) {
            dto.setEndDate(promotion.getEndDate().atTime(23, 59, 59));
        }
        
        // Nota: Las entidades Promotion no tienen createdAt/updatedAt en el esquema actual
        // Esto se puede agregar más adelante si es necesario
        
        // Convertir status
        if (promotion.getStatus() != null) {
            dto.setStatus(promotion.getStatus().getStatusName());
        }
        
        // Convertir category si existe
        if (promotion.getCategory() != null) {
            CategoryDTO categoryDTO = new CategoryDTO();
            categoryDTO.setCategoryId(promotion.getCategory().getCategoryId());
            categoryDTO.setCategoryName(promotion.getCategory().getCategoryName());
            categoryDTO.setDescription(promotion.getCategory().getDescription());
            dto.setCategory(categoryDTO);
        }
        
        // Nota: La entidad Promotion actual no tiene relación directa con Product
        // Solo tiene relación con Category, que puede contener productos
        dto.setProduct(null);
        
        return dto;
    }

    // === MÉTODOS PARA GRAPHQL que retornan entidades directamente ===

    /**
     * Obtiene todas las promociones activas como entidades para GraphQL
     */
    public List<Promotion> getAllActivePromotionsEntities() {
        LocalDate today = LocalDate.now();
        
        // Buscar promociones activas
        List<Promotion> activePromotions = promotionRepository.findActivePromotions();
        
        // Filtrar las que están vigentes (fecha actual entre start y end)
        return activePromotions.stream()
                .filter(promotion -> !today.isBefore(promotion.getStartDate()) && !today.isAfter(promotion.getEndDate()))
                .collect(Collectors.toList());
    }

    /**
     * Obtiene promociones expiradas como entidades para GraphQL
     */
    public List<Promotion> getAllExpiredPromotionsEntities() {
        return promotionRepository.findExpiredPromotions();
    }

    /**
     * Obtiene promociones programadas como entidades para GraphQL
     */
    public List<Promotion> getAllScheduledPromotionsEntities() {
        return promotionRepository.findScheduledPromotions();
    }

    /**
     * Obtiene promociones por estado específico como entidades para GraphQL
     */
    public List<Promotion> getPromotionsByStatusEntities(String statusName) {
        return promotionRepository.findByStatusName(statusName);
    }

    /**
     * Obtiene promociones por categoría como entidades para GraphQL
     */
    public List<Promotion> getPromotionsByCategoryEntities(Integer categoryId) {
        return promotionRepository.findByCategoryCategoryId(categoryId);
    }

    /**
     * Obtiene una promoción por ID como entidad para GraphQL
     */
    public Promotion getPromotionByIdEntity(Integer id) {
        return promotionRepository.findById(id).orElse(null);
    }

    // === MÉTODOS CRUD PARA MUTACIONES ===

    /**
     * Crea una nueva promoción
     */
    public Promotion createPromotion(String promotionName, String description, 
                                   LocalDate startDate, LocalDate endDate, 
                                   Double discountValue, Integer statusId, 
                                   Integer userId, Integer categoryId) {
        Promotion promotion = new Promotion();
        promotion.setPromotionName(promotionName);
        promotion.setDescription(description);
        promotion.setStartDate(startDate);
        promotion.setEndDate(endDate);
        promotion.setDiscountValue(discountValue);
        
        // Buscar y asignar entidades relacionadas
        if (statusId != null) {
            Status status = statusRepository.findById(statusId).orElse(null);
            promotion.setStatus(status);
        }
        
        if (userId != null) {
            User user = userRepository.findById(userId).orElse(null);
            promotion.setUser(user);
        }
        
        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId).orElse(null);
            promotion.setCategory(category);
        }
        
        return promotionRepository.save(promotion);
    }

    /**
     * Actualiza una promoción existente
     */
    public Promotion updatePromotion(Integer promotionId, String promotionName, String description,
                                   LocalDate startDate, LocalDate endDate,
                                   Double discountValue, Integer statusId,
                                   Integer userId, Integer categoryId) {
        Promotion promotion = promotionRepository.findById(promotionId).orElse(null);
        if (promotion == null) {
            return null;
        }
        
        // Actualizar campos
        if (promotionName != null) promotion.setPromotionName(promotionName);
        if (description != null) promotion.setDescription(description);
        if (startDate != null) promotion.setStartDate(startDate);
        if (endDate != null) promotion.setEndDate(endDate);
        if (discountValue != null) promotion.setDiscountValue(discountValue);
        
        // Actualizar entidades relacionadas
        if (statusId != null) {
            Status status = statusRepository.findById(statusId).orElse(null);
            promotion.setStatus(status);
        }
        
        if (userId != null) {
            User user = userRepository.findById(userId).orElse(null);
            promotion.setUser(user);
        }
        
        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId).orElse(null);
            promotion.setCategory(category);
        }
        
        return promotionRepository.save(promotion);
    }

    /**
     * Elimina una promoción
     */
    public boolean deletePromotion(Integer promotionId) {
        try {
            if (promotionRepository.existsById(promotionId)) {
                promotionRepository.deleteById(promotionId);
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error deleting promotion: " + e.getMessage());
            return false;
        }
    }
}
