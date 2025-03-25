package com.canmertek.leave_management.service;

import com.canmertek.leave_management.model.Employee;
import com.canmertek.leave_management.model.LeaveRequest;
import com.canmertek.leave_management.repository.EmployeeRepository;
import com.canmertek.leave_management.repository.LeaveRequestRepository;

import jakarta.validation.Valid;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
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
    public String createLeaveRequest(LeaveRequest leaveRequest) {
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

        LeaveRequest newLeaveRequest = new LeaveRequest(employee, leaveRequest.getLeaveDaysRequested());
        leaveRequestRepository.save(newLeaveRequest);

        //Bildirimi RabbitMQ kuyruğuna gönder
        String notificationMessage = String.format("Yeni izin talebi: %s %s - %d gün",
                employee.getName(), employee.getSurname(), leaveRequest.getLeaveDaysRequested());

        rabbitTemplate.convertAndSend("notificationsQueue", notificationMessage);

        return "İzin talebi başarıyla oluşturuldu ve bildirim gönderildi.";
    }


    public void deleteLeaveRequest(UUID id) {
        if (!leaveRequestRepository.existsById(id)) {
            throw new RuntimeException("İzin talebi bulunamadı.");
        }
        leaveRequestRepository.deleteById(id);
    }

    public String approveLeaveRequest(UUID id) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("İzin talebi bulunamadı."));

        Employee employee = leaveRequest.getEmployee();

        if (leaveRequest.getStatus().equals("APPROVED")) {
            throw new IllegalStateException("Bu izin zaten onaylanmış!");
        }

        if (employee.getLeaveDays() < leaveRequest.getLeaveDaysRequested()) {
            throw new IllegalStateException("Çalışanın yeterli izin günü yok!");
        }

        // Onay verildiğinde çalışanın izin günlerini düş
        employee.setLeaveDays(employee.getLeaveDays() - leaveRequest.getLeaveDaysRequested());
        employeeRepository.save(employee);

        leaveRequest.setStatus("APPROVED");
        leaveRequestRepository.save(leaveRequest);

        return "İzin talebi onaylandı ve çalışanın izin günleri güncellendi.";
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
}
