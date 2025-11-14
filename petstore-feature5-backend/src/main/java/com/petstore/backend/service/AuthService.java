package com.petstore.backend.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.petstore.backend.dto.LoginResponse;
import com.petstore.backend.entity.User;
import com.petstore.backend.repository.UserRepository;
import com.petstore.backend.util.JwtUtil;

@Service
public class AuthService implements UserDetailsService {

    private final UserRepository userRepository; // Inyección de dependencia del repositorio de usuarios
    private final JwtUtil jwtUtil; // Inyección de dependencia de la utilidad JWT
    // private final PasswordEncoder passwordEncoder; // Inyección de dependencia del codificador de contraseñas

    public AuthService(UserRepository userRepository, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        // this.passwordEncoder = passwordEncoder;
    }


    /**
     * Autenticar específicamente un Marketing Admin
     */
    public LoginResponse authenticateMarketingAdmin(String email, String password) {
        // Buscar usuario que sea Marketing Admin
        User user = userRepository.findMarketingAdminByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado o no es Marketing Admin"));

        // Verificar contraseña (en producción usar passwordEncoder.matches)
        /* if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        } */

        // Verificar contraseña para pruebas (sin encriptar)
        if (!password.equals(user.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        // Generar token JWT
        String token = jwtUtil.generateToken(user.getEmail());
        
        // Crear respuesta
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUserName(user.getUserName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole().getRoleName());
        response.setSuccess(true);
        response.setMessage("Login exitoso");
        
        return response;
    }

    /**
     * Validar token JWT
     */
    public boolean validateToken(String token) {
        try {
            return jwtUtil.validateToken(token);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Obtener información del usuario desde el token
     */
    public Map<String, Object> getUserFromToken(String token) {
        if (!jwtUtil.validateToken(token)) {
            throw new RuntimeException("Token inválido");
        }
        
        String email = jwtUtil.getEmailFromToken(token);
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("userId", user.getUserId());
        userInfo.put("userName", user.getUserName());
        userInfo.put("email", user.getEmail());
        userInfo.put("role", user.getRole().getRoleName());
        userInfo.put("roleId", user.getRole().getRoleId());
        
        return userInfo;
    }

    /**
     * Implementación de UserDetailsService para Spring Security
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().getRoleName().replace(" ", "_")) // Convertir "Marketing Admin" a "Marketing_Admin"
                .build();
    }

    /**
     * Verificar si un usuario existe y es Marketing Admin
     */
    public boolean isMarketingAdmin(String email) {
        return userRepository.findMarketingAdminByEmail(email).isPresent();
    }

    /**
     * Login method para GraphQL que retorna Map
     */
    public Map<String, Object> login(String email, String password) {
        try {
            LoginResponse response = authenticateMarketingAdmin(email, password);
            
            // Convertir LoginResponse a Map para GraphQL
            Map<String, Object> result = new HashMap<>();
            result.put("success", response.isSuccess());
            result.put("token", response.getToken());
            
            // Obtener el usuario
            User user = userRepository.findByEmail(email).orElse(null);
            if (user != null) {
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("userId", user.getUserId());
                userMap.put("userName", user.getUserName());
                userMap.put("email", user.getEmail());
                
                // Agregar role info
                if (user.getRole() != null) {
                    Map<String, Object> roleMap = new HashMap<>();
                    roleMap.put("roleId", user.getRole().getRoleId());
                    roleMap.put("roleName", user.getRole().getRoleName());
                    userMap.put("role", roleMap);
                }
                
                result.put("user", userMap);
            }
            
            return result;
            
        } catch (Exception e) {
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("token", "");
            errorResult.put("user", new HashMap<>());
            return errorResult;
        }
    }
}


