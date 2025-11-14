package com.petstore.backend.mapper;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.petstore.backend.dto.PromotionDeletedDTO;
import com.petstore.backend.entity.Category;
import com.petstore.backend.entity.PromotionDeleted;
import com.petstore.backend.entity.Status;
import com.petstore.backend.entity.User;

@SpringBootTest
@ActiveProfiles("test")
class PromotionDeletedMapperTest {

    @Autowired
    private PromotionDeletedMapper promotionDeletedMapper;

    private PromotionDeleted promotionDeleted;
    private PromotionDeletedDTO promotionDeletedDTO;

    @BeforeEach
    void setUp() {
        // Setup entities
        Status status = new Status();
        status.setStatusName("DELETED");

        User user = new User();
        user.setUserName("testuser");

        Category category = new Category();
        category.setCategoryName("Test Category");

        User deletedBy = new User();
        deletedBy.setUserName("admin");

        promotionDeleted = new PromotionDeleted();
        promotionDeleted.setPromotionId(1);
        promotionDeleted.setPromotionName("Test Promotion");
        promotionDeleted.setDiscountValue(20.0);
        promotionDeleted.setStartDate(LocalDate.now().minusDays(10));
        promotionDeleted.setEndDate(LocalDate.now().plusDays(10));
        promotionDeleted.setDescription("Test description");
        promotionDeleted.setDeletedAt(ZonedDateTime.now());
        promotionDeleted.setStatus(status);
        promotionDeleted.setUser(user);
        promotionDeleted.setCategory(category);
        promotionDeleted.setDeletedBy(deletedBy);

        // Setup DTO
        promotionDeletedDTO = new PromotionDeletedDTO();
        promotionDeletedDTO.setPromotionId(1);
        promotionDeletedDTO.setPromotionName("Test Promotion DTO");
        promotionDeletedDTO.setDiscountValue(25.0);
        promotionDeletedDTO.setStartDate(LocalDate.now().minusDays(5));
        promotionDeletedDTO.setEndDate(LocalDate.now().plusDays(15));
        promotionDeletedDTO.setDescription("Test DTO description");
        promotionDeletedDTO.setStatusName("ACTIVE");
        promotionDeletedDTO.setUserName("dtouser");
        promotionDeletedDTO.setCategoryName("DTO Category");
        promotionDeletedDTO.setDeletedByUserName("dtoadmin");
    }

    @Test
    void toDTO_ShouldMapAllFields() {
        // When
        PromotionDeletedDTO result = promotionDeletedMapper.toDTO(promotionDeleted);

        // Then
        assertNotNull(result);
        assertEquals(promotionDeleted.getPromotionId(), result.getPromotionId());
        assertEquals(promotionDeleted.getPromotionName(), result.getPromotionName());
        assertEquals(promotionDeleted.getDiscountValue(), result.getDiscountValue());
        assertEquals(promotionDeleted.getStartDate(), result.getStartDate());
        assertEquals(promotionDeleted.getEndDate(), result.getEndDate());
        assertEquals(promotionDeleted.getDescription(), result.getDescription());
        assertEquals(promotionDeleted.getStatus().getStatusName(), result.getStatusName());
        assertEquals(promotionDeleted.getUser().getUserName(), result.getUserName());
        assertEquals(promotionDeleted.getCategory().getCategoryName(), result.getCategoryName());
        assertEquals(promotionDeleted.getDeletedBy().getUserName(), result.getDeletedByUserName());
    }

    @Test
    void toDTO_WithNullEntity_ShouldReturnNull() {
        // When
        PromotionDeletedDTO result = promotionDeletedMapper.toDTO(null);

        // Then
        assertNull(result);
    }

    @Test
    void toDTO_WithNullStatus_ShouldHandleGracefully() {
        // Given
        promotionDeleted.setStatus(null);

        // When
        PromotionDeletedDTO result = promotionDeletedMapper.toDTO(promotionDeleted);

        // Then
        assertNotNull(result);
        assertNull(result.getStatusName());
    }

    @Test
    void toDTO_WithNullUser_ShouldHandleGracefully() {
        // Given
        promotionDeleted.setUser(null);

        // When
        PromotionDeletedDTO result = promotionDeletedMapper.toDTO(promotionDeleted);

        // Then
        assertNotNull(result);
        assertNull(result.getUserName());
    }

    @Test
    void toDTO_WithNullCategory_ShouldHandleGracefully() {
        // Given
        promotionDeleted.setCategory(null);

        // When
        PromotionDeletedDTO result = promotionDeletedMapper.toDTO(promotionDeleted);

        // Then
        assertNotNull(result);
        assertNull(result.getCategoryName());
    }

    @Test
    void toDTO_WithNullDeletedBy_ShouldHandleGracefully() {
        // Given
        promotionDeleted.setDeletedBy(null);

        // When
        PromotionDeletedDTO result = promotionDeletedMapper.toDTO(promotionDeleted);

        // Then
        assertNotNull(result);
        assertNull(result.getDeletedByUserName());
    }

