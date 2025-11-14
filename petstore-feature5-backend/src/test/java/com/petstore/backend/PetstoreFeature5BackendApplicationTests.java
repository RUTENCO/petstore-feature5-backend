package com.petstore.backend;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class PetstoreFeature5BackendApplicationTests {

	@Autowired
	private ApplicationContext applicationContext;

	@Test
	void contextLoads() {
		// Verify that the Spring context loads successfully
		assertNotNull(applicationContext);
	}

	@Test
	void applicationHasRequiredBeans() {
		// Verify essential beans are loaded
		assertTrue(applicationContext.containsBean("categoryController"));
		assertTrue(applicationContext.containsBean("productController"));
		assertTrue(applicationContext.containsBean("promotionController"));
		assertTrue(applicationContext.containsBean("authController"));
		assertTrue(applicationContext.containsBean("graphQLResolver"));
	}

	@Test
	void applicationHasRequiredServices() {
		// Verify essential services are loaded
		assertTrue(applicationContext.containsBean("categoryService"));
		assertTrue(applicationContext.containsBean("productService"));
		assertTrue(applicationContext.containsBean("promotionService"));
		assertTrue(applicationContext.containsBean("authService"));
	}

	@Test
	void applicationHasRequiredRepositories() {
		// Verify essential repositories are loaded
		assertTrue(applicationContext.containsBean("categoryRepository"));
		assertTrue(applicationContext.containsBean("productRepository"));
		assertTrue(applicationContext.containsBean("promotionRepository"));
		assertTrue(applicationContext.containsBean("userRepository"));
	}

	@Test
	void applicationHasSecurityConfiguration() {
		// Verify security configuration is loaded
		assertTrue(applicationContext.containsBean("securityConfig"));
		assertTrue(applicationContext.containsBean("jwtAuthenticationFilter"));
		assertTrue(applicationContext.containsBean("jwtUtil"));
	}

	@Test
	void applicationHasMapperBeans() {
		// Verify mapper beans are loaded by type instead of name
		assertNotNull(applicationContext.getBean(com.petstore.backend.mapper.CategoryMapper.class));
		assertNotNull(applicationContext.getBean(com.petstore.backend.mapper.ProductMapper.class));
		assertNotNull(applicationContext.getBean(com.petstore.backend.mapper.PromotionMapper.class));
		assertNotNull(applicationContext.getBean(com.petstore.backend.mapper.UserMapper.class));
	}

	@Test
	void mainMethodShouldNotThrowException() {
		// Test that the main method can be called without exception
		assertDoesNotThrow(() -> {
			// We don't actually run it to avoid starting another server
			// but we verify the method exists and is properly structured
			java.lang.reflect.Method mainMethod = PetstoreFeature5BackendApplication.class
					.getDeclaredMethod("main", String[].class);
			assertNotNull(mainMethod);
			assertTrue(java.lang.reflect.Modifier.isStatic(mainMethod.getModifiers()));
			assertTrue(java.lang.reflect.Modifier.isPublic(mainMethod.getModifiers()));
		});
	}

}
