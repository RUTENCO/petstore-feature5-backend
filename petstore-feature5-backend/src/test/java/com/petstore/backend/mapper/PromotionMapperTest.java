package com.petstore.backend.mapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.petstore.backend.dto.PromotionDTO;
import com.petstore.backend.entity.Promotion;
import com.petstore.backend.entity.Status;

@SpringBootTest
@ActiveProfiles("test")
class PromotionMapperTest {

    @Autowired
    private PromotionMapper promotionMapper;

    private Promotion testPromotion;
    private PromotionDTO testPromotionDTO;
    private Status activeStatus;

    @BeforeEach
    void setUp() {
        activeStatus = new Status();
        activeStatus.setStatusId(1);
        activeStatus.setStatusName("ACTIVE");

        testPromotion = new Promotion();
        testPromotion.setPromotionId(1);
        testPromotion.setPromotionName("Summer Sale");
        testPromotion.setDescription("Great summer discount");
        testPromotion.setDiscountValue(25.5);
        testPromotion.setStartDate(LocalDate.of(2024, 6, 1));
        testPromotion.setEndDate(LocalDate.of(2024, 8, 31));
        testPromotion.setStatus(activeStatus);

        testPromotionDTO = new PromotionDTO();
        testPromotionDTO.setPromotionName("Winter Sale");
        testPromotionDTO.setDescription("Amazing winter deals");
        testPromotionDTO.setDiscountPercentage(BigDecimal.valueOf(30.0));
        testPromotionDTO.setStartDate(LocalDate.of(2024, 12, 1));
        testPromotionDTO.setEndDate(LocalDate.of(2024, 12, 31));
    }

    @Test
    void toDTO_ShouldConvertPromotionToPromotionDTO() {
        // When
        PromotionDTO result = promotionMapper.toDTO(testPromotion);

        // Then
        assertNotNull(result);
        assertEquals(testPromotion.getPromotionName(), result.getPromotionName());
        assertEquals(testPromotion.getDescription(), result.getDescription());
        assertEquals(BigDecimal.valueOf(testPromotion.getDiscountValue()), result.getDiscountPercentage());
        assertEquals(testPromotion.getStatus().getStatusName(), result.getStatus());
        assertEquals(testPromotion.getStartDate(), result.getStartDate());
        assertEquals(testPromotion.getEndDate(), result.getEndDate());
    }

    @Test
    void toDTO_ShouldHandleNullPromotion() {
        // When
        PromotionDTO result = promotionMapper.toDTO(null);

        // Then
        assertNull(result);
    }

    @Test
    void toDTO_ShouldHandlePromotionWithNullStatus() {
        // Given
        testPromotion.setStatus(null);

        // When
        PromotionDTO result = promotionMapper.toDTO(testPromotion);

        // Then
        assertNotNull(result);
        assertEquals(testPromotion.getPromotionName(), result.getPromotionName());
        assertNull(result.getStatus());
    }

    @Test
    void toDTO_ShouldHandlePromotionWithNullDates() {
        // Given
        testPromotion.setStartDate(null);
        testPromotion.setEndDate(null);

        // When
        PromotionDTO result = promotionMapper.toDTO(testPromotion);

        // Then
        assertNotNull(result);
        assertNull(result.getStartDate());
        assertNull(result.getEndDate());
    }

    @Test
    void toEntity_ShouldConvertPromotionDTOToPromotion() {
        // When
        Promotion result = promotionMapper.toEntity(testPromotionDTO);

        // Then
        assertNotNull(result);
        assertEquals(testPromotionDTO.getPromotionName(), result.getPromotionName());
        assertEquals(testPromotionDTO.getDescription(), result.getDescription());
        assertEquals(testPromotionDTO.getDiscountPercentage().doubleValue(), result.getDiscountValue());
        assertEquals(testPromotionDTO.getStartDate(), result.getStartDate());
        assertEquals(testPromotionDTO.getEndDate(), result.getEndDate());
        // promotionId should be ignored in mapping
        assertNull(result.getPromotionId());
    }

