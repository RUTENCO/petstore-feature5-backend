package com.petstore.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO para representar una categoría de productos")
public class CategoryDTO {
    
    @Schema(description = "ID único de la categoría", example = "1")
    private Integer categoryId;
    
    @Schema(description = "Nombre de la categoría", example = "Accesorios para perros")
    private String categoryName;
    
    @Schema(description = "Descripción de la categoría", example = "Accesorios y complementos para perros de todas las razas")
    private String description;

    // Constructors
    public CategoryDTO() {}

    public CategoryDTO(Integer categoryId, String categoryName, String description) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.description = description;
    }

    // Getters and Setters
    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
