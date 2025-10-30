package com.petstore.backend.controller;

import com.petstore.backend.dto.CategoryDTO;
import com.petstore.backend.entity.Category;
import com.petstore.backend.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controlador REST para gestión de categorías
 * Endpoints disponibles:
 * - GET /api/categories - Obtener todas las categorías
 * - GET /api/categories/{id} - Obtener categoría por ID
 * - POST /api/categories - Crear nueva categoría
 * - PUT /api/categories/{id} - Actualizar categoría
 * - DELETE /api/categories/{id} - Eliminar categoría
 */
@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * GET /api/categories
     * Obtiene todas las categorías disponibles
     */
    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        try {
            List<Category> categories = categoryService.findAll();
            List<CategoryDTO> categoryDTOs = categories.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(categoryDTOs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /api/categories/{id}
     * Obtiene una categoría específica por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Integer id) {
        try {
            Optional<Category> categoryOpt = categoryService.findById(id);
            
            if (categoryOpt.isPresent()) {
                CategoryDTO categoryDTO = convertToDTO(categoryOpt.get());
                return ResponseEntity.ok(categoryDTO);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * POST /api/categories
     * Crea una nueva categoría
     */
    @PostMapping
    public ResponseEntity<CategoryDTO> createCategory(@RequestBody CategoryDTO categoryDTO) {
        try {
            Category category = convertToEntity(categoryDTO);
            
            Category savedCategory = categoryService.save(category);
            CategoryDTO responseDTO = convertToDTO(savedCategory);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * PUT /api/categories/{id}
     * Actualiza una categoría existente
     */
    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Integer id, @RequestBody CategoryDTO categoryDTO) {
        try {
            Optional<Category> existingCategoryOpt = categoryService.findById(id);
            
            if (existingCategoryOpt.isPresent()) {
                Category existingCategory = existingCategoryOpt.get();
                
                // Actualizar campos
                existingCategory.setCategoryName(categoryDTO.getCategoryName());
                existingCategory.setDescription(categoryDTO.getDescription());
                
                Category updatedCategory = categoryService.save(existingCategory);
                CategoryDTO responseDTO = convertToDTO(updatedCategory);
                
                return ResponseEntity.ok(responseDTO);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * DELETE /api/categories/{id}
     * Elimina una categoría por ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Integer id) {
        try {
            if (categoryService.existsById(id)) {
                categoryService.deleteById(id);
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * GET /api/categories/info
     * Información sobre los endpoints disponibles
     */
    @GetMapping("/info")
    public ResponseEntity<String> getEndpointsInfo() {
        String info = """
                Endpoints disponibles para Categories:
                - GET /api/categories - Todas las categorías
                - GET /api/categories/{id} - Categoría por ID
                - POST /api/categories - Crear categoría
                - PUT /api/categories/{id} - Actualizar categoría
                - DELETE /api/categories/{id} - Eliminar categoría
                """;
        return ResponseEntity.ok(info);
    }

    // Métodos de conversión
    private CategoryDTO convertToDTO(Category category) {
        CategoryDTO dto = new CategoryDTO();
        dto.setCategoryId(category.getCategoryId());
        dto.setCategoryName(category.getCategoryName());
        dto.setDescription(category.getDescription());
        return dto;
    }

    private Category convertToEntity(CategoryDTO dto) {
        Category category = new Category();
        category.setCategoryName(dto.getCategoryName());
        category.setDescription(dto.getDescription());
        return category;
    }
}
