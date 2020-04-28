package com.javieraviles.notificationsms.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.javieraviles.notificationsms.dto.NotificationDto;

@Component
public class NotificationService {

	Logger logger = LoggerFactory.getLogger(NotificationService.class);

	public void sendEmailNotification(final NotificationDto notification) {
		// here there would be some implementation to send an email with credit change
		// details
		logger.info("Sending email to notify customer {}, new account balance is {}", notification.getCustomerEmail(),
				notification.getCredit());
	}
}