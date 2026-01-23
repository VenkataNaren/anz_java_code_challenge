package com.anz.challenge.service;

import com.anz.challenge.config.NotificationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class NotificationService {

    @Autowired
    private NotificationConfig config;

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    // Simple in-memory store for idempotency
    private final ConcurrentHashMap<String, Boolean> notificationLog = new ConcurrentHashMap<>();

    /**
     * Trigger notification with retry.
     * Uses Spring Retry to automatically retry on transient failures.
     */
    @Retryable(
        value = { RuntimeException.class }, // Retry on transient failures
        maxAttempts = 3,
        backoff = @Backoff(delay = 1000, multiplier = 2, maxDelay = 8000)
    )
    public void notifyStatusChange(Long orderId, String status) {

        // Idempotency key: prevents duplicate notifications
        String key = orderId + ":" + status;
        if (notificationLog.putIfAbsent(key, true) != null) {
            log.info("Notification already sent for order {} with status {}. Skipping.", orderId, status);
            return;
        }
        log.info("Email enabled? " + config.isEmailEnabled());
        log.info("SMS enabled? " + config.isSmsEnabled());
        // Send notifications if enabled
        if (config.isEmailEnabled()) {
            sendEmail(orderId, status);
        }
        if (config.isSmsEnabled()) {
            sendSms(orderId, status);
        }

        log.info("Notification processed successfully for order {} with status {}", orderId, status);
    }

    /**
     * Recovery method if retries are exhausted.
     */
    @Recover
    public void recover(RuntimeException ex, Long orderId, String status) {
        log.error("Notification failed after retries for order {} with status {}: {}", orderId, status, ex.getMessage());
        // Optional: persist to DB, send alert, or push to DLQ
    }

    // Simulated Email Notification
    private void sendEmail(Long orderId, String status) {
        log.info("Email sent: Order {} changed to {}", orderId, status);

        // Optional: simulate occasional failure
        simulateFailure(orderId, "Email");
    }

    // Simulated SMS Notification
    private void sendSms(Long orderId, String status) {
        log.info("SMS sent: Order {} changed to {}", orderId, status);

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
