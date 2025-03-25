package com.canmertek.leave_management.rabbitmq;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class NotificationListener {

    private static final Logger logger = LoggerFactory.getLogger(NotificationListener.class);

    // Kuyruktan mesajları dinleyip işlem yapar
    @RabbitListener(queues = "notificationsQueue")
    public void receiveNotification(String message) {
        if (message == null || message.trim().isEmpty()) {
            logger.warn("Boş bir bildirim alındı.");
            return;
        }

        // sadece log
        logger.info("Yeni Bildirim: {}", message);

  
    }
}
