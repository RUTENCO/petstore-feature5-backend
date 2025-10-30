package com.petstore.backend.mapper;

import com.petstore.backend.dto.UserResponseDTO;
import com.petstore.backend.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * Mapper para transformaciones User <-> UserResponseDTO
 */
@Mapper(componentModel = "spring")
public interface UserMapper {
    
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    /**
     * Convierte una entidad User a UserResponseDTO
     * Mapea role.roleName a roleName para aplanar la estructura
     */
    @Mapping(target = "roleName", source = "role.roleName")
    UserResponseDTO toResponseDTO(User user);

    /**
     * Convierte una lista de entidades User a lista de UserResponseDTO
     */
    java.util.List<UserResponseDTO> toResponseDTOList(java.util.List<User> users);
}
