package com.anz.challenge.dto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public record OrderSummary(Long id, String description, String status) {
	
	private static final Logger logger = LoggerFactory.getLogger(OrderSummary.class);
	
	  public void logDetails() {
	        logger.info("Order ID: {}", id());
	        logger.info("Description: {}", description());
	        logger.info("Status: {}", status());
	    }

}