package com.petstore.backend.repository;

import com.petstore.backend.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Integer> {
    
    // Buscar promociones activas
    @Query("SELECT p FROM Promotion p WHERE p.status.statusName = 'ACTIVE'")
    List<Promotion> findActivePromotions();
    
    // Buscar promociones expiradas
    @Query("SELECT p FROM Promotion p WHERE p.status.statusName = 'EXPIRED'")
    List<Promotion> findExpiredPromotions();
    
    // Buscar promociones programadas
    @Query("SELECT p FROM Promotion p WHERE p.status.statusName = 'SCHEDULE'")
    List<Promotion> findScheduledPromotions();
    
    // Buscar promociones por estado específico
    @Query("SELECT p FROM Promotion p WHERE p.status.statusName = :statusName")
    List<Promotion> findByStatusName(@Param("statusName") String statusName);
    
    // Buscar promociones por categoría
    List<Promotion> findByCategoryCategoryId(Integer categoryId);
    
    // Buscar promociones por usuario (Marketing Admin que las creó)
    List<Promotion> findByUserUserId(Integer userId);
    
    // Buscar promociones vigentes (fecha actual entre start y end)
    @Query("SELECT p FROM Promotion p WHERE :currentDate BETWEEN p.startDate AND p.endDate")
    List<Promotion> findValidPromotions(@Param("currentDate") LocalDate currentDate);
    
    // Buscar promociones por rango de descuento
    @Query("SELECT p FROM Promotion p WHERE p.discountValue >= :minDiscount AND p.discountValue <= :maxDiscount")
    List<Promotion> findByDiscountRange(@Param("minDiscount") Double minDiscount, @Param("maxDiscount") Double maxDiscount);

    @Query("""
    SELECT COUNT(p) FROM Promotion p
    WHERE p.category.id = :categoryId
      AND p.status.name = 'ACTIVE'
      AND (p.startDate <= :endDate AND :startDate <= p.endDate)
""")
long countActiveOverlaps(Integer categoryId, java.time.LocalDate startDate, java.time.LocalDate endDate);

}
