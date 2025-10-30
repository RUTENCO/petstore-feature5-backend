package com.petstore.backend.mapper;

import org.springframework.stereotype.Component;

/**
 * Clase central que proporciona acceso fácil a todos los mappers
 * Útil para inyección de dependencias centralizada
 */
@Component
public class MapperFacade {

    private final UserMapper userMapper;
    private final PromotionMapper promotionMapper;
    private final ProductMapper productMapper;
    private final CategoryMapper categoryMapper;

    public MapperFacade(UserMapper userMapper, PromotionMapper promotionMapper, 
                       ProductMapper productMapper, CategoryMapper categoryMapper) {
        this.userMapper = userMapper;
        this.promotionMapper = promotionMapper;
        this.productMapper = productMapper;
        this.categoryMapper = categoryMapper;
    }

    public UserMapper getUserMapper() {
        return userMapper;
    }

    public PromotionMapper getPromotionMapper() {
        return promotionMapper;
    }

    public ProductMapper getProductMapper() {
        return productMapper;
    }

    public CategoryMapper getCategoryMapper() {
        return categoryMapper;
    }
}
