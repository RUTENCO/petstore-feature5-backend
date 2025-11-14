package com.petstore.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PromotionDTOTest {

    private PromotionDTO promotionDTO;
    private CategoryDTO categoryDTO;

    @BeforeEach
    void setUp() {
        promotionDTO = new PromotionDTO();
        
        categoryDTO = new CategoryDTO();
        categoryDTO.setCategoryId(1);
        categoryDTO.setCategoryName("Electronics");
    }

    @Test
    void constructor_ShouldCreateEmptyPromotionDTO() {
        // Then
        assertNotNull(promotionDTO);
        assertNull(promotionDTO.getPromotionId());
        assertNull(promotionDTO.getPromotionName());
        assertNull(promotionDTO.getDescription());
        assertNull(promotionDTO.getDiscountPercentage());
        assertNull(promotionDTO.getStartDate());
        assertNull(promotionDTO.getEndDate());
        assertNull(promotionDTO.getStatus());
        assertNull(promotionDTO.getCategory());
        assertNull(promotionDTO.getProduct());
        assertNull(promotionDTO.getCreatedAt());
        assertNull(promotionDTO.getUpdatedAt());
    }

    @Test
    void setPromotionId_ShouldSetPromotionId() {
        // Given
        Integer promotionId = 1;

        // When
        promotionDTO.setPromotionId(promotionId);

        // Then
        assertEquals(promotionId, promotionDTO.getPromotionId());
    }

    @Test
    void setPromotionName_ShouldSetPromotionName() {
        // Given
        String promotionName = "Summer Sale";

        // When
        promotionDTO.setPromotionName(promotionName);

        // Then
        assertEquals(promotionName, promotionDTO.getPromotionName());
    }

    @Test
    void setDescription_ShouldSetDescription() {
        // Given
        String description = "Great summer discounts";

        // When
        promotionDTO.setDescription(description);

        // Then
        assertEquals(description, promotionDTO.getDescription());
    }

    @Test
    void setDiscountPercentage_ShouldSetDiscountPercentage() {
        // Given
        BigDecimal discountPercentage = BigDecimal.valueOf(25.5);

        // When
        promotionDTO.setDiscountPercentage(discountPercentage);

        // Then
        assertEquals(discountPercentage, promotionDTO.getDiscountPercentage());
    }

    @Test
    void setStartDate_ShouldSetStartDate() {
        // Given
        LocalDate startDate = LocalDate.of(2025, 6, 1);

        // When
        promotionDTO.setStartDate(startDate);

        // Then
        assertEquals(startDate, promotionDTO.getStartDate());
    }

    @Test
    void setEndDate_ShouldSetEndDate() {
        // Given
        LocalDate endDate = LocalDate.of(2025, 8, 31);

        // When
        promotionDTO.setEndDate(endDate);

        // Then
        assertEquals(endDate, promotionDTO.getEndDate());
    }

    @Test
    void setStatus_ShouldSetStatus() {
        // Given
        String status = "ACTIVE";

        // When
        promotionDTO.setStatus(status);

        // Then
        assertEquals(status, promotionDTO.getStatus());
    }

    @Test
    void setCategory_ShouldSetCategory() {
        // When
        promotionDTO.setCategory(categoryDTO);

        // Then
        assertEquals(categoryDTO, promotionDTO.getCategory());
    }

    @Test
    void setProduct_ShouldSetProduct() {
        // Given
        ProductDTO productDTO = new ProductDTO();
        productDTO.setProductId(1);
        productDTO.setProductName("Laptop");

        // When
        promotionDTO.setProduct(productDTO);

        // Then
        assertEquals(productDTO, promotionDTO.getProduct());
    }

    @Test
    void setCreatedAt_ShouldSetCreatedAt() {
        // Given
        LocalDateTime createdAt = LocalDateTime.now();

        // When
        promotionDTO.setCreatedAt(createdAt);

        // Then
        assertEquals(createdAt, promotionDTO.getCreatedAt());
    }

    @Test
    void setUpdatedAt_ShouldSetUpdatedAt() {
        // Given
        LocalDateTime updatedAt = LocalDateTime.now();

        // When
        promotionDTO.setUpdatedAt(updatedAt);

        // Then
        assertEquals(updatedAt, promotionDTO.getUpdatedAt());
    }

    @Test
    void allFieldsCanBeSetAndRetrieved() {
        // Given
        Integer promotionId = 1;
        String promotionName = "Black Friday";
        String description = "Biggest sale of the year";
        BigDecimal discountPercentage = BigDecimal.valueOf(50.0);
        LocalDate startDate = LocalDate.of(2025, 11, 25);
        LocalDate endDate = LocalDate.of(2025, 11, 30);
        String status = "ACTIVE";
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime updatedAt = LocalDateTime.now();
        ProductDTO productDTO = new ProductDTO();

        // When
        promotionDTO.setPromotionId(promotionId);
        promotionDTO.setPromotionName(promotionName);
        promotionDTO.setDescription(description);
        promotionDTO.setDiscountPercentage(discountPercentage);
        promotionDTO.setStartDate(startDate);
        promotionDTO.setEndDate(endDate);
        promotionDTO.setStatus(status);
        promotionDTO.setCategory(categoryDTO);
        promotionDTO.setProduct(productDTO);
        promotionDTO.setCreatedAt(createdAt);
        promotionDTO.setUpdatedAt(updatedAt);

        // Then
        assertEquals(promotionId, promotionDTO.getPromotionId());
        assertEquals(promotionName, promotionDTO.getPromotionName());
        assertEquals(description, promotionDTO.getDescription());
        assertEquals(discountPercentage, promotionDTO.getDiscountPercentage());
        assertEquals(startDate, promotionDTO.getStartDate());
        assertEquals(endDate, promotionDTO.getEndDate());
        assertEquals(status, promotionDTO.getStatus());
        assertEquals(categoryDTO, promotionDTO.getCategory());
        assertEquals(productDTO, promotionDTO.getProduct());
        assertEquals(createdAt, promotionDTO.getCreatedAt());
        assertEquals(updatedAt, promotionDTO.getUpdatedAt());
    }

    @Test
    void fieldsCanBeNullSafely() {
        // Given - Set all fields to null
        promotionDTO.setPromotionId(null);
        promotionDTO.setPromotionName(null);
        promotionDTO.setDescription(null);
        promotionDTO.setDiscountPercentage(null);
        promotionDTO.setStartDate(null);
        promotionDTO.setEndDate(null);
        promotionDTO.setStatus(null);
        promotionDTO.setCategory(null);
        promotionDTO.setProduct(null);
        promotionDTO.setCreatedAt(null);
        promotionDTO.setUpdatedAt(null);

        // When & Then
        assertNull(promotionDTO.getPromotionId());
        assertNull(promotionDTO.getPromotionName());
        assertNull(promotionDTO.getDescription());
        assertNull(promotionDTO.getDiscountPercentage());
        assertNull(promotionDTO.getStartDate());
        assertNull(promotionDTO.getEndDate());
        assertNull(promotionDTO.getStatus());
        assertNull(promotionDTO.getCategory());
        assertNull(promotionDTO.getProduct());
        assertNull(promotionDTO.getCreatedAt());
        assertNull(promotionDTO.getUpdatedAt());

        // Should not throw any exceptions
        assertDoesNotThrow(() -> {
            promotionDTO.toString();
            promotionDTO.hashCode();
        });
    }

    @Test
    void discountPercentage_ShouldAcceptZero() {
        // Given
        BigDecimal zeroDiscount = BigDecimal.ZERO;

        // When
        promotionDTO.setDiscountPercentage(zeroDiscount);

        // Then
        assertEquals(zeroDiscount, promotionDTO.getDiscountPercentage());
    }

    @Test
    void discountPercentage_ShouldAcceptLargeValue() {
        // Given
        BigDecimal largeDiscount = BigDecimal.valueOf(999.99);

        // When
        promotionDTO.setDiscountPercentage(largeDiscount);

        // Then
        assertEquals(largeDiscount, promotionDTO.getDiscountPercentage());
    }

    @Test
    void status_ShouldAcceptEmptyString() {
        // Given
        String emptyStatus = "";

        // When
        promotionDTO.setStatus(emptyStatus);

        // Then
        assertEquals(emptyStatus, promotionDTO.getStatus());
    }

    @Test
    void promotionName_ShouldAcceptLongString() {
        // Given
        String longName = "A".repeat(500);

        // When
        promotionDTO.setPromotionName(longName);

        // Then
        assertEquals(longName, promotionDTO.getPromotionName());
    }
}
