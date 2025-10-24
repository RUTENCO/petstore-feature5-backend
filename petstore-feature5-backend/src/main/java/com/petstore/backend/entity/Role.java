package com.petstore.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "roles", schema = "public")
public class Role {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Integer roleId;
    
    @Column(name = "role_name", nullable = false, unique = true)
    private String roleName;
    
    // Constructores
    public Role() {}
    
    public Role(String roleName) {
        this.roleName = roleName;
    }
    
    // Getters y Setters
    public Integer getRoleId() {
        return roleId;
    }
    
    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }
    
    public String getRoleName() {
        return roleName;
    }
    
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}

