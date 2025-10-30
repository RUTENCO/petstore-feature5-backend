package com.petstore.backend.repository;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.petstore.backend.entity.PromotionDeleted;

@Repository
public interface PromotionDeletedRepository extends JpaRepository<PromotionDeleted, Integer> {
    
    /**
     * Busca promociones eliminadas por usuario que las eliminó
     */
    @Query("SELECT pd FROM PromotionDeleted pd WHERE pd.deletedBy.userId = :userId ORDER BY pd.deletedAt DESC")
    List<PromotionDeleted> findByDeletedByUserId(@Param("userId") Integer userId);
    
    /**
     * Busca promociones eliminadas en los últimos 30 días
     */
    @Query("SELECT pd FROM PromotionDeleted pd WHERE pd.deletedAt >= :cutoffDate ORDER BY pd.deletedAt DESC")
    List<PromotionDeleted> findRecentlyDeleted(@Param("cutoffDate") ZonedDateTime cutoffDate);
    
    /**
     * Busca promociones eliminadas que se pueden restaurar (menos de 30 días)
     */
    @Query("SELECT pd FROM PromotionDeleted pd WHERE pd.deletedAt >= :thirtyDaysAgo ORDER BY pd.deletedAt DESC")
    List<PromotionDeleted> findRestorable(@Param("thirtyDaysAgo") ZonedDateTime thirtyDaysAgo);
    
    /**
     * Busca promociones eliminadas que deben ser purgadas (más de 30 días)
     */
    @Query("SELECT pd FROM PromotionDeleted pd WHERE pd.deletedAt < :thirtyDaysAgo ORDER BY pd.deletedAt ASC")
    List<PromotionDeleted> findPurgeable(@Param("thirtyDaysAgo") ZonedDateTime thirtyDaysAgo);
    
    /**
     * Cuenta promociones eliminadas por un usuario específico
     */
    @Query("SELECT COUNT(pd) FROM PromotionDeleted pd WHERE pd.deletedBy.userId = :userId")
    Long countByDeletedByUserId(@Param("userId") Integer userId);
    
    /**
     * Verifica si existe una promoción eliminada con el ID específico
     */
    boolean existsByPromotionId(Integer promotionId);
}
