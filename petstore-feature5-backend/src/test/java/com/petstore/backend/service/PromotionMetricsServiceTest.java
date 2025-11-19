package com.petstore.backend.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.petstore.backend.dto.ProductMetricsDTO;
import com.petstore.backend.dto.PromotionPerformanceDTO;
import com.petstore.backend.entity.Category;
import com.petstore.backend.entity.Product;
import com.petstore.backend.entity.Promotion;
import com.petstore.backend.entity.PromotionMetrics;
import com.petstore.backend.entity.Status;
import com.petstore.backend.repository.ProductRepository;
import com.petstore.backend.repository.PromotionMetricsRepository;
import com.petstore.backend.repository.PromotionRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("PromotionMetricsService Tests")
class PromotionMetricsServiceTest {

    @Mock
    private PromotionMetricsRepository promotionMetricsRepository;

    @Mock
    private PromotionRepository promotionRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private PromotionMetricsService promotionMetricsService;

    private Promotion testPromotion;
    private Product testProduct;
    private Category testCategory;
    private PromotionMetrics testMetrics;
    private Status testStatus;

    @BeforeEach
    void setUp() {
        // Setup test data
        testStatus = new Status();
        testStatus.setStatusId(1);
        testStatus.setStatusName("ACTIVE");

        testCategory = new Category();
        testCategory.setCategoryId(1);
        testCategory.setCategoryName("Accesorios");
        testCategory.setDescription("Accesorios para mascotas");

        testProduct = new Product();
        testProduct.setProductId(1);
        testProduct.setProductName("Collar Premium");
        testProduct.setBasePrice(25.99);
        testProduct.setSku(12345);
        testProduct.setCategory(testCategory);

        testPromotion = new Promotion();
        testPromotion.setPromotionId(1);
        testPromotion.setPromotionName("Black Friday 2024");
        testPromotion.setDescription("Descuentos especiales");
        testPromotion.setDiscountValue(25.0);
        testPromotion.setStartDate(LocalDate.now().minusDays(1));
        testPromotion.setEndDate(LocalDate.now().plusDays(7));
        testPromotion.setStatus(testStatus);

        testMetrics = new PromotionMetrics();
        testMetrics.setMetricsId(1);
        testMetrics.setPromotion(testPromotion);
        testMetrics.setProduct(testProduct);
        testMetrics.setInitialInventory(100);
        testMetrics.setCurrentInventory(75);
        testMetrics.setUnitsSold(25);
        testMetrics.setRevenueGenerated(BigDecimal.valueOf(487.31));
        testMetrics.setSnapshotDate(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should get promotion performance successfully")
    void testGetPromotionPerformance_Success() {
        // Given
        Integer promotionId = 1;
        when(promotionRepository.findById(promotionId)).thenReturn(Optional.of(testPromotion));
        when(promotionMetricsRepository.existsByPromotionPromotionId(promotionId)).thenReturn(true);
        when(promotionMetricsRepository.getTotalUnitsSoldByPromotionId(promotionId)).thenReturn(25);
        when(promotionMetricsRepository.getTotalRevenueByPromotionId(promotionId)).thenReturn(487.31);
        when(promotionMetricsRepository.getTotalInitialInventoryByPromotionId(promotionId)).thenReturn(100);
        when(promotionMetricsRepository.getTotalCurrentInventoryByPromotionId(promotionId)).thenReturn(75);
        when(promotionMetricsRepository.getUniqueProductCountByPromotionId(promotionId)).thenReturn(1);
        when(promotionMetricsRepository.getLastUpdatedByPromotionId(promotionId)).thenReturn(LocalDateTime.now());
        when(promotionMetricsRepository.findLatestMetricsByPromotionId(promotionId))
                .thenReturn(Arrays.asList(testMetrics));

        // When
        Optional<PromotionPerformanceDTO> result = promotionMetricsService.getPromotionPerformance(promotionId);

        // Then
        assertTrue(result.isPresent());
        PromotionPerformanceDTO performance = result.get();
        assertEquals(promotionId, performance.getPromotionId());
        assertEquals("Black Friday 2024", performance.getPromotionName());
        assertEquals(25, performance.getTotalUnitsSold());
        assertEquals(BigDecimal.valueOf(487.31), performance.getTotalRevenue());
        assertEquals(100, performance.getTotalInitialInventory());
        assertEquals(75, performance.getTotalCurrentInventory());
        assertEquals(1, performance.getTotalProducts());
        assertTrue(performance.getIsActive());
    }

    @Test
    @DisplayName("Should return empty when promotion not found")
    void testGetPromotionPerformance_PromotionNotFound() {
        // Given
        Integer promotionId = 999;
        when(promotionRepository.findById(promotionId)).thenReturn(Optional.empty());

        // When
        Optional<PromotionPerformanceDTO> result = promotionMetricsService.getPromotionPerformance(promotionId);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return empty performance when no metrics exist")
    void testGetPromotionPerformance_NoMetrics() {
        // Given
        Integer promotionId = 1;
        when(promotionRepository.findById(promotionId)).thenReturn(Optional.of(testPromotion));
        when(promotionMetricsRepository.existsByPromotionPromotionId(promotionId)).thenReturn(false);

        // When
        Optional<PromotionPerformanceDTO> result = promotionMetricsService.getPromotionPerformance(promotionId);

        // Then
        assertTrue(result.isPresent());
        PromotionPerformanceDTO performance = result.get();
        assertEquals(0, performance.getTotalUnitsSold());
        assertEquals(BigDecimal.ZERO, performance.getTotalRevenue());
        assertEquals(0, performance.getTotalProducts());
    }

    @Test
    @DisplayName("Should get product metrics by promotion ID")
    void testGetProductMetricsByPromotionId() {
        // Given
        Integer promotionId = 1;
        when(promotionMetricsRepository.findLatestMetricsByPromotionId(promotionId))
                .thenReturn(Arrays.asList(testMetrics));

        // When
        List<ProductMetricsDTO> result = promotionMetricsService.getProductMetricsByPromotionId(promotionId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        ProductMetricsDTO productMetrics = result.get(0);
        assertEquals(testProduct.getProductId(), productMetrics.getProductId());
        assertEquals(testProduct.getProductName(), productMetrics.getProductName());
        assertEquals(100, productMetrics.getInitialInventory());
        assertEquals(75, productMetrics.getCurrentInventory());
        assertEquals(25, productMetrics.getUnitsSold());
        assertEquals(25, productMetrics.getInventoryDifference());
    }

    @Test
    @DisplayName("Should save or update metrics successfully")
    void testSaveOrUpdateMetrics_Success() {
        // Given
        Integer promotionId = 1;
        Integer productId = 1;
        when(promotionRepository.findById(promotionId)).thenReturn(Optional.of(testPromotion));
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));
        when(promotionMetricsRepository.findByPromotionPromotionIdAndProductProductId(promotionId, productId))
                .thenReturn(Optional.empty());
        when(promotionMetricsRepository.save(any(PromotionMetrics.class))).thenReturn(testMetrics);

        // When
        PromotionMetrics result = promotionMetricsService.saveOrUpdateMetrics(
                promotionId, productId, 100, 75, 25, BigDecimal.valueOf(487.31));

        // Then
        assertNotNull(result);
        verify(promotionMetricsRepository).save(any(PromotionMetrics.class));
    }

    @Test
    @DisplayName("Should update existing metrics")
    void testSaveOrUpdateMetrics_UpdateExisting() {
        // Given
        Integer promotionId = 1;
        Integer productId = 1;
        when(promotionRepository.findById(promotionId)).thenReturn(Optional.of(testPromotion));
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));
        when(promotionMetricsRepository.findByPromotionPromotionIdAndProductProductId(promotionId, productId))
                .thenReturn(Optional.of(testMetrics));
        when(promotionMetricsRepository.save(testMetrics)).thenReturn(testMetrics);

        // When
        PromotionMetrics result = promotionMetricsService.saveOrUpdateMetrics(
                promotionId, productId, 100, 70, 30, BigDecimal.valueOf(600.00));

        // Then
        assertNotNull(result);
        assertEquals(70, testMetrics.getCurrentInventory());
        assertEquals(30, testMetrics.getUnitsSold());
        assertEquals(BigDecimal.valueOf(600.00), testMetrics.getRevenueGenerated());
        verify(promotionMetricsRepository).save(testMetrics);
    }

