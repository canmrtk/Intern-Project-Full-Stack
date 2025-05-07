package com.canmertek.leave_management.repository;

import com.canmertek.leave_management.model.Notification;
import com.canmertek.leave_management.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findByUserAndSeenFalseOrderByCreatedAtDesc(Employee user);
}
