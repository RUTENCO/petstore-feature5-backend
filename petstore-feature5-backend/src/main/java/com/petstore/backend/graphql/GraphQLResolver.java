package com.petstore.backend.graphql;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import com.petstore.backend.dto.LoginResponse;
import com.petstore.backend.dto.PromotionDTO;
import com.petstore.backend.dto.PromotionDeletedDTO;
import com.petstore.backend.entity.Category;
import com.petstore.backend.entity.Product;
import com.petstore.backend.entity.Promotion;
import com.petstore.backend.entity.User;
import com.petstore.backend.exception.GraphQLException;
import com.petstore.backend.repository.CategoryRepository;
import com.petstore.backend.repository.ProductRepository;
import com.petstore.backend.repository.PromotionRepository;
import com.petstore.backend.repository.UserRepository;
import com.petstore.backend.service.AuthService; // Importar Logger
import com.petstore.backend.service.PromotionService; // Importar LoggerFactory

@Controller
public class GraphQLResolver {

    private final PromotionService promotionService;
    private final AuthService authService;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final PromotionRepository promotionRepository;
    private static final Logger loggerGraphQL = LoggerFactory.getLogger(GraphQLResolver.class);

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
            throw new GraphQLException("AUTHENTICATION", "Authentication required", "Please provide a valid JWT token");
        }
    }
    


    // === QUERIES ===

    @QueryMapping
    public String health() {
        return "GraphQL API is running!";
    }

    /**
     * Obtener usuario autenticado actual
     */
    private User getAuthenticatedUser() {
        requireAuthentication();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByEmail(auth.getName()).orElse(null);
    }

    @QueryMapping
    public User currentUser() {
        return getAuthenticatedUser();
    }

    @QueryMapping
    public List<Promotion> promotions() {
        // Público - sin autenticación
        try {
            return promotionRepository.findAll();
        } catch (Exception e) {
            loggerGraphQL.error("Error getting all promotions: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @QueryMapping
    public List<Promotion> promotionsActive() {
        // Público - sin autenticación
        try {
            return promotionService.getAllActivePromotionsEntities();
        } catch (Exception e) {
            loggerGraphQL.error("Error getting active promotions: {}" , e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @QueryMapping
    public List<Promotion> promotionsExpired() {
        // Público - sin autenticación
        try {
            return promotionService.getAllExpiredPromotionsEntities();
        } catch (Exception e) {
            loggerGraphQL.error("Error getting expired promotions: {}" , e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @QueryMapping
    public List<Promotion> promotionsScheduled() {
        // Público - sin autenticación
        try {
            return promotionService.getAllScheduledPromotionsEntities();
        } catch (Exception e) {
            loggerGraphQL.error("Error getting scheduled promotions: {}" , e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @QueryMapping
    public List<Promotion> promotionsByStatus(@Argument String statusName) {
        // Público - sin autenticación
        try {
            return promotionService.getPromotionsByStatusEntities(statusName);
        } catch (Exception e) {
            loggerGraphQL.error("Error getting promotions by status: {}" , e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @QueryMapping
    public List<Promotion> promotionsByCategory(@Argument Integer categoryId) {
        // Público - sin autenticación
        try {
            return promotionService.getPromotionsByCategoryEntities(categoryId);
        } catch (Exception e) {
            loggerGraphQL.error("Error getting promotions by category: {}" , e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @QueryMapping
    public Promotion promotion(@Argument Integer id) {
        // Público - sin autenticación
        try {
            return promotionRepository.findById(id).orElse(null);
        } catch (Exception e) {
            loggerGraphQL.error("Error getting promotion by id: {}" , e.getMessage(), e);
            return null;
        }
    }

    @QueryMapping
    public List<Category> categories() {
        // Público - sin autenticación
        try {
            return categoryRepository.findAll();
        } catch (Exception e) {
            loggerGraphQL.error("Error getting categories: {}" , e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @QueryMapping
    public Category category(@Argument Integer id) {
        // Público - sin autenticación
        try {
            return categoryRepository.findById(id).orElse(null);
        } catch (Exception e) {
            loggerGraphQL.error("Error getting category by id: {}" , e.getMessage(), e);
            return null;
        }
    }

    @QueryMapping
    public List<Product> products() {
        // Público - sin autenticación
        try {
            return productRepository.findAll();
        } catch (Exception e) {
            loggerGraphQL.error("Error getting products: {}" , e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @QueryMapping
    public List<Product> productsByCategory(@Argument Integer categoryId) {
        // Público - sin autenticación
        try {
            return productRepository.findByCategoryCategoryId(categoryId);
        } catch (Exception e) {
            loggerGraphQL.error("Error getting products by category: {}" , e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @QueryMapping
    public Product product(@Argument Integer id) {
        // Público - sin autenticación
        try {
            return productRepository.findById(id).orElse(null);
        } catch (Exception e) {
            loggerGraphQL.error("Error getting product by id: {}" , e.getMessage(), e);
            return null;
        }
    }

    // === MUTATIONS ===

    @MutationMapping
    public LoginResponse login(@Argument String email, @Argument String password) {
        try {
            loggerGraphQL.info("GraphQL Login attempt for email: {}" , email);
            
            // Usar AuthService para generar JWT real (igual que REST)
            LoginResponse response = authService.authenticateMarketingAdmin(email, password);
            loggerGraphQL.info("GraphQL Login successful, using real JWT token");
            return response;
            
        } catch (Exception e) {
            loggerGraphQL.error("Error in GraphQL login: {}" , e.getMessage(), e);
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
            loggerGraphQL.info("Creating promotion with input: {}", input);
            
            // Validar campos requeridos
            if (input.getCategoryId() == null) {
                throw new GraphQLException("CREATE", "CategoryId is required", "CategoryId cannot be null");
            }
            if (input.getStatusId() == null) {
                throw new GraphQLException("CREATE", "StatusId is required", "StatusId cannot be null");
            }
            if (input.getUserId() == null) {
                throw new GraphQLException("CREATE", "UserId is required", "UserId cannot be null");
            }
            
            // Usar el método existente createPromotion del service
            return promotionService.createPromotion(
                input.getPromotionName(),
                input.getDescription(),
                input.getStartDate(),
                input.getEndDate(),
                input.getDiscountPercentage().doubleValue(),
                input.getStatusId(),
                input.getUserId(),
                input.getCategoryId()
            );
        } catch (Exception e) {
            loggerGraphQL.error("Error creating promotion: {}", e.getMessage(), e);
            throw new GraphQLException("CREATE", "Failed to create promotion", "Input: " + input + ", Error: " + e.getMessage(), e);
        }
    }

    @MutationMapping
    public Promotion updatePromotion(@Argument Integer id, @Argument PromotionDTO input) {
        requireAuthentication();
        try {
            loggerGraphQL.info("Updating promotion {} with input: {}", id, input);
            
            // Usar el método existente updatePromotion del service
            Promotion updated = promotionService.updatePromotion(
                id,
                input.getPromotionName(),
                input.getDescription(),
                input.getStartDate(),
                input.getEndDate(),
                input.getDiscountPercentage().doubleValue(),
                input.getStatusId() != null ? input.getStatusId() : 1,
                input.getUserId() != null ? input.getUserId() : 1,
                input.getCategoryId() != null ? input.getCategoryId() : 
                    (input.getCategory() != null ? input.getCategory().getCategoryId() : null)
            );
            
            return updated;
        } catch (Exception e) {
            loggerGraphQL.error("Error updating promotion {}: {}", id, e.getMessage(), e);
            throw new GraphQLException("UPDATE", "Failed to update promotion", "ID: " + id + ", Input: " + input + ", Error: " + e.getMessage(), e);
        }
    }

    @MutationMapping
    public Boolean deletePromotion(@Argument Integer id, @Argument Integer userId) {
        requireAuthentication();
        try {
            if (userId != null) {
                loggerGraphQL.info("Deleting promotion with id: {} by user: {}", id, userId);
            } else {
                loggerGraphQL.info("Deleting promotion with id: {}", id);
            }
            
            boolean deleted = promotionService.deletePromotion(id, userId);
            
            if (!deleted) {
                throw new GraphQLException("DELETE", "Promotion not found or could not be deleted", "ID: " + id);
            }
            
            return true;
        } catch (GraphQLException e) {
            loggerGraphQL.error("GraphQL error deleting promotion {}: {}", id, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            loggerGraphQL.error("Unexpected error deleting promotion {}: {}", id, e.getMessage(), e);
            throw new GraphQLException("DELETE", "Unexpected error during deletion", "ID: " + id + ", Error: " + e.getMessage(), e);
        }
    }

    // ================== MUTACIONES DE ASOCIACIÓN PRODUCTO-PROMOCIÓN ==================

    @MutationMapping
    public Promotion associateProductsToPromotion(@Argument Integer promotionId, @Argument List<Integer> productIds) {
        requireAuthentication();
        try {
            loggerGraphQL.info("Associating products {} to promotion {}", productIds, promotionId);
            
            // Obtener la promoción
            Promotion promotion = promotionRepository.findById(promotionId).orElse(null);
            if (promotion == null) {
                throw new GraphQLException("ASSOCIATE", "Promotion not found", "ID: " + promotionId);
            }
            
            // Asociar productos a la promoción
            for (Integer productId : productIds) {
                Product product = productRepository.findById(productId).orElse(null);
                if (product != null) {
                    product.setPromotion(promotion);
                    productRepository.save(product);
                }
            }
            
            // Retornar la promoción actualizada
            return promotionRepository.findById(promotionId).orElse(null);
        } catch (GraphQLException e) {
            loggerGraphQL.error("GraphQL error associating products to promotion {}: {}", promotionId, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            loggerGraphQL.error("Unexpected error associating products to promotion {}: {}", promotionId, e.getMessage(), e);
            throw new GraphQLException("ASSOCIATE", "Unexpected error during association", "PromotionID: " + promotionId + ", ProductIDs: " + productIds + ", Error: " + e.getMessage(), e);
        }
    }

    @MutationMapping
    public Promotion removeProductsFromPromotion(@Argument Integer promotionId, @Argument List<Integer> productIds) {
        requireAuthentication();
        try {
            loggerGraphQL.info("Removing products {} from promotion {}", productIds, promotionId);
            
            // Verificar que la promoción existe
            Promotion promotion = promotionRepository.findById(promotionId).orElse(null);
            if (promotion == null) {
                throw new GraphQLException("REMOVE_ASSOCIATION", "Promotion not found", "ID: " + promotionId);
            }
            
            // Remover asociación de productos
            for (Integer productId : productIds) {
                Product product = productRepository.findById(productId).orElse(null);
                if (product != null && product.getPromotion() != null && product.getPromotion().getPromotionId().equals(promotionId)) {
                    product.setPromotion(null);
                    productRepository.save(product);
                }
            }
            
            // Retornar la promoción actualizada
            return promotionRepository.findById(promotionId).orElse(null);
        } catch (GraphQLException e) {
            loggerGraphQL.error("GraphQL error removing products from promotion {}: {}", promotionId, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            loggerGraphQL.error("Unexpected error removing products from promotion {}: {}", promotionId, e.getMessage(), e);
            throw new GraphQLException("REMOVE_ASSOCIATION", "Unexpected error during removal", "PromotionID: " + promotionId + ", ProductIDs: " + productIds + ", Error: " + e.getMessage(), e);
        }
    }

    // ================== QUERIES DE PAPELERA TEMPORAL ==================

    @QueryMapping
    public List<PromotionDeletedDTO> deletedPromotions() {
        requireAuthentication();
        try {
            return promotionService.getDeletedPromotions();
        } catch (Exception e) {
            loggerGraphQL.error("Error fetching deleted promotions: {}", e.getMessage(), e);
            throw new GraphQLException("QUERY", "Failed to fetch deleted promotions", "Error: " + e.getMessage(), e);
        }
    }

    @QueryMapping 
    public List<PromotionDeletedDTO> deletedPromotionsByUser(@Argument String userId) {
        requireAuthentication();
        try {
            // Convertir String ID a Integer
            Integer userIdInt = Integer.parseInt(userId);
            return promotionService.getDeletedPromotionsByUser(userIdInt);
        } catch (NumberFormatException e) {
            loggerGraphQL.error("Invalid userId format: {}", userId);
            throw new GraphQLException("QUERY", "Invalid userId format", "UserId: " + userId, e);
        } catch (Exception e) {
            loggerGraphQL.error("Error fetching deleted promotions by user {}: {}", userId, e.getMessage(), e);
            throw new GraphQLException("QUERY", "Failed to fetch deleted promotions by user", "UserId: " + userId + ", Error: " + e.getMessage(), e);
        }
    }

    // ================== MUTACIONES DE PAPELERA TEMPORAL ==================

    @MutationMapping
    public Boolean restorePromotion(@Argument Integer id, @Argument Integer userId) {
        requireAuthentication();
        try {
            loggerGraphQL.info("Restoring promotion with id: {} by user: {}", id, userId);
            
            // Usar la función de base de datos que maneja automáticamente el proceso
            boolean restored = promotionService.restorePromotionUsingDBFunction(id, userId);
            
            if (!restored) {
                throw new GraphQLException("RESTORE", "Promotion not found or could not be restored", "ID: " + id);
            }
            
            return true;
        } catch (GraphQLException e) {
            loggerGraphQL.error("GraphQL error restoring promotion {}: {}", id, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            loggerGraphQL.error("Unexpected error restoring promotion {}: {}", id, e.getMessage(), e);
            throw new GraphQLException("RESTORE", "Unexpected error during restoration", "ID: " + id + ", Error: " + e.getMessage(), e);
        }
    }

    @MutationMapping
    public Boolean permanentDeletePromotion(@Argument Integer id, @Argument Integer userId) {
        requireAuthentication();
        try {
            loggerGraphQL.info("Permanently deleting promotion with id: {} by user: {}", id, userId);
            
            boolean deleted = promotionService.permanentDeletePromotion(id, userId);
            
            if (!deleted) {
                throw new GraphQLException("PERMANENT_DELETE", "Promotion not found in trash or could not be deleted", "ID: " + id);
            }
            
            return true;
        } catch (GraphQLException e) {
            loggerGraphQL.error("GraphQL error permanently deleting promotion {}: {}", id, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            loggerGraphQL.error("Unexpected error permanently deleting promotion {}: {}", id, e.getMessage(), e);
            throw new GraphQLException("PERMANENT_DELETE", "Unexpected error during permanent deletion", "ID: " + id + ", Error: " + e.getMessage(), e);
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
            loggerGraphQL.error("Error getting products for promotion: {}" , e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @SchemaMapping(typeName = "Category", field = "promotions")
    public List<Promotion> categoryPromotions(Category category) {
        try {
            return promotionService.getPromotionsByCategoryEntities(category.getCategoryId());
        } catch (Exception e) {
            loggerGraphQL.error("Error getting promotions for category: {}" , e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @SchemaMapping(typeName = "Category", field = "products")
    public List<Product> categoryProducts(Category category) {
        try {
            return productRepository.findByCategoryCategoryId(category.getCategoryId());
        } catch (Exception e) {
            loggerGraphQL.error("Error getting products for category: {}" , e.getMessage(), e);
            return Collections.emptyList();
        }
    }
}
