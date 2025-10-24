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
    date = "2025-10-01T02:13:09-0500",
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

        promotionDTO.setPromotionId( promotion.getPromotionId() );
        promotionDTO.setPromotionName( promotion.getPromotionName() );
        promotionDTO.setDescription( promotion.getDescription() );
        promotionDTO.setCategory( categoryToCategoryDTO( promotion.getCategory() ) );

        promotionDTO.setDiscountPercentage( java.math.BigDecimal.valueOf(promotion.getDiscountValue()) );
        promotionDTO.setStatus( promotion.getStatus() != null ? promotion.getStatus().getStatusName() : null );
        promotionDTO.setStartDate( promotion.getStartDate() != null ? promotion.getStartDate().atStartOfDay() : null );
        promotionDTO.setEndDate( promotion.getEndDate() != null ? promotion.getEndDate().atStartOfDay() : null );

        return promotionDTO;
    }

    @Override
    public Promotion toEntity(PromotionDTO promotionDTO) {
        if ( promotionDTO == null ) {
            return null;
        }

        Promotion promotion = new Promotion();

        promotion.setPromotionName( promotionDTO.getPromotionName() );
        promotion.setDescription( promotionDTO.getDescription() );
        promotion.setCategory( categoryDTOToCategory( promotionDTO.getCategory() ) );

        promotion.setDiscountValue( promotionDTO.getDiscountPercentage() != null ? promotionDTO.getDiscountPercentage().doubleValue() : null );
        promotion.setStartDate( promotionDTO.getStartDate() != null ? promotionDTO.getStartDate().toLocalDate() : null );
        promotion.setEndDate( promotionDTO.getEndDate() != null ? promotionDTO.getEndDate().toLocalDate() : null );

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

        promotion.setPromotionName( promotionDTO.getPromotionName() );
        promotion.setDescription( promotionDTO.getDescription() );
        if ( promotionDTO.getCategory() != null ) {
            if ( promotion.getCategory() == null ) {
                promotion.setCategory( new Category() );
            }
            categoryDTOToCategory1( promotionDTO.getCategory(), promotion.getCategory() );
        }
        else {
            promotion.setCategory( null );
        }

        promotion.setDiscountValue( promotionDTO.getDiscountPercentage() != null ? promotionDTO.getDiscountPercentage().doubleValue() : null );
        promotion.setStartDate( promotionDTO.getStartDate() != null ? promotionDTO.getStartDate().toLocalDate() : null );
        promotion.setEndDate( promotionDTO.getEndDate() != null ? promotionDTO.getEndDate().toLocalDate() : null );
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

    protected Category categoryDTOToCategory(CategoryDTO categoryDTO) {
        if ( categoryDTO == null ) {
            return null;
        }

        Category category = new Category();

        category.setCategoryId( categoryDTO.getCategoryId() );
        category.setCategoryName( categoryDTO.getCategoryName() );
        category.setDescription( categoryDTO.getDescription() );

        return category;
    }

    protected void categoryDTOToCategory1(CategoryDTO categoryDTO, Category mappingTarget) {
        if ( categoryDTO == null ) {
            return;
        }

        mappingTarget.setCategoryId( categoryDTO.getCategoryId() );
        mappingTarget.setCategoryName( categoryDTO.getCategoryName() );
        mappingTarget.setDescription( categoryDTO.getDescription() );
    }
}
