package com.anz.challenge.model;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;

import jakarta.persistence.*;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    @Enumerated(EnumType.STRING)
    private Status status = Status.CREATED;

    public Order() {}

    public Order(Long id, String description, Status status) {
        this.id = id;
        this.description = description;
        this.status = status;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    @JsonSetter(nulls = Nulls.SKIP)
    public void setStatus(Status status) {
        if (status == null) {
            this.status = Status.CREATED;
        } else {
            this.status = status;
        }
    }

    public enum Status {
        CREATED,
        COMPLETED,
        CANCELLED
    }
}
