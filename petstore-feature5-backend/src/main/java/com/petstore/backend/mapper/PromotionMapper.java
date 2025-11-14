package com.petstore.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.petstore.backend.dto.PromotionDTO;
import com.petstore.backend.entity.Promotion;

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
    @Mapping(target = "startDate", source = "startDate")
    @Mapping(target = "endDate", source = "endDate")
    @Mapping(target = "categoryId", expression = "java(promotion.getCategory() != null ? promotion.getCategory().getCategoryId() : null)")
    @Mapping(target = "statusId", expression = "java(promotion.getStatus() != null ? promotion.getStatus().getStatusId() : null)")
    @Mapping(target = "userId", expression = "java(promotion.getUser() != null ? promotion.getUser().getUserId() : null)")
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    PromotionDTO toDTO(Promotion promotion);

    /**
     * Convierte PromotionDTO a entidad Promotion
     */
    @Mapping(target = "promotionId", ignore = true)
    @Mapping(target = "discountValue", expression = "java(promotionDTO.getDiscountPercentage() != null ? promotionDTO.getDiscountPercentage().doubleValue() : null)")
    @Mapping(target = "startDate", source = "startDate")
    @Mapping(target = "endDate", source = "endDate")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "category", ignore = true)
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
    @Mapping(target = "startDate", source = "startDate")
    @Mapping(target = "endDate", source = "endDate")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "category", ignore = true)
    void updateEntityFromDTO(PromotionDTO promotionDTO, @MappingTarget Promotion promotion);
}