    @Test
    @DisplayName("Should throw exception when promotion not found for save/update")
    void testSaveOrUpdateMetrics_PromotionNotFound() {
        // Given
        Integer promotionId = 999;
        Integer productId = 1;
        when(promotionRepository.findById(promotionId)).thenReturn(Optional.empty());
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> promotionMetricsService.saveOrUpdateMetrics(
                        promotionId, productId, 100, 75, 25, BigDecimal.valueOf(487.31))
        );
        assertEquals("Promotion or Product not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should initialize metrics for promotion")
    void testInitializeMetricsForPromotion() {
        // Given
        Integer promotionId = 1;
        List<Product> products = Arrays.asList(testProduct);
        
        when(productRepository.findByPromotionPromotionId(promotionId)).thenReturn(products);
        when(promotionMetricsRepository.findByPromotionPromotionIdAndProductProductId(promotionId, testProduct.getProductId()))
                .thenReturn(Optional.empty());
        when(promotionRepository.findById(promotionId)).thenReturn(Optional.of(testPromotion));
        when(productRepository.findById(testProduct.getProductId())).thenReturn(Optional.of(testProduct));
        when(promotionMetricsRepository.save(any(PromotionMetrics.class))).thenReturn(testMetrics);

        // When
        promotionMetricsService.initializeMetricsForPromotion(promotionId);

        // Then
        verify(promotionMetricsRepository).save(any(PromotionMetrics.class));
    }

