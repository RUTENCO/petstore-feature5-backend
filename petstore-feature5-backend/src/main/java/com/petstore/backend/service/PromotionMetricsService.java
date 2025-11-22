package com.petstore.backend.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.petstore.backend.dto.CategoryDTO;
import com.petstore.backend.dto.ProductMetricsDTO;
import com.petstore.backend.dto.PromotionPerformanceDTO;
import com.petstore.backend.entity.Product;
import com.petstore.backend.entity.Promotion;
import com.petstore.backend.entity.PromotionMetrics;
import com.petstore.backend.repository.ProductRepository;
import com.petstore.backend.repository.PromotionMetricsRepository;
import com.petstore.backend.repository.PromotionRepository;

@Service
@Transactional(readOnly = true)
public class PromotionMetricsService {

    private final PromotionMetricsRepository promotionMetricsRepository;
    private final PromotionRepository promotionRepository;
    private final ProductRepository productRepository;

    public PromotionMetricsService(PromotionMetricsRepository promotionMetricsRepository,
                                 PromotionRepository promotionRepository,
                                 ProductRepository productRepository) {
        this.promotionMetricsRepository = promotionMetricsRepository;
        this.promotionRepository = promotionRepository;
        this.productRepository = productRepository;
    }

    /**
     * Obtiene métricas de rendimiento completas de una promoción
     */
    public Optional<PromotionPerformanceDTO> getPromotionPerformance(Integer promotionId) {
        Optional<Promotion> promotionOpt = promotionRepository.findById(promotionId);
        if (promotionOpt.isEmpty()) {
            return Optional.empty();
        }

        Promotion promotion = promotionOpt.get();
        
        // Verificar si existen métricas para esta promoción
        if (!promotionMetricsRepository.existsByPromotionPromotionId(promotionId)) {
            // Si no hay métricas, crear un DTO vacío con información básica
            return Optional.of(createEmptyPerformanceDTO(promotion));
        }

        // Obtener métricas agregadas
        Integer totalUnitsSold = promotionMetricsRepository.getTotalUnitsSoldByPromotionId(promotionId);
        Double totalRevenue = promotionMetricsRepository.getTotalRevenueByPromotionId(promotionId);
        Integer totalInitialInventory = promotionMetricsRepository.getTotalInitialInventoryByPromotionId(promotionId);
        Integer totalCurrentInventory = promotionMetricsRepository.getTotalCurrentInventoryByPromotionId(promotionId);
        Integer totalProducts = promotionMetricsRepository.getUniqueProductCountByPromotionId(promotionId);
        LocalDateTime lastUpdated = promotionMetricsRepository.getLastUpdatedByPromotionId(promotionId);

        // Obtener métricas detalladas por producto
        List<ProductMetricsDTO> productMetrics = getProductMetricsByPromotionId(promotionId);

        // Determinar si la promoción está activa
        LocalDateTime now = LocalDateTime.now();
        boolean isActive = now.isAfter(promotion.getStartDate().atStartOfDay()) && 
                          now.isBefore(promotion.getEndDate().atTime(23, 59, 59));

        PromotionPerformanceDTO performanceDTO = new PromotionPerformanceDTO(
            promotionId,
            promotion.getPromotionName(),
            promotion.getDescription(),
            promotion.getDiscountValue(),
            promotion.getStartDate().atStartOfDay(),
            promotion.getEndDate().atTime(23, 59, 59),
            isActive,
            totalProducts,
            totalUnitsSold != null ? totalUnitsSold : 0,
            totalRevenue != null ? BigDecimal.valueOf(totalRevenue) : BigDecimal.ZERO,
            totalInitialInventory != null ? totalInitialInventory : 0,
            totalCurrentInventory != null ? totalCurrentInventory : 0,
            lastUpdated,
            productMetrics
        );

        return Optional.of(performanceDTO);
    }

    /**
     * Obtiene métricas detalladas por producto para una promoción
     */
    public List<ProductMetricsDTO> getProductMetricsByPromotionId(Integer promotionId) {
        List<PromotionMetrics> latestMetrics = promotionMetricsRepository.findLatestMetricsByPromotionId(promotionId);
        
        return latestMetrics.stream()
                .map(this::convertToProductMetricsDTO)
                .collect(Collectors.toList());
    }

    /**
     * Crea o actualiza métricas para un producto en una promoción
     */
    @Transactional
    public PromotionMetrics saveOrUpdateMetrics(Integer promotionId, Integer productId, 
                                              Integer initialInventory, Integer currentInventory, 
                                              Integer unitsSold, BigDecimal revenueGenerated) {
        
        Optional<Promotion> promotionOpt = promotionRepository.findById(promotionId);
        Optional<Product> productOpt = productRepository.findById(productId);
        
        if (promotionOpt.isEmpty() || productOpt.isEmpty()) {
            throw new IllegalArgumentException("Promotion or Product not found");
        }

        // Buscar métricas existentes
        Optional<PromotionMetrics> existingMetrics = 
            promotionMetricsRepository.findByPromotionPromotionIdAndProductProductId(promotionId, productId);

        PromotionMetrics metrics;
        if (existingMetrics.isPresent()) {
            // Actualizar métricas existentes
            metrics = existingMetrics.get();
            metrics.setCurrentInventory(currentInventory);
            metrics.setUnitsSold(unitsSold);
            metrics.setRevenueGenerated(revenueGenerated);
            metrics.setSnapshotDate(LocalDateTime.now());
            // Recalcular variación de SKU
            if (metrics.getInitialInventory() != null && unitsSold != null && metrics.getInitialInventory() > 0) {
                metrics.setSkuVariationPercentage(((double) unitsSold / metrics.getInitialInventory()) * 100);
            } else {
                metrics.setSkuVariationPercentage(0.0);
            }
            metrics.preUpdate();
        } else {
            // Crear nuevas métricas
            metrics = new PromotionMetrics(
                promotionOpt.get(),
                productOpt.get(),
                initialInventory,
                currentInventory,
                unitsSold,
                revenueGenerated
            );
        }

        return promotionMetricsRepository.save(metrics);
    }

