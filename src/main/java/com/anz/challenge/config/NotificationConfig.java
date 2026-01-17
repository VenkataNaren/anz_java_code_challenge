package com.anz.challenge.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "notification")
@Data
public class NotificationConfig {
    private boolean emailEnabled;
    private boolean smsEnabled;
}
