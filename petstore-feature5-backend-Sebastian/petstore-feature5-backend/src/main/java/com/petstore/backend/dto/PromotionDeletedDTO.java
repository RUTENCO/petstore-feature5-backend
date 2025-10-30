package com.petstore.backend.dto;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.petstore.backend.entity.Category;
import com.petstore.backend.entity.Status;
import com.petstore.backend.entity.User;

public class PromotionDeletedDTO {
    
    private Integer promotionId;
    private String promotionName;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double discountValue;
    private String statusName; // Para compatibilidad con REST
    @JsonIgnore
    private Status status; // Para GraphQL
    private String userName; // Para compatibilidad con REST
    @JsonIgnore
    private User user; // Para GraphQL
    private String categoryName; // Para compatibilidad con REST
    @JsonIgnore
    private Category category; // Para GraphQL
    private ZonedDateTime deletedAt;
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
