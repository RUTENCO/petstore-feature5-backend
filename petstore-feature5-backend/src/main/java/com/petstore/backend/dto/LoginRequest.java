package com.petstore.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos de solicitud para login de usuario")
public class LoginRequest {
    @Schema(description = "Email del usuario", example = "admin@petstore.com", required = true)
    private String email;
    
    @Schema(description = "Contraseña del usuario", example = "password123", required = true)
    private String password;

    public LoginRequest() {
    }

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        LoginRequest that = (LoginRequest) obj;
        return java.util.Objects.equals(email, that.email) && 
               java.util.Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(email, password);
    }
}
