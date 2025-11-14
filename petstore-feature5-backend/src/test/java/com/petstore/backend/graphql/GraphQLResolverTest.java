package com.petstore.backend.graphql;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.petstore.backend.dto.LoginResponse;
import com.petstore.backend.entity.Category;
import com.petstore.backend.entity.Product;
import com.petstore.backend.entity.Promotion;
import com.petstore.backend.entity.Role;
import com.petstore.backend.entity.Status;
import com.petstore.backend.entity.User;
import com.petstore.backend.exception.GraphQLException;
import com.petstore.backend.repository.CategoryRepository;
import com.petstore.backend.repository.ProductRepository;
import com.petstore.backend.repository.PromotionRepository;
import com.petstore.backend.repository.UserRepository;
import com.petstore.backend.service.AuthService;
import com.petstore.backend.service.PromotionService;

class GraphQLResolverTest {

    @Mock
    private PromotionService promotionService;

    @Mock
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private PromotionRepository promotionRepository;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    private GraphQLResolver graphQLResolver;
    private User testUser;
    private Promotion testPromotion;
    private Category testCategory;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        graphQLResolver = new GraphQLResolver(
                promotionService,
                authService,
                userRepository,
                categoryRepository,
                productRepository,
                promotionRepository
        );

        // Setup test entities
        Role marketingRole = new Role();
        marketingRole.setRoleId(1);
        marketingRole.setRoleName("MARKETING_ADMIN");

        testUser = new User();
        testUser.setUserId(1);
        testUser.setEmail("admin@petstore.com");
        testUser.setPassword("encodedPassword");
        testUser.setRole(marketingRole);

        testCategory = new Category();
        testCategory.setCategoryId(1);
        testCategory.setCategoryName("Electronics");
        testCategory.setDescription("Electronic devices");

        testProduct = new Product();
        testProduct.setProductId(1);
        testProduct.setProductName("Smartphone");
        testProduct.setBasePrice(599.99);
        testProduct.setSku(12345);
        testProduct.setCategory(testCategory);

        Status activeStatus = new Status();
        activeStatus.setStatusId(1);
        activeStatus.setStatusName("ACTIVE");

