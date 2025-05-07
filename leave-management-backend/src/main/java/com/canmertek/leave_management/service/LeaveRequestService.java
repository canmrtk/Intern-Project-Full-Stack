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
// import jakarta.validation.Valid; 

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

    // Belirli bir çalışanın izin taleplerini getir (Refactor edilmiş)
    public List<LeaveRequest> getLeaveRequestsByEmployeeId(UUID employeeId) {
         if (!employeeRepository.existsById(employeeId)) {
             throw new ResourceNotFoundException("ID " + employeeId + " ile çalışan bulunamadı!");
         }
        return leaveRequestRepository.findByEmployeeId(employeeId);
    }

    
    public LeaveRequest createLeaveRequest(LeaveRequestDTO leaveRequestDTO) {
        Employee employee = employeeRepository.findByEmail(leaveRequestDTO.getEmployeeEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Çalışan bulunamadı: " + leaveRequestDTO.getEmployeeEmail()));

        if (leaveRequestRepository.existsByEmployeeAndStatus(employee, "PENDING")) {
            throw new RuntimeException("Zaten beklemede olan bir izin talebiniz mevcut.");
        }

        if (employee.getLeaveDays() < leaveRequestDTO.getLeaveDaysRequested()) {
            throw new RuntimeException("Yetersiz izin gününüz var!");
        }

        LeaveType leaveType = leaveRequestDTO.getLeaveType() != null ? leaveRequestDTO.getLeaveType() : LeaveType.ANNUAL;

        LeaveRequest newLeaveRequest = new LeaveRequest(
                employee,
                leaveRequestDTO.getLeaveDaysRequested(),
                leaveType
        );

        LeaveRequest savedRequest = leaveRequestRepository.save(newLeaveRequest);

        String notificationMessage = String.format("Yeni izin talebi: %s %s - %d gün (%s)",
                employee.getName(),
                employee.getSurname(),
                savedRequest.getLeaveDaysRequested(),
                savedRequest.getLeaveType().name());
        rabbitTemplate.convertAndSend("notificationsQueue", notificationMessage);

        return savedRequest;
    }

    
    public ResponseEntity<?> approveLeaveRequest(UUID id) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("İzin talebi bulunamadı: " + id));

        
        if ("APPROVED".equals(leaveRequest.getStatus())) {
            
             return ResponseEntity.badRequest().body("Bu izin talebi zaten onaylanmış!");
        }
     
        if ("REJECTED".equals(leaveRequest.getStatus())) {
             return ResponseEntity.badRequest().body("Bu izin talebi daha önce reddedilmiş!");
        }

        Employee employee = leaveRequest.getEmployee();

        // Yetersiz izin günü kontrolü
        if (employee.getLeaveDays() < leaveRequest.getLeaveDaysRequested()) {
             
             return ResponseEntity.badRequest().body("Çalışanın bu talebi karşılayacak yeterli izin günü yok!");
        }

       
        employee.setLeaveDays(employee.getLeaveDays() - leaveRequest.getLeaveDaysRequested());
        employeeRepository.save(employee); // Çalışanı güncelle

        leaveRequest.setStatus("APPROVED");
        LeaveRequest updatedRequest = leaveRequestRepository.save(leaveRequest); // Talebi güncelle

        // RabbitMQ bildirim
        String message = String.format("Onaylanan izin: %s %s - %d gün (%s)",
                employee.getName(),
                employee.getSurname(),
                leaveRequest.getLeaveDaysRequested(),
                leaveRequest.getLeaveType().name());
        rabbitTemplate.convertAndSend("notificationsQueue", message); // Bildirim kuyruğuna gönderilebilir

        return ResponseEntity.ok(updatedRequest);} 

   
    public String rejectLeaveRequest(UUID id) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("İzin talebi bulunamadı: " + id));

       
        if ("APPROVED".equals(leaveRequest.getStatus())) {
            throw new IllegalStateException("Onaylanmış bir izin talebi reddedilemez!");
        }
        
        if ("REJECTED".equals(leaveRequest.getStatus())) {
             return "Bu izin talebi zaten reddedilmiş."; // Tekrar işlem yapma
        }

        leaveRequest.setStatus("REJECTED"); // Durumu REJECTED yap
        leaveRequestRepository.save(leaveRequest); // Kaydet

        // Bildirim gönderilebilir
        String message = String.format("Reddedilen izin: %s %s - %d gün (%s)",
                        leaveRequest.getEmployee().getName(),
                        leaveRequest.getEmployee().getSurname(),
                        leaveRequest.getLeaveDaysRequested(),
                        leaveRequest.getLeaveType().name());
        rabbitTemplate.convertAndSend("notificationsQueue", message);

        return "İzin talebi reddedildi."; // Veya güncellenmiş request'i dön
    }

   
    public void sendLeaveRequestNotification(String message) {
    	
        rabbitTemplate.convertAndSend("leaveRequestsQueue", message);
    }
    

    
    public void deleteLeaveRequest(UUID id) {
        if (!leaveRequestRepository.existsById(id)) {
            throw new ResourceNotFoundException("İzin talebi bulunamadı.");
        }
        leaveRequestRepository.deleteById(id);
    }
    
}