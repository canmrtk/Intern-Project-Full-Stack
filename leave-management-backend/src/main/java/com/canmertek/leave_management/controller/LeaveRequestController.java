package com.canmertek.leave_management.controller;

import com.canmertek.leave_management.dto.LeaveRequestDTO;
import com.canmertek.leave_management.model.Employee;
import com.canmertek.leave_management.model.LeaveRequest;
import com.canmertek.leave_management.service.LeaveRequestService;
import com.canmertek.leave_management.repository.EmployeeRepository;
import com.canmertek.leave_management.repository.LeaveRequestRepository;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/leave-requests")  // http://localhost:9090/api/leave-requests
@CrossOrigin(origins = "http://localhost:3000")
public class LeaveRequestController {

    @Autowired
    private LeaveRequestService leaveRequestService;

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private RabbitTemplate rabbitTemplate;


    // Tüm izin taleplerini listeleme
    @GetMapping
    public ResponseEntity<List<LeaveRequest>> getAllLeaveRequests() {
        return ResponseEntity.ok(leaveRequestService.getAllLeaveRequests());
    }

    // Belirli bir çalışanın izin taleplerini listeleme
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<?> getLeaveRequestsByEmployee(@PathVariable UUID employeeId) {
        try {
            Employee employee = employeeRepository.findById(employeeId)
                    .orElseThrow(() -> new RuntimeException("Çalışan bulunamadı!"));

            List<LeaveRequest> leaveRequests = leaveRequestRepository.findByEmployee(employee);
            return ResponseEntity.ok(leaveRequests);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // Yeni izin talebi oluştur
    @PostMapping("/request")
    public ResponseEntity<String> requestLeave(@RequestBody LeaveRequestDTO leaveRequestDTO) {
        Optional<Employee> employeeOpt = employeeRepository.findByEmail(leaveRequestDTO.getEmployeeEmail());

        if (employeeOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Çalışan bulunamadı!");
        }

        Employee employee = employeeOpt.get();

        if (employee.getLeaveDays() < leaveRequestDTO.getLeaveDaysRequested()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Yetersiz izin gününüz var!");
        }

        LeaveRequest leaveRequest = new LeaveRequest(employee, leaveRequestDTO.getLeaveDaysRequested());
        leaveRequestRepository.save(leaveRequest);

        // 🎯 Bildirim olarak mesaj gönder!
        String message = employee.getName() + " " + employee.getSurname() + " yeni bir izin talebinde bulundu (" +
                leaveRequest.getLeaveDaysRequested() + " gün)";
        rabbitTemplate.convertAndSend("leaveRequestsQueue", message);

        return ResponseEntity.ok("İzin talebi başarıyla oluşturuldu!");
    }

    // İzin talebi onaylama
    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approveLeaveRequest(@PathVariable UUID id) {
        try {
            ResponseEntity<?> result = leaveRequestService.approveLeaveRequest(id);
            return ResponseEntity.ok(result);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // İzin talebi reddetme
    @PutMapping("/{id}/reject")
    public ResponseEntity<?> rejectLeaveRequest(@PathVariable UUID id) {
        try {
            String result = leaveRequestService.rejectLeaveRequest(id);
            return ResponseEntity.ok(result);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // Belirli bir çalışanın izin geçmişini getir
    @GetMapping("/{employeeId}")
    public ResponseEntity<?> getEmployeeLeaveRequests(@PathVariable UUID employeeId) {
        try {
            List<LeaveRequest> leaveRequests = leaveRequestRepository.findByEmployeeId(employeeId);
            return ResponseEntity.ok(leaveRequests);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Çalışan bulunamadı!");
        }
    }
}
