package com.petstore.backend.controller;

import com.petstore.backend.dto.CategoryDTO;
import com.petstore.backend.dto.ProductDTO;
import com.petstore.backend.entity.Product;
import com.petstore.backend.mapper.MapperFacade;
import com.petstore.backend.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {

    @Autowired
    private ProductService productService;
    
    @Autowired
    private MapperFacade mapperFacade;

    /**
     * GET /api/products
     * Lista todos los productos disponibles usando MapStruct
     */
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        try {
            List<Product> products = productService.findAll();
            List<ProductDTO> productDTOs = products.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(productDTOs);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * GET /api/products/category/{categoryId}
     * Lista todos los productos de una categoría específica usando MapStruct
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductDTO>> getProductsByCategory(@PathVariable Integer categoryId) {
        try {
            List<Product> products = productService.findByCategoryId(categoryId);
            List<ProductDTO> productDTOs = products.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(productDTOs);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * GET /api/products/{id}
     * Obtiene un producto específico por su ID usando MapStruct
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Integer id) {
        try {
            Optional<Product> product = productService.findById(id);
            if (product.isPresent()) {
                ProductDTO productDTO = convertToDTO(product.get());
                return ResponseEntity.ok(productDTO);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * GET /api/products/search
     * Busca productos por nombre (contiene el texto)
     */
    @GetMapping("/search")
    public ResponseEntity<List<ProductDTO>> searchProducts(@RequestParam String name) {
        try {
            List<Product> products = productService.findByNameContaining(name);
            List<ProductDTO> productDTOs = products.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
            return ResponseEntity.ok(productDTOs);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * GET /api/products/price-range
     * Lista productos dentro de un rango de precios
     */
    @GetMapping("/price-range")
    public ResponseEntity<List<ProductDTO>> getProductsByPriceRange(
            @RequestParam Double minPrice, 
            @RequestParam Double maxPrice) {
        try {
            List<Product> products = productService.findByPriceBetween(minPrice, maxPrice);
            List<ProductDTO> productDTOs = products.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
            return ResponseEntity.ok(productDTOs);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Convierte una entidad Product a ProductDTO
     */
    private ProductDTO convertToDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setProductId(product.getProductId());
        dto.setProductName(product.getProductName());
        // La entidad Product no tiene description, se puede dejar null o usar productName
        dto.setDescription(product.getProductName());
        // Convertir Double a BigDecimal
        dto.setPrice(BigDecimal.valueOf(product.getBasePrice()));
        // La entidad Product no tiene stock, usar un valor por defecto o null
        dto.setStock(null);
        // La entidad Product no tiene imageUrl, se puede dejar null
        dto.setImageUrl(null);
        dto.setStatus("ACTIVE"); // Valor por defecto
        dto.setCreatedAt(LocalDateTime.now()); // Valor por defecto
        dto.setUpdatedAt(LocalDateTime.now()); // Valor por defecto
        
        if (product.getCategory() != null) {
            CategoryDTO categoryDTO = new CategoryDTO();
            categoryDTO.setCategoryId(product.getCategory().getCategoryId());
            categoryDTO.setCategoryName(product.getCategory().getCategoryName());
            dto.setCategory(categoryDTO);
        }
        
        return dto;
    }
}