    @Test
    void toEntity_ShouldMapBasicFields() {
        // When
        PromotionDeleted result = promotionDeletedMapper.toEntity(promotionDeletedDTO);

        // Then
        assertNotNull(result);
        assertEquals(promotionDeletedDTO.getPromotionId(), result.getPromotionId());
        assertEquals(promotionDeletedDTO.getPromotionName(), result.getPromotionName());
        assertEquals(promotionDeletedDTO.getDiscountValue(), result.getDiscountValue());
        assertEquals(promotionDeletedDTO.getStartDate(), result.getStartDate());
        assertEquals(promotionDeletedDTO.getEndDate(), result.getEndDate());
        assertEquals(promotionDeletedDTO.getDescription(), result.getDescription());
        
        // Ignored fields should be null
        assertNull(result.getStatus());
        assertNull(result.getUser());
        assertNull(result.getCategory());
        assertNull(result.getDeletedBy());
        assertNull(result.getDeletedAt());
    }

    @Test
    void toEntity_WithNullDTO_ShouldReturnNull() {
        // When
        PromotionDeleted result = promotionDeletedMapper.toEntity(null);

        // Then
        assertNull(result);
    }

    @Test
    void toEntity_WithMinimalDTO_ShouldWork() {
        // Given
        PromotionDeletedDTO minimalDTO = new PromotionDeletedDTO();
        minimalDTO.setPromotionId(1);
        minimalDTO.setPromotionName("Minimal");

        // When
        PromotionDeleted result = promotionDeletedMapper.toEntity(minimalDTO);

        // Then
        assertNotNull(result);
        assertEquals(Integer.valueOf(1), result.getPromotionId());
        assertEquals("Minimal", result.getPromotionName());
    }

    @Test
    void mapper_ShouldHaveInstanceField() {
        // Then
        assertNotNull(PromotionDeletedMapper.INSTANCE);
    }

    @Test
    void toDTO_WithZeroes_ShouldMapCorrectly() {
        // Given
        promotionDeleted.setDiscountValue(0.0);
        promotionDeleted.setPromotionId(0);

        // When
        PromotionDeletedDTO result = promotionDeletedMapper.toDTO(promotionDeleted);

        // Then
        assertNotNull(result);
        assertEquals(0.0, result.getDiscountValue());
        assertEquals(Integer.valueOf(0), result.getPromotionId());
    }

    @Test
    void toEntity_WithZeroes_ShouldMapCorrectly() {
        // Given
        promotionDeletedDTO.setDiscountValue(0.0);
        promotionDeletedDTO.setPromotionId(0);

        // When
        PromotionDeleted result = promotionDeletedMapper.toEntity(promotionDeletedDTO);

        // Then
        assertNotNull(result);
        assertEquals(0.0, result.getDiscountValue());
        assertEquals(Integer.valueOf(0), result.getPromotionId());
    }

    @Test
    void roundTripMapping_ShouldPreserveBasicData() {
        // Given - Create a simple entity
        PromotionDeleted simple = new PromotionDeleted();
        simple.setPromotionId(123);
        simple.setPromotionName("Round Trip Test");
        simple.setDiscountValue(15.5);
        simple.setDescription("Round trip description");

        // When - Convert to DTO and back to entity
        PromotionDeletedDTO dto = promotionDeletedMapper.toDTO(simple);
        PromotionDeleted backToEntity = promotionDeletedMapper.toEntity(dto);

        // Then - Basic fields should be preserved
        assertNotNull(backToEntity);
        assertEquals(simple.getPromotionId(), backToEntity.getPromotionId());
        assertEquals(simple.getPromotionName(), backToEntity.getPromotionName());
        assertEquals(simple.getDiscountValue(), backToEntity.getDiscountValue());
        assertEquals(simple.getDescription(), backToEntity.getDescription());
    }

    @Test
    void toDTO_WithEmptyStrings_ShouldPreserveEmptyStrings() {
        // Given
        promotionDeleted.setPromotionName("");
        promotionDeleted.setDescription("");

        // When
        PromotionDeletedDTO result = promotionDeletedMapper.toDTO(promotionDeleted);

        // Then
        assertNotNull(result);
        assertEquals("", result.getPromotionName());
        assertEquals("", result.getDescription());
    }

    @Test
    void toEntity_WithEmptyStrings_ShouldPreserveEmptyStrings() {
        // Given
        promotionDeletedDTO.setPromotionName("");
        promotionDeletedDTO.setDescription("");

        // When
        PromotionDeleted result = promotionDeletedMapper.toEntity(promotionDeletedDTO);

        // Then
        assertNotNull(result);
        assertEquals("", result.getPromotionName());
        assertEquals("", result.getDescription());
    }
}
