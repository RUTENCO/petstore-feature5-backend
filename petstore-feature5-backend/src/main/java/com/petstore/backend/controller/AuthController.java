package com.petstore.backend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.petstore.backend.dto.LoginRequest;
import com.petstore.backend.dto.LoginResponse;
import com.petstore.backend.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001", "http://localhost:5173", "http://127.0.0.1:5500", "http://localhost:5500", "http://localhost:8081"})
public class AuthController {

    // Constantes para keys de respuesta (para cumplir con SonarQube)
    private static final String VALID_KEY = "valid";
    private static final String MESSAGE_KEY = "message";
    private static final String STATUS_KEY = "status";
    private static final String SERVICE_KEY = "service";
    private static final String TIMESTAMP_KEY = "timestamp";
    private static final String ENDPOINTS_KEY = "endpoints";

    @Autowired
    private AuthService authService;

    /**
     * Endpoint de prueba para verificar que el servicio funciona
     * GET /api/auth/status
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        return ResponseEntity.ok().body(Map.of(
            SERVICE_KEY, "Authentication Service",
            STATUS_KEY, "active",
            TIMESTAMP_KEY, java.time.LocalDateTime.now(),
            ENDPOINTS_KEY, List.of(
                "POST /api/auth/login - Login de Marketing Admin",
                "GET /api/auth/verify - Verificar token",
                "GET /api/auth/me - Obtener perfil del usuario",
                "POST /api/auth/logout - Logout",
                "GET /api/auth/status - Estado del servicio"
            )
        ));
    }

    /**
     * Endpoint para el login de Marketing Admin
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        try {
            // Validar que el usuario sea Marketing Admin
            LoginResponse response = authService.authenticateMarketingAdmin(
                loginRequest.getEmail(), 
                loginRequest.getPassword()
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            // En caso de error de autenticación
            LoginResponse errorResponse = new LoginResponse();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Email o contraseña incorrectos, o el usuario no tiene permisos de Marketing Admin");
            
            return ResponseEntity.status(401).body(errorResponse);
        }
    }

    /**
     * Endpoint para verificar si el token es válido
     * GET /api/auth/verify
     */
    @GetMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyToken(@RequestHeader("Authorization") String authHeader) {
        try {
            // Extraer el token del header Authorization
            String token = authHeader.substring(7); // Remover "Bearer "
            
            // Verificar el token
            boolean isValid = authService.validateToken(token);

            if (isValid) {
                // Devolver un Map.of() para una respuesta válida
                return ResponseEntity.ok().body(Map.of(VALID_KEY, true, MESSAGE_KEY, "Token válido"));
            } else {
                // Devolver un Map.of() para una respuesta inválida
                return ResponseEntity.status(401).body(Map.of(VALID_KEY, false, MESSAGE_KEY, "Token inválido"));
            }
            
        } catch (Exception e) {
            // Devolver un Map.of() para una respuesta con excepción
            return ResponseEntity.status(401).body(Map.of(VALID_KEY, false, MESSAGE_KEY, "Token inválido o malformado"));
        }
    }

    /**
     * Endpoint para obtener información del usuario actual
     * GET /api/auth/me
     */
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7); // Remover "Bearer "
            
            // Obtener información del usuario desde el token
            var userInfo = authService.getUserFromToken(token);
            
            return ResponseEntity.ok(userInfo);
            
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", "Token inválido"));
        }
    }

    /**
     * Endpoint para logout (opcional, principalmente para limpiar el token del frontend)
     * POST /api/auth/logout
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout() {
        // En JWT stateless, el logout se maneja en el frontend eliminando el token
        return ResponseEntity.ok().body(Map.of(MESSAGE_KEY, "Logout exitoso"));
    }
}
