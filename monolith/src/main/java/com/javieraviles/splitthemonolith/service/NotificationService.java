package com.javieraviles.splitthemonolith.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.javieraviles.splitthemonolith.entity.Customer;

@Component
public class NotificationService {

	Logger logger = LoggerFactory.getLogger(NotificationService.class);

	public void sendEmailNotification(final Customer updatedCustomer) {
		// here there would be some implementation to send an email with credit change
		// details
		logger.info("Sending email to notify customer {}, new account balance is {}", updatedCustomer.getName(),
				updatedCustomer.getCredit());
	}
}