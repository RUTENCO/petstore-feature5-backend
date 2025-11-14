package com.petstore.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO de respuesta para User - No expone información sensible como password
 */
@Schema(description = "DTO de respuesta para información de usuario (sin datos sensibles)")
public class UserResponseDTO {
    
    @Schema(description = "ID único del usuario", example = "1")
    private Integer userId;
    
    @Schema(description = "Nombre de usuario", example = "juan_perez")
    private String userName;
    
    @Schema(description = "Email del usuario", example = "juan@example.com")
    private String email;
    
    @Schema(description = "Rol del usuario", example = "ADMIN")
    private String roleName;

    // Constructors
    public UserResponseDTO() {}

    public UserResponseDTO(Integer userId, String userName, String email, String roleName) {
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.roleName = roleName;
    }

    // Getters and Setters
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    @Override
    public String toString() {
        return "UserResponseDTO{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", roleName='" + roleName + '\'' +
                '}';
    }
}