        testPromotion = new Promotion();
        testPromotion.setPromotionId(1);
        testPromotion.setPromotionName("Summer Sale");
        testPromotion.setDescription("Great summer discount");
        testPromotion.setDiscountValue(25.0);
        testPromotion.setStartDate(LocalDate.now());
        testPromotion.setEndDate(LocalDate.now().plusDays(30));
        testPromotion.setStatus(activeStatus);
        testPromotion.setUser(testUser);
        testPromotion.setCategory(testCategory);
    }

    // === TESTS FROM ORIGINAL FILE ===

    @Test
    void health_ShouldReturnHealthMessage() {
        // When
        String result = graphQLResolver.health();

        // Then
        assertNotNull(result);
        assertTrue(result.contains("GraphQL API is running!"));
    }

    @Test
    void promotions_ShouldReturnAllPromotions() {
        // Given
        List<Promotion> promotions = Arrays.asList(testPromotion);
        when(promotionRepository.findAll()).thenReturn(promotions);

        // When
        List<Promotion> result = graphQLResolver.promotions();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Summer Sale", result.get(0).getPromotionName());
    }

    @Test
    void promotions_WhenException_ShouldReturnEmptyList() {
        // Given
        when(promotionRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        // When
        List<Promotion> result = graphQLResolver.promotions();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // === ADDITIONAL TESTS FOR BETTER COVERAGE ===

    @Test
    void promotionsActive_ShouldReturnActivePromotions() {
        // Given
        List<Promotion> activePromotions = Arrays.asList(testPromotion);
        when(promotionService.getAllActivePromotionsEntities()).thenReturn(activePromotions);

        // When
        List<Promotion> result = graphQLResolver.promotionsActive();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Summer Sale", result.get(0).getPromotionName());
    }

    @Test
    void promotionsActive_WhenException_ShouldReturnEmptyList() {
        // Given
        when(promotionService.getAllActivePromotionsEntities()).thenThrow(new RuntimeException("Service error"));

        // When
        List<Promotion> result = graphQLResolver.promotionsActive();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }


    @Test
    void promotionsScheduled_ServiceException_ShouldReturnEmptyList() {
        // Given
        when(promotionService.getAllScheduledPromotionsEntities()).thenThrow(new RuntimeException("Service error"));

        // When
        List<Promotion> result = graphQLResolver.promotionsScheduled();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void promotionsByStatus_ShouldReturnPromotionsByStatus_Test() {
        // Given
        List<Promotion> promotions = Arrays.asList(testPromotion);
        when(promotionService.getPromotionsByStatusEntities("ACTIVE")).thenReturn(promotions);

        // When
        List<Promotion> result = graphQLResolver.promotionsByStatus("ACTIVE");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Summer Sale", result.get(0).getPromotionName());
    }

    @Test
    void promotion_ShouldReturnSinglePromotion() {
        // Given
        when(promotionRepository.findById(1)).thenReturn(Optional.of(testPromotion));

        // When
        Promotion result = graphQLResolver.promotion(1);

        // Then
        assertNotNull(result);
        assertEquals("Summer Sale", result.getPromotionName());
    }

    @Test
    void promotion_WhenNotFound_ShouldReturnNull() {
        // Given
        when(promotionRepository.findById(999)).thenReturn(Optional.empty());

        // When
        Promotion result = graphQLResolver.promotion(999);

        // Then
        assertEquals(null, result);
    }

    @Test
    void categories_ShouldReturnAllCategories() {
        // Given
        List<Category> categories = Arrays.asList(testCategory);
        when(categoryRepository.findAll()).thenReturn(categories);

        // When
        List<Category> result = graphQLResolver.categories();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Electronics", result.get(0).getCategoryName());
    }

    @Test
    void category_ShouldReturnSingleCategory() {
        // Given
        when(categoryRepository.findById(1)).thenReturn(Optional.of(testCategory));

        // When
        Category result = graphQLResolver.category(1);

        // Then
        assertNotNull(result);
        assertEquals("Electronics", result.getCategoryName());
    }

    @Test
    void products_ShouldReturnAllProducts() {
        // Given
        List<Product> products = Arrays.asList(testProduct);
        when(productRepository.findAll()).thenReturn(products);

        // When
        List<Product> result = graphQLResolver.products();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Smartphone", result.get(0).getProductName());
    }

    @Test
    void product_ShouldReturnSingleProduct() {
        // Given
        when(productRepository.findById(1)).thenReturn(Optional.of(testProduct));

        // When
        Product result = graphQLResolver.product(1);

        // Then
        assertNotNull(result);
        assertEquals("Smartphone", result.getProductName());
    }

    @Test
    void currentUser_WhenAuthenticated_ShouldReturnUser() {
        // Given
        setupAuthenticatedUser();
        when(userRepository.findByEmail("admin@petstore.com")).thenReturn(Optional.of(testUser));

        // When
        User result = graphQLResolver.currentUser();

        // Then
        assertNotNull(result);
        assertEquals("admin@petstore.com", result.getEmail());
    }

    @Test
    void currentUser_WhenNotAuthenticated_ShouldThrowException() {
        // Given
        setupUnauthenticatedUser();

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            graphQLResolver.currentUser();
        });
        assertTrue(exception.getMessage().contains("Authentication required"));
    }

    @Test
    void login_WithValidCredentials_ShouldReturnLoginResponse() {
        // Given
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setSuccess(true);
        loginResponse.setToken("valid-token");
        loginResponse.setMessage("Login successful");

        when(authService.authenticateMarketingAdmin(anyString(), anyString()))
                .thenReturn(loginResponse);

        // When
        LoginResponse result = graphQLResolver.login("admin@petstore.com", "password");

        // Then
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("valid-token", result.getToken());
    }

    @Test
    void login_WithInvalidCredentials_ShouldReturnErrorResponse() {
        // Given
        when(authService.authenticateMarketingAdmin(anyString(), anyString()))
                .thenThrow(new RuntimeException("Invalid credentials"));

        // When
        LoginResponse result = graphQLResolver.login("admin@petstore.com", "wrongpassword");

        // Then
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Invalid credentials") || result.getMessage().contains("authentication"));
    }

    @Test
    void createPromotion_WhenAuthenticated_ShouldCreatePromotion() {
        // Given
        setupAuthenticatedUser();
        when(userRepository.findByEmail("admin@petstore.com")).thenReturn(Optional.of(testUser));
        
        com.petstore.backend.dto.PromotionDTO promotionDTO = new com.petstore.backend.dto.PromotionDTO();
        promotionDTO.setPromotionName("Summer Sale");
        promotionDTO.setDescription("Great discount");
        promotionDTO.setStartDate(java.time.LocalDate.now());
        promotionDTO.setEndDate(java.time.LocalDate.now().plusDays(30));
        promotionDTO.setDiscountPercentage(java.math.BigDecimal.valueOf(20.0));
        promotionDTO.setCategoryId(1);
        promotionDTO.setStatusId(1);
        promotionDTO.setUserId(1);
        
        when(promotionService.createPromotion(anyString(), anyString(), any(), any(), any(), any(), any(), any()))
                .thenReturn(testPromotion);

        // When
        Promotion result = graphQLResolver.createPromotion(promotionDTO);

        // Then
        assertNotNull(result);
        assertEquals("Summer Sale", result.getPromotionName());
    }

    @Test
    void createPromotion_WhenNotAuthenticated_ShouldThrowException() {
        // Given
        setupUnauthenticatedUser();
        com.petstore.backend.dto.PromotionDTO promotionDTO = new com.petstore.backend.dto.PromotionDTO();
        promotionDTO.setPromotionName("Summer Sale");

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            graphQLResolver.createPromotion(promotionDTO);
        });
        assertTrue(exception.getMessage().contains("Authentication required"));
    }

    private void setupAuthenticatedUser() {
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("admin@petstore.com");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    private void setupUnauthenticatedUser() {
        when(authentication.isAuthenticated()).thenReturn(false);
        when(authentication.getName()).thenReturn("anonymousUser");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    // === ADDITIONAL COMPREHENSIVE TESTS FOR FULL COVERAGE ===

    @Test
    void promotionsExpired_ShouldReturnExpiredPromotions() {
        // Given
        List<Promotion> expiredPromotions = Arrays.asList(testPromotion);
        when(promotionService.getAllExpiredPromotionsEntities()).thenReturn(expiredPromotions);

        // When
        List<Promotion> result = graphQLResolver.promotionsExpired();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Summer Sale", result.get(0).getPromotionName());
    }

    @Test
    void promotionsExpired_WhenException_ShouldReturnEmptyList() {
        // Given
        when(promotionService.getAllExpiredPromotionsEntities()).thenThrow(new RuntimeException("Database error"));

        // When
        List<Promotion> result = graphQLResolver.promotionsExpired();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void promotionsScheduled_ShouldReturnScheduledPromotions() {
        // Given
        List<Promotion> scheduledPromotions = Arrays.asList(testPromotion);
        when(promotionService.getAllScheduledPromotionsEntities()).thenReturn(scheduledPromotions);

        // When
        List<Promotion> result = graphQLResolver.promotionsScheduled();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Summer Sale", result.get(0).getPromotionName());
    }


    @Test
    void promotionsByStatus_WhenException_ShouldReturnEmptyList() {
        // Given
        when(promotionService.getPromotionsByStatusEntities("ACTIVE")).thenThrow(new RuntimeException("Database error"));

        // When
        List<Promotion> result = graphQLResolver.promotionsByStatus("ACTIVE");

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void promotionsByCategory_ShouldReturnPromotionsByCategory() {
        // Given
        List<Promotion> promotionsByCategory = Arrays.asList(testPromotion);
        when(promotionService.getPromotionsByCategoryEntities(1)).thenReturn(promotionsByCategory);

        // When
        List<Promotion> result = graphQLResolver.promotionsByCategory(1);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Summer Sale", result.get(0).getPromotionName());
    }

    @Test
    void promotionsByCategory_WhenException_ShouldReturnEmptyList() {
        // Given
        when(promotionService.getPromotionsByCategoryEntities(1)).thenThrow(new RuntimeException("Database error"));

        // When
        List<Promotion> result = graphQLResolver.promotionsByCategory(1);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void categories_WhenException_ShouldReturnEmptyList() {
        // Given
        when(categoryRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        // When
        List<Category> result = graphQLResolver.categories();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void category_WhenException_ShouldReturnNull() {
        // Given
        when(categoryRepository.findById(1)).thenThrow(new RuntimeException("Database error"));

        // When
        Category result = graphQLResolver.category(1);

        // Then
        assertEquals(null, result);
    }

    @Test
    void category_WhenNotFound_ShouldReturnNull() {
        // Given
        when(categoryRepository.findById(999)).thenReturn(Optional.empty());

        // When
        Category result = graphQLResolver.category(999);

        // Then
        assertEquals(null, result);
    }

    @Test
    void products_WhenException_ShouldReturnEmptyList() {
        // Given
        when(productRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        // When
        List<Product> result = graphQLResolver.products();

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void productsByCategory_ShouldReturnProductsByCategory() {
        // Given
        List<Product> productsByCategory = Arrays.asList(testProduct);
        when(productRepository.findByCategoryCategoryId(1)).thenReturn(productsByCategory);

        // When
        List<Product> result = graphQLResolver.productsByCategory(1);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Smartphone", result.get(0).getProductName());
    }

    @Test
    void productsByCategory_WhenException_ShouldReturnEmptyList() {
        // Given
        when(productRepository.findByCategoryCategoryId(1)).thenThrow(new RuntimeException("Database error"));

        // When
        List<Product> result = graphQLResolver.productsByCategory(1);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void product_WhenException_ShouldReturnNull() {
        // Given
        when(productRepository.findById(1)).thenThrow(new RuntimeException("Database error"));

        // When
        Product result = graphQLResolver.product(1);

        // Then
        assertEquals(null, result);
    }

    @Test
    void product_WhenNotFound_ShouldReturnNull() {
        // Given
        when(productRepository.findById(999)).thenReturn(Optional.empty());

        // When
        Product result = graphQLResolver.product(999);

        // Then
        assertEquals(null, result);
    }

    @Test
    void updatePromotion_WhenAuthenticated_ShouldUpdatePromotion() {
        // Given
        setupAuthenticatedUser();
        when(userRepository.findByEmail("admin@petstore.com")).thenReturn(Optional.of(testUser));
        
        com.petstore.backend.dto.PromotionDTO promotionDTO = new com.petstore.backend.dto.PromotionDTO();
        promotionDTO.setPromotionName("Updated Summer Sale");
        promotionDTO.setDescription("Updated discount");
        promotionDTO.setStartDate(java.time.LocalDate.now());
        promotionDTO.setEndDate(java.time.LocalDate.now().plusDays(30));
        promotionDTO.setDiscountPercentage(java.math.BigDecimal.valueOf(25.0));
        
        when(promotionService.updatePromotion(any(), anyString(), anyString(), any(), any(), any(), any(), any(), any()))
                .thenReturn(testPromotion);

        // When
        Promotion result = graphQLResolver.updatePromotion(1, promotionDTO);

        // Then
        assertNotNull(result);
        assertEquals("Summer Sale", result.getPromotionName());
    }

    @Test
    void updatePromotion_WhenNotAuthenticated_ShouldThrowException() {
        // Given
        setupUnauthenticatedUser();
        com.petstore.backend.dto.PromotionDTO promotionDTO = new com.petstore.backend.dto.PromotionDTO();

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            graphQLResolver.updatePromotion(1, promotionDTO);
        });
        assertTrue(exception.getMessage().contains("Authentication required"));
    }

    @Test
    void updatePromotion_WhenServiceThrowsException_ShouldThrowRuntimeException() {
        // Given
        setupAuthenticatedUser();
        com.petstore.backend.dto.PromotionDTO promotionDTO = new com.petstore.backend.dto.PromotionDTO();
        promotionDTO.setPromotionName("Updated Sale");
        promotionDTO.setDescription("Updated description");
        promotionDTO.setStartDate(java.time.LocalDate.now());
        promotionDTO.setEndDate(java.time.LocalDate.now().plusDays(30));
        promotionDTO.setDiscountPercentage(java.math.BigDecimal.valueOf(25.0));
        
        // Create a category DTO to avoid null pointer
        com.petstore.backend.dto.CategoryDTO categoryDTO = new com.petstore.backend.dto.CategoryDTO();
        categoryDTO.setCategoryId(1);
        promotionDTO.setCategory(categoryDTO);
        
        when(promotionService.updatePromotion(any(Integer.class), anyString(), anyString(), 
                any(java.time.LocalDate.class), any(java.time.LocalDate.class), any(Double.class), 
                any(Integer.class), any(Integer.class), any(Integer.class)))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            graphQLResolver.updatePromotion(1, promotionDTO);
        });
        assertTrue(exception.getMessage().contains("Failed to update promotion"));
    }

    @Test
    void deletePromotion_WhenAuthenticated_ShouldDeletePromotion() {
        // Given
        setupAuthenticatedUser();
        when(promotionService.deletePromotion(1, 1)).thenReturn(true);

        // When
        Boolean result = graphQLResolver.deletePromotion(1, 1);

        // Then
        assertNotNull(result);
        assertTrue(result);
    }

    @Test
    void deletePromotion_WhenNotAuthenticated_ShouldThrowException() {
        // Given
        setupUnauthenticatedUser();

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            graphQLResolver.deletePromotion(1, 1);
        });
        assertTrue(exception.getMessage().contains("Authentication required"));
    }

    @Test
    void deletePromotion_WhenPromotionNotFound_ShouldThrowException() {
        // Given
        setupAuthenticatedUser();
        when(promotionService.deletePromotion(999, 1)).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            graphQLResolver.deletePromotion(999, 1);
        });
        assertTrue(exception.getMessage().contains("Promotion not found"));
    }

    @Test
    void deletePromotion_WhenServiceThrowsException_ShouldThrowGraphQLException() {
        // Given
        setupAuthenticatedUser();
        when(promotionService.deletePromotion(1, 1)).thenThrow(new RuntimeException("Database error"));

        // When & Then
        GraphQLException exception = assertThrows(GraphQLException.class, () -> {
            graphQLResolver.deletePromotion(1, 1);
        });
        assertEquals("DELETE", exception.getOperation());
        assertTrue(exception.getMessage().contains("Unexpected error during deletion"));
    }

    @Test
    void deletedPromotions_WhenAuthenticated_ShouldReturnDeletedPromotions() {
        // Given
        setupAuthenticatedUser();
        com.petstore.backend.dto.PromotionDeletedDTO deletedPromo = new com.petstore.backend.dto.PromotionDeletedDTO();
        deletedPromo.setPromotionName("Deleted Promo");
        List<com.petstore.backend.dto.PromotionDeletedDTO> deletedPromotions = Arrays.asList(deletedPromo);
        when(promotionService.getDeletedPromotions()).thenReturn(deletedPromotions);

        // When
        List<com.petstore.backend.dto.PromotionDeletedDTO> result = graphQLResolver.deletedPromotions();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Deleted Promo", result.get(0).getPromotionName());
    }

    @Test
    void deletedPromotions_WhenNotAuthenticated_ShouldThrowException() {
        // Given
        setupUnauthenticatedUser();

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            graphQLResolver.deletedPromotions();
        });
        assertTrue(exception.getMessage().contains("Authentication required"));
    }

    @Test
    void deletedPromotions_WhenServiceThrowsException_ShouldThrowRuntimeException() {
        // Given
        setupAuthenticatedUser();
        when(promotionService.getDeletedPromotions()).thenThrow(new RuntimeException("Database error"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            graphQLResolver.deletedPromotions();
        });
        assertTrue(exception.getMessage().contains("Failed to fetch deleted promotions"));
    }

    @Test
    void deletedPromotionsByUser_WhenAuthenticated_ShouldReturnDeletedPromotionsByUser() {
        // Given
        setupAuthenticatedUser();
        com.petstore.backend.dto.PromotionDeletedDTO deletedPromo = new com.petstore.backend.dto.PromotionDeletedDTO();
        deletedPromo.setPromotionName("User Deleted Promo");
        List<com.petstore.backend.dto.PromotionDeletedDTO> deletedPromotions = Arrays.asList(deletedPromo);
        when(promotionService.getDeletedPromotionsByUser(1)).thenReturn(deletedPromotions);

        // When
        List<com.petstore.backend.dto.PromotionDeletedDTO> result = graphQLResolver.deletedPromotionsByUser("1");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("User Deleted Promo", result.get(0).getPromotionName());
    }

    @Test
    void deletedPromotionsByUser_WhenNotAuthenticated_ShouldThrowException() {
        // Given
        setupUnauthenticatedUser();

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            graphQLResolver.deletedPromotionsByUser("1");
        });
        assertTrue(exception.getMessage().contains("Authentication required"));
    }

    @Test
    void deletedPromotionsByUser_WhenInvalidUserId_ShouldThrowException() {
        // Given
        setupAuthenticatedUser();

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            graphQLResolver.deletedPromotionsByUser("invalid");
        });
        assertTrue(exception.getMessage().contains("Invalid userId format"));
    }

    @Test
    void deletedPromotionsByUser_WhenServiceThrowsException_ShouldThrowRuntimeException() {
        // Given
        setupAuthenticatedUser();
        when(promotionService.getDeletedPromotionsByUser(1)).thenThrow(new RuntimeException("Database error"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            graphQLResolver.deletedPromotionsByUser("1");
        });
        assertTrue(exception.getMessage().contains("Failed to fetch deleted promotions by user"));
    }

    @Test
    void restorePromotion_WhenAuthenticated_ShouldRestorePromotion() {
        // Given
        setupAuthenticatedUser();
        when(promotionService.restorePromotionUsingDBFunction(1, 1)).thenReturn(true);

        // When
        Boolean result = graphQLResolver.restorePromotion(1, 1);

        // Then
        assertNotNull(result);
        assertTrue(result);
    }

    @Test
    void restorePromotion_WhenNotAuthenticated_ShouldThrowException() {
        // Given
        setupUnauthenticatedUser();

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            graphQLResolver.restorePromotion(1, 1);
        });
        assertTrue(exception.getMessage().contains("Authentication required"));
    }

    @Test
    void restorePromotion_WhenPromotionNotFound_ShouldThrowGraphQLException() {
        // Given
        setupAuthenticatedUser();
        when(promotionService.restorePromotionUsingDBFunction(999, 1)).thenReturn(false);

        // When & Then
        GraphQLException exception = assertThrows(GraphQLException.class, () -> {
            graphQLResolver.restorePromotion(999, 1);
        });
        assertEquals("RESTORE", exception.getOperation());
        assertTrue(exception.getMessage().contains("Promotion not found or could not be restored"));
    }

    @Test
    void restorePromotion_WhenServiceThrowsException_ShouldThrowGraphQLException() {
        // Given
        setupAuthenticatedUser();
        when(promotionService.restorePromotionUsingDBFunction(1, 1)).thenThrow(new RuntimeException("Database error"));

        // When & Then
        GraphQLException exception = assertThrows(GraphQLException.class, () -> {
            graphQLResolver.restorePromotion(1, 1);
        });
        assertEquals("RESTORE", exception.getOperation());
        assertTrue(exception.getMessage().contains("Unexpected error during restoration"));
    }

    // === SCHEMA MAPPING TESTS ===

    @Test
    void promotionProducts_ShouldReturnProductsForPromotion() {
        // Given
        List<Product> products = Arrays.asList(testProduct);
        when(productRepository.findByPromotionPromotionId(1)).thenReturn(products);

        // When
        List<Product> result = graphQLResolver.promotionProducts(testPromotion);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Smartphone", result.get(0).getProductName());
    }

    @Test
    void promotionProducts_WhenException_ShouldReturnEmptyList() {
        // Given
        when(productRepository.findByPromotionPromotionId(anyInt())).thenThrow(new RuntimeException("Database error"));

        // When
        List<Product> result = graphQLResolver.promotionProducts(testPromotion);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void categoryPromotions_ShouldReturnPromotionsForCategory() {
        // Given
        List<Promotion> promotions = Arrays.asList(testPromotion);
        when(promotionService.getPromotionsByCategoryEntities(1)).thenReturn(promotions);

        // When
        List<Promotion> result = graphQLResolver.categoryPromotions(testCategory);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Summer Sale", result.get(0).getPromotionName());
    }

    @Test
    void categoryPromotions_WhenException_ShouldReturnEmptyList() {
        // Given
        when(promotionService.getPromotionsByCategoryEntities(1)).thenThrow(new RuntimeException("Database error"));

        // When
        List<Promotion> result = graphQLResolver.categoryPromotions(testCategory);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void categoryProducts_ShouldReturnProductsForCategory() {
        // Given
        List<Product> products = Arrays.asList(testProduct);
        when(productRepository.findByCategoryCategoryId(1)).thenReturn(products);

        // When
        List<Product> result = graphQLResolver.categoryProducts(testCategory);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Smartphone", result.get(0).getProductName());
    }

    @Test
    void categoryProducts_WhenException_ShouldReturnEmptyList() {
        // Given
        when(productRepository.findByCategoryCategoryId(1)).thenThrow(new RuntimeException("Database error"));

        // When
        List<Product> result = graphQLResolver.categoryProducts(testCategory);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void createPromotion_WhenServiceThrowsException_ShouldThrowRuntimeException() {
        // Given
        setupAuthenticatedUser();
        com.petstore.backend.dto.PromotionDTO promotionDTO = new com.petstore.backend.dto.PromotionDTO();
        promotionDTO.setPromotionName("Failed Sale");
        promotionDTO.setDescription("Failed description");
        promotionDTO.setStartDate(java.time.LocalDate.now());
        promotionDTO.setEndDate(java.time.LocalDate.now().plusDays(30));
        promotionDTO.setDiscountPercentage(java.math.BigDecimal.valueOf(20.0));
        
        // Create a category DTO to avoid null pointer
        com.petstore.backend.dto.CategoryDTO categoryDTO = new com.petstore.backend.dto.CategoryDTO();
        categoryDTO.setCategoryId(1);
        promotionDTO.setCategory(categoryDTO);
        
        when(promotionService.createPromotion(anyString(), anyString(), 
                any(java.time.LocalDate.class), any(java.time.LocalDate.class), any(Double.class), 
                any(Integer.class), any(Integer.class), any(Integer.class)))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            graphQLResolver.createPromotion(promotionDTO);
        });
        assertTrue(exception.getMessage().contains("Failed to create promotion"));
    }

    @Test
    void associateProductsToPromotion_WhenAuthenticated_ShouldAssociateProducts() {
        // Given
        setupAuthenticatedUser();
        when(userRepository.findByEmail("admin@petstore.com")).thenReturn(Optional.of(testUser));
        when(promotionService.associateProductsToPromotion(1, Arrays.asList(1, 2))).thenReturn(true);
        when(promotionRepository.findById(1)).thenReturn(Optional.of(testPromotion));

        // When
        Promotion result = graphQLResolver.associateProductsToPromotion(1, Arrays.asList(1, 2));

        // Then
        assertNotNull(result);
        assertEquals("Summer Sale", result.getPromotionName());
    }

    @Test
    void associateProductsToPromotion_WhenNotAuthenticated_ShouldThrowException() {
        // Given
        SecurityContextHolder.clearContext();

        // When & Then
        assertThrows(GraphQLException.class, () -> {
            graphQLResolver.associateProductsToPromotion(1, Arrays.asList(1, 2));
        });
    }

    @Test
    void associateProductsToPromotion_WhenServiceFails_ShouldThrowException() {
        // Given
        setupAuthenticatedUser();
        when(userRepository.findByEmail("admin@petstore.com")).thenReturn(Optional.of(testUser));
        when(promotionService.associateProductsToPromotion(1, Arrays.asList(1, 2))).thenReturn(false);

        // When & Then
        assertThrows(GraphQLException.class, () -> {
            graphQLResolver.associateProductsToPromotion(1, Arrays.asList(1, 2));
        });
    }

    @Test
    void associateProductsToPromotion_WhenPromotionNotFound_ShouldThrowException() {
        // Given
        setupAuthenticatedUser();
        when(userRepository.findByEmail("admin@petstore.com")).thenReturn(Optional.of(testUser));
        when(promotionService.associateProductsToPromotion(1, Arrays.asList(1, 2))).thenReturn(true);
        when(promotionRepository.findById(1)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(GraphQLException.class, () -> {
            graphQLResolver.associateProductsToPromotion(1, Arrays.asList(1, 2));
        });
    }

    @Test
    void removeProductsFromPromotion_WhenAuthenticated_ShouldRemoveProducts() {
        // Given
        setupAuthenticatedUser();
        when(userRepository.findByEmail("admin@petstore.com")).thenReturn(Optional.of(testUser));
        when(promotionService.removeProductsFromPromotion(1, Arrays.asList(1, 2))).thenReturn(true);
        when(promotionRepository.findById(1)).thenReturn(Optional.of(testPromotion));

        // When
        Promotion result = graphQLResolver.removeProductsFromPromotion(1, Arrays.asList(1, 2));

        // Then
        assertNotNull(result);
        assertEquals("Summer Sale", result.getPromotionName());
    }

    @Test
    void removeProductsFromPromotion_WhenNotAuthenticated_ShouldThrowException() {
        // Given
        SecurityContextHolder.clearContext();

        // When & Then
        assertThrows(GraphQLException.class, () -> {
            graphQLResolver.removeProductsFromPromotion(1, Arrays.asList(1, 2));
        });
    }

    @Test
    void removeProductsFromPromotion_WhenServiceFails_ShouldThrowException() {
        // Given
        setupAuthenticatedUser();
        when(userRepository.findByEmail("admin@petstore.com")).thenReturn(Optional.of(testUser));
        when(promotionService.removeProductsFromPromotion(1, Arrays.asList(1, 2))).thenReturn(false);

        // When & Then
        assertThrows(GraphQLException.class, () -> {
            graphQLResolver.removeProductsFromPromotion(1, Arrays.asList(1, 2));
        });
    }

    @Test
    void permanentDeletePromotion_WhenAuthenticated_ShouldDeletePermanently() {
        // Given
        setupAuthenticatedUser();
        when(userRepository.findByEmail("admin@petstore.com")).thenReturn(Optional.of(testUser));
        when(promotionService.permanentDeletePromotion(1, 1)).thenReturn(true);

        // When
        Boolean result = graphQLResolver.permanentDeletePromotion(1, 1);

        // Then
        assertTrue(result);
    }

    @Test
    void permanentDeletePromotion_WhenNotAuthenticated_ShouldThrowException() {
        // Given
        SecurityContextHolder.clearContext();

        // When & Then
        assertThrows(GraphQLException.class, () -> {
            graphQLResolver.permanentDeletePromotion(1, 1);
        });
    }

    @Test
    void permanentDeletePromotion_WhenServiceFails_ShouldThrowException() {
        // Given
        setupAuthenticatedUser();
        when(userRepository.findByEmail("admin@petstore.com")).thenReturn(Optional.of(testUser));
        when(promotionService.permanentDeletePromotion(1, 1)).thenReturn(false);

        // When & Then
        assertThrows(GraphQLException.class, () -> {
            graphQLResolver.permanentDeletePromotion(1, 1);
        });
    }

    @Test
    void updatePromotion_WithValidInput_ShouldUpdatePromotion() {
        // Given
        setupAuthenticatedUser();
        when(userRepository.findByEmail("admin@petstore.com")).thenReturn(Optional.of(testUser));
        
        com.petstore.backend.dto.PromotionDTO promotionDTO = new com.petstore.backend.dto.PromotionDTO();
        promotionDTO.setPromotionName("Updated Sale");
        promotionDTO.setDescription("Updated description");
        promotionDTO.setStartDate(LocalDate.now());
        promotionDTO.setEndDate(LocalDate.now().plusDays(30));
        promotionDTO.setDiscountPercentage(java.math.BigDecimal.valueOf(25.0));
        promotionDTO.setCategoryId(1);
        promotionDTO.setStatusId(1);
        promotionDTO.setUserId(1);

        when(promotionService.updatePromotion(any(Integer.class), anyString(), anyString(), 
                any(LocalDate.class), any(LocalDate.class), any(Double.class), 
                any(Integer.class), any(Integer.class), any(Integer.class)))
                .thenReturn(testPromotion);

        // When
        Promotion result = graphQLResolver.updatePromotion(1, promotionDTO);

        // Then
        assertNotNull(result);
        assertEquals("Summer Sale", result.getPromotionName());
    }

    @Test
    void updatePromotion_WithMissingFields_ShouldThrowException() {
        // Given
        setupAuthenticatedUser();
        when(userRepository.findByEmail("admin@petstore.com")).thenReturn(Optional.of(testUser));
        
        com.petstore.backend.dto.PromotionDTO promotionDTO = new com.petstore.backend.dto.PromotionDTO();
        promotionDTO.setPromotionName("Updated Sale");
        // Missing categoryId, statusId, userId

        // When & Then
        assertThrows(GraphQLException.class, () -> {
            graphQLResolver.updatePromotion(1, promotionDTO);
        });
    }

    @Test
    void health_ShouldReturnHealthyMessage() {
        // When
        String result = graphQLResolver.health();

        // Then
        assertEquals("GraphQL API is running!", result);
    }

    @Test
    void promotionProducts_WhenSuccessful_ShouldReturnProducts() {
        // Given
        Product product1 = new Product();
        product1.setProductId(1);
        product1.setProductName("Product 1");
        
        when(productRepository.findByPromotionPromotionId(1))
                .thenReturn(Arrays.asList(product1));

        // When
        List<Product> result = graphQLResolver.promotionProducts(testPromotion);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Product 1", result.get(0).getProductName());
    }
}
