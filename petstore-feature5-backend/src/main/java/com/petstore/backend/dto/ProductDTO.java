package com.petstore.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO para representar un producto de la tienda de mascotas")
public class ProductDTO {
    
    @Schema(description = "ID único del producto", example = "1")
    private Integer productId;
    
    @Schema(description = "Nombre del producto", example = "Collar para perro")
    private String productName;
    
    @Schema(description = "Descripción detallada del producto", example = "Collar ajustable de cuero para perros medianos")
    private String description;
    
    @Schema(description = "Precio del producto", example = "25.99")
    private BigDecimal price;
    
    @Schema(description = "Código SKU del producto", example = "SKU12345")
    private String sku;
    
    @Schema(description = "Cantidad en stock", example = "15")
    private Integer stock;
    
    @Schema(description = "URL de la imagen del producto", example = "https://ejemplo.com/imagen.jpg")
    private String imageUrl;
    
    @Schema(description = "Categoría a la que pertenece el producto")
    private CategoryDTO category;
    
    @Schema(description = "Estado del producto", example = "ACTIVE", allowableValues = {"ACTIVE", "INACTIVE", "DISCONTINUED"})
    private String status;
    
    @Schema(description = "Fecha de creación del producto")
    private LocalDateTime createdAt;
    
    @Schema(description = "Fecha de última actualización del producto")
    private LocalDateTime updatedAt;

    // Constructors
    public ProductDTO() {}

    public ProductDTO(Integer productId, String productName, String description, 
                     BigDecimal price, String sku, Integer stock, String imageUrl, 
                     CategoryDTO category, String status, LocalDateTime createdAt, 
                     LocalDateTime updatedAt) {
        this.productId = productId;
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.sku = sku;
        this.stock = stock;
        this.imageUrl = imageUrl;
        this.category = category;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public CategoryDTO getCategory() {
        return category;
    }

    public void setCategory(CategoryDTO category) {
        this.category = category;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
}
