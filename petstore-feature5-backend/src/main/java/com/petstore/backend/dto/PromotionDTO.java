package com.petstore.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO para representar una promoción en el sistema")
public class PromotionDTO {
    @Schema(description = "ID único de la promoción", example = "1")
    private Integer promotionId;
    
    @Schema(description = "Nombre de la promoción", example = "Descuento de Verano")
    private String promotionName;
    
    @Schema(description = "Descripción detallada de la promoción", example = "Descuento especial para productos de verano")
    private String description;
    
    @Schema(description = "Porcentaje de descuento", example = "25.50")
    private BigDecimal discountPercentage;
    
    @Schema(description = "Fecha de inicio de la promoción", example = "2024-06-01")
    private LocalDate startDate;
    
    @Schema(description = "Fecha de finalización de la promoción", example = "2024-08-31")
    private LocalDate endDate;
    
    @Schema(description = "Estado de la promoción", example = "Active")
    private String status;
    
    @Schema(description = "Categoría asociada a la promoción")
    private CategoryDTO category;
    
    @Schema(description = "Producto asociado a la promoción")
    private ProductDTO product;
    
    @Schema(description = "Fecha y hora de creación")
    private LocalDateTime createdAt;
    
    @Schema(description = "Fecha y hora de última actualización")
    private LocalDateTime updatedAt;
    
    // Campos adicionales para GraphQL input
    private Integer categoryId;
    private Integer statusId;
    private Integer userId;

    // Constructors
    public PromotionDTO() {}

    public PromotionDTO(Integer promotionId, String promotionName, String description, 
                       BigDecimal discountPercentage, LocalDate startDate, 
                       LocalDate endDate, String status, CategoryDTO category, 
                       ProductDTO product, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.promotionId = promotionId;
        this.promotionName = promotionName;
        this.description = description;
        this.discountPercentage = discountPercentage;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.category = category;
        this.product = product;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
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

    public BigDecimal getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(BigDecimal discountPercentage) {
        this.discountPercentage = discountPercentage;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public CategoryDTO getCategory() {
        return category;
    }

    public void setCategory(CategoryDTO category) {
        this.category = category;
    }

    public ProductDTO getProduct() {
        return product;
    }

    public void setProduct(ProductDTO product) {
        this.product = product;
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

    // Getters y Setters para campos adicionales de GraphQL input
    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
