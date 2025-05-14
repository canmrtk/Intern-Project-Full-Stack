// NotificationListener.java
package com.canmertek.leave_management.rabbitmq;

import com.canmertek.leave_management.service.NotificationService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// ---- DEĞİŞİKLİK BAŞLANGICI ----
import org.json.JSONObject; // org.json.JSONObject importunu ekleyin
import org.json.JSONException; // Hata yakalama için
// ---- DEĞİŞİKLİK SONU ----
import java.util.UUID;

@Component
public class NotificationListener {

    private static final Logger logger = LoggerFactory.getLogger(NotificationListener.class);

    private final NotificationService notificationService;

    public NotificationListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @RabbitListener(queues = "notificationsQueue")
    public void receiveNotification(String jsonMessage) { // Parametre adını jsonMessage olarak değiştirdim
        if (jsonMessage == null || jsonMessage.trim().isEmpty()) {
            logger.warn("Boş bir bildirim mesajı alındı.");
            return;
        }
        logger.info("Notification received from notificationsQueue: {}", jsonMessage);

        try {
            // ---- DEĞİŞİKLİK BAŞLANGICI: JSON Parse Etme ----
            JSONObject payload = new JSONObject(jsonMessage);

            if (!payload.has("userId") || !payload.has("message")) {
                logger.warn("Alınan JSON mesajında 'userId' veya 'message' alanı eksik. Mesaj: {}", jsonMessage);
                return;
            }

            String userIdString = payload.getString("userId");
            String messageText = payload.getString("message");

            if (userIdString == null || userIdString.trim().isEmpty() || messageText == null || messageText.trim().isEmpty()) {
                logger.warn("JSON mesajındaki 'userId' veya 'message' değeri boş. UserId: '{}', Message: '{}'", userIdString, messageText);
                return;
            }

            UUID targetUserId;
            try {
                targetUserId = UUID.fromString(userIdString);
            } catch (IllegalArgumentException e) {
                logger.error("Geçersiz UUID formatı alındı: {}. Mesaj: {}", userIdString, jsonMessage, e);
                return;
            }
            // ---- DEĞİŞİKLİK SONU ----

            notificationService.saveNotification(targetUserId, messageText); // testUserId yerine parse edilen targetUserId kullanılıyor
            logger.info("Bildirim başarıyla kaydedildi. Kullanıcı: {}, Mesaj: {}", targetUserId, messageText);

        } catch (JSONException e) {
            logger.error("RabbitMQ'dan gelen mesaj JSON formatında değil veya parse edilemedi. Mesaj: {}. Hata: {}", jsonMessage, e.getMessage());
        } catch (Exception e) {
            // NotificationService.saveNotification içinde ResourceNotFoundException gibi hatalar olabilir.
            logger.error("Bildirim işlenirken genel bir hata oluştu. Mesaj: {}. Hata: {}", jsonMessage, e.getMessage(), e);
            // Burada mesajı tekrar kuyruğa gönderme (retry) veya dead-letter queue'ya yönlendirme düşünülebilir.
        }
    }
}