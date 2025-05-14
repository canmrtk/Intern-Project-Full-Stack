package com.canmertek.leave_management.service;

import com.canmertek.leave_management.dto.LeaveRequestDTO;
import com.canmertek.leave_management.exception.ResourceNotFoundException;
import com.canmertek.leave_management.model.Employee;
import com.canmertek.leave_management.model.LeaveRequest;
import com.canmertek.leave_management.model.LeaveType;
import com.canmertek.leave_management.repository.EmployeeRepository;
import com.canmertek.leave_management.repository.LeaveRequestRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class LeaveRequestService {

    private static final Logger logger = LoggerFactory.getLogger(LeaveRequestService.class);

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private void sendNotificationToQueue(UUID userId, String messageText, String queueName) {
        try {
            Map<String, String> notificationPayload = new HashMap<>();
            notificationPayload.put("userId", userId.toString());
            notificationPayload.put("message", messageText);

            JSONObject jsonPayload = new JSONObject(notificationPayload);
            rabbitTemplate.convertAndSend(queueName, jsonPayload.toString());
            logger.info("Notification sent to queue '{}' for user {}: {}", queueName, userId, messageText);
        } catch (Exception e) {
            logger.error("Error sending notification to queue '{}' for user {}: {}", queueName, userId, messageText, e);
        }
    }

    public List<LeaveRequest> getAllLeaveRequests() {
        logger.info("Fetching all leave requests");
        return leaveRequestRepository.findAll();
    }

    public List<LeaveRequest> getLeaveRequestsByEmployeeId(UUID employeeId) {
        logger.info("Fetching leave requests for employeeId: {}", employeeId);
        if (!employeeRepository.existsById(employeeId)) {
            logger.warn("Employee not found with id: {}", employeeId);
            throw new ResourceNotFoundException("ID " + employeeId + " ile çalışan bulunamadı!");
        }
        return leaveRequestRepository.findByEmployeeId(employeeId);
    }

    @Transactional
    public LeaveRequest createLeaveRequest(LeaveRequestDTO leaveRequestDTO) {
        logger.info("Attempting to create leave request for email: {}", leaveRequestDTO.getEmployeeEmail());
        Employee employee = employeeRepository.findByEmail(leaveRequestDTO.getEmployeeEmail())
                .orElseThrow(() -> {
                    logger.warn("Employee not found with email: {}", leaveRequestDTO.getEmployeeEmail());
                    return new ResourceNotFoundException("Çalışan bulunamadı: " + leaveRequestDTO.getEmployeeEmail());
                });

        
        if (leaveRequestRepository.existsByEmployeeAndStatus(employee, "PENDING")) {
            logger.warn("User {} already has a pending leave request.", employee.getEmail());
            throw new RuntimeException("Zaten beklemede olan bir izin talebiniz mevcut.");
        }

        if (employee.getLeaveDays() < leaveRequestDTO.getLeaveDaysRequested()) {
            logger.warn("User {} has insufficient leave days. Available: {}, Requested: {}",
                    employee.getEmail(), employee.getLeaveDays(), leaveRequestDTO.getLeaveDaysRequested());
            throw new RuntimeException("Yetersiz izin gününüz var!");
        }


        LeaveType leaveType = leaveRequestDTO.getLeaveType() != null ? leaveRequestDTO.getLeaveType() : LeaveType.ANNUAL;
        logger.info("Leave type determined as: {}. Requested days: {}", leaveType.getDisplayName(), leaveRequestDTO.getLeaveDaysRequested());

        LeaveRequest newLeaveRequest = new LeaveRequest(
                employee,
                leaveRequestDTO.getLeaveDaysRequested(),
                leaveType
        );

        LeaveRequest savedRequest = leaveRequestRepository.save(newLeaveRequest);
        logger.info("Leave request saved with ID: {} for employee {}", savedRequest.getId(), employee.getEmail());

        // Çalışana bildirim gönder
        String employeeNotificationMessage = String.format("%s tipinde %d günlük izin talebiniz alındı ve değerlendiriliyor.",
                savedRequest.getLeaveType().getDisplayName(),
                savedRequest.getLeaveDaysRequested());
        sendNotificationToQueue(employee.getId(), employeeNotificationMessage, "notificationsQueue");

        // Yöneticilere bildirim gönder
        logger.info("Attempting to notify managers about the new leave request from employee ID: {}", employee.getId());
        List<Employee> managers = employeeRepository.findByRole(Employee.Role.MANAGER);
        if (managers.isEmpty()) {
            logger.warn("No managers found to notify for new leave request.");
        } else {
            for (Employee manager : managers) {
                
                if (!manager.getId().equals(employee.getId())) {
                    String managerNotificationMessage = String.format("%s %s adlı çalışan, %s için %d günlük izin talebinde bulundu.",
                        employee.getName(),
                        employee.getSurname(),
                        savedRequest.getLeaveType().getDisplayName(),
                        savedRequest.getLeaveDaysRequested());
                    sendNotificationToQueue(manager.getId(), managerNotificationMessage, "notificationsQueue");
                } else {
                    logger.info("Skipping manager notification for manager {} as it's their own request.", manager.getEmail());
                }
               
            }
            
            long notifiedManagerCount = managers.stream().filter(m -> !m.getId().equals(employee.getId())).count();
            if (notifiedManagerCount > 0) {
                logger.info("{} manager(s) notified about the new leave request.", notifiedManagerCount);
            } else {
                logger.info("No managers (other than the requester, if manager) were notified.");
            }
        }

        return savedRequest;
    }
    @Transactional
    public ResponseEntity<?> approveLeaveRequest(UUID id) {
        logger.info("Attempting to approve leave request with ID: {}", id);
        LeaveRequest leaveRequest = leaveRequestRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Leave request not found with ID: {}", id);
                    return new ResourceNotFoundException("İzin talebi bulunamadı: " + id);
                });

        Employee employee = leaveRequest.getEmployee();
        logger.info("Leave request found for employee: {}. Current status: {}", employee.getEmail(), leaveRequest.getStatus());

        if ("APPROVED".equals(leaveRequest.getStatus())) {
            logger.warn("Leave request {} is already approved.", id);
            return ResponseEntity.badRequest().body("Bu izin talebi zaten onaylanmış!");
        }

        if ("REJECTED".equals(leaveRequest.getStatus())) {
            logger.warn("Leave request {} was previously rejected and cannot be approved now.", id);
            return ResponseEntity.badRequest().body("Bu izin talebi daha önce reddedilmiş, onaylanamaz!");
        }

        if (employee.getLeaveDays() < leaveRequest.getLeaveDaysRequested()) {
            logger.warn("Employee {} has insufficient leave days for request {}. Available: {}, Requested: {}",
                    employee.getEmail(), id, employee.getLeaveDays(), leaveRequest.getLeaveDaysRequested());
            return ResponseEntity.badRequest().body("Çalışanın bu talebi karşılayacak yeterli izin günü kalmamış!");
        }

        employee.setLeaveDays(employee.getLeaveDays() - leaveRequest.getLeaveDaysRequested());
        employeeRepository.save(employee);
        logger.info("Updated leave days for employee {}. New balance: {}", employee.getEmail(), employee.getLeaveDays());

        leaveRequest.setStatus("APPROVED");
        LeaveRequest updatedRequest = leaveRequestRepository.save(leaveRequest);
        logger.info("Leave request {} status updated to APPROVED.", id);

        String notificationMessageForEmployeeApproval = String.format("%s tipindeki %d günlük izin talebiniz onaylandı. Kalan izin gününüz: %d",
                leaveRequest.getLeaveType().getDisplayName(),
                leaveRequest.getLeaveDaysRequested(),
                employee.getLeaveDays());
        sendNotificationToQueue(employee.getId(), notificationMessageForEmployeeApproval, "notificationsQueue");

        return ResponseEntity.ok(updatedRequest);
    }

    @Transactional
    public String rejectLeaveRequest(UUID id) {
        logger.info("Attempting to reject leave request with ID: {}", id);
        LeaveRequest leaveRequest = leaveRequestRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Leave request not found with ID: {}", id);
                    return new ResourceNotFoundException("İzin talebi bulunamadı: " + id);
                });

        Employee employee = leaveRequest.getEmployee();
        logger.info("Leave request found for employee: {}. Current status: {}", employee.getEmail(), leaveRequest.getStatus());

        if ("APPROVED".equals(leaveRequest.getStatus())) {
            logger.warn("Cannot reject leave request {} because it is already APPROVED.", id);
            throw new IllegalStateException("Onaylanmış bir izin talebi reddedilemez!");
        }

        if ("REJECTED".equals(leaveRequest.getStatus())) {
            logger.info("Leave request {} is already REJECTED.", id);
            return "Bu izin talebi zaten reddedilmiş.";
        }

        leaveRequest.setStatus("REJECTED");
        leaveRequestRepository.save(leaveRequest);
        logger.info("Leave request {} status updated to REJECTED.", id);

        String notificationMessageForEmployeeRejection = String.format("%s tipindeki %d günlük izin talebiniz reddedildi.",
                leaveRequest.getLeaveType().getDisplayName(),
                leaveRequest.getLeaveDaysRequested());
        sendNotificationToQueue(employee.getId(), notificationMessageForEmployeeRejection, "notificationsQueue");

        return "İzin talebi başarıyla reddedildi.";
    }

    public void sendCustomLeaveNotification(UUID targetUserId, String messageContent) {
        logger.info("Sending custom leave notification to user {}: {}", targetUserId, messageContent);
        sendNotificationToQueue(targetUserId, messageContent, "notificationsQueue");
    }

    @Transactional
    public void deleteLeaveRequest(UUID id) {
        logger.info("Attempting to delete leave request with ID: {}", id);
        LeaveRequest leaveRequest = leaveRequestRepository.findById(id)
            .orElseThrow(() -> {
                logger.warn("Leave request not found for deletion with ID: {}", id);
                return new ResourceNotFoundException("İzin talebi bulunamadı: " + id);
            });

     
        if ("APPROVED".equals(leaveRequest.getStatus())) {
            Employee employee = leaveRequest.getEmployee();
            employee.setLeaveDays(employee.getLeaveDays() + leaveRequest.getLeaveDaysRequested());
            employeeRepository.save(employee);
            logger.info("Reverted leave days for employee {} due to deletion of approved request {}. New balance: {}",
                employee.getEmail(), id, employee.getLeaveDays());
        }
       

        leaveRequestRepository.deleteById(id);
        logger.info("Leave request with ID: {} successfully deleted.", id);
  
    }
}