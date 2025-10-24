package com.petstore.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.petstore.backend.entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    
    // Buscar usuario por email con role cargado
    @Query("SELECT u FROM User u JOIN FETCH u.role WHERE u.email = :email")
    Optional<User> findByEmail(@Param("email") String email);
    
    // Buscar usuario por email y verificar que sea Marketing Admin
    @Query("SELECT u FROM User u JOIN u.role r WHERE u.email = :email AND r.roleName = 'Marketing Admin'")
    Optional<User> findMarketingAdminByEmail(@Param("email") String email);
    
    // Verificar si existe un usuario con el email
    boolean existsByEmail(String email);
    
    // Buscar usuarios por rol
    @Query("SELECT u FROM User u JOIN u.role r WHERE r.roleName = :roleName")
    Optional<User> findByRoleName(@Param("roleName") String roleName);
}