    @Test
    @DisplayName("Should simulate metrics update")
    void testSimulateMetricsUpdate() {
        // Given
        Integer promotionId = 1;
        Integer productId = 1;
        Integer unitsSoldIncrement = 5;
        
        when(promotionMetricsRepository.findByPromotionPromotionIdAndProductProductId(promotionId, productId))
                .thenReturn(Optional.of(testMetrics));
        when(productRepository.findById(productId)).thenReturn(Optional.of(testProduct));
        when(promotionRepository.findById(promotionId)).thenReturn(Optional.of(testPromotion));
        when(promotionMetricsRepository.save(any(PromotionMetrics.class))).thenReturn(testMetrics);

        // When
        promotionMetricsService.simulateMetricsUpdate(promotionId, productId, unitsSoldIncrement);

        // Then
        verify(promotionMetricsRepository).save(any(PromotionMetrics.class));
    }

    @Test
    @DisplayName("Should check if metrics exist")
    void testHasMetrics() {
        // Given
        Integer promotionId = 1;
        when(promotionMetricsRepository.existsByPromotionPromotionId(promotionId)).thenReturn(true);

        // When
        boolean result = promotionMetricsService.hasMetrics(promotionId);

        // Then
        assertTrue(result);
    }

    @Test
    @DisplayName("Should delete metrics for promotion")
    void testDeleteMetricsForPromotion() {
        // Given
        Integer promotionId = 1;
        doNothing().when(promotionMetricsRepository).deleteByPromotionPromotionId(promotionId);

        // When
        promotionMetricsService.deleteMetricsForPromotion(promotionId);

        // Then
        verify(promotionMetricsRepository).deleteByPromotionPromotionId(promotionId);
    }
}
