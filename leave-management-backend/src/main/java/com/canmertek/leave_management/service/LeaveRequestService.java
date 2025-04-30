package com.canmertek.leave_management.service;

import com.canmertek.leave_management.dto.LeaveRequestDTO; // DTO importu eklendi
import com.canmertek.leave_management.exception.ResourceNotFoundException; // Exception importu eklendi
import com.canmertek.leave_management.model.Employee;
import com.canmertek.leave_management.model.LeaveRequest;
import com.canmertek.leave_management.model.LeaveType;
import com.canmertek.leave_management.repository.EmployeeRepository;
import com.canmertek.leave_management.repository.LeaveRequestRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
// import jakarta.validation.Valid; // Servis içinde DTO validasyonu yapılmayacaksa gerek yok

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class LeaveRequestService {

    // --- HATA ALINAN YERLER: @Autowired EKLENDİ ---
    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;
    // --- HATA ALINAN YERLER: @Autowired EKLENDİ ---


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

    // Yeni izin talebi oluştur (Refactor edilmiş)
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

    // İzin talebini onayla (Refactor edilmiş)
    public ResponseEntity<?> approveLeaveRequest(UUID id) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("İzin talebi bulunamadı: " + id));

        // Zaten onaylıysa hata fırlat (veya farklı bir cevap dön)
        if ("APPROVED".equals(leaveRequest.getStatus())) {
            // throw new IllegalStateException("Bu izin zaten onaylanmış!"); // Veya ResponseEntity dön
             return ResponseEntity.badRequest().body("Bu izin talebi zaten onaylanmış!");
        }
        // Zaten reddedilmişse? (Opsiyonel kontrol)
        if ("REJECTED".equals(leaveRequest.getStatus())) {
             return ResponseEntity.badRequest().body("Bu izin talebi daha önce reddedilmiş!");
        }

        Employee employee = leaveRequest.getEmployee();

        // Yetersiz izin günü kontrolü
        if (employee.getLeaveDays() < leaveRequest.getLeaveDaysRequested()) {
             // throw new RuntimeException("Çalışanın yeterli izin günü yok!"); // Veya ResponseEntity dön
             return ResponseEntity.badRequest().body("Çalışanın bu talebi karşılayacak yeterli izin günü yok!");
        }

        // İş mantığı: İzin gününü düşür, durumu güncelle
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

        return ResponseEntity.ok(updatedRequest); // Başarılı yanıt ve güncellenmiş talep
    }

    // İzin talebini reddet (Refactor edilmiş)
    public String rejectLeaveRequest(UUID id) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("İzin talebi bulunamadı: " + id));

        // Zaten onaylıysa reddedilemez
        if ("APPROVED".equals(leaveRequest.getStatus())) {
            throw new IllegalStateException("Onaylanmış bir izin talebi reddedilemez!");
        }
        // Zaten reddedilmişse? (Opsiyonel)
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

    // Bu metodun LeaveRequestService içinde olmasına gerek yok gibi duruyor,
    // bildirimler genellikle işlem sonucunda gönderilir.
    /*
    public void sendLeaveRequestNotification(String message) {
        rabbitTemplate.convertAndSend("leaveRequestsQueue", message);
    }
    */

     // Silme işlemi genellikle yöneticiler tarafından yapılır ve ID ile olur.
     // Reddetme işlemi status'u REJECTED yapmalı, silmemeli.
     // Eğer silme isteniyorsa ayrı bir metod olmalı.
    /*
    public void deleteLeaveRequest(UUID id) {
        if (!leaveRequestRepository.existsById(id)) {
            throw new ResourceNotFoundException("İzin talebi bulunamadı.");
        }
        leaveRequestRepository.deleteById(id);
    }
    */
}