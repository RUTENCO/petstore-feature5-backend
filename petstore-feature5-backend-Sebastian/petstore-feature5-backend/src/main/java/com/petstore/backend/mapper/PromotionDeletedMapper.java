package com.petstore.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.petstore.backend.dto.PromotionDeletedDTO;
import com.petstore.backend.entity.PromotionDeleted;

@Mapper(componentModel = "spring")
public interface PromotionDeletedMapper {
    
    PromotionDeletedMapper INSTANCE = Mappers.getMapper(PromotionDeletedMapper.class);
    
    @Mapping(source = "status.statusName", target = "statusName")
    @Mapping(source = "user.userName", target = "userName")
    @Mapping(source = "category.categoryName", target = "categoryName")
    @Mapping(source = "deletedBy.userName", target = "deletedByUserName")
    @Mapping(target = "daysUntilPurge", ignore = true)
    PromotionDeletedDTO toDTO(PromotionDeleted promotionDeleted);
    
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    PromotionDeleted toEntity(PromotionDeletedDTO promotionDeletedDTO);
}
