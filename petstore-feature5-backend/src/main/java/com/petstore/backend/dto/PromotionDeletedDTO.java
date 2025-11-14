package com.petstore.backend.dto;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.petstore.backend.entity.Category;
import com.petstore.backend.entity.Status;
import com.petstore.backend.entity.User;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO para promociones eliminadas (papelera temporal)")
public class PromotionDeletedDTO {
    
    @Schema(description = "ID único de la promoción", example = "1")
    private Integer promotionId;
    
    @Schema(description = "Nombre de la promoción", example = "Descuento Halloween")
    private String promotionName;
    
    @Schema(description = "Descripción detallada de la promoción", example = "20% de descuento en productos para mascotas")
    private String description;
    
    @Schema(description = "Fecha de inicio de la promoción", example = "2024-10-01")
    private LocalDate startDate;
    
    @Schema(description = "Fecha de fin de la promoción", example = "2024-10-31")
    private LocalDate endDate;
    
    @Schema(description = "Valor del descuento", example = "20.0")
    private Double discountValue;
    
    @Schema(description = "Nombre del estado de la promoción", example = "ACTIVE")
    private String statusName; // Para compatibilidad con REST
    
    @JsonIgnore
    private Status status; // Para GraphQL
    
    @Schema(description = "Nombre del usuario que creó la promoción", example = "admin_user")
    private String userName; // Para compatibilidad con REST
    
    @JsonIgnore
    private User user; // Para GraphQL
    
    @Schema(description = "Nombre de la categoría", example = "Accesorios")
    private String categoryName; // Para compatibilidad con REST
    
    @JsonIgnore
    private Category category; // Para GraphQL
    
    @Schema(description = "Fecha y hora de eliminación")
    private ZonedDateTime deletedAt;
    
    @Schema(description = "Nombre del usuario que eliminó la promoción", example = "admin_user")
    private String deletedByUserName; // Para compatibilidad con REST
    
    @JsonIgnore
    private User deletedBy; // Para GraphQL
    private Integer daysUntilPurge;
    
    // Constructores
    public PromotionDeletedDTO() {}
    
    public PromotionDeletedDTO(Integer promotionId, String promotionName, String description,
                              LocalDate startDate, LocalDate endDate, Double discountValue,
                              String statusName, String userName, String categoryName,
                              ZonedDateTime deletedAt, String deletedByUserName) {
        this.promotionId = promotionId;
        this.promotionName = promotionName;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.discountValue = discountValue;
        this.statusName = statusName;
        this.userName = userName;
        this.categoryName = categoryName;
        this.deletedAt = deletedAt;
        this.deletedByUserName = deletedByUserName;
        
        // Calcular días hasta purga (30 días desde eliminación)
        if (deletedAt != null) {
            ZonedDateTime purgeDate = deletedAt.plusDays(30);
            long daysLeft = java.time.Duration.between(ZonedDateTime.now(), purgeDate).toDays();
            this.daysUntilPurge = Math.max(0, (int) daysLeft);
        }
    }
    
    // Constructor con objetos completos para GraphQL
    public PromotionDeletedDTO(Integer promotionId, String promotionName, String description,
                              LocalDate startDate, LocalDate endDate, Double discountValue,
                              Status status, User user, Category category,
                              ZonedDateTime deletedAt, User deletedBy) {
        this.promotionId = promotionId;
        this.promotionName = promotionName;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.discountValue = discountValue;
        this.status = status;
        this.statusName = status != null ? status.getStatusName() : null;
        this.user = user;
        this.userName = user != null ? user.getUserName() : null;
        this.category = category;
        this.categoryName = category != null ? category.getCategoryName() : null;
        this.deletedAt = deletedAt;
        this.deletedBy = deletedBy;
        this.deletedByUserName = deletedBy != null ? deletedBy.getUserName() : null;
        
        // Calcular días hasta purga (30 días desde eliminación)
        if (deletedAt != null) {
            ZonedDateTime purgeDate = deletedAt.plusDays(30);
            long daysLeft = java.time.Duration.between(ZonedDateTime.now(), purgeDate).toDays();
            this.daysUntilPurge = Math.max(0, (int) daysLeft);
        }
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
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    public LocalDate getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    
    public Double getDiscountValue() {
        return discountValue;
    }
    
    public void setDiscountValue(Double discountValue) {
        this.discountValue = discountValue;
    }
    
    public String getStatusName() {
        return statusName;
    }
    
    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public String getCategoryName() {
        return categoryName;
    }
    
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    
    public ZonedDateTime getDeletedAt() {
        return deletedAt;
    }
    
    public void setDeletedAt(ZonedDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
    
    public String getDeletedByUserName() {
        return deletedByUserName;
    }
    
    public void setDeletedByUserName(String deletedByUserName) {
        this.deletedByUserName = deletedByUserName;
    }
    
    public Integer getDaysUntilPurge() {
        return daysUntilPurge;
    }
    
    public void setDaysUntilPurge(Integer daysUntilPurge) {
        this.daysUntilPurge = daysUntilPurge;
    }
    
    // Getters y Setters para GraphQL
    public Status getStatus() {
        return status;
    }
    
    public void setStatus(Status status) {
        this.status = status;
        this.statusName = status != null ? status.getStatusName() : null;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
        this.userName = user != null ? user.getUserName() : null;
    }
    
    public Category getCategory() {
        return category;
    }
    
    public void setCategory(Category category) {
        this.category = category;
        this.categoryName = category != null ? category.getCategoryName() : null;
    }
    
    public User getDeletedBy() {
        return deletedBy;
    }
    
    public void setDeletedBy(User deletedBy) {
        this.deletedBy = deletedBy;
        this.deletedByUserName = deletedBy != null ? deletedBy.getUserName() : null;
    }
}
