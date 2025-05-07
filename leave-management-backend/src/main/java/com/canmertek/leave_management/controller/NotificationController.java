package com.canmertek.leave_management.controller;

import com.canmertek.leave_management.model.Notification;
import com.canmertek.leave_management.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "http://localhost:3000")
public class NotificationController {

    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<Notification>> getNotificationsForUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(notificationService.getUnseenNotifications(userId));
    }

    @PostMapping("/{userId}/seen")
    public ResponseEntity<String> markNotificationsAsSeen(@PathVariable UUID userId) {
        notificationService.markAllAsSeen(userId);
        return ResponseEntity.ok("Tüm bildirimler okundu olarak işaretlendi.");
    }
}
