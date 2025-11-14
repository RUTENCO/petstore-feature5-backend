package com.petstore.backend.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.petstore.backend.dto.CategoryDTO;
import com.petstore.backend.dto.ProductDTO;
import com.petstore.backend.entity.Product;
import com.petstore.backend.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Productos", description = "Gestión de productos de la tienda de mascotas")
@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080", "https://petstore-feature5-backend.onrender.com", "https://fluffy-deals-hub.vercel.app"})
public class ProductController {

    private final ProductService productService; // Inyección de dependencia del servicio de productos

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(
            summary = "Obtener todos los productos",
            description = "Retorna una lista completa de todos los productos disponibles en la tienda"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", 
                    description = "Lista de productos obtenida exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ProductDTO.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "500", 
                    description = "Error interno del servidor",
                    content = @Content
            )
    })
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        try {
            List<Product> products = productService.findAll();
            List<ProductDTO> productDTOs = products.stream()
                    .map(this::convertToDTO)
                    .toList();
            return ResponseEntity.ok(productDTOs);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(
            summary = "Obtener productos por categoría",
            description = "Retorna todos los productos que pertenecen a una categoría específica"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", 
                    description = "Productos de la categoría obtenidos exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ProductDTO.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "500", 
                    description = "Error interno del servidor",
                    content = @Content
            )
    })
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductDTO>> getProductsByCategory(
            @Parameter(description = "ID de la categoría", example = "1", required = true)
            @PathVariable Integer categoryId) {
        try {
            List<Product> products = productService.findByCategoryId(categoryId);
            List<ProductDTO> productDTOs = products.stream()
                    .map(this::convertToDTO)
                    .toList();
            return ResponseEntity.ok(productDTOs);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(
            summary = "Obtener producto por ID",
            description = "Retorna un producto específico utilizando su identificador único"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", 
                    description = "Producto encontrado exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProductDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404", 
                    description = "Producto no encontrado",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500", 
                    description = "Error interno del servidor",
                    content = @Content
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getProductById(
            @Parameter(description = "ID del producto", example = "1", required = true)
            @PathVariable Integer id) {
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

    @Operation(
            summary = "Buscar productos por nombre",
            description = "Busca productos cuyo nombre contenga el texto especificado (búsqueda parcial)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", 
                    description = "Búsqueda realizada exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ProductDTO.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "500", 
                    description = "Error interno del servidor",
                    content = @Content
            )
    })
    @GetMapping("/search")
    public ResponseEntity<List<ProductDTO>> searchProducts(
            @Parameter(description = "Texto a buscar en el nombre del producto", example = "collar", required = true)
            @RequestParam String name) {
        try {
            List<Product> products = productService.findByNameContaining(name);
            List<ProductDTO> productDTOs = products.stream()
                .map(this::convertToDTO)
                .toList();
            return ResponseEntity.ok(productDTOs);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(
            summary = "Obtener productos por rango de precios",
            description = "Retorna productos cuyo precio esté dentro del rango especificado (inclusive)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", 
                    description = "Productos en el rango de precios obtenidos exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ProductDTO.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "500", 
                    description = "Error interno del servidor",
                    content = @Content
            )
    })
    @GetMapping("/price-range")
    public ResponseEntity<List<ProductDTO>> getProductsByPriceRange(
            @Parameter(description = "Precio mínimo", example = "10.0", required = true)
            @RequestParam Double minPrice, 
            @Parameter(description = "Precio máximo", example = "50.0", required = true)
            @RequestParam Double maxPrice) {
        try {
            List<Product> products = productService.findByPriceBetween(minPrice, maxPrice);
            List<ProductDTO> productDTOs = products.stream()
                .map(this::convertToDTO)
                .toList();
            return ResponseEntity.ok(productDTOs);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(
            summary = "Obtener productos por promoción",
            description = "Retorna todos los productos que están asociados a una promoción específica"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", 
                    description = "Productos de la promoción obtenidos exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = ProductDTO.class))
                    )
            ),
            @ApiResponse(
                    responseCode = "500", 
                    description = "Error interno del servidor",
                    content = @Content
            )
    })
    @GetMapping("/promotion/{promotionId}")
    public ResponseEntity<List<ProductDTO>> getProductsByPromotion(
            @Parameter(description = "ID de la promoción", example = "1", required = true)
            @PathVariable Integer promotionId) {
        try {
            List<Product> products = productService.findByPromotionId(promotionId);
            List<ProductDTO> productDTOs = products.stream()
                    .map(this::convertToDTO)
                    .toList();
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
        // Asignar SKU desde la entidad Product
        dto.setSku(String.valueOf(product.getSku()));
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
            // Asignar descripción de la categoría
            categoryDTO.setDescription(product.getCategory().getDescription());
            dto.setCategory(categoryDTO);
        }
        
        return dto;
    }
}
