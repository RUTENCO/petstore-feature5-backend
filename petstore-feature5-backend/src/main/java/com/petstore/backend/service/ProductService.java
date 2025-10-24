package com.petstore.backend.service;


import com.petstore.backend.entity.Product;
import com.petstore.backend.repository.CategoryRepository;
import com.petstore.backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductService {

    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;

    /**
     * Encuentra todos los productos
     */
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    /**
     * Encuentra productos por ID de categoría
     */
    public List<Product> findByCategoryId(Integer categoryId) {
        return productRepository.findByCategoryCategoryId(categoryId);
    }

    /**
     * Encuentra un producto por ID
     */
    public Optional<Product> findById(Integer id) {
        return productRepository.findById(id);
    }

    /**
     * Guarda un producto (crear o actualizar)
     */
    public Product save(Product product) {
        return productRepository.save(product);
    }

    /**
     * Elimina un producto por ID
     */
    public void deleteById(Integer id) {
        productRepository.deleteById(id);
    }

    /**
     * Busca productos por nombre que contenga el texto
     */
    public List<Product> findByNameContaining(String name) {
        return productRepository.findByProductNameContainingIgnoreCase(name);
    }

    /**
     * Encuentra productos en un rango de precios
     */
    public List<Product> findByPriceBetween(Double minPrice, Double maxPrice) {
        return productRepository.findByBasePriceBetween(minPrice, maxPrice);
    }

    /**
     * Verifica si existe un producto por ID
     */
    public boolean existsById(Integer id) {
        return productRepository.existsById(id);
    }

    /**
     * Cuenta total de productos
     */
    public long count() {
        return productRepository.count();
    }
}
