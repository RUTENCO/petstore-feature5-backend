package com.petstore.backend.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.petstore.backend.dto.CategoryDTO;
import com.petstore.backend.entity.Category;
import com.petstore.backend.service.CategoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

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
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080", "https://petstore-feature5-backend.onrender.com", "https://fluffy-deals-hub.vercel.app"})
@Tag(name = "Categorías", description = "API para gestión de categorías de productos")
public class CategoryController {

    
    private final CategoryService categoryService; // Inyección de dependencia del servicio de categorías

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Operation(
            summary = "Obtener todas las categorías",
            description = "Retorna una lista completa de todas las categorías de productos disponibles"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", 
                    description = "Lista de categorías obtenida exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = CategoryDTO.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "500", 
                    description = "Error interno del servidor",
                    content = @Content
            )
    })
    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        try {
            List<Category> categories = categoryService.findAll();
            List<CategoryDTO> categoryDTOs = categories.stream()
                    .map(this::convertToDTO)
                    .toList();
            
            return ResponseEntity.ok(categoryDTOs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(
            summary = "Obtener categoría por ID",
            description = "Retorna una categoría específica utilizando su identificador único"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", 
                    description = "Categoría encontrada exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CategoryDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404", 
                    description = "Categoría no encontrada",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500", 
                    description = "Error interno del servidor",
                    content = @Content
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(
            @Parameter(description = "ID de la categoría", example = "1", required = true)
            @PathVariable Integer id) {
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

    @Operation(
            summary = "Crear nueva categoría",
            description = "Crea una nueva categoría de productos en el sistema"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201", 
                    description = "Categoría creada exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CategoryDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400", 
                    description = "Datos de entrada inválidos",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500", 
                    description = "Error interno del servidor",
                    content = @Content
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public ResponseEntity<CategoryDTO> createCategory(
            @Parameter(description = "Datos de la categoría a crear", required = true)
            @RequestBody CategoryDTO categoryDTO) {
        try {
            Category category = convertToEntity(categoryDTO);
            
            Category savedCategory = categoryService.save(category);
            CategoryDTO responseDTO = convertToDTO(savedCategory);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @Operation(
            summary = "Actualizar categoría existente",
            description = "Actualiza los datos de una categoría existente utilizando su ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", 
                    description = "Categoría actualizada exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CategoryDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404", 
                    description = "Categoría no encontrada",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400", 
                    description = "Datos de entrada inválidos",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500", 
                    description = "Error interno del servidor",
                    content = @Content
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(
            @Parameter(description = "ID de la categoría a actualizar", example = "1", required = true)
            @PathVariable Integer id, 
            @Parameter(description = "Nuevos datos de la categoría", required = true)
            @RequestBody CategoryDTO categoryDTO) {
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

    @Operation(
            summary = "Eliminar categoría",
            description = "Elimina permanentemente una categoría del sistema utilizando su ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204", 
                    description = "Categoría eliminada exitosamente",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404", 
                    description = "Categoría no encontrada",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500", 
                    description = "Error interno del servidor",
                    content = @Content
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(
            @Parameter(description = "ID de la categoría a eliminar", example = "1", required = true)
            @PathVariable Integer id) {
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

    @Operation(
            summary = "Información de la API de categorías",
            description = "Retorna información sobre todos los endpoints disponibles en la API de categorías"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", 
                    description = "Información obtenida exitosamente",
                    content = @Content(mediaType = "application/json")
            )
    })
    @GetMapping("/info")
    public ResponseEntity<Object> getEndpointsInfo() {
        var info = java.util.Map.of(
            "service", "Categories API",
            "endpoints", java.util.List.of(
                "GET /api/categories - Todas las categorías",
                "GET /api/categories/{id} - Categoría por ID",
                "POST /api/categories - Crear categoría",
                "PUT /api/categories/{id} - Actualizar categoría",
                "DELETE /api/categories/{id} - Eliminar categoría"
            )
        );
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
