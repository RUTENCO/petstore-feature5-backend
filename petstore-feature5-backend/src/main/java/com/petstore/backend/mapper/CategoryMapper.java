package com.petstore.backend.mapper;

import com.petstore.backend.dto.CategoryDTO;
import com.petstore.backend.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * Mapper para transformaciones Category <-> CategoryDTO
 */
@Mapper(componentModel = "spring")
public interface CategoryMapper {
    
    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    /**
     * Convierte una entidad Category a CategoryDTO
     */
    CategoryDTO toDTO(Category category);

    /**
     * Convierte CategoryDTO a entidad Category
     */
    Category toEntity(CategoryDTO categoryDTO);

    /**
     * Convierte una lista de entidades Category a lista de CategoryDTO
     */
    java.util.List<CategoryDTO> toDTOList(java.util.List<Category> categories);

    /**
     * Convierte una lista de CategoryDTO a lista de entidades Category
     */
    java.util.List<Category> toEntityList(java.util.List<CategoryDTO> categoryDTOs);
}
