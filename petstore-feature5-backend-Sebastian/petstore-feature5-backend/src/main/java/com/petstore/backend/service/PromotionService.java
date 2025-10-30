package com.petstore.backend.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.petstore.backend.dto.CategoryDTO;
import com.petstore.backend.dto.PromotionDTO;
import com.petstore.backend.dto.PromotionDeletedDTO;
import com.petstore.backend.entity.Category;
import com.petstore.backend.entity.Promotion;
import com.petstore.backend.entity.PromotionDeleted;
import com.petstore.backend.entity.Status;
import com.petstore.backend.entity.User;
import com.petstore.backend.repository.CategoryRepository;
import com.petstore.backend.repository.PromotionDeletedRepository;
import com.petstore.backend.repository.PromotionRepository;
import com.petstore.backend.repository.StatusRepository;
import com.petstore.backend.repository.UserRepository;

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
    
    @Autowired
    private PromotionDeletedRepository promotionDeletedRepository;

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
     * Obtiene promociones en la papelera temporal
     */
    public List<PromotionDeletedDTO> getDeletedPromotions() {
        ZonedDateTime thirtyDaysAgo = ZonedDateTime.now().minusDays(30);
        List<PromotionDeleted> deletedPromotions = promotionDeletedRepository.findRestorable(thirtyDaysAgo);
        
        return deletedPromotions.stream()
                .map(this::convertDeletedToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtiene promociones eliminadas por un usuario específico
     */
    public List<PromotionDeletedDTO> getDeletedPromotionsByUser(Integer userId) {
        List<PromotionDeleted> deletedPromotions = promotionDeletedRepository.findByDeletedByUserId(userId);
        
        return deletedPromotions.stream()
                .map(this::convertDeletedToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Restaura una promoción de la papelera temporal
     */
    @Transactional
    public boolean restorePromotion(Integer promotionId, Integer userId) {
        try {
            Optional<PromotionDeleted> deletedPromotionOpt = promotionDeletedRepository.findById(promotionId);
            if (!deletedPromotionOpt.isPresent()) {
                return false;
            }
            
            PromotionDeleted deletedPromotion = deletedPromotionOpt.get();
            
            // Verificar que no han pasado 30 días
            ZonedDateTime thirtyDaysAgo = ZonedDateTime.now().minusDays(30);
            if (deletedPromotion.getDeletedAt().isBefore(thirtyDaysAgo)) {
                return false;
            }
            
            // Crear nueva promoción basada en los datos eliminados
            Promotion restoredPromotion = new Promotion();
            restoredPromotion.setPromotionName(deletedPromotion.getPromotionName());
            restoredPromotion.setDescription(deletedPromotion.getDescription());
            restoredPromotion.setStartDate(deletedPromotion.getStartDate());
            restoredPromotion.setEndDate(deletedPromotion.getEndDate());
            restoredPromotion.setDiscountValue(deletedPromotion.getDiscountValue());
            restoredPromotion.setStatus(deletedPromotion.getStatus());
            restoredPromotion.setUser(deletedPromotion.getUser());
            restoredPromotion.setCategory(deletedPromotion.getCategory());
            
            // Guardar promoción restaurada
            promotionRepository.save(restoredPromotion);
            
            // Eliminar de papelera
            promotionDeletedRepository.delete(deletedPromotion);
            
            return true;
            
        } catch (Exception e) {
            System.err.println("Error restoring promotion: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Restaura una promoción usando la función de base de datos
     */
    @Transactional
    public boolean restorePromotionUsingDBFunction(Integer promotionId, Integer userId) {
        try {
            // 1. Establecer el actor para la función de BD
            if (userId != null) {
                promotionRepository.setActor(userId);
            }
            
            // 2. Llamar a la función de BD que hace todo automáticamente
            promotionRepository.restorePromotionUsingFunction(promotionId);
            
            return true;
            
        } catch (Exception e) {
            System.err.println("Error restoring promotion using DB function: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Convierte PromotionDeleted a DTO
     */
    private PromotionDeletedDTO convertDeletedToDTO(PromotionDeleted deletedPromotion) {
        return new PromotionDeletedDTO(
                deletedPromotion.getPromotionId(),
                deletedPromotion.getPromotionName(),
                deletedPromotion.getDescription(),
                deletedPromotion.getStartDate(),
                deletedPromotion.getEndDate(),
                deletedPromotion.getDiscountValue(),
                deletedPromotion.getStatus(), // Objeto completo
                deletedPromotion.getUser(), // Objeto completo
                deletedPromotion.getCategory(), // Objeto completo
                deletedPromotion.getDeletedAt(),
                deletedPromotion.getDeletedBy() // Objeto completo
        );
    }
    
    /**
     * Elimina una promoción guardándola primero en papelera temporal
     */
    @Transactional
    public boolean deletePromotion(Integer promotionId) {
        return deletePromotion(promotionId, null);
    }
    
    /**
     * Elimina una promoción usando los triggers de la base de datos
     * Los triggers se encargan de:
     * 1. Desvincular productos (trg_promotions_soft_delete)
     * 2. Mover a promotions_deleted (trg_promotions_soft_delete)
     * 3. Registrar auditoría (trg_promotions_audit)
     * 
     * @param promotionId ID de la promoción a eliminar
     * @param deletedByUserId ID del usuario que elimina (opcional)
     */
    @Transactional
    public boolean deletePromotion(Integer promotionId, Integer deletedByUserId) {
        try {
            // Verificar que la promoción existe
            Optional<Promotion> promotionOpt = promotionRepository.findById(promotionId);
            if (!promotionOpt.isPresent()) {
                return false;
            }
            
            // 1. Establecer el actor (usuario que elimina) para los triggers de BD
            if (deletedByUserId != null) {
                // Usar función de BD para establecer el contexto del usuario
                promotionRepository.setActor(deletedByUserId);
            }
            
            // 2. ELIMINAR la promoción - Los triggers se encargan de TODO automáticamente:
            //    - trg_promotions_soft_delete: Desvincula productos, mueve a promotions_deleted
            //    - trg_promotions_audit: Registra la auditoría
            //    - trg_promotions_deleted_guard: Impide duplicados en promotions_deleted
            Promotion promotion = promotionOpt.get();
            promotionRepository.delete(promotion);
            
            return true;
            
        } catch (Exception e) {
            System.err.println("Error deleting promotion: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
