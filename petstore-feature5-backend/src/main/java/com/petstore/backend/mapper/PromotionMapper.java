package com.petstore.backend.mapper;

import com.petstore.backend.dto.PromotionDTO;
import com.petstore.backend.entity.Promotion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

/**
 * Mapper para transformaciones Promotion <-> PromotionDTO
 */
@Mapper(componentModel = "spring")
public interface PromotionMapper {
    
    PromotionMapper INSTANCE = Mappers.getMapper(PromotionMapper.class);

    /**
     * Convierte una entidad Promotion a PromotionDTO
     */
    @Mapping(target = "discountPercentage", expression = "java(java.math.BigDecimal.valueOf(promotion.getDiscountValue()))")
    @Mapping(target = "status", expression = "java(promotion.getStatus() != null ? promotion.getStatus().getStatusName() : null)")
    @Mapping(target = "startDate", expression = "java(promotion.getStartDate() != null ? promotion.getStartDate().atStartOfDay() : null)")
    @Mapping(target = "endDate", expression = "java(promotion.getEndDate() != null ? promotion.getEndDate().atStartOfDay() : null)")
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    PromotionDTO toDTO(Promotion promotion);

    /**
     * Convierte PromotionDTO a entidad Promotion
     */
    @Mapping(target = "promotionId", ignore = true)
    @Mapping(target = "discountValue", expression = "java(promotionDTO.getDiscountPercentage() != null ? promotionDTO.getDiscountPercentage().doubleValue() : null)")
    @Mapping(target = "startDate", expression = "java(promotionDTO.getStartDate() != null ? promotionDTO.getStartDate().toLocalDate() : null)")
    @Mapping(target = "endDate", expression = "java(promotionDTO.getEndDate() != null ? promotionDTO.getEndDate().toLocalDate() : null)")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "user", ignore = true)
    Promotion toEntity(PromotionDTO promotionDTO);

    /**
     * Convierte una lista de entidades Promotion a lista de PromotionDTO
     */
    java.util.List<PromotionDTO> toDTOList(java.util.List<Promotion> promotions);

    /**
     * Actualiza una entidad Promotion existente con datos de PromotionDTO
     */
    @Mapping(target = "promotionId", ignore = true)
    @Mapping(target = "discountValue", expression = "java(promotionDTO.getDiscountPercentage() != null ? promotionDTO.getDiscountPercentage().doubleValue() : null)")
    @Mapping(target = "startDate", expression = "java(promotionDTO.getStartDate() != null ? promotionDTO.getStartDate().toLocalDate() : null)")
    @Mapping(target = "endDate", expression = "java(promotionDTO.getEndDate() != null ? promotionDTO.getEndDate().toLocalDate() : null)")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "user", ignore = true)
    void updateEntityFromDTO(PromotionDTO promotionDTO, @MappingTarget Promotion promotion);
}
