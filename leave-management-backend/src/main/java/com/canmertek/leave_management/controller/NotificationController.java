package com.canmertek.leave_management.controller;

import com.canmertek.leave_management.model.Notification;
import com.canmertek.leave_management.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus; // Eklendi
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.canmertek.leave_management.exception.ResourceNotFoundException; // Eklendi

import java.util.List;
import java.util.UUID;

// ---- DEĞİŞİKLİK BAŞLANGICI ----
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
// ---- DEĞİŞİKLİK SONU ----

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "http://localhost:3000") // Bu zaten vardı, kontrol amaçlı
public class NotificationController {

    // ---- DEĞİŞİKLİK BAŞLANGICI ----
    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);
    // ---- DEĞİŞİKLİK SONU ----

    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<Notification>> getNotificationsForUser(@PathVariable String userId) { // UUID yerine String alıp parse edelim
        // ---- DEĞİŞİKLİK BAŞLANGICI ----
        logger.info("Received request to fetch notifications for userId (raw string): {}", userId);
        UUID userUuid;
        try {
            userUuid = UUID.fromString(userId);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid UUID format for userId: {}", userId, e);
            return ResponseEntity.badRequest().build(); // Geçersiz UUID formatı
        }
        logger.info("Fetching notifications for parsed UUID: {}", userUuid);
        // ---- DEĞİŞİKLİK SONU ----
        try {
            List<Notification> notifications = notificationService.getUnseenNotifications(userUuid);
            logger.info("Found {} notifications for userId: {}", notifications.size(), userUuid);
            return ResponseEntity.ok(notifications);
        } catch (ResourceNotFoundException e) {
            logger.warn("User not found for notifications: {}. Error: {}", userUuid, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Veya boş liste: ResponseEntity.ok(Collections.emptyList());
        } catch (Exception e) {
            logger.error("Error fetching notifications for userId: {}. Error: {}", userUuid, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{userId}/seen")
    public ResponseEntity<String> markNotificationsAsSeen(@PathVariable String userId) { // UUID yerine String alıp parse edelim
         // ---- DEĞİŞİKLİK BAŞLANGICI ----
        logger.info("Received request to mark notifications as seen for userId (raw string): {}", userId);
        UUID userUuid;
        try {
            userUuid = UUID.fromString(userId);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid UUID format for userId: {}", userId, e);
            return ResponseEntity.badRequest().body("Geçersiz kullanıcı ID formatı.");
        }
        logger.info("Marking notifications as seen for parsed UUID: {}", userUuid);
        // ---- DEĞİŞİKLİK SONU ----
        try {
            notificationService.markAllAsSeen(userUuid);
            logger.info("Successfully marked notifications as seen for userId: {}", userUuid);
            return ResponseEntity.ok("Tüm bildirimler okundu olarak işaretlendi.");
        } catch (ResourceNotFoundException e) {
            logger.warn("User not found when marking notifications as seen: {}. Error: {}", userUuid, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error marking notifications as seen for userId: {}. Error: {}", userUuid, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Bildirimler işaretlenirken bir hata oluştu.");
        }
    }
}