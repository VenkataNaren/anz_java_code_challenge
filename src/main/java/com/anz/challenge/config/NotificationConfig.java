package com.anz.challenge.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import jakarta.annotation.PostConstruct;

@Configuration
@PropertySource("classpath:notification.properties")
public class NotificationConfig {
	
	private static final Logger log = LoggerFactory.getLogger(NotificationConfig.class);

	@Value("${notification.email.enabled}")
    private boolean emailEnabled;
	
	@Value("${notification.sms.enabled}")
    private boolean smsEnabled;

    public boolean isEmailEnabled() {
        return emailEnabled;
    }

    public void setEmailEnabled(boolean emailEnabled) {
        this.emailEnabled = emailEnabled;
    }

    public boolean isSmsEnabled() {
        return smsEnabled;
    }

    public void setSmsEnabled(boolean smsEnabled) {
        this.smsEnabled = smsEnabled;
    }
    
    @PostConstruct
    public void init() {
        log.info("Email enabled? " + emailEnabled);
        log.info("SMS enabled? " + smsEnabled);
    }
}
