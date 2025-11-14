package com.petstore.backend.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.Test;

class GraphQLExceptionTest {

    @Test
    void constructor_WithMessage_ShouldCreateException() {
        // Given
        String message = "Test error message";
        
        // When
        GraphQLException exception = new GraphQLException(message);
        
        // Then
        assertEquals(message, exception.getMessage());
        assertNull(exception.getOperation());
        assertNull(exception.getDetails());
        assertNull(exception.getCause());
    }
    
    @Test
    void constructor_WithMessageAndCause_ShouldCreateException() {
        // Given
        String message = "Test error message";
        Throwable cause = new RuntimeException("Cause exception");
        
        // When
        GraphQLException exception = new GraphQLException(message, cause);
        
        // Then
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
        assertNull(exception.getOperation());
        assertNull(exception.getDetails());
    }
    
    @Test
    void constructor_WithOperationMessageAndDetails_ShouldCreateException() {
        // Given
        String operation = "CREATE";
        String message = "Failed to create resource";
        String details = "Resource ID: 123";
        
        // When
        GraphQLException exception = new GraphQLException(operation, message, details);
        
        // Then
        assertEquals("GraphQL CREATE operation failed: Failed to create resource", exception.getMessage());
        assertEquals(operation, exception.getOperation());
        assertEquals(details, exception.getDetails());
        assertNull(exception.getCause());
    }
    
    @Test
    void constructor_WithOperationMessageDetailsAndCause_ShouldCreateException() {
        // Given
        String operation = "UPDATE";
        String message = "Failed to update resource";
        String details = "Resource ID: 456";
        Throwable cause = new IllegalArgumentException("Invalid input");
        
        // When
        GraphQLException exception = new GraphQLException(operation, message, details, cause);
        
        // Then
        assertEquals("GraphQL UPDATE operation failed: Failed to update resource", exception.getMessage());
        assertEquals(operation, exception.getOperation());
        assertEquals(details, exception.getDetails());
        assertEquals(cause, exception.getCause());
    }
    
    @Test
    void constructor_WithNullOperation_ShouldHandleGracefully() {
        // Given
        String operation = null;
        String message = "Test message";
        String details = "Test details";
        
        // When
        GraphQLException exception = new GraphQLException(operation, message, details);
        
        // Then
        assertEquals("GraphQL null operation failed: Test message", exception.getMessage());
        assertNull(exception.getOperation());
        assertEquals(details, exception.getDetails());
    }
    
    @Test
    void getters_ShouldReturnCorrectValues() {
        // Given
        String operation = "DELETE";
        String message = "Delete failed";
        String details = "Entity not found";
        Throwable cause = new RuntimeException("Database error");
        
        // When
        GraphQLException exception = new GraphQLException(operation, message, details, cause);
        
        // Then
        assertEquals(operation, exception.getOperation());
        assertEquals(details, exception.getDetails());
        assertEquals("GraphQL DELETE operation failed: Delete failed", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
    
    @Test
    void constructor_WithEmptyStrings_ShouldHandleGracefully() {
        // Given
        String operation = "";
        String message = "";
        String details = "";
        
        // When
        GraphQLException exception = new GraphQLException(operation, message, details);
        
        // Then
        assertEquals("GraphQL  operation failed: ", exception.getMessage());
        assertEquals("", exception.getOperation());
        assertEquals("", exception.getDetails());
    }
}
