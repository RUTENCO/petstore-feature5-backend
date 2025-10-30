package com.petstore.backend.entity;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "promotions_deleted", schema = "public")
public class PromotionDeleted {
    
    @Id
    @Column(name = "promotion_id")
    private Integer promotionId;
    
    @Column(name = "promotion_name", nullable = false)
    private String promotionName;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;
    
    @Column(name = "discount_value", nullable = false)
    private Double discountValue;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "status_id", nullable = false)
    private Status status;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    
    @Column(name = "deleted_at", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE DEFAULT NOW()")
    private ZonedDateTime deletedAt;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deleted_by")
    private User deletedBy;
    
    // Constructores
    public PromotionDeleted() {}
    
    public PromotionDeleted(Promotion promotion) {
        this.promotionId = promotion.getPromotionId();
        this.promotionName = promotion.getPromotionName();
        this.description = promotion.getDescription();
        this.startDate = promotion.getStartDate();
        this.endDate = promotion.getEndDate();
        this.discountValue = promotion.getDiscountValue();
        this.status = promotion.getStatus();
        this.user = promotion.getUser();
        this.category = promotion.getCategory();
        this.deletedAt = ZonedDateTime.now();
        this.deletedBy = null; // Se establecerá después
    }
    
    public PromotionDeleted(Promotion promotion, User deletedBy) {
        this.promotionId = promotion.getPromotionId();
        this.promotionName = promotion.getPromotionName();
        this.description = promotion.getDescription();
        this.startDate = promotion.getStartDate();
        this.endDate = promotion.getEndDate();
        this.discountValue = promotion.getDiscountValue();
        this.status = promotion.getStatus();
        this.user = promotion.getUser();
        this.category = promotion.getCategory();
        this.deletedAt = ZonedDateTime.now();
        this.deletedBy = deletedBy;
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
    
    public Status getStatus() {
        return status;
    }
    
    public void setStatus(Status status) {
        this.status = status;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public Category getCategory() {
        return category;
    }
    
    public void setCategory(Category category) {
        this.category = category;
    }
    
    public ZonedDateTime getDeletedAt() {
        return deletedAt;
    }
    
    public void setDeletedAt(ZonedDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
    
    public User getDeletedBy() {
        return deletedBy;
    }
    
    public void setDeletedBy(User deletedBy) {
        this.deletedBy = deletedBy;
    }
    
    @Override
    public String toString() {
        return "PromotionDeleted{" +
                "promotionId=" + promotionId +
                ", promotionName='" + promotionName + '\'' +
                ", deletedAt=" + deletedAt +
                '}';
    }
}
