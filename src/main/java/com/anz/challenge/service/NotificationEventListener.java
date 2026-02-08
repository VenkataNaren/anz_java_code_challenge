package com.anz.challenge.service;

import com.anz.challenge.dto.NotificationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

@Service
public class NotificationEventListener {

    private static final Logger log = LoggerFactory.getLogger(NotificationEventListener.class);

    private final NotificationService notificationService;

    public NotificationEventListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @JmsListener(destination = "${notification.queue.name}")
    public void onMessage(NotificationEvent event) {
        if (event == null) {
            log.warn("Received null notification event. Skipping.");
            return;
        }
        log.info("Received notification event: orderId={}, status={}, description={}", event.getOrderId(), event.getStatus(), event.getDescription());
        notificationService.notifyStatusChange(event.getOrderId(), event.getStatus());
    }
}
