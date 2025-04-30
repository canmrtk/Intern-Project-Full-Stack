package com.canmertek.leave_management.controller;

import com.canmertek.leave_management.dto.LeaveRequestDTO;
import com.canmertek.leave_management.exception.ResourceNotFoundException;
import com.canmertek.leave_management.model.LeaveRequest;
import com.canmertek.leave_management.service.LeaveRequestService;
import jakarta.validation.Valid; // DTO validasyonu için import
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/leave-requests")
@CrossOrigin(origins = "http://localhost:3000")
public class LeaveRequestController {

    @Autowired
    private LeaveRequestService leaveRequestService;

    // Repository bağımlılıkları kaldırıldı.

    @GetMapping
    public ResponseEntity<List<LeaveRequest>> getAllLeaveRequests() {
        // Servisteki getAllLeaveRequests çağrılıyor.
        return ResponseEntity.ok(leaveRequestService.getAllLeaveRequests());
    }

    @PostMapping("/request")
    public ResponseEntity<?> requestLeave(@Valid @RequestBody LeaveRequestDTO leaveRequestDTO) {
        try {
            // Servisteki createLeaveRequest çağrılıyor.
            LeaveRequest createdRequest = leaveRequestService.createLeaveRequest(leaveRequestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdRequest);
        } catch (RuntimeException e) {
            // Hata durumunda (örn: yetersiz izin, bekleyen talep var) Bad Request dön.
            // GlobalExceptionHandler da bunu yakalayabilir.
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approveLeaveRequest(@PathVariable UUID id) {
        // Servisteki approveLeaveRequest çağrılıyor.
        // Servis zaten ResponseEntity<?> döndüğü için try-catch'e gerek yok.
        // Hata durumunda servis uygun ResponseEntity'yi dönecek veya Exception fırlatacak (Handler yakalar).
        return leaveRequestService.approveLeaveRequest(id);
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<?> rejectLeaveRequest(@PathVariable UUID id) {
        try {
             // Servisteki rejectLeaveRequest çağrılıyor.
            String result = leaveRequestService.rejectLeaveRequest(id);
            return ResponseEntity.ok(result);
        } catch (IllegalStateException | ResourceNotFoundException e) {
            // Servisten gelen beklenen hataları yakalayıp uygun cevap dönülebilir.
            // Veya GlobalExceptionHandler'a bırakılabilir.
             return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Çalışana göre izin taleplerini getiren endpoint
    // Eğer /api/leave-requests/{employeeId} formatı isteniyorsa:
    @GetMapping("/{employeeId}")
    public ResponseEntity<?> getEmployeeLeaveRequests(@PathVariable UUID employeeId) {
         // Servisteki getLeaveRequestsByEmployeeId çağrılıyor.
         // ResourceNotFoundException fırlatabilir, GlobalExceptionHandler yakalar.
         List<LeaveRequest> leaveRequests = leaveRequestService.getLeaveRequestsByEmployeeId(employeeId);
         return ResponseEntity.ok(leaveRequests);
    }

     // Eğer /api/leave-requests/employee/{employeeId} formatı isteniyorsa:
    /*
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<?> getLeaveRequestsByEmployee(@PathVariable UUID employeeId) {
         List<LeaveRequest> leaveRequests = leaveRequestService.getLeaveRequestsByEmployeeId(employeeId);
         return ResponseEntity.ok(leaveRequests);
    }
    */
}