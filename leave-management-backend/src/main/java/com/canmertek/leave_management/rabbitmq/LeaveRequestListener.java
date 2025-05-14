package com.canmertek.leave_management.rabbitmq;

import com.canmertek.leave_management.service.NotificationService;
import org.json.JSONObject;
import org.json.JSONException;
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
    public void receiveMessage(String jsonMessage) { 
        if (jsonMessage == null || jsonMessage.trim().isEmpty()) {
            logger.warn("Boş bir mesaj alındı from leaveRequestsQueue.");
            return;
        }
        logger.info("Message received from leaveRequestsQueue: {}", jsonMessage);

        try {
            JSONObject payload = new JSONObject(jsonMessage);

            if (!payload.has("userId") || !payload.has("message")) {
                logger.error("Invalid JSON message format from leaveRequestsQueue. 'userId' or 'message' field is missing. Message: {}", jsonMessage);
               
                return;
            }

            String userIdString = payload.getString("userId");
            String messageText = payload.getString("message");

            if (userIdString == null || userIdString.trim().isEmpty() || messageText == null || messageText.trim().isEmpty()) {
                logger.error("Empty 'userId' or 'message' in JSON payload from leaveRequestsQueue. UserId: '{}', Message: '{}', FullPayload: {}", userIdString, messageText, jsonMessage);
                return;
            }
            
            UUID targetUserId;
            try {
                targetUserId = UUID.fromString(userIdString);
            } catch (IllegalArgumentException e) {
                logger.error("Invalid UUID format for userId: '{}' from leaveRequestsQueue. Full Message: {}. Error: {}", userIdString, jsonMessage, e.getMessage());
                return; 
            }

            notificationService.saveNotification(targetUserId, messageText);
            logger.info("Notification successfully saved for user {} from leaveRequestsQueue. Message: {}", targetUserId, messageText);

        } catch (JSONException e) {
            logger.error("Failed to parse JSON message from leaveRequestsQueue. Message: {}. Error: {}", jsonMessage, e.getMessage());
            
        } catch (Exception e) {
          
            logger.error("An unexpected error occurred while processing message from leaveRequestsQueue. Message: {}. Error: {}", jsonMessage, e.getMessage(), e);
          
        }
    }
}