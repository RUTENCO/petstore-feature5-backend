package com.petstore.backend.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.petstore.backend.entity.Promotion;

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
    
    // Establecer actor para triggers de base de datos
    @Query(value = "SELECT public.fn_set_actor(:userId)", nativeQuery = true)
    void setActor(@Param("userId") Integer userId);
    
    // Restaurar promoción usando función de base de datos
    @Query(value = "SELECT public.fn_restore_promotion(:promotionId)", nativeQuery = true)
    void restorePromotionUsingFunction(@Param("promotionId") Integer promotionId);
}
