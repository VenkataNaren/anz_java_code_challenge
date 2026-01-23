package com.anz.challenge.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "orders",
				indexes = {
				        @Index(name = "idx_orders_status", columnList = "status"),
				        @Index(name = "idx_orders_created_at", columnList = "createdAt"),
				        @Index(name = "idx_orders_status_created_at", columnList = "status, createdAt")
				    })
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Description must not be empty")
    private String description;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Status cannot be null")
    private Status status = Status.CREATED;

    @Column(name = "created_at", nullable = false, updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime createdAt;

    // Default constructor
    public Order() {}

    // Convenience constructor
    public Order(Long id, String description, Status status) {
        this.id = id;
        this.description = description;
        this.status = status;
    }

    // Auto-populate createdAt before inserting into DB
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Status getStatus() { return status; }

    @JsonSetter(nulls = Nulls.SKIP)
    public void setStatus(Status status) {
        this.status = (status == null) ? Status.CREATED : status;
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // Enum for Order status
    public enum Status {
        CREATED,
        COMPLETED,
        CANCELLED
    }
}
