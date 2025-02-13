package com.canmertek.leave_management.rabbitmq;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class LeaveRequestListener {

    private static final Logger logger = LoggerFactory.getLogger(LeaveRequestListener.class);

    private final RabbitTemplate rabbitTemplate;

    public LeaveRequestListener(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = "leaveRequestsQueue")
    public void receiveMessage(String message) {
        try {
            logger.info(" RabbitMQ'dan yeni mesaj alındı: {}", message);

            // Eğer mesaj boşsa hata 
            if (message == null || message.trim().isEmpty()) {
                throw new IllegalArgumentException("Boş mesaj alındı.");
            }

            
            logger.info(" Mesaj başarıyla işlendi: {}", message);

        } catch (Exception e) {
            logger.error(" RabbitMQ mesaj işlenirken hata oluştu: {} | Mesaj tekrar kuyruğa eklendi.", e.getMessage());
            rabbitTemplate.convertAndSend("leaveRequestsQueue", message); // Mesajı tekrar kuyruğa ekle
        }
    }
}
