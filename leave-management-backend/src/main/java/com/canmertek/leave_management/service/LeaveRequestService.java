package com.canmertek.leave_management.service;

import com.canmertek.leave_management.model.Employee;
import com.canmertek.leave_management.model.LeaveRequest;
import com.canmertek.leave_management.model.LeaveType;
import com.canmertek.leave_management.repository.EmployeeRepository;
import com.canmertek.leave_management.repository.LeaveRequestRepository;

import jakarta.validation.Valid;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class LeaveRequestService {

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    // Tüm izin taleplerini getir
    public List<LeaveRequest> getAllLeaveRequests() {
        return leaveRequestRepository.findAll();
    }

    // Belirli bir çalışanın izin taleplerini getir
    public List<LeaveRequest> getLeaveRequestsByEmployee(UUID employeeId) {
        return leaveRequestRepository.findByEmployeeId(employeeId);
    }

    // Yeni izin talebi oluştur
    public String createLeaveRequest(@Valid LeaveRequest leaveRequest) {
        Optional<Employee> employeeOpt = employeeRepository.findByEmail(leaveRequest.getEmployee().getEmail());

        if (employeeOpt.isEmpty()) {
            throw new RuntimeException("Çalışan bulunamadı!");
        }

        Employee employee = employeeOpt.get();

        if (leaveRequestRepository.existsByEmployeeAndStatus(employee, "PENDING")) {
            throw new RuntimeException("Zaten bekleyen bir izin talebiniz var!");
        }

        if (employee.getLeaveDays() < leaveRequest.getLeaveDaysRequested()) {
            throw new RuntimeException("Yetersiz izin gününüz var!");
        }

        // Yeni izin talebi nesnesini oluştururken leaveType set edilmiş olmalı
        LeaveRequest newLeaveRequest = new LeaveRequest(
                employee,
                leaveRequest.getLeaveDaysRequested(),
                leaveRequest.getLeaveType() != null ? leaveRequest.getLeaveType() : LeaveType.ANNUAL // default tip
        );

        leaveRequestRepository.save(newLeaveRequest);

        // RabbitMQ ile bildirim gönder
        String notificationMessage = String.format("Yeni izin talebi: %s %s - %d gün (%s)",
                employee.getName(),
                employee.getSurname(),
                leaveRequest.getLeaveDaysRequested(),
                newLeaveRequest.getLeaveType().name());

        rabbitTemplate.convertAndSend("notificationsQueue", notificationMessage);
        sendLeaveRequestNotification("Yeni bir izin talebi geldi: " + employee.getEmail());

        return "İzin talebi başarıyla oluşturuldu ve bildirim gönderildi.";
    }

    public void deleteLeaveRequest(UUID id) {
        if (!leaveRequestRepository.existsById(id)) {
            throw new RuntimeException("İzin talebi bulunamadı.");
        }
        leaveRequestRepository.deleteById(id);
    }

    public ResponseEntity<?> approveLeaveRequest(UUID id) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("İzin talebi bulunamadı."));

        Employee employee = leaveRequest.getEmployee();

        if ("APPROVED".equals(leaveRequest.getStatus())) {
            return ResponseEntity.badRequest().body("Bu izin zaten onaylanmış!");
        }

        if (employee.getLeaveDays() < leaveRequest.getLeaveDaysRequested()) {
            return ResponseEntity.badRequest().body("Çalışanın yeterli izin günü yok!");
        }

        employee.setLeaveDays(employee.getLeaveDays() - leaveRequest.getLeaveDaysRequested());
        employeeRepository.save(employee);

        leaveRequest.setStatus("APPROVED");
        leaveRequestRepository.save(leaveRequest);

        // RabbitMQ bildirim
        String message = String.format("Onaylanan izin: %s %s - %d gün (%s)",
                employee.getName(),
                employee.getSurname(),
                leaveRequest.getLeaveDaysRequested(),
                leaveRequest.getLeaveType().name());

        rabbitTemplate.convertAndSend("leaveRequestsQueue", message);

        return ResponseEntity.ok("İzin talebi onaylandı ve bildirim gönderildi.");
    }

    public String rejectLeaveRequest(UUID id) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("İzin talebi bulunamadı."));

        if (leaveRequest.getStatus().equals("APPROVED")) {
            throw new IllegalStateException("Bu izin zaten onaylanmış, iptal edilemez!");
        }

        leaveRequestRepository.deleteById(id);
        return "İzin talebi reddedildi.";
    }

    public void sendLeaveRequestNotification(String message) {
        rabbitTemplate.convertAndSend("leaveRequestsQueue", message);
    }
}
