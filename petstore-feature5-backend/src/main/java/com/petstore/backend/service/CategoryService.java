package com.petstore.backend.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.petstore.backend.entity.Category;
import com.petstore.backend.repository.CategoryRepository;

@Service
public class CategoryService {

    
    private final CategoryRepository categoryRepository; // Inyección de dependencia del repositorio de categorías

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }


    /**
     * Encuentra todas las categorías
     */
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    /**
     * Encuentra una categoría por ID
     */
    public Optional<Category> findById(Integer id) {
        return categoryRepository.findById(id);
    }

    /**
     * Guarda una categoría
     */
    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    /**
     * Elimina una categoría por ID
     */
    public void deleteById(Integer id) {
        categoryRepository.deleteById(id);
    }

    /**
     * Verifica si existe una categoría por ID
     */
    public boolean existsById(Integer id) {
        return categoryRepository.existsById(id);
    }
}
