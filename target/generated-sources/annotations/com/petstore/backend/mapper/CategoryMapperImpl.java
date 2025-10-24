package com.petstore.backend.mapper;

import com.petstore.backend.dto.CategoryDTO;
import com.petstore.backend.entity.Category;
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
public class CategoryMapperImpl implements CategoryMapper {

    @Override
    public CategoryDTO toDTO(Category category) {
        if ( category == null ) {
            return null;
        }

        CategoryDTO categoryDTO = new CategoryDTO();

        categoryDTO.setCategoryId( category.getCategoryId() );
        categoryDTO.setCategoryName( category.getCategoryName() );
        categoryDTO.setDescription( category.getDescription() );

        return categoryDTO;
    }

    @Override
    public Category toEntity(CategoryDTO categoryDTO) {
        if ( categoryDTO == null ) {
            return null;
        }

        Category category = new Category();

        category.setCategoryId( categoryDTO.getCategoryId() );
        category.setCategoryName( categoryDTO.getCategoryName() );
        category.setDescription( categoryDTO.getDescription() );

        return category;
    }

    @Override
    public List<CategoryDTO> toDTOList(List<Category> categories) {
        if ( categories == null ) {
            return null;
        }

        List<CategoryDTO> list = new ArrayList<CategoryDTO>( categories.size() );
        for ( Category category : categories ) {
            list.add( toDTO( category ) );
        }

        return list;
    }

    @Override
    public List<Category> toEntityList(List<CategoryDTO> categoryDTOs) {
        if ( categoryDTOs == null ) {
            return null;
        }

        List<Category> list = new ArrayList<Category>( categoryDTOs.size() );
        for ( CategoryDTO categoryDTO : categoryDTOs ) {
            list.add( toEntity( categoryDTO ) );
        }

        return list;
    }
}