    /**
     * Inicializa métricas para todos los productos de una promoción
     */
    @Transactional
    public void initializeMetricsForPromotion(Integer promotionId) {
        List<Product> products = productRepository.findByPromotionPromotionId(promotionId);
        
        for (Product product : products) {
            // Verificar si ya existen métricas para este producto
            Optional<PromotionMetrics> existing = 
                promotionMetricsRepository.findByPromotionPromotionIdAndProductProductId(promotionId, product.getProductId());
            
            if (existing.isEmpty()) {
                // Obtener el stock real del inventario usando el SKU del producto
                Integer initialStock = getInitialStockForProduct(product);
                
                // Crear métricas iniciales con stock real
                saveOrUpdateMetrics(promotionId, product.getProductId(), initialStock, initialStock, 0, BigDecimal.ZERO);
            }
        }
    }
    
    /**
     * Obtiene el stock inicial de un producto (el SKU ES el stock)
     */
    private Integer getInitialStockForProduct(Product product) {
        // El SKU es el stock disponible
        return product.getSku() != null ? product.getSku() : 50;
    }

    /**
     * Simula la actualización de métricas (para testing/demo)
     */
    @Transactional
    public void simulateMetricsUpdate(Integer promotionId, Integer productId, Integer unitsSoldIncrement) {
        Optional<PromotionMetrics> metricsOpt = 
            promotionMetricsRepository.findByPromotionPromotionIdAndProductProductId(promotionId, productId);
        
        if (metricsOpt.isPresent()) {
            PromotionMetrics metrics = metricsOpt.get();
            int newUnitsSold = metrics.getUnitsSold() + unitsSoldIncrement;
            int newCurrentInventory = Math.max(0, metrics.getCurrentInventory() - unitsSoldIncrement);
            
            // Calcular nuevos ingresos (precio base * unidades adicionales * descuento)
            Optional<Product> productOpt = productRepository.findById(productId);
            BigDecimal additionalRevenue = BigDecimal.ZERO;
            if (productOpt.isPresent()) {
                Product product = productOpt.get();
                Optional<Promotion> promotionOpt = promotionRepository.findById(promotionId);
                if (promotionOpt.isPresent()) {
                    double discountMultiplier = (100.0 - promotionOpt.get().getDiscountValue()) / 100.0;
                    double discountedPrice = product.getBasePrice() * discountMultiplier;
                    additionalRevenue = BigDecimal.valueOf(discountedPrice * unitsSoldIncrement);
                }
            }
            
            BigDecimal newRevenue = metrics.getRevenueGenerated().add(additionalRevenue);
            
            saveOrUpdateMetrics(promotionId, productId, metrics.getInitialInventory(), 
                              newCurrentInventory, newUnitsSold, newRevenue);
        }
    }

    /**
     * Verifica si existen métricas para una promoción
     */
    public boolean hasMetrics(Integer promotionId) {
        return promotionMetricsRepository.existsByPromotionPromotionId(promotionId);
    }

    /**
     * Elimina todas las métricas de una promoción
     */
    @Transactional
    public void deleteMetricsForPromotion(Integer promotionId) {
        promotionMetricsRepository.deleteByPromotionPromotionId(promotionId);
    }

    // Métodos privados de utilidad

    private PromotionPerformanceDTO createEmptyPerformanceDTO(Promotion promotion) {
        LocalDateTime now = LocalDateTime.now();
        boolean isActive = now.isAfter(promotion.getStartDate().atStartOfDay()) && 
                          now.isBefore(promotion.getEndDate().atTime(23, 59, 59));

        return new PromotionPerformanceDTO(
            promotion.getPromotionId(),
            promotion.getPromotionName(),
            promotion.getDescription(),
            promotion.getDiscountValue(),
            promotion.getStartDate().atStartOfDay(),
            promotion.getEndDate().atTime(23, 59, 59),
            isActive,
            0,  // totalProducts
            0,  // totalUnitsSold
            BigDecimal.ZERO,  // totalRevenue
            0,  // totalInitialInventory
            0,  // totalCurrentInventory
            null,  // lastUpdated
            List.of()  // productMetrics
        );
    }

    private ProductMetricsDTO convertToProductMetricsDTO(PromotionMetrics metrics) {
        Product product = metrics.getProduct();
        
        // Calcular precio con descuento
        double discountMultiplier = (100.0 - metrics.getPromotion().getDiscountValue()) / 100.0;
        BigDecimal discountedPrice = BigDecimal.valueOf(product.getBasePrice() * discountMultiplier);
        
        // Convertir categoría
        CategoryDTO categoryDTO = null;
        if (product.getCategory() != null) {
            categoryDTO = new CategoryDTO();
            categoryDTO.setCategoryId(product.getCategory().getCategoryId());
            categoryDTO.setCategoryName(product.getCategory().getCategoryName());
            categoryDTO.setDescription(product.getCategory().getDescription());
        }

        return new ProductMetricsDTO(
            product.getProductId(),
            product.getProductName(),
            String.valueOf(product.getSku()),
            BigDecimal.valueOf(product.getBasePrice()),
            discountedPrice,
            metrics.getInitialInventory(),
            metrics.getCurrentInventory(),
            metrics.getUnitsSold(),
            metrics.getRevenueGenerated(),
            categoryDTO,
            metrics.getUpdatedAt()
        );
    }
}
