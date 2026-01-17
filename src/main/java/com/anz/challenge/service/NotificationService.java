package com.anz.challenge.service;

import com.anz.challenge.config.NotificationConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationConfig config;

    public void notifyStatusChange(Long orderId, String status) {
        if(config.isEmailEnabled()) {
            System.out.println("Email sent: Order " + orderId + " changed to " + status);
        }
        if(config.isSmsEnabled()) {
            System.out.println("SMS sent: Order " + orderId + " changed to " + status);
        }
    }
}
