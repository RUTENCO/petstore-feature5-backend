package com.petstore.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO agregado con métricas de rendimiento de una promoción")
public class PromotionPerformanceDTO {
    
    @Schema(description = "ID de la promoción", example = "1")
    private Integer promotionId;
    
    @Schema(description = "Nombre de la promoción", example = "Black Friday 2024")
    private String promotionName;
    
    @Schema(description = "Descripción de la promoción")
    private String promotionDescription;
    
    @Schema(description = "Porcentaje de descuento aplicado", example = "25.5")
    private Double discountPercentage;
    
    @Schema(description = "Fecha de inicio de la promoción")
    private LocalDateTime startDate;
    
    @Schema(description = "Fecha de fin de la promoción")
    private LocalDateTime endDate;
    
    @Schema(description = "Si la promoción está actualmente activa", example = "true")
    private Boolean isActive;
    
    @Schema(description = "Total de productos asociados a la promoción", example = "15")
    private Integer totalProducts;
    
    @Schema(description = "Total de unidades vendidas durante la promoción", example = "234")
    private Integer totalUnitsSold;
    
    @Schema(description = "Total de ingresos generados por la promoción", example = "5678.90")
    private BigDecimal totalRevenue;
    
    @Schema(description = "Inventario inicial total de todos los productos", example = "500")
    private Integer totalInitialInventory;
    
    @Schema(description = "Inventario actual total de todos los productos", example = "266")
    private Integer totalCurrentInventory;
    
    @Schema(description = "Diferencia total de inventario (inicial - actual)", example = "234")
    private Integer totalInventoryDifference;
    
    @Schema(description = "Porcentaje de reducción de inventario", example = "46.8")
    private Double inventoryReductionPercentage;
    
    @Schema(description = "Fecha de la última actualización de métricas")
    private LocalDateTime lastUpdated;
    
    @Schema(description = "Métricas detalladas por producto")
    private List<ProductMetricsDTO> productMetrics;
    
    // Constructor por defecto
    public PromotionPerformanceDTO() {}
    
    // Constructor completo
    public PromotionPerformanceDTO(Integer promotionId, String promotionName, String promotionDescription,
                                 Double discountPercentage, LocalDateTime startDate, LocalDateTime endDate,
                                 Boolean isActive, Integer totalProducts, Integer totalUnitsSold,
                                 BigDecimal totalRevenue, Integer totalInitialInventory, 
                                 Integer totalCurrentInventory, LocalDateTime lastUpdated,
                                 List<ProductMetricsDTO> productMetrics) {
        this.promotionId = promotionId;
        this.promotionName = promotionName;
        this.promotionDescription = promotionDescription;
        this.discountPercentage = discountPercentage;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isActive = isActive;
        this.totalProducts = totalProducts;
        this.totalUnitsSold = totalUnitsSold;
        this.totalRevenue = totalRevenue;
        this.totalInitialInventory = totalInitialInventory;
        this.totalCurrentInventory = totalCurrentInventory;
        this.lastUpdated = lastUpdated;
        this.productMetrics = productMetrics;
        
        // Calcular campos derivados
        calculateDerivedFields();
    }
    
    // Getters y Setters
    public Integer getPromotionId() {
        return promotionId;
    }
    
    public void setPromotionId(Integer promotionId) {
        this.promotionId = promotionId;
    }
    
    public String getPromotionName() {
        return promotionName;
    }
    
    public void setPromotionName(String promotionName) {
        this.promotionName = promotionName;
    }
    
    public String getPromotionDescription() {
        return promotionDescription;
    }
    
    public void setPromotionDescription(String promotionDescription) {
        this.promotionDescription = promotionDescription;
    }
    
    public Double getDiscountPercentage() {
        return discountPercentage;
    }
    
    public void setDiscountPercentage(Double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }
    
    public LocalDateTime getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }
    
    public LocalDateTime getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public Integer getTotalProducts() {
        return totalProducts;
    }
    
    public void setTotalProducts(Integer totalProducts) {
        this.totalProducts = totalProducts;
    }
    
    public Integer getTotalUnitsSold() {
        return totalUnitsSold;
    }
    
    public void setTotalUnitsSold(Integer totalUnitsSold) {
        this.totalUnitsSold = totalUnitsSold;
    }
    
    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }
    
    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
    
    public Integer getTotalInitialInventory() {
        return totalInitialInventory;
    }
    
    public void setTotalInitialInventory(Integer totalInitialInventory) {
        this.totalInitialInventory = totalInitialInventory;
    }
    
    public Integer getTotalCurrentInventory() {
        return totalCurrentInventory;
    }
    
    public void setTotalCurrentInventory(Integer totalCurrentInventory) {
        this.totalCurrentInventory = totalCurrentInventory;
    }
    
    public Integer getTotalInventoryDifference() {
        return totalInventoryDifference;
    }
    
    public void setTotalInventoryDifference(Integer totalInventoryDifference) {
        this.totalInventoryDifference = totalInventoryDifference;
    }
    
    public Double getInventoryReductionPercentage() {
        return inventoryReductionPercentage;
    }
    
    public void setInventoryReductionPercentage(Double inventoryReductionPercentage) {
        this.inventoryReductionPercentage = inventoryReductionPercentage;
    }
    
    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }
    
    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    
    public List<ProductMetricsDTO> getProductMetrics() {
        return productMetrics;
    }
    
    public void setProductMetrics(List<ProductMetricsDTO> productMetrics) {
        this.productMetrics = productMetrics;
    }
    
    // Métodos de utilidad
    private void calculateDerivedFields() {
        if (totalInitialInventory != null && totalCurrentInventory != null) {
            this.totalInventoryDifference = totalInitialInventory - totalCurrentInventory;
            
            if (totalInitialInventory > 0) {
                this.inventoryReductionPercentage = 
                    ((double) totalInventoryDifference / totalInitialInventory) * 100;
            } else {
                this.inventoryReductionPercentage = 0.0;
            }
        }
    }
    
    /**
     * Recalcula los campos derivados después de cambios
     */
    public void recalculateDerivedFields() {
        calculateDerivedFields();
    }
}
