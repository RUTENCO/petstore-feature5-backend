package com.petstore.backend.graphql;

import java.util.Collections;
import java.util.List;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import com.petstore.backend.dto.CreatePromotionInput;

import com.petstore.backend.dto.LoginResponse;
import com.petstore.backend.dto.PromotionDTO;
import com.petstore.backend.entity.Category;
import com.petstore.backend.entity.Product;
import com.petstore.backend.entity.Promotion;
import com.petstore.backend.entity.User;
import com.petstore.backend.repository.CategoryRepository;
import com.petstore.backend.repository.ProductRepository;
import com.petstore.backend.repository.PromotionRepository;
import com.petstore.backend.repository.UserRepository;
import com.petstore.backend.service.AuthService;
import com.petstore.backend.service.PromotionService;

@Controller
public class GraphQLResolver {

    private final PromotionService promotionService;
    private final AuthService authService;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final PromotionRepository promotionRepository;

    public GraphQLResolver(
            PromotionService promotionService,
            AuthService authService,
            UserRepository userRepository,
            CategoryRepository categoryRepository,
            ProductRepository productRepository,
            PromotionRepository promotionRepository) {
        this.promotionService = promotionService;
        this.authService = authService;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.promotionRepository = promotionRepository;
    }

    // === HELPER METHODS ===
    
