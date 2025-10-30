package com.petstore.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "categories", schema = "public")
public class Category {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Integer categoryId;
    
    @Column(name = "category_name", nullable = false, unique = true)
    private String categoryName;
    
    @Column(name = "description")
    private String description;
    
    // Constructores
    public Category() {}
    
    public Category(String categoryName, String description) {
        this.categoryName = categoryName;
        this.description = description;
    }
    
    // Getters y Setters
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
