package com.petstore.backend.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "promotion_metrics", schema = "public")
public class PromotionMetrics {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "metrics_id")
    private Integer metricsId;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "promotion_id", nullable = false)
    private Promotion promotion;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @Column(name = "initial_inventory", nullable = false)
    private Integer initialInventory;
    
    @Column(name = "current_inventory", nullable = false)
    private Integer currentInventory;
    
    @Column(name = "units_sold", nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Integer unitsSold = 0;
    
    @Column(name = "sku_variation_percentage")
    private Double skuVariationPercentage;
    
    @Column(name = "revenue_generated", precision = 10, scale = 2)
    private BigDecimal revenueGenerated;
    
    @Column(name = "snapshot_date", nullable = false)
    private LocalDateTime snapshotDate;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructores
    public PromotionMetrics() {
        this.createdAt = LocalDateTime.now();
        this.snapshotDate = LocalDateTime.now();
    }
    
    public PromotionMetrics(Promotion promotion, Product product, Integer initialInventory, 
                          Integer currentInventory, Integer unitsSold, BigDecimal revenueGenerated) {
        this();
        this.promotion = promotion;
        this.product = product;
        this.initialInventory = initialInventory;
        this.currentInventory = currentInventory;
        this.unitsSold = unitsSold;
        this.revenueGenerated = revenueGenerated;
        // Calcular automáticamente la variación de SKU
        if (initialInventory != null && unitsSold != null && initialInventory > 0) {
            this.skuVariationPercentage = ((double) unitsSold / initialInventory) * 100;
        } else {
            this.skuVariationPercentage = 0.0;
        }
    }
    
    // Getters y Setters
    public Integer getMetricsId() {
        return metricsId;
    }
    
    public void setMetricsId(Integer metricsId) {
        this.metricsId = metricsId;
    }
    
    public Promotion getPromotion() {
        return promotion;
    }
    
    public void setPromotion(Promotion promotion) {
        this.promotion = promotion;
    }
    
    public Product getProduct() {
        return product;
    }
    
    public void setProduct(Product product) {
        this.product = product;
    }
    
    public Integer getInitialInventory() {
        return initialInventory;
    }
    
    public void setInitialInventory(Integer initialInventory) {
        this.initialInventory = initialInventory;
    }
    
    public Integer getCurrentInventory() {
        return currentInventory;
    }
    
    public void setCurrentInventory(Integer currentInventory) {
        this.currentInventory = currentInventory;
    }
    
    public Integer getUnitsSold() {
        return unitsSold;
    }
    
    public void setUnitsSold(Integer unitsSold) {
        this.unitsSold = unitsSold;
        // Recalcular automáticamente la variación cuando cambien las unidades vendidas
        if (initialInventory != null && unitsSold != null && initialInventory > 0) {
            this.skuVariationPercentage = ((double) unitsSold / initialInventory) * 100;
        } else {
            this.skuVariationPercentage = 0.0;
        }
    }
    
    public Double getSkuVariationPercentage() {
        return skuVariationPercentage;
    }
    
    public void setSkuVariationPercentage(Double skuVariationPercentage) {
        this.skuVariationPercentage = skuVariationPercentage;
    }
    
    public BigDecimal getRevenueGenerated() {
        return revenueGenerated;
    }
    
    public void setRevenueGenerated(BigDecimal revenueGenerated) {
        this.revenueGenerated = revenueGenerated;
    }
    
    public LocalDateTime getSnapshotDate() {
        return snapshotDate;
    }
    
    public void setSnapshotDate(LocalDateTime snapshotDate) {
        this.snapshotDate = snapshotDate;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    // Métodos de utilidad
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Calcula la diferencia de inventario (inicial - actual)
     */
    public Integer getInventoryDifference() {
        return this.initialInventory - this.currentInventory;
    }
    
    /**
     * Calcula el porcentaje de reducción de inventario
     */
    public Double getInventoryReductionPercentage() {
        if (initialInventory == 0) return 0.0;
        return ((double) getInventoryDifference() / initialInventory) * 100;
    }
    
    /**
     * Calcula y actualiza la variación de SKU en porcentaje basado en unidades vendidas
     */
    public void calculateSkuVariationPercentage() {
        if (initialInventory != null && initialInventory > 0 && unitsSold != null) {
            this.skuVariationPercentage = ((double) unitsSold / initialInventory) * 100;
        } else {
            this.skuVariationPercentage = 0.0;
        }
    }
}
