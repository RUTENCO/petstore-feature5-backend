package com.petstore.backend.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petstore.backend.dto.CategoryDTO;
import com.petstore.backend.dto.ProductMetricsDTO;
import com.petstore.backend.dto.PromotionPerformanceDTO;
import com.petstore.backend.service.PromotionMetricsService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("PromotionMetricsController Tests")
class PromotionMetricsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PromotionMetricsService promotionMetricsService;

    @Autowired
    private ObjectMapper objectMapper;

    private PromotionPerformanceDTO testPerformanceDTO;
    private ProductMetricsDTO testProductMetricsDTO;
    private CategoryDTO testCategoryDTO;

    @BeforeEach
    void setUp() {
        testCategoryDTO = new CategoryDTO();
        testCategoryDTO.setCategoryId(1);
        testCategoryDTO.setCategoryName("Accesorios");
        testCategoryDTO.setDescription("Accesorios para mascotas");

        testProductMetricsDTO = new ProductMetricsDTO(
            1,
            "Collar Premium",
            "SKU12345",
            BigDecimal.valueOf(25.99),
            BigDecimal.valueOf(19.49),
            100,
            75,
            25,
            BigDecimal.valueOf(487.31),
            testCategoryDTO,
            LocalDateTime.now()
        );

        testPerformanceDTO = new PromotionPerformanceDTO(
            1,
            "Black Friday 2024",
            "Descuentos especiales",
            25.0,
            LocalDateTime.now().minusDays(1),
            LocalDateTime.now().plusDays(7),
            true,
            1,
            25,
            BigDecimal.valueOf(487.31),
            100,
            75,
            LocalDateTime.now(),
            Arrays.asList(testProductMetricsDTO)
        );
    }

    @Test
    @DisplayName("GET /api/promotions/metrics/{promotionId} - Should return promotion metrics")
    void testGetPromotionMetrics_Success() throws Exception {
        // Given
        Integer promotionId = 1;
        when(promotionMetricsService.getPromotionPerformance(promotionId))
                .thenReturn(Optional.of(testPerformanceDTO));

        // When & Then
        mockMvc.perform(get("/api/promotions/metrics/{promotionId}", promotionId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.promotionId").value(1))
                .andExpect(jsonPath("$.promotionName").value("Black Friday 2024"))
                .andExpect(jsonPath("$.totalUnitsSold").value(25))
                .andExpect(jsonPath("$.totalRevenue").value(487.31))
                .andExpect(jsonPath("$.totalInitialInventory").value(100))
                .andExpect(jsonPath("$.totalCurrentInventory").value(75))
                .andExpect(jsonPath("$.isActive").value(true));
    }

    @Test
    @DisplayName("GET /api/promotions/metrics/{promotionId} - Should return 404 when promotion not found")
    void testGetPromotionMetrics_NotFound() throws Exception {
        // Given
        Integer promotionId = 999;
        when(promotionMetricsService.getPromotionPerformance(promotionId))
                .thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/promotions/metrics/{promotionId}", promotionId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/promotions/metrics/{promotionId} - Should return 204 when no metrics available")
    void testGetPromotionMetrics_NoContent() throws Exception {
        // Given
        Integer promotionId = 1;
        PromotionPerformanceDTO emptyPerformance = new PromotionPerformanceDTO();
        emptyPerformance.setPromotionId(promotionId);
        emptyPerformance.setTotalProducts(0); // Indica que no hay métricas
        
        when(promotionMetricsService.getPromotionPerformance(promotionId))
                .thenReturn(Optional.of(emptyPerformance));

        // When & Then
        mockMvc.perform(get("/api/promotions/metrics/{promotionId}", promotionId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /api/promotions/metrics/{promotionId}/products - Should return product metrics")
    void testGetProductMetrics_Success() throws Exception {
        // Given
        Integer promotionId = 1;
        List<ProductMetricsDTO> productMetrics = Arrays.asList(testProductMetricsDTO);
        when(promotionMetricsService.getProductMetricsByPromotionId(promotionId))
                .thenReturn(productMetrics);

        // When & Then
        mockMvc.perform(get("/api/promotions/metrics/{promotionId}/products", promotionId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].productId").value(1))
                .andExpect(jsonPath("$[0].productName").value("Collar Premium"))
                .andExpect(jsonPath("$[0].initialInventory").value(100))
                .andExpect(jsonPath("$[0].currentInventory").value(75))
                .andExpect(jsonPath("$[0].unitsSold").value(25));
    }

    @Test
    @DisplayName("GET /api/promotions/metrics/{promotionId}/products - Should return 404 when no products")
    void testGetProductMetrics_NotFound() throws Exception {
        // Given
        Integer promotionId = 1;
        when(promotionMetricsService.getProductMetricsByPromotionId(promotionId))
                .thenReturn(Collections.emptyList());

        // When & Then
        mockMvc.perform(get("/api/promotions/metrics/{promotionId}/products", promotionId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/promotions/metrics/{promotionId}/exists - Should check metrics existence")
    void testCheckMetricsExist_True() throws Exception {
        // Given
        Integer promotionId = 1;
        when(promotionMetricsService.hasMetrics(promotionId)).thenReturn(true);

        // When & Then
        mockMvc.perform(get("/api/promotions/metrics/{promotionId}/exists", promotionId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.promotionId").value(1))
                .andExpect(jsonPath("$.hasMetrics").value(true))
                .andExpect(jsonPath("$.message").value("Métricas disponibles"));
    }

    @Test
    @DisplayName("GET /api/promotions/metrics/{promotionId}/exists - Should return false when no metrics")
    void testCheckMetricsExist_False() throws Exception {
        // Given
        Integer promotionId = 1;
        when(promotionMetricsService.hasMetrics(promotionId)).thenReturn(false);

        // When & Then
        mockMvc.perform(get("/api/promotions/metrics/{promotionId}/exists", promotionId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.promotionId").value(1))
                .andExpect(jsonPath("$.hasMetrics").value(false))
                .andExpect(jsonPath("$.message").value("No hay datos disponibles para esta promoción"));
    }

    @Test
    @DisplayName("POST /api/promotions/metrics/{promotionId}/initialize - Should initialize metrics")
    void testInitializeMetrics_Success() throws Exception {
        // Given
        Integer promotionId = 1;
        doNothing().when(promotionMetricsService).initializeMetricsForPromotion(promotionId);

        // When & Then
        mockMvc.perform(post("/api/promotions/metrics/{promotionId}/initialize", promotionId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.promotionId").value(1))
                .andExpect(jsonPath("$.message").value("Métricas inicializadas exitosamente"));
    }

    @Test
    @DisplayName("POST /api/promotions/metrics/{promotionId}/simulate - Should simulate metrics update")
    void testSimulateMetricsUpdate_Success() throws Exception {
        // Given
        Integer promotionId = 1;
        PromotionMetricsController.SimulationRequest request = new PromotionMetricsController.SimulationRequest();
        request.setProductId(1);
        request.setUnitsSold(5);

        doNothing().when(promotionMetricsService).simulateMetricsUpdate(promotionId, 1, 5);

        // When & Then
        mockMvc.perform(post("/api/promotions/metrics/{promotionId}/simulate", promotionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.promotionId").value(1))
                .andExpect(jsonPath("$.productId").value(1))
                .andExpect(jsonPath("$.unitsSoldAdded").value(5));
    }

    @Test
    @DisplayName("POST /api/promotions/metrics/{promotionId}/simulate - Should return 400 for invalid request")
    void testSimulateMetricsUpdate_BadRequest() throws Exception {
        // Given
        Integer promotionId = 1;
        PromotionMetricsController.SimulationRequest request = new PromotionMetricsController.SimulationRequest();
        // Omitir productId para generar error

        // When & Then
        mockMvc.perform(post("/api/promotions/metrics/{promotionId}/simulate", promotionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Should handle service exceptions gracefully")
    void testServiceException() throws Exception {
        // Given
        Integer promotionId = 1;
        when(promotionMetricsService.getPromotionPerformance(promotionId))
                .thenThrow(new RuntimeException("Database error"));

        // When & Then
        mockMvc.perform(get("/api/promotions/metrics/{promotionId}", promotionId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }
}
