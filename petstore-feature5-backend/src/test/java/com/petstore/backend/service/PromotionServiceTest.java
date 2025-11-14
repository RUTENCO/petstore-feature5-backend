package com.petstore.backend.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.petstore.backend.dto.PromotionDTO;
import com.petstore.backend.dto.PromotionDeletedDTO;
import com.petstore.backend.entity.Category;
import com.petstore.backend.entity.Product;
import com.petstore.backend.entity.Promotion;
import com.petstore.backend.entity.PromotionDeleted;
import com.petstore.backend.entity.Status;
import com.petstore.backend.entity.User;
import com.petstore.backend.repository.CategoryRepository;
import com.petstore.backend.repository.ProductRepository;
import com.petstore.backend.repository.PromotionDeletedRepository;
import com.petstore.backend.repository.PromotionRepository;
import com.petstore.backend.repository.StatusRepository;
import com.petstore.backend.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class PromotionServiceTest {

    @Mock
    private PromotionRepository promotionRepository;
    
    @Mock
    private StatusRepository statusRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private CategoryRepository categoryRepository;
    
    @Mock
    private PromotionDeletedRepository promotionDeletedRepository;
    
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private PromotionService promotionService;

    private Promotion testPromotion;
    private Category testCategory;
    private Status testStatus;
    private User testUser;
    private PromotionDeleted testPromotionDeleted;

    @BeforeEach
    void setUp() {
        // Setup test entities
        testCategory = new Category();
        testCategory.setCategoryId(1);
        testCategory.setCategoryName("Test Category");
        testCategory.setDescription("Test Description");

        testStatus = new Status();
        testStatus.setStatusId(1);
        testStatus.setStatusName("ACTIVE");

        testUser = new User();
        testUser.setUserId(1);
        testUser.setEmail("test@test.com");

        testPromotion = new Promotion();
        testPromotion.setPromotionId(1);
        testPromotion.setPromotionName("Test Promotion");
        testPromotion.setDescription("Test Description");
        testPromotion.setStartDate(LocalDate.now().minusDays(1));
        testPromotion.setEndDate(LocalDate.now().plusDays(10));
        testPromotion.setDiscountValue(20.0);
        testPromotion.setStatus(testStatus);
        testPromotion.setUser(testUser);
        testPromotion.setCategory(testCategory);

        testPromotionDeleted = new PromotionDeleted();
        testPromotionDeleted.setPromotionId(1);
        testPromotionDeleted.setPromotionName("Deleted Promotion");
        testPromotionDeleted.setDescription("Deleted Description");
        testPromotionDeleted.setStartDate(LocalDate.now().minusDays(5));
        testPromotionDeleted.setEndDate(LocalDate.now().plusDays(5));
        testPromotionDeleted.setDiscountValue(15.0);
        testPromotionDeleted.setStatus(testStatus);
        testPromotionDeleted.setUser(testUser);
        testPromotionDeleted.setCategory(testCategory);
        testPromotionDeleted.setDeletedAt(ZonedDateTime.now().minusDays(1));
        testPromotionDeleted.setDeletedBy(testUser);
    }

    @Test
    void getAllActivePromotions_ShouldReturnCurrentActivePromotions() {
        // Given
        List<Promotion> activePromotions = Arrays.asList(testPromotion);
        when(promotionRepository.findActivePromotions()).thenReturn(activePromotions);

        // When
        List<PromotionDTO> result = promotionService.getAllActivePromotions();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Promotion", result.get(0).getPromotionName());
        assertEquals(BigDecimal.valueOf(20.0), result.get(0).getDiscountPercentage());
        verify(promotionRepository).findActivePromotions();
    }

    @Test
    void getAllActivePromotions_ShouldFilterExpiredPromotions() {
        // Given
        Promotion expiredPromotion = new Promotion();
        expiredPromotion.setPromotionId(2);
        expiredPromotion.setPromotionName("Expired Promotion");
        expiredPromotion.setStartDate(LocalDate.now().minusDays(20));
        expiredPromotion.setEndDate(LocalDate.now().minusDays(1)); // Expired yesterday
        expiredPromotion.setDiscountValue(10.0);

        List<Promotion> activePromotions = Arrays.asList(testPromotion, expiredPromotion);
        when(promotionRepository.findActivePromotions()).thenReturn(activePromotions);

        // When
        List<PromotionDTO> result = promotionService.getAllActivePromotions();

        // Then
        assertEquals(1, result.size());
        assertEquals("Test Promotion", result.get(0).getPromotionName());
    }

    @Test
    void getAllPromotions_ShouldReturnAllPromotions() {
        // Given
        List<Promotion> allPromotions = Arrays.asList(testPromotion);
        when(promotionRepository.findAll()).thenReturn(allPromotions);

        // When
        List<PromotionDTO> result = promotionService.getAllPromotions();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Promotion", result.get(0).getPromotionName());
        verify(promotionRepository).findAll();
    }

    @Test
    void getPromotionsByCategory_ShouldReturnPromotionsForCategory() {
        // Given
        Integer categoryId = 1;
        List<Promotion> promotions = Arrays.asList(testPromotion);
        when(promotionRepository.findByCategoryCategoryId(categoryId)).thenReturn(promotions);

        // When
        List<PromotionDTO> result = promotionService.getPromotionsByCategory(categoryId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Promotion", result.get(0).getPromotionName());
        verify(promotionRepository).findByCategoryCategoryId(categoryId);
    }

    @Test
    void getValidPromotions_ShouldReturnValidPromotions() {
        // Given
        List<Promotion> validPromotions = Arrays.asList(testPromotion);
        when(promotionRepository.findValidPromotions(any(LocalDate.class))).thenReturn(validPromotions);

        // When
        List<PromotionDTO> result = promotionService.getValidPromotions();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Promotion", result.get(0).getPromotionName());
        verify(promotionRepository).findValidPromotions(any(LocalDate.class));
    }

    @Test
    void getAllActivePromotionsEntities_ShouldReturnActivePromotionEntities() {
        // Given
        List<Promotion> activePromotions = Arrays.asList(testPromotion);
        when(promotionRepository.findActivePromotions()).thenReturn(activePromotions);

        // When
        List<Promotion> result = promotionService.getAllActivePromotionsEntities();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Promotion", result.get(0).getPromotionName());
        verify(promotionRepository).findActivePromotions();
    }

    @Test
    void getAllExpiredPromotionsEntities_ShouldReturnExpiredPromotions() {
        // Given
        List<Promotion> expiredPromotions = Arrays.asList(testPromotion);
        when(promotionRepository.findExpiredPromotions()).thenReturn(expiredPromotions);

        // When
        List<Promotion> result = promotionService.getAllExpiredPromotionsEntities();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(promotionRepository).findExpiredPromotions();
    }

    @Test
    void getAllScheduledPromotionsEntities_ShouldReturnScheduledPromotions() {
        // Given
        List<Promotion> scheduledPromotions = Arrays.asList(testPromotion);
        when(promotionRepository.findScheduledPromotions()).thenReturn(scheduledPromotions);

        // When
        List<Promotion> result = promotionService.getAllScheduledPromotionsEntities();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(promotionRepository).findScheduledPromotions();
    }

    @Test
    void getPromotionsByStatusEntities_ShouldReturnPromotionsByStatus() {
        // Given
        String statusName = "ACTIVE";
        List<Promotion> promotions = Arrays.asList(testPromotion);
        when(promotionRepository.findByStatusName(statusName)).thenReturn(promotions);

        // When
        List<Promotion> result = promotionService.getPromotionsByStatusEntities(statusName);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(promotionRepository).findByStatusName(statusName);
    }

    @Test
    void getPromotionsByCategoryEntities_ShouldReturnPromotionsByCategory() {
        // Given
        Integer categoryId = 1;
        List<Promotion> promotions = Arrays.asList(testPromotion);
        when(promotionRepository.findByCategoryCategoryId(categoryId)).thenReturn(promotions);

        // When
        List<Promotion> result = promotionService.getPromotionsByCategoryEntities(categoryId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(promotionRepository).findByCategoryCategoryId(categoryId);
    }

    @Test
    void getPromotionByIdEntity_ShouldReturnPromotionWhenExists() {
        // Given
        Integer promotionId = 1;
        when(promotionRepository.findById(promotionId)).thenReturn(Optional.of(testPromotion));

        // When
        Promotion result = promotionService.getPromotionByIdEntity(promotionId);

        // Then
        assertNotNull(result);
        assertEquals("Test Promotion", result.getPromotionName());
        verify(promotionRepository).findById(promotionId);
    }

    @Test
    void getPromotionByIdEntity_ShouldReturnNullWhenNotExists() {
        // Given
        Integer promotionId = 999;
        when(promotionRepository.findById(promotionId)).thenReturn(Optional.empty());

        // When
        Promotion result = promotionService.getPromotionByIdEntity(promotionId);

        // Then
        assertNull(result);
        verify(promotionRepository).findById(promotionId);
    }

    @Test
    void createPromotion_ShouldCreateNewPromotion() {
        // Given
        String promotionName = "New Promotion";
        String description = "New Description";
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(30);
        Double discountValue = 25.0;
        Integer statusId = 1;
        Integer userId = 1;
        Integer categoryId = 1;

        when(statusRepository.findById(statusId)).thenReturn(Optional.of(testStatus));
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(testCategory));
        when(promotionRepository.save(any(Promotion.class))).thenReturn(testPromotion);

        // When
        Promotion result = promotionService.createPromotion(promotionName, description, 
                startDate, endDate, discountValue, statusId, userId, categoryId);

        // Then
        assertNotNull(result);
        verify(statusRepository).findById(statusId);
        verify(userRepository).findById(userId);
        verify(categoryRepository).findById(categoryId);
        verify(promotionRepository).save(any(Promotion.class));
    }

    @Test
    void createPromotion_ShouldHandleNullRelatedEntities() {
        // Given
        String promotionName = "New Promotion";
        String description = "New Description";
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(30);
        Double discountValue = 25.0;

        when(promotionRepository.save(any(Promotion.class))).thenReturn(testPromotion);

        // When
        Promotion result = promotionService.createPromotion(promotionName, description, 
                startDate, endDate, discountValue, null, null, null);

        // Then
        assertNotNull(result);
        verify(promotionRepository).save(any(Promotion.class));
        verifyNoInteractions(statusRepository, userRepository, categoryRepository);
    }

    @Test
    void updatePromotion_ShouldUpdateExistingPromotion() {
        // Given
        Integer promotionId = 1;
        String newName = "Updated Promotion";
        String newDescription = "Updated Description";
        LocalDate newStartDate = LocalDate.now().plusDays(1);
        LocalDate newEndDate = LocalDate.now().plusDays(31);
        Double newDiscountValue = 30.0;
        Integer statusId = 1;
        Integer userId = 1;
        Integer categoryId = 1;

        when(promotionRepository.findById(promotionId)).thenReturn(Optional.of(testPromotion));
        when(statusRepository.findById(statusId)).thenReturn(Optional.of(testStatus));
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(testCategory));
        when(promotionRepository.save(any(Promotion.class))).thenReturn(testPromotion);

        // When
        Promotion result = promotionService.updatePromotion(promotionId, newName, newDescription,
                newStartDate, newEndDate, newDiscountValue, statusId, userId, categoryId);

        // Then
        assertNotNull(result);
        verify(promotionRepository).findById(promotionId);
        verify(promotionRepository).save(any(Promotion.class));
    }

    @Test
    void updatePromotion_ShouldReturnNullWhenPromotionNotExists() {
        // Given
        Integer promotionId = 999;
        when(promotionRepository.findById(promotionId)).thenReturn(Optional.empty());

        // When
        Promotion result = promotionService.updatePromotion(promotionId, "New Name", "New Desc",
                LocalDate.now(), LocalDate.now().plusDays(30), 25.0, 1, 1, 1);

        // Then
        assertNull(result);
        verify(promotionRepository).findById(promotionId);
        verify(promotionRepository, never()).save(any(Promotion.class));
    }

    @Test
    void getDeletedPromotions_ShouldReturnRestorablePromotions() {
        // Given
        List<PromotionDeleted> deletedPromotions = Arrays.asList(testPromotionDeleted);
        when(promotionDeletedRepository.findRestorable(any(ZonedDateTime.class))).thenReturn(deletedPromotions);

        // When
        List<PromotionDeletedDTO> result = promotionService.getDeletedPromotions();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Deleted Promotion", result.get(0).getPromotionName());
        verify(promotionDeletedRepository).findRestorable(any(ZonedDateTime.class));
    }

    @Test
    void getDeletedPromotionsByUser_ShouldReturnDeletedPromotionsByUser() {
        // Given
        Integer userId = 1;
        List<PromotionDeleted> deletedPromotions = Arrays.asList(testPromotionDeleted);
        when(promotionDeletedRepository.findByDeletedByUserId(userId)).thenReturn(deletedPromotions);

        // When
        List<PromotionDeletedDTO> result = promotionService.getDeletedPromotionsByUser(userId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Deleted Promotion", result.get(0).getPromotionName());
        verify(promotionDeletedRepository).findByDeletedByUserId(userId);
    }

    @Test
    void restorePromotion_ShouldRestoreWhenPromotionExistsAndNotExpired() {
        // Given
        Integer promotionId = 1;
        Integer userId = 1;
        when(promotionDeletedRepository.findById(promotionId)).thenReturn(Optional.of(testPromotionDeleted));
        when(promotionRepository.save(any(Promotion.class))).thenReturn(testPromotion);

        // When
        boolean result = promotionService.restorePromotion(promotionId, userId);

        // Then
        assertTrue(result);
        verify(promotionDeletedRepository).findById(promotionId);
        verify(promotionRepository).save(any(Promotion.class));
        verify(promotionDeletedRepository).delete(testPromotionDeleted);
    }

    @Test
    void restorePromotion_ShouldReturnFalseWhenPromotionNotExists() {
        // Given
        Integer promotionId = 999;
        Integer userId = 1;
        when(promotionDeletedRepository.findById(promotionId)).thenReturn(Optional.empty());

        // When
        boolean result = promotionService.restorePromotion(promotionId, userId);

        // Then
        assertFalse(result);
        verify(promotionDeletedRepository).findById(promotionId);
        verify(promotionRepository, never()).save(any(Promotion.class));
    }

    @Test
    void restorePromotion_ShouldReturnFalseWhenPromotionExpired() {
        // Given
        Integer promotionId = 1;
        Integer userId = 1;
        
        // Create expired promotion (deleted more than 30 days ago)
        PromotionDeleted expiredPromotion = new PromotionDeleted();
        expiredPromotion.setPromotionId(1);
        expiredPromotion.setPromotionName("Expired Promotion");
        expiredPromotion.setDeletedAt(ZonedDateTime.now().minusDays(35));
        
        when(promotionDeletedRepository.findById(promotionId)).thenReturn(Optional.of(expiredPromotion));

        // When
        boolean result = promotionService.restorePromotion(promotionId, userId);

        // Then
        assertFalse(result);
        verify(promotionDeletedRepository).findById(promotionId);
        verify(promotionRepository, never()).save(any(Promotion.class));
    }

    @Test
    void restorePromotionUsingDBFunction_ShouldCallDatabaseFunction() {
        // Given
        Integer promotionId = 1;
        Integer userId = 1;

        // When
        boolean result = promotionService.restorePromotionUsingDBFunction(promotionId, userId);

        // Then
        assertTrue(result);
        verify(promotionRepository).setActor(userId);
        verify(promotionRepository).restorePromotionUsingFunction(promotionId);
    }

    @Test
    void deletePromotion_ShouldDeletePromotionWithoutUser() {
        // Given
        Integer promotionId = 1;
        when(promotionRepository.findById(promotionId)).thenReturn(Optional.of(testPromotion));

        // When
        boolean result = promotionService.deletePromotion(promotionId);

        // Then
        assertTrue(result);
        verify(promotionRepository).findById(promotionId);
        verify(promotionRepository).delete(testPromotion);
    }

    @Test
    void deletePromotion_ShouldDeletePromotionWithUser() {
        // Given
        Integer promotionId = 1;
        Integer deletedByUserId = 1;
        when(promotionRepository.findById(promotionId)).thenReturn(Optional.of(testPromotion));

        // When
        boolean result = promotionService.deletePromotion(promotionId, deletedByUserId);

        // Then
        assertTrue(result);
        verify(promotionRepository).findById(promotionId);
        verify(promotionRepository).setActor(deletedByUserId);
        verify(promotionRepository).delete(testPromotion);
    }

    @Test
    void deletePromotion_ShouldReturnFalseWhenPromotionNotExists() {
        // Given
        Integer promotionId = 999;
        when(promotionRepository.findById(promotionId)).thenReturn(Optional.empty());

        // When
        boolean result = promotionService.deletePromotion(promotionId);

        // Then
        assertFalse(result);
        verify(promotionRepository).findById(promotionId);
        verify(promotionRepository, never()).delete(any(Promotion.class));
    }

    @Test
    void associateProductsToPromotion_WhenValidData_ShouldReturnTrue() {
        // Given
        Integer promotionId = 1;
        List<Integer> productIds = Arrays.asList(1, 2);
        
        Product product1 = new Product();
        product1.setProductId(1);
        Product product2 = new Product();
        product2.setProductId(2);
        
        when(promotionRepository.findById(promotionId)).thenReturn(Optional.of(testPromotion));
        when(productRepository.findById(1)).thenReturn(Optional.of(product1));
        when(productRepository.findById(2)).thenReturn(Optional.of(product2));
        when(productRepository.save(any(Product.class))).thenReturn(product1);

        // When
        boolean result = promotionService.associateProductsToPromotion(promotionId, productIds);

        // Then
        assertTrue(result);
        verify(promotionRepository).findById(promotionId);
        verify(productRepository).findById(1);
        verify(productRepository).findById(2);
        verify(productRepository, times(2)).save(any(Product.class));
    }

    @Test
    void associateProductsToPromotion_WhenPromotionNotFound_ShouldReturnFalse() {
        // Given
        Integer promotionId = 999;
        List<Integer> productIds = Arrays.asList(1, 2);
        
        when(promotionRepository.findById(promotionId)).thenReturn(Optional.empty());

        // When
        boolean result = promotionService.associateProductsToPromotion(promotionId, productIds);

        // Then
        assertFalse(result);
        verify(promotionRepository).findById(promotionId);
        verify(productRepository, never()).findById(anyInt());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void associateProductsToPromotion_WhenExceptionThrown_ShouldReturnFalse() {
        // Given
        Integer promotionId = 1;
        List<Integer> productIds = Arrays.asList(1, 2);
        
        when(promotionRepository.findById(promotionId)).thenThrow(new RuntimeException("Database error"));

        // When
        boolean result = promotionService.associateProductsToPromotion(promotionId, productIds);

        // Then
        assertFalse(result);
        verify(promotionRepository).findById(promotionId);
    }

    @Test
    void removeProductsFromPromotion_WhenValidData_ShouldReturnTrue() {
        // Given
        Integer promotionId = 1;
        List<Integer> productIds = Arrays.asList(1, 2);
        
        Product product1 = new Product();
        product1.setProductId(1);
        product1.setPromotion(testPromotion);
        
        Product product2 = new Product();
        product2.setProductId(2);
        product2.setPromotion(testPromotion);
        
        when(promotionRepository.findById(promotionId)).thenReturn(Optional.of(testPromotion));
        when(productRepository.findById(1)).thenReturn(Optional.of(product1));
        when(productRepository.findById(2)).thenReturn(Optional.of(product2));
        when(productRepository.save(any(Product.class))).thenReturn(product1);

        // When
        boolean result = promotionService.removeProductsFromPromotion(promotionId, productIds);

        // Then
        assertTrue(result);
        verify(promotionRepository).findById(promotionId);
        verify(productRepository).findById(1);
        verify(productRepository).findById(2);
        verify(productRepository, times(2)).save(any(Product.class));
        assertNull(product1.getPromotion());
        assertNull(product2.getPromotion());
    }

    @Test
    void removeProductsFromPromotion_WhenPromotionNotFound_ShouldReturnFalse() {
        // Given
        Integer promotionId = 999;
        List<Integer> productIds = Arrays.asList(1, 2);
        
        when(promotionRepository.findById(promotionId)).thenReturn(Optional.empty());

        // When
        boolean result = promotionService.removeProductsFromPromotion(promotionId, productIds);

        // Then
        assertFalse(result);
        verify(promotionRepository).findById(promotionId);
        verify(productRepository, never()).findById(anyInt());
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void removeProductsFromPromotion_WhenExceptionThrown_ShouldReturnFalse() {
        // Given
        Integer promotionId = 1;
        List<Integer> productIds = Arrays.asList(1, 2);
        
        when(promotionRepository.findById(promotionId)).thenThrow(new RuntimeException("Database error"));

        // When
        boolean result = promotionService.removeProductsFromPromotion(promotionId, productIds);

        // Then
        assertFalse(result);
        verify(promotionRepository).findById(promotionId);
    }

    @Test
    void permanentDeletePromotion_WhenPromotionExists_ShouldReturnTrue() {
        // Given
        Integer promotionId = 1;
        Integer userId = 1;
        
        when(promotionDeletedRepository.findById(promotionId)).thenReturn(Optional.of(testPromotionDeleted));

        // When
        boolean result = promotionService.permanentDeletePromotion(promotionId, userId);

        // Then
        assertTrue(result);
        verify(promotionDeletedRepository).findById(promotionId);
        verify(promotionDeletedRepository).delete(testPromotionDeleted);
    }

    @Test
    void permanentDeletePromotion_WhenPromotionNotFound_ShouldReturnFalse() {
        // Given
        Integer promotionId = 999;
        Integer userId = 1;
        
        when(promotionDeletedRepository.findById(promotionId)).thenReturn(Optional.empty());

        // When
        boolean result = promotionService.permanentDeletePromotion(promotionId, userId);

        // Then
        assertFalse(result);
        verify(promotionDeletedRepository).findById(promotionId);
        verify(promotionDeletedRepository, never()).delete(any(PromotionDeleted.class));
    }

    @Test
    void permanentDeletePromotion_WhenExceptionThrown_ShouldReturnFalse() {
        // Given
        Integer promotionId = 1;
        Integer userId = 1;
        
        when(promotionDeletedRepository.findById(promotionId)).thenThrow(new RuntimeException("Database error"));

        // When
        boolean result = promotionService.permanentDeletePromotion(promotionId, userId);

        // Then
        assertFalse(result);
        verify(promotionDeletedRepository).findById(promotionId);
    }
}
