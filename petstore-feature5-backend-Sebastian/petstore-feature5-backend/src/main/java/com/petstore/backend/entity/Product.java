package com.petstore.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "products", schema = "public")
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Integer productId;
    
    @Column(name = "product_name", nullable = false)
    private String productName;
    
    @Column(name = "base_price", nullable = false)
    private Double basePrice;
    
    @Column(name = "sku", nullable = false, unique = true)
    private Integer sku;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promotion_id")
    private Promotion promotion;
    
    // Constructores
    public Product() {}
    
    public Product(String productName, Double basePrice, Integer sku, Category category) {
        this.productName = productName;
        this.basePrice = basePrice;
        this.sku = sku;
        this.category = category;
    }
    
    // Getters y Setters
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
    
    public Double getBasePrice() {
        return basePrice;
    }
    
    public void setBasePrice(Double basePrice) {
        this.basePrice = basePrice;
    }
    
    public Integer getSku() {
        return sku;
    }
    
    public void setSku(Integer sku) {
        this.sku = sku;
    }
    
    public Category getCategory() {
        return category;
    }
    
    public void setCategory(Category category) {
        this.category = category;
    }
    
    public Promotion getPromotion() {
        return promotion;
    }
    
    public void setPromotion(Promotion promotion) {
        this.promotion = promotion;
    }
}
