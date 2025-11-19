package com.petstore.backend.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.petstore.backend.entity.PromotionMetrics;

@Repository
public interface PromotionMetricsRepository extends JpaRepository<PromotionMetrics, Integer> {
    
    /**
     * Encuentra todas las métricas de una promoción específica
     */
    List<PromotionMetrics> findByPromotionPromotionId(Integer promotionId);
    
    /**
     * Encuentra métricas de un producto específico en una promoción
     */
    Optional<PromotionMetrics> findByPromotionPromotionIdAndProductProductId(Integer promotionId, Integer productId);
    
    /**
     * Encuentra las métricas más recientes por promoción
     */
    @Query("SELECT pm FROM PromotionMetrics pm WHERE pm.promotion.promotionId = :promotionId " +
           "AND pm.snapshotDate = (SELECT MAX(pm2.snapshotDate) FROM PromotionMetrics pm2 " +
           "WHERE pm2.promotion.promotionId = :promotionId AND pm2.product.productId = pm.product.productId)")
    List<PromotionMetrics> findLatestMetricsByPromotionId(@Param("promotionId") Integer promotionId);
    
    /**
     * Encuentra métricas por rango de fechas
     */
    @Query("SELECT pm FROM PromotionMetrics pm WHERE pm.promotion.promotionId = :promotionId " +
           "AND pm.snapshotDate BETWEEN :startDate AND :endDate " +
           "ORDER BY pm.snapshotDate DESC")
    List<PromotionMetrics> findByPromotionIdAndDateRange(@Param("promotionId") Integer promotionId,
                                                        @Param("startDate") LocalDateTime startDate,
                                                        @Param("endDate") LocalDateTime endDate);
    
    /**
     * Calcula el total de unidades vendidas por promoción
     */
    @Query("SELECT COALESCE(SUM(pm.unitsSold), 0) FROM PromotionMetrics pm " +
           "WHERE pm.promotion.promotionId = :promotionId " +
           "AND pm.snapshotDate = (SELECT MAX(pm2.snapshotDate) FROM PromotionMetrics pm2 " +
           "WHERE pm2.promotion.promotionId = :promotionId AND pm2.product.productId = pm.product.productId)")
    Integer getTotalUnitsSoldByPromotionId(@Param("promotionId") Integer promotionId);
    
    /**
     * Calcula el total de ingresos generados por promoción
     */
    @Query("SELECT COALESCE(SUM(pm.revenueGenerated), 0) FROM PromotionMetrics pm " +
           "WHERE pm.promotion.promotionId = :promotionId " +
           "AND pm.snapshotDate = (SELECT MAX(pm2.snapshotDate) FROM PromotionMetrics pm2 " +
           "WHERE pm2.promotion.promotionId = :promotionId AND pm2.product.productId = pm.product.productId)")
    Double getTotalRevenueByPromotionId(@Param("promotionId") Integer promotionId);
    
    /**
     * Calcula el inventario inicial total por promoción
     */
    @Query("SELECT COALESCE(SUM(pm.initialInventory), 0) FROM PromotionMetrics pm " +
           "WHERE pm.promotion.promotionId = :promotionId " +
           "AND pm.snapshotDate = (SELECT MIN(pm2.snapshotDate) FROM PromotionMetrics pm2 " +
           "WHERE pm2.promotion.promotionId = :promotionId AND pm2.product.productId = pm.product.productId)")
    Integer getTotalInitialInventoryByPromotionId(@Param("promotionId") Integer promotionId);
    
    /**
     * Calcula el inventario actual total por promoción
     */
    @Query("SELECT COALESCE(SUM(pm.currentInventory), 0) FROM PromotionMetrics pm " +
           "WHERE pm.promotion.promotionId = :promotionId " +
           "AND pm.snapshotDate = (SELECT MAX(pm2.snapshotDate) FROM PromotionMetrics pm2 " +
           "WHERE pm2.promotion.promotionId = :promotionId AND pm2.product.productId = pm.product.productId)")
    Integer getTotalCurrentInventoryByPromotionId(@Param("promotionId") Integer promotionId);
    
    /**
     * Cuenta el número de productos únicos en una promoción
     */
    @Query("SELECT COUNT(DISTINCT pm.product.productId) FROM PromotionMetrics pm " +
           "WHERE pm.promotion.promotionId = :promotionId")
    Integer getUniqueProductCountByPromotionId(@Param("promotionId") Integer promotionId);
    
    /**
     * Encuentra la última fecha de actualización de métricas para una promoción
     */
    @Query("SELECT MAX(pm.updatedAt) FROM PromotionMetrics pm " +
           "WHERE pm.promotion.promotionId = :promotionId")
    LocalDateTime getLastUpdatedByPromotionId(@Param("promotionId") Integer promotionId);
    
    /**
     * Verifica si existen métricas para una promoción
     */
    boolean existsByPromotionPromotionId(Integer promotionId);
    
    /**
     * Elimina todas las métricas de una promoción (para limpieza)
     */
    void deleteByPromotionPromotionId(Integer promotionId);
    
    /**
     * Encuentra métricas por producto (para todas las promociones)
     */
    List<PromotionMetrics> findByProductProductId(Integer productId);
}
