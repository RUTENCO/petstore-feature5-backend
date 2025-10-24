package com.petstore.backend.mapper;

import com.petstore.backend.dto.CategoryDTO;
import com.petstore.backend.dto.ProductDTO;
import com.petstore.backend.entity.Category;
import com.petstore.backend.entity.Product;
import java.math.BigDecimal;
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
public class ProductMapperImpl implements ProductMapper {

    @Override
    public ProductDTO toDTO(Product product) {
        if ( product == null ) {
            return null;
        }

        ProductDTO productDTO = new ProductDTO();

        if ( product.getBasePrice() != null ) {
            productDTO.setPrice( BigDecimal.valueOf( product.getBasePrice() ) );
        }
        productDTO.setProductId( product.getProductId() );
        productDTO.setProductName( product.getProductName() );
        productDTO.setCategory( categoryToCategoryDTO( product.getCategory() ) );

        return productDTO;
    }

    @Override
    public Product toEntity(ProductDTO productDTO) {
        if ( productDTO == null ) {
            return null;
        }

        Product product = new Product();

        if ( productDTO.getPrice() != null ) {
            product.setBasePrice( productDTO.getPrice().doubleValue() );
        }
        product.setProductName( productDTO.getProductName() );
        product.setCategory( categoryDTOToCategory( productDTO.getCategory() ) );

        return product;
    }

    @Override
    public List<ProductDTO> toDTOList(List<Product> products) {
        if ( products == null ) {
            return null;
        }

        List<ProductDTO> list = new ArrayList<ProductDTO>( products.size() );
        for ( Product product : products ) {
            list.add( toDTO( product ) );
        }

        return list;
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
}
