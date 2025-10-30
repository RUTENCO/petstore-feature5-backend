package com.petstore.backend.mapper;

import com.petstore.backend.dto.ProductDTO;
import com.petstore.backend.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import org.mapstruct.factory.Mappers;

/**
 * Mapper para transformaciones Product <-> ProductDTO
 */
@Mapper(componentModel = "spring")
public interface ProductMapper {
    
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    /**
     * Convierte una entidad Product a ProductDTO
     */
    @Mapping(target = "price", source = "basePrice")
    @Mapping(target = "stock", ignore = true)
    @Mapping(target = "imageUrl", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ProductDTO toDTO(Product product);

    /**
     * Convierte ProductDTO a entidad Product
     */
    @Mapping(target = "productId", ignore = true)
    @Mapping(target = "basePrice", source = "price")
    @Mapping(target = "sku", ignore = true)
    @Mapping(target = "promotion", ignore = true)
    Product toEntity(ProductDTO productDTO);

    /**
     * Convierte una lista de entidades Product a lista de ProductDTO
     */
    java.util.List<ProductDTO> toDTOList(java.util.List<Product> products);
}
