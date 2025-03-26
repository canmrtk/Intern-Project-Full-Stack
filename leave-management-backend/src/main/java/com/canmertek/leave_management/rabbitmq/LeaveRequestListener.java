package com.canmertek.leave_management.rabbitmq;

import com.canmertek.leave_management.service.NotificationService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class LeaveRequestListener {

    private static final Logger logger = LoggerFactory.getLogger(LeaveRequestListener.class);

    private final RabbitTemplate rabbitTemplate;
    private final NotificationService notificationService;

    public LeaveRequestListener(RabbitTemplate rabbitTemplate, NotificationService notificationService) {
        this.rabbitTemplate = rabbitTemplate;
        this.notificationService = notificationService;
    }

    @RabbitListener(queues = "leaveRequestsQueue")
    public void receiveMessage(String message) {
        try {
            logger.info(" RabbitMQ'dan mesaj alındı: {}", message);

            if (message == null || message.trim().isEmpty()) {
                throw new IllegalArgumentException("Boş mesaj alındı.");
            }

            // Bildirim servisine mesajı ekle
            notificationService.addNotification(message);
            logger.info(" Bildirim servisine eklendi: {}", message);

        } catch (Exception e) {
            logger.error(" Hata: {} | Mesaj tekrar kuyruğa eklendi.", e.getMessage());
            rabbitTemplate.convertAndSend("leaveRequestsQueue", message); // Retry
        }
    }
}