    /**
     * Verificar si el usuario está autenticado
     * Lanza excepción si no está autenticado
     */
    private void requireAuthentication() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            throw new RuntimeException("Authentication required. Please provide a valid JWT token.");
        }
    }
    
    /**
     * Obtener usuario autenticado actual
     */
    private User getAuthenticatedUser() {
        requireAuthentication();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByEmail(auth.getName()).orElse(null);
    }

    // === QUERIES ===

    @QueryMapping
    public String health() {
        return "GraphQL API is running! " + java.time.LocalDateTime.now();
    }

    @QueryMapping
    public User currentUser() {
        // Solo requerir autenticación para currentUser
        requireAuthentication();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByEmail(auth.getName()).orElse(null);
    }

    @QueryMapping
    public List<Promotion> promotions() {
        // Público - sin autenticación
        try {
            return promotionRepository.findAll();
        } catch (Exception e) {
            System.err.println("Error getting all promotions: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    @QueryMapping
    public List<Promotion> promotionsActive() {
        // Público - sin autenticación
        try {
            return promotionService.getAllActivePromotionsEntities();
        } catch (Exception e) {
            System.err.println("Error getting active promotions: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    @QueryMapping
    public List<Promotion> promotionsExpired() {
        // Público - sin autenticación
        try {
            return promotionService.getAllExpiredPromotionsEntities();
        } catch (Exception e) {
            System.err.println("Error getting expired promotions: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    @QueryMapping
    public List<Promotion> promotionsScheduled() {
        // Público - sin autenticación
        try {
            return promotionService.getAllScheduledPromotionsEntities();
        } catch (Exception e) {
            System.err.println("Error getting scheduled promotions: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    @QueryMapping
    public List<Promotion> promotionsByStatus(@Argument String statusName) {
        // Público - sin autenticación
        try {
            return promotionService.getPromotionsByStatusEntities(statusName);
        } catch (Exception e) {
            System.err.println("Error getting promotions by status: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    @QueryMapping
    public List<Promotion> promotionsByCategory(@Argument Integer categoryId) {
        // Público - sin autenticación
        try {
            return promotionService.getPromotionsByCategoryEntities(categoryId);
        } catch (Exception e) {
            System.err.println("Error getting promotions by category: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    @QueryMapping
    public Promotion promotion(@Argument Integer id) {
        // Público - sin autenticación
        try {
            return promotionRepository.findById(id).orElse(null);
        } catch (Exception e) {
            System.err.println("Error getting promotion by id: " + e.getMessage());
            return null;
        }
    }

    @QueryMapping
    public List<Category> categories() {
        // Público - sin autenticación
        try {
            return categoryRepository.findAll();
        } catch (Exception e) {
            System.err.println("Error getting categories: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    @QueryMapping
    public Category category(@Argument Integer id) {
        // Público - sin autenticación
        try {
            return categoryRepository.findById(id).orElse(null);
        } catch (Exception e) {
            System.err.println("Error getting category by id: " + e.getMessage());
            return null;
        }
    }

    @QueryMapping
    public List<Product> products() {
        // Público - sin autenticación
        try {
            return productRepository.findAll();
        } catch (Exception e) {
            System.err.println("Error getting products: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    @QueryMapping
    public List<Product> productsByCategory(@Argument Integer categoryId) {
        // Público - sin autenticación
        try {
            return productRepository.findByCategoryCategoryId(categoryId);
        } catch (Exception e) {
            System.err.println("Error getting products by category: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    @QueryMapping
    public Product product(@Argument Integer id) {
        // Público - sin autenticación
        try {
            return productRepository.findById(id).orElse(null);
        } catch (Exception e) {
            System.err.println("Error getting product by id: " + e.getMessage());
            return null;
        }
    }

    // === MUTATIONS ===

    @MutationMapping
    public LoginResponse login(@Argument String email, @Argument String password) {
        try {
            System.out.println("GraphQL Login attempt for email: " + email);
            
            // Usar AuthService para generar JWT real (igual que REST)
            LoginResponse response = authService.authenticateMarketingAdmin(email, password);
            System.out.println("GraphQL Login successful, using real JWT token");
            return response;
            
        } catch (Exception e) {
            System.err.println("Error in GraphQL login: " + e.getMessage());
            LoginResponse response = new LoginResponse();
            response.setSuccess(false);
            response.setMessage("Invalid credentials or authentication error");
            return response;
        }
    }

    // === PROMOTION MUTATIONS ===

    @MutationMapping
    public Promotion createPromotion(@Argument PromotionDTO input) {
        requireAuthentication();
        try {
            System.out.println("Creating promotion with input: " + input);
            
            // Usar el método existente createPromotion del service
            return promotionService.createPromotion(
                input.getPromotionName(),
                input.getDescription(),
                input.getStartDate().toLocalDate(),
                input.getEndDate().toLocalDate(),
                input.getDiscountPercentage().doubleValue(),
                1, // statusId por defecto
                1, // userId por defecto  
                input.getCategory() != null ? input.getCategory().getCategoryId() : null
            );
        } catch (Exception e) {
            System.err.println("Error creating promotion: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to create promotion: " + e.getMessage());
        }
    }

    @MutationMapping
    public Promotion updatePromotion(@Argument Integer id, @Argument PromotionDTO input) {
        requireAuthentication();
        try {
            System.out.println("Updating promotion " + id + " with input: " + input);
            
            // Usar el método existente updatePromotion del service
            Promotion updated = promotionService.updatePromotion(
                id,
                input.getPromotionName(),
                input.getDescription(),
                input.getStartDate().toLocalDate(),
                input.getEndDate().toLocalDate(),
                input.getDiscountPercentage().doubleValue(),
                1, // statusId por defecto
                1, // userId por defecto
                input.getCategory() != null ? input.getCategory().getCategoryId() : null
            );
            

            
            return updated;
        } catch (Exception e) {
            System.err.println("Error updating promotion: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to update promotion: " + e.getMessage());
        }
    }

    @MutationMapping
    public Boolean deletePromotion(@Argument Integer id) {
        requireAuthentication();
        try {
            System.out.println("Deleting promotion with id: " + id);
            
            boolean deleted = promotionService.deletePromotion(id);
            
            if (!deleted) {
                throw new RuntimeException("Promotion not found with id: " + id);
            }
            
            return true;
        } catch (Exception e) {
            System.err.println("Error deleting promotion: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to delete promotion: " + e.getMessage());
        }
    }
    @MutationMapping
    public Promotion createPromotion(@Argument CreatePromotionInput input) {
        try {
            return promotionService.createPromotion(input);
        } catch (Exception e) {
            System.err.println("Error creating promotion: " + e.getMessage());
            throw e;
        }
    }

    // === SCHEMA MAPPINGS para resolver relaciones ===
    // Nota: Con los DTOs de respuesta, estas relaciones ya están aplanadas
    // Estos métodos pueden ser opcionales si usas solo los DTOs de respuesta
    
    @SchemaMapping(typeName = "Promotion", field = "products")
    public List<Product> promotionProducts(Promotion promotion) {
        try {
            return productRepository.findByPromotionPromotionId(promotion.getPromotionId());
        } catch (Exception e) {
            System.err.println("Error getting products for promotion: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    @SchemaMapping(typeName = "Category", field = "promotions")
    public List<Promotion> categoryPromotions(Category category) {
        try {
            return promotionService.getPromotionsByCategoryEntities(category.getCategoryId());
        } catch (Exception e) {
            System.err.println("Error getting promotions for category: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    @SchemaMapping(typeName = "Category", field = "products")
    public List<Product> categoryProducts(Category category) {
        try {
            return productRepository.findByCategoryCategoryId(category.getCategoryId());
        } catch (Exception e) {
            System.err.println("Error getting products for category: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}
