package com.canmertek.leave_management.controller;

import com.canmertek.leave_management.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "http://localhost:3000")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/send")
    public String sendNotification(@RequestBody String message) {
        notificationService.sendNotification(message);
        return "Bildirim kuyruğa gönderildi!";
    }
}
