package com.petstore.backend.mapper;

import com.petstore.backend.dto.CategoryDTO;
import com.petstore.backend.dto.PromotionDTO;
import com.petstore.backend.entity.Category;
import com.petstore.backend.entity.Promotion;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-14T10:05:42-0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 24.0.2 (Oracle Corporation)"
)
@Component
public class PromotionMapperImpl implements PromotionMapper {

    @Override
    public PromotionDTO toDTO(Promotion promotion) {
        if ( promotion == null ) {
            return null;
        }

        PromotionDTO promotionDTO = new PromotionDTO();

        promotionDTO.setStartDate( promotion.getStartDate() );
        promotionDTO.setEndDate( promotion.getEndDate() );
        promotionDTO.setPromotionId( promotion.getPromotionId() );
        promotionDTO.setPromotionName( promotion.getPromotionName() );
        promotionDTO.setDescription( promotion.getDescription() );
        promotionDTO.setCategory( categoryToCategoryDTO( promotion.getCategory() ) );

        promotionDTO.setDiscountPercentage( java.math.BigDecimal.valueOf(promotion.getDiscountValue()) );
        promotionDTO.setStatus( promotion.getStatus() != null ? promotion.getStatus().getStatusName() : null );
        promotionDTO.setCategoryId( promotion.getCategory() != null ? promotion.getCategory().getCategoryId() : null );
        promotionDTO.setStatusId( promotion.getStatus() != null ? promotion.getStatus().getStatusId() : null );
        promotionDTO.setUserId( promotion.getUser() != null ? promotion.getUser().getUserId() : null );

        return promotionDTO;
    }

    @Override
    public Promotion toEntity(PromotionDTO promotionDTO) {
        if ( promotionDTO == null ) {
            return null;
        }

        Promotion promotion = new Promotion();

        promotion.setStartDate( promotionDTO.getStartDate() );
        promotion.setEndDate( promotionDTO.getEndDate() );
        promotion.setPromotionName( promotionDTO.getPromotionName() );
        promotion.setDescription( promotionDTO.getDescription() );

        promotion.setDiscountValue( promotionDTO.getDiscountPercentage() != null ? promotionDTO.getDiscountPercentage().doubleValue() : null );

        return promotion;
    }

    @Override
    public List<PromotionDTO> toDTOList(List<Promotion> promotions) {
        if ( promotions == null ) {
            return null;
        }

        List<PromotionDTO> list = new ArrayList<PromotionDTO>( promotions.size() );
        for ( Promotion promotion : promotions ) {
            list.add( toDTO( promotion ) );
        }

        return list;
    }

    @Override
    public void updateEntityFromDTO(PromotionDTO promotionDTO, Promotion promotion) {
        if ( promotionDTO == null ) {
            return;
        }

        promotion.setStartDate( promotionDTO.getStartDate() );
        promotion.setEndDate( promotionDTO.getEndDate() );
        promotion.setPromotionName( promotionDTO.getPromotionName() );
        promotion.setDescription( promotionDTO.getDescription() );

        promotion.setDiscountValue( promotionDTO.getDiscountPercentage() != null ? promotionDTO.getDiscountPercentage().doubleValue() : null );
    }

    protected CategoryDTO categoryToCategoryDTO(Category category) {
        if ( category == null ) {
            return null;
        }

        CategoryDTO categoryDTO = new CategoryDTO();

        categoryDTO.setCategoryId( category.getCategoryId() );
        categoryDTO.setCategoryName( category.getCategoryName() );
        categoryDTO.setDescription( category.getDescription() );

        return categoryDTO;
    }
}
