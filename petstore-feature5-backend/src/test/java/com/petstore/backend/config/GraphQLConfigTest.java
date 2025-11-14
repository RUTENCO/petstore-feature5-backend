package com.petstore.backend.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GraphQLConfigTest {

    private GraphQLConfig graphQLConfig;

    @BeforeEach
    void setUp() {
        graphQLConfig = new GraphQLConfig();
    }

    @Test
    void constructor_ShouldCreateGraphQLConfig() {
        // Then
        assertNotNull(graphQLConfig);
    }

    @Test
    void graphQLConfig_ShouldImplementWebMvcConfigurer() {
        // Then
        assertTrue(graphQLConfig instanceof org.springframework.web.servlet.config.annotation.WebMvcConfigurer);
    }

    @Test
    void graphQLConfig_ShouldNotBeNull() {
        // Given
        GraphQLConfig config = new GraphQLConfig();

        // Then  
        assertNotNull(config, "GraphQLConfig should not be null");
    }

    @Test
    void graphQLConfig_ShouldBeInstanceOfWebMvcConfigurer() {
        // Given
        GraphQLConfig config = new GraphQLConfig();

        // Then
        assertTrue(config instanceof org.springframework.web.servlet.config.annotation.WebMvcConfigurer,
                "GraphQLConfig should implement WebMvcConfigurer");
    }
}