    @Test
    void toEntity_ShouldHandleNullPromotionDTO() {
        // When
        Promotion result = promotionMapper.toEntity(null);

        // Then
        assertNull(result);
    }

    @Test
    void toEntity_ShouldHandlePromotionDTOWithNullValues() {
        // Given
        testPromotionDTO.setDiscountPercentage(null);
        testPromotionDTO.setStartDate(null);
        testPromotionDTO.setEndDate(null);

        // When
        Promotion result = promotionMapper.toEntity(testPromotionDTO);

        // Then
        assertNotNull(result);
        assertEquals(testPromotionDTO.getPromotionName(), result.getPromotionName());
        assertNull(result.getDiscountValue());
        assertNull(result.getStartDate());
        assertNull(result.getEndDate());
    }

    @Test
    void toDTOList_ShouldConvertPromotionListToPromotionDTOList() {
        // Given
        Promotion promotion2 = new Promotion();
        promotion2.setPromotionId(2);
        promotion2.setPromotionName("Black Friday");
        promotion2.setDiscountValue(50.0);
        promotion2.setStatus(activeStatus);

        List<Promotion> promotions = Arrays.asList(testPromotion, promotion2);

        // When
        List<PromotionDTO> result = promotionMapper.toDTOList(promotions);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Summer Sale", result.get(0).getPromotionName());
        assertEquals("Black Friday", result.get(1).getPromotionName());
        assertEquals(BigDecimal.valueOf(25.5), result.get(0).getDiscountPercentage());
        assertEquals(BigDecimal.valueOf(50.0), result.get(1).getDiscountPercentage());
    }

    @Test
    void toDTOList_ShouldHandleNullList() {
        // When
        List<PromotionDTO> result = promotionMapper.toDTOList(null);

        // Then
        assertNull(result);
    }

    @Test
    void toDTOList_ShouldHandleEmptyList() {
        // When
        List<PromotionDTO> result = promotionMapper.toDTOList(Arrays.asList());

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void updateEntityFromDTO_ShouldUpdatePromotionFromPromotionDTO() {
        // Given
        Promotion existingPromotion = new Promotion();
        existingPromotion.setPromotionId(10);
        existingPromotion.setPromotionName("Old Name");
        existingPromotion.setDiscountValue(15.0);

        // When
        promotionMapper.updateEntityFromDTO(testPromotionDTO, existingPromotion);

        // Then
        // promotionId should not be updated (ignored)
        assertEquals(10, existingPromotion.getPromotionId());
        // Other fields should be updated
        assertEquals(testPromotionDTO.getPromotionName(), existingPromotion.getPromotionName());
        assertEquals(testPromotionDTO.getDescription(), existingPromotion.getDescription());
        assertEquals(testPromotionDTO.getDiscountPercentage().doubleValue(), existingPromotion.getDiscountValue());
        assertEquals(testPromotionDTO.getStartDate(), existingPromotion.getStartDate());
        assertEquals(testPromotionDTO.getEndDate(), existingPromotion.getEndDate());
    }

    @Test
    void updateEntityFromDTO_ShouldHandleNullValues() {
        // Given
        Promotion existingPromotion = new Promotion();
        existingPromotion.setPromotionId(10);
        existingPromotion.setPromotionName("Old Name");

        PromotionDTO nullDTO = new PromotionDTO();
        nullDTO.setPromotionName("New Name");
        nullDTO.setDiscountPercentage(null);
        nullDTO.setStartDate(null);
        nullDTO.setEndDate(null);

        // When
        promotionMapper.updateEntityFromDTO(nullDTO, existingPromotion);

        // Then
        assertEquals("New Name", existingPromotion.getPromotionName());
        assertNull(existingPromotion.getDiscountValue());
        assertNull(existingPromotion.getStartDate());
        assertNull(existingPromotion.getEndDate());
    }

    @Test
    void mapperInstance_ShouldNotBeNull() {
        // Then
        assertNotNull(PromotionMapper.INSTANCE);
    }
}
