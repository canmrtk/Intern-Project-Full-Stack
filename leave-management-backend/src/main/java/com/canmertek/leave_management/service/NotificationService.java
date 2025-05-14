package com.canmertek.leave_management.service;

import com.canmertek.leave_management.model.Employee;
import com.canmertek.leave_management.model.Notification;
import com.canmertek.leave_management.repository.EmployeeRepository;
import com.canmertek.leave_management.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.UUID;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    public void saveNotification(UUID userId, String message) {
        Employee user = employeeRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setMessage(message);
        notificationRepository.save(notification);
    }

    public List<Notification> getUnseenNotifications(UUID userId) {
        Employee user = employeeRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
        return notificationRepository.findByUserAndSeenFalseOrderByCreatedAtDesc(user);
    }

    public void markAllAsSeen(UUID userId) {
        List<Notification> unseen = getUnseenNotifications(userId);
        unseen.forEach(n -> n.setSeen(true));
        notificationRepository.saveAll(unseen);
    }


}
