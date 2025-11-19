package com.petstore.backend.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.petstore.backend.dto.LoginResponse;
import com.petstore.backend.entity.Role;
import com.petstore.backend.entity.User;
import com.petstore.backend.repository.RoleRepository;
import com.petstore.backend.repository.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @BeforeEach
    void setUp() {
        // Crear rol Marketing Admin si no existe
        java.util.Optional<Role> roleOptional = roleRepository.findByRoleName("Marketing Admin");
        Role marketingAdminRole;
        if (roleOptional.isPresent()) {
            marketingAdminRole = roleOptional.get();
        } else {
            marketingAdminRole = new Role("Marketing Admin");
            roleRepository.save(marketingAdminRole);
        }
        
        // Crear usuario admin@petstore.com si no existe
        if (!userRepository.findByEmail("admin@petstore.com").isPresent()) {
            User adminUser = new User();
            adminUser.setUserName("Admin User");
            adminUser.setEmail("admin@petstore.com");
            adminUser.setPassword(passwordEncoder.encode("admin123")); // Contraseña cifrada
            adminUser.setRole(marketingAdminRole);
            userRepository.save(adminUser);
            
            System.out.println("Usuario creado: " + adminUser.getEmail());
            System.out.println("Rol: " + adminUser.getRole().getRoleName());
            System.out.println("Password hash: " + adminUser.getPassword());
        }
    }

    @Test
    void testGetStatus() throws Exception {
        mockMvc.perform(get("/api/auth/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.service", is("Authentication Service")))
                .andExpect(jsonPath("$.status", is("active")))
                .andExpect(jsonPath("$.timestamp", notNullValue()))
                .andExpect(jsonPath("$.endpoints", hasSize(6)));
    }

    @Test
    void testGetStatus_CheckAllEndpoints() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/auth/status"))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        System.out.println("Status response: " + content);
        
        mockMvc.perform(get("/api/auth/status"))
                .andExpect(jsonPath("$.endpoints[0]", is("POST /api/auth/login - Login de Marketing Admin")))
                .andExpect(jsonPath("$.endpoints[1]", is("GET /api/auth/verify - Verificar token")))
                .andExpect(jsonPath("$.endpoints[2]", is("GET /api/auth/me - Obtener perfil del usuario")))
                .andExpect(jsonPath("$.endpoints[3]", is("POST /api/auth/logout - Logout")))
                .andExpect(jsonPath("$.endpoints[4]", is("POST /api/auth/encrypt-password - Cifrar contraseña")))
                .andExpect(jsonPath("$.endpoints[5]", is("GET /api/auth/status - Estado del servicio")));
    }

    @Test
    void testLoginEndpoint_ValidCredentials() throws Exception {
        String loginRequest = """
            {
                "email": "admin@petstore.com",
                "password": "admin123"
            }""";

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.userName", is("Admin User")))
                .andExpect(jsonPath("$.email", is("admin@petstore.com")))
                .andExpect(jsonPath("$.role", is("Marketing Admin")));
    }

    @Test
    void testLoginEndpoint_InvalidCredentials() throws Exception {
        String loginRequest = """
            {
                "email": "admin@petstore.com",
                "password": "wrongpassword"
            }""";

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginRequest))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Email o contraseña incorrectos, o el usuario no tiene permisos de Marketing Admin")));
    }

    @Test
    void testLoginEndpoint_UserNotFound() throws Exception {
        String loginRequest = """
            {
                "email": "nonexistent@petstore.com",
                "password": "admin123"
            }""";

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginRequest))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", is("Email o contraseña incorrectos, o el usuario no tiene permisos de Marketing Admin")));
    }

    @Test
    void testLoginEndpoint_MissingEmail() throws Exception {
        String loginRequest = """
            {
                "password": "admin123"
            }""";

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginRequest))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success", is(false)));
    }

    @Test
    void testLoginEndpoint_MissingPassword() throws Exception {
        String loginRequest = """
            {
                "email": "admin@petstore.com"
            }""";

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginRequest))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success", is(false)));
    }

    @Test
    void testEncryptPasswordEndpoint_ValidPassword() throws Exception {
        String request = """
            {
                "password": "testpassword123"
            }""";

        mockMvc.perform(post("/api/auth/encrypt-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rawPassword", is("testpassword123")))
                .andExpect(jsonPath("$.encryptedPassword", notNullValue()))
                .andExpect(jsonPath("$.message", is("Contraseña cifrada exitosamente")));
    }

    @Test
    void testEncryptPasswordEndpoint_EmptyPassword() throws Exception {
        String request = """
            {
                "password": ""
            }""";

        mockMvc.perform(post("/api/auth/encrypt-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("La contraseña no puede estar vacía")));
    }

    @Test
    void testEncryptPasswordEndpoint_NullPassword() throws Exception {
        String request = """
            {
                "password": null
            }""";

        mockMvc.perform(post("/api/auth/encrypt-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("La contraseña no puede estar vacía")));
    }

    @Test
    void testEncryptPasswordEndpoint_MissingPasswordField() throws Exception {
        String request = """
            {}""";

        mockMvc.perform(post("/api/auth/encrypt-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("La contraseña no puede estar vacía")));
    }

    @Test
    void testVerifyEndpoint_WithValidToken() throws Exception {
        // Primero hacer login para obtener un token válido
        String loginRequest = """
            {
                "email": "admin@petstore.com",
                "password": "admin123"
            }""";

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginRequest))
                .andExpect(status().isOk())
                .andReturn();

        LoginResponse loginResponse = objectMapper.readValue(
            loginResult.getResponse().getContentAsString(), 
            LoginResponse.class
        );

        // Usar el token para verificar
        mockMvc.perform(get("/api/auth/verify")
                .header("Authorization", "Bearer " + loginResponse.getToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid", is(true)))
                .andExpect(jsonPath("$.message", is("Token válido")));
    }

    @Test
    void testVerifyEndpoint_WithoutToken() throws Exception {
        mockMvc.perform(get("/api/auth/verify"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetCurrentUser_WithValidToken() throws Exception {
        // Primero hacer login para obtener un token válido
        String loginRequest = """
            {
                "email": "admin@petstore.com",
                "password": "admin123"
            }""";

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginRequest))
                .andExpect(status().isOk())
                .andReturn();

        LoginResponse loginResponse = objectMapper.readValue(
            loginResult.getResponse().getContentAsString(), 
            LoginResponse.class
        );

        // Usar el token para obtener información del usuario
        mockMvc.perform(get("/api/auth/me")
                .header("Authorization", "Bearer " + loginResponse.getToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName", is("Admin User")))
                .andExpect(jsonPath("$.email", is("admin@petstore.com")))
                .andExpect(jsonPath("$.role", is("Marketing Admin")));
    }

    @Test
    void testGetCurrentUser_WithoutToken() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testOptions_CorsRequest() throws Exception {
        mockMvc.perform(options("/api/auth/login")
                .header("Origin", "http://localhost:3000")
                .header("Access-Control-Request-Method", "POST"))
                .andExpect(status().isOk());
    }

    @Test
    void testLogoutEndpoint() throws Exception {
        // Primero hacer login para obtener un token válido
        String loginRequest = """
            {
                "email": "admin@petstore.com",
                "password": "admin123"
            }""";

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginRequest))
                .andExpect(status().isOk())
                .andReturn();

        LoginResponse loginResponse = objectMapper.readValue(
            loginResult.getResponse().getContentAsString(), 
            LoginResponse.class
        );

        // Hacer logout con el token
        mockMvc.perform(post("/api/auth/logout")
                .header("Authorization", "Bearer " + loginResponse.getToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Logout exitoso")));
    }

    @Test
    void testLogoutEndpoint_WithoutToken() throws Exception {
        mockMvc.perform(post("/api/auth/logout"))
                .andExpect(status().isBadRequest());
    }
}
