package com.petstore.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "statuses", schema = "public")
public class Status {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "status_id")
    private Integer statusId;
    
    @Column(name = "status_name", nullable = false, unique = true)
    private String statusName;
    
    // Constructores
    public Status() {}
    
    public Status(String statusName) {
        this.statusName = statusName;
    }
    
    // Getters y Setters
    public Integer getStatusId() {
        return statusId;
    }
    
    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }
    
    public String getStatusName() {
        return statusName;
    }
    
    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }
}
