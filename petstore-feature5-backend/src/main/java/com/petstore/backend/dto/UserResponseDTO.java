package com.petstore.backend.dto;

/**
 * DTO de respuesta para User - No expone información sensible como password
 */
public class UserResponseDTO {
    private Integer userId;
    private String userName;
    private String email;
    private String roleName;

    // Constructors
    public UserResponseDTO() {}

    public UserResponseDTO(Integer userId, String userName, String email, String roleName) {
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.roleName = roleName;
    }

    // Getters and Setters
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
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

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    @Override
    public String toString() {
        return "UserResponseDTO{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                ", roleName='" + roleName + '\'' +
                '}';
    }
}
