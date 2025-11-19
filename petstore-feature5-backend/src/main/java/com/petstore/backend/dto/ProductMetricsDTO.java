package com.petstore.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO con métricas específicas de un producto dentro de una promoción")
public class ProductMetricsDTO {
    
    @Schema(description = "ID del producto", example = "1")
    private Integer productId;
    
    @Schema(description = "Nombre del producto", example = "Collar Premium para Perros")
    private String productName;
    
    @Schema(description = "SKU del producto", example = "SKU12345")
    private String productSku;
    
    @Schema(description = "Precio base del producto", example = "25.99")
    private BigDecimal basePrice;
    
    @Schema(description = "Precio con descuento aplicado", example = "19.49")
    private BigDecimal discountedPrice;
    
    @Schema(description = "Inventario inicial al inicio de la promoción", example = "50")
    private Integer initialInventory;
    
    @Schema(description = "Inventario actual", example = "23")
    private Integer currentInventory;
    
    @Schema(description = "Diferencia de inventario (inicial - actual)", example = "27")
    private Integer inventoryDifference;
    
    @Schema(description = "Porcentaje de reducción de inventario", example = "54.0")
    private Double inventoryReductionPercentage;
    
    @Schema(description = "Unidades vendidas durante la promoción", example = "27")
    private Integer unitsSold;
    
    @Schema(description = "Ingresos generados por este producto", example = "526.23")
    private BigDecimal revenueGenerated;
    
    @Schema(description = "Categoría del producto")
    private CategoryDTO category;
    
    @Schema(description = "Fecha de la última actualización")
    private LocalDateTime lastUpdated;
    
    // Constructor por defecto
    public ProductMetricsDTO() {}
    
    // Constructor completo
    public ProductMetricsDTO(Integer productId, String productName, String productSku,
                           BigDecimal basePrice, BigDecimal discountedPrice, Integer initialInventory,
                           Integer currentInventory, Integer unitsSold, BigDecimal revenueGenerated,
                           CategoryDTO category, LocalDateTime lastUpdated) {
        this.productId = productId;
        this.productName = productName;
        this.productSku = productSku;
        this.basePrice = basePrice;
        this.discountedPrice = discountedPrice;
        this.initialInventory = initialInventory;
        this.currentInventory = currentInventory;
        this.unitsSold = unitsSold;
        this.revenueGenerated = revenueGenerated;
        this.category = category;
        this.lastUpdated = lastUpdated;
        
        // Calcular campos derivados
        calculateDerivedFields();
    }
    
    // Getters y Setters
    public Integer getProductId() {
        return productId;
    }
    
    public void setProductId(Integer productId) {
        this.productId = productId;
    }
    
    public String getProductName() {
        return productName;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
    }
    
    public String getProductSku() {
        return productSku;
    }
    
    public void setProductSku(String productSku) {
        this.productSku = productSku;
    }
    
    public BigDecimal getBasePrice() {
        return basePrice;
    }
    
    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }
    
    public BigDecimal getDiscountedPrice() {
        return discountedPrice;
    }
    
    public void setDiscountedPrice(BigDecimal discountedPrice) {
        this.discountedPrice = discountedPrice;
    }
    
    public Integer getInitialInventory() {
        return initialInventory;
    }
    
    public void setInitialInventory(Integer initialInventory) {
        this.initialInventory = initialInventory;
        calculateDerivedFields();
    }
    
    public Integer getCurrentInventory() {
        return currentInventory;
    }
    
    public void setCurrentInventory(Integer currentInventory) {
        this.currentInventory = currentInventory;
        calculateDerivedFields();
    }
    
    public Integer getInventoryDifference() {
        return inventoryDifference;
    }
    
    public void setInventoryDifference(Integer inventoryDifference) {
        this.inventoryDifference = inventoryDifference;
    }
    
    public Double getInventoryReductionPercentage() {
        return inventoryReductionPercentage;
    }
    
    public void setInventoryReductionPercentage(Double inventoryReductionPercentage) {
        this.inventoryReductionPercentage = inventoryReductionPercentage;
    }
    
    public Integer getUnitsSold() {
        return unitsSold;
    }
    
    public void setUnitsSold(Integer unitsSold) {
        this.unitsSold = unitsSold;
    }
    
    public BigDecimal getRevenueGenerated() {
        return revenueGenerated;
    }
    
    public void setRevenueGenerated(BigDecimal revenueGenerated) {
        this.revenueGenerated = revenueGenerated;
    }
    
    public CategoryDTO getCategory() {
        return category;
    }
    
    public void setCategory(CategoryDTO category) {
        this.category = category;
    }
    
    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }
    
    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    
    // Métodos de utilidad
    private void calculateDerivedFields() {
        if (initialInventory != null && currentInventory != null) {
            this.inventoryDifference = initialInventory - currentInventory;
            
            if (initialInventory > 0) {
                this.inventoryReductionPercentage = 
                    ((double) inventoryDifference / initialInventory) * 100;
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
