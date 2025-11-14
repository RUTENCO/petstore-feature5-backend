package com.petstore.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Respuesta del proceso de autenticación")
public class LoginResponse {
    
    @Schema(description = "Token JWT para autenticación", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;
    
    @Schema(description = "Nombre del usuario", example = "juan_perez")
    private String userName;
    
    @Schema(description = "Email del usuario", example = "juan@example.com")
    private String email;
    
    @Schema(description = "Rol del usuario", example = "ADMIN", allowableValues = {"ADMIN", "USER", "MANAGER"})
    private String role;
    
    @Schema(description = "Indica si el login fue exitoso", example = "true")
    private boolean success;
    
    @Schema(description = "Mensaje descriptivo del resultado", example = "Login exitoso")
    private String message;

    // Constructores
    public LoginResponse() {
    }

    public LoginResponse(String token, String userName, String email, String role) {
        this.token = token;
        this.userName = userName;
        this.email = email;
        this.role = role;
        this.success = true;
    }

    // Getters y Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
