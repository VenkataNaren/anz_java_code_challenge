package com.anz.challenge.service;

import com.anz.challenge.dto.NotificationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(NotificationEventPublisher.class);

    private final JmsTemplate jmsTemplate;
    private final String queueName;

    public NotificationEventPublisher(JmsTemplate jmsTemplate,
                                      @Value("${notification.queue.name}") String queueName) {
        this.jmsTemplate = jmsTemplate;
        this.queueName = queueName;
    }

    public void publish(NotificationEvent event) {
        jmsTemplate.convertAndSend(queueName, event);
        log.info("Published notification event: orderId={}, status={}", event.getOrderId(), event.getStatus());
    }
}
