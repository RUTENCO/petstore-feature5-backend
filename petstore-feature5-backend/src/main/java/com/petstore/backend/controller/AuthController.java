package com.petstore.backend.controller;

import java.util.List;
import java.util.Map;

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
import com.petstore.backend.dto.UserResponseDTO;
import com.petstore.backend.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080", "https://petstore-feature5-backend.onrender.com", "https://fluffy-deals-hub.vercel.app"})
@Tag(name = "Autenticación", description = "API para autenticación y autorización de usuarios")
public class AuthController {

    // Constantes para keys de respuesta (para cumplir con SonarQube)
    private static final String VALID_KEY = "valid";
    private static final String MESSAGE_KEY = "message";
    private static final String STATUS_KEY = "status";
    private static final String SERVICE_KEY = "service";
    private static final String TIMESTAMP_KEY = "timestamp";
    private static final String ENDPOINTS_KEY = "endpoints";

    
    private final AuthService authService; // Inyección de dependencia del servicio de autenticación

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(
            summary = "Estado del servicio de autenticación",
            description = "Endpoint de health check que retorna el estado del servicio y lista de endpoints disponibles"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", 
                    description = "Estado del servicio obtenido exitosamente",
                    content = @Content(mediaType = "application/json")
            )
    })
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
    @Operation(
        summary = "Iniciar sesión",
        description = "Autentica a un usuario Marketing Admin y devuelve un token JWT"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login exitoso",
                content = @Content(mediaType = "application/json", 
                          schema = @Schema(implementation = LoginResponse.class))),
        @ApiResponse(responseCode = "401", description = "Credenciales inválidas"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Parameter(description = "Credenciales de login", required = true)
            @RequestBody LoginRequest loginRequest) {
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

    @Operation(
            summary = "Verificar token JWT",
            description = "Verifica si el token JWT proporcionado es válido y no ha expirado"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", 
                    description = "Token verificado exitosamente",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "401", 
                    description = "Token inválido o expirado",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500", 
                    description = "Error interno del servidor",
                    content = @Content
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyToken(
            @Parameter(description = "Token JWT en formato 'Bearer {token}'", required = true)
            @RequestHeader("Authorization") String authHeader) {
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

    @Operation(
            summary = "Obtener perfil del usuario actual",
            description = "Retorna la información del usuario autenticado basada en el token JWT"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", 
                    description = "Información del usuario obtenida exitosamente",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401", 
                    description = "Token inválido o usuario no encontrado",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "500", 
                    description = "Error interno del servidor",
                    content = @Content
            )
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(
            @Parameter(description = "Token JWT en formato 'Bearer {token}'", required = true)
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7); // Remover "Bearer "
            
            // Obtener información del usuario desde el token
            var userInfo = authService.getUserFromToken(token);
            
            return ResponseEntity.ok(userInfo);
            
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", "Token inválido"));
        }
    }

    @Operation(
            summary = "Cerrar sesión",
            description = "Endpoint para logout. En JWT stateless, el token se elimina en el frontend"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", 
                    description = "Logout exitoso",
                    content = @Content(mediaType = "application/json")
            )
    })
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout() {
        // En JWT stateless, el logout se maneja en el frontend eliminando el token
        return ResponseEntity.ok().body(Map.of(MESSAGE_KEY, "Logout exitoso"));
    }
}
