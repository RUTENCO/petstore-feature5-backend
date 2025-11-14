package com.petstore.backend.mapper;

import com.petstore.backend.dto.PromotionDeletedDTO;
import com.petstore.backend.entity.Category;
import com.petstore.backend.entity.PromotionDeleted;
import com.petstore.backend.entity.Status;
import com.petstore.backend.entity.User;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-14T10:05:42-0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 24.0.2 (Oracle Corporation)"
)
@Component
public class PromotionDeletedMapperImpl implements PromotionDeletedMapper {

    @Override
    public PromotionDeletedDTO toDTO(PromotionDeleted promotionDeleted) {
        if ( promotionDeleted == null ) {
            return null;
        }

        PromotionDeletedDTO promotionDeletedDTO = new PromotionDeletedDTO();

        promotionDeletedDTO.setStatusName( promotionDeletedStatusStatusName( promotionDeleted ) );
        promotionDeletedDTO.setUserName( promotionDeletedUserUserName( promotionDeleted ) );
        promotionDeletedDTO.setCategoryName( promotionDeletedCategoryCategoryName( promotionDeleted ) );
        promotionDeletedDTO.setDeletedByUserName( promotionDeletedDeletedByUserName( promotionDeleted ) );
        promotionDeletedDTO.setPromotionId( promotionDeleted.getPromotionId() );
        promotionDeletedDTO.setPromotionName( promotionDeleted.getPromotionName() );
        promotionDeletedDTO.setDescription( promotionDeleted.getDescription() );
        promotionDeletedDTO.setStartDate( promotionDeleted.getStartDate() );
        promotionDeletedDTO.setEndDate( promotionDeleted.getEndDate() );
        promotionDeletedDTO.setDiscountValue( promotionDeleted.getDiscountValue() );
        promotionDeletedDTO.setDeletedAt( promotionDeleted.getDeletedAt() );
        promotionDeletedDTO.setStatus( promotionDeleted.getStatus() );
        promotionDeletedDTO.setUser( promotionDeleted.getUser() );
        promotionDeletedDTO.setCategory( promotionDeleted.getCategory() );
        promotionDeletedDTO.setDeletedBy( promotionDeleted.getDeletedBy() );

        return promotionDeletedDTO;
    }

    @Override
    public PromotionDeleted toEntity(PromotionDeletedDTO promotionDeletedDTO) {
        if ( promotionDeletedDTO == null ) {
            return null;
        }

        PromotionDeleted promotionDeleted = new PromotionDeleted();

        promotionDeleted.setPromotionId( promotionDeletedDTO.getPromotionId() );
        promotionDeleted.setPromotionName( promotionDeletedDTO.getPromotionName() );
        promotionDeleted.setDescription( promotionDeletedDTO.getDescription() );
        promotionDeleted.setStartDate( promotionDeletedDTO.getStartDate() );
        promotionDeleted.setEndDate( promotionDeletedDTO.getEndDate() );
        promotionDeleted.setDiscountValue( promotionDeletedDTO.getDiscountValue() );

        return promotionDeleted;
    }

    private String promotionDeletedStatusStatusName(PromotionDeleted promotionDeleted) {
        if ( promotionDeleted == null ) {
            return null;
        }
        Status status = promotionDeleted.getStatus();
        if ( status == null ) {
            return null;
        }
        String statusName = status.getStatusName();
        if ( statusName == null ) {
            return null;
        }
        return statusName;
    }

    private String promotionDeletedUserUserName(PromotionDeleted promotionDeleted) {
        if ( promotionDeleted == null ) {
            return null;
        }
        User user = promotionDeleted.getUser();
        if ( user == null ) {
            return null;
        }
        String userName = user.getUserName();
        if ( userName == null ) {
            return null;
        }
        return userName;
    }

    private String promotionDeletedCategoryCategoryName(PromotionDeleted promotionDeleted) {
        if ( promotionDeleted == null ) {
            return null;
        }
        Category category = promotionDeleted.getCategory();
        if ( category == null ) {
            return null;
        }
        String categoryName = category.getCategoryName();
        if ( categoryName == null ) {
            return null;
        }
        return categoryName;
    }

    private String promotionDeletedDeletedByUserName(PromotionDeleted promotionDeleted) {
        if ( promotionDeleted == null ) {
            return null;
        }
        User deletedBy = promotionDeleted.getDeletedBy();
        if ( deletedBy == null ) {
            return null;
        }
        String userName = deletedBy.getUserName();
        if ( userName == null ) {
            return null;
        }
        return userName;
    }
}
