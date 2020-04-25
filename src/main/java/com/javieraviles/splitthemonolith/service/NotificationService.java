package com.javieraviles.splitthemonolith.service;

import com.javieraviles.splitthemonolith.entity.Order;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class NotificationService {

    Logger logger = LoggerFactory.getLogger(NotificationService.class);

    public void sendEmailNotification(final Order order) {
        // here there would be some implementation to send an email with order details
        logger.info("Sending email to notify order with id {}", order.getId());
    }
}