package com.anz.challenge.service;

import com.anz.challenge.config.NotificationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    @Autowired
    private NotificationConfig config;

    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 1000; // 1 second delay between retries
    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    public void notifyStatusChange(Long orderId, String status) {
        int attempt = 0;
        boolean success = false;

        while (attempt < MAX_RETRIES && !success) {
            attempt++;
            try {
                // Send notifications if enabled
                if (config.isEmailEnabled()) {
                    sendEmail(orderId, status);
                }
                if (config.isSmsEnabled()) {
                    sendSms(orderId, status);
                }

                log.info("Notification sent successfully for order {} on attempt {}", orderId, attempt);
                success = true;

            } catch (Exception e) {
                log.error("Failed to send notification for order {} on attempt {}: {}", orderId, attempt, e.getMessage());

                if (attempt == MAX_RETRIES) {
                    log.error("Max retries reached for order {}. Giving up.", orderId);
                } else {
                    log.info("Retrying notification for order {} in {} ms...", orderId, RETRY_DELAY_MS);
                    try {
                        Thread.sleep(RETRY_DELAY_MS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        log.error("Retry sleep interrupted for order {}", orderId);
                    }
                }
            }
        }
    }

    // Simulated Email Notification
    private void sendEmail(Long orderId, String status) {
        // You can replace this with actual email service integration
        System.out.println("Email sent: Order " + orderId + " changed to " + status);

        // Optional: simulate occasional failure
        simulateFailure(orderId, "Email");
    }

    // Simulated SMS Notification
    private void sendSms(Long orderId, String status) {
        // You can replace this with actual SMS service integration
        System.out.println("SMS sent: Order " + orderId + " changed to " + status);

        // Optional: simulate occasional failure
        simulateFailure(orderId, "SMS");
    }

    // Simulate a random failure (for demonstration/testing)
    private void simulateFailure(Long orderId, String type) {
        if (Math.random() < 0.2) { // 20% chance to fail
            throw new RuntimeException(type + " service simulated failure for order " + orderId);
        }
    }
}
