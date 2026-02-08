package com.anz.challenge.dto;

public class NotificationEvent {

    private Long orderId;
    private String status;
    private String description;

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public NotificationEvent() {
    }

    public NotificationEvent(Long orderId, String status, String description) {
        this.orderId = orderId;
        this.status = status;
        this.description = description; 
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
