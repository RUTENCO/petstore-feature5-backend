package com.petstore.backend.exception;

/**
 * Custom exception for GraphQL operations
 */
public class GraphQLException extends RuntimeException {
    
    private final String operation;
    private final String details;
    
    public GraphQLException(String message) {
        super(message);
        this.operation = null;
        this.details = null;
    }
    
    public GraphQLException(String message, Throwable cause) {
        super(message, cause);
        this.operation = null;
        this.details = null;
    }
    
    public GraphQLException(String operation, String message, String details) {
        super(String.format("GraphQL %s operation failed: %s", operation, message));
        this.operation = operation;
        this.details = details;
    }
    
    public GraphQLException(String operation, String message, String details, Throwable cause) {
        super(String.format("GraphQL %s operation failed: %s", operation, message), cause);
        this.operation = operation;
        this.details = details;
    }
    
    public String getOperation() {
        return operation;
    }
    
    public String getDetails() {
        return details;
    }
}
