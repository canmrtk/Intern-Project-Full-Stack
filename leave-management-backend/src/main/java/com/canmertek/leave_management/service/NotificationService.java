package com.canmertek.leave_management.service;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@Service
public class NotificationService {

    private final List<String> notifications = new LinkedList<>();

    public void addNotification(String message) {
        notifications.add(0, message); // Son gelen mesaj en başta gösterilir
        if (notifications.size() > 20) {
            notifications.remove(notifications.size() - 1); // Son 20 mesajı tut
        }
    }

    public List<String> getAllNotifications() {
        return Collections.unmodifiableList(notifications);
    }
}
