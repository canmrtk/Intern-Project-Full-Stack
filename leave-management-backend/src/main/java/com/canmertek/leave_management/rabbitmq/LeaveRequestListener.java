package com.canmertek.leave_management.rabbitmq;

import com.canmertek.leave_management.service.NotificationService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

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
            logger.info("RabbitMQ'dan mesaj alındı: {}", message);

            if (message == null || message.trim().isEmpty()) {
                throw new IllegalArgumentException("Boş mesaj alındı.");
            }

            // Mesaj formatı: {"userId":"uuid","message":"İzin talebi geldi!"}
            JSONObject json = new JSONObject(message);
            UUID userId = UUID.fromString(json.getString("userId"));
            String msg = json.getString("message");

            notificationService.saveNotification(userId, msg);
            logger.info("Bildirim kaydedildi: {} - {}", userId, msg);

        } catch (Exception e) {
            logger.error("RabbitMQ mesaj işlenirken hata: {} | Mesaj tekrar kuyruğa alındı.", e.getMessage());
            rabbitTemplate.convertAndSend("leaveRequestsQueue", message); // Retry
        }
    }
}
