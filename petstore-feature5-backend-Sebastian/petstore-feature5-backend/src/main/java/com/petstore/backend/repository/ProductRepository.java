package com.petstore.backend.repository;

import com.petstore.backend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findByCategoryCategoryId(Integer categoryId);
    List<Product> findByPromotionPromotionId(Integer promotionId);
    List<Product> findByProductNameContainingIgnoreCase(String productName);
    List<Product> findByBasePriceBetween(Double minPrice, Double maxPrice);
}
