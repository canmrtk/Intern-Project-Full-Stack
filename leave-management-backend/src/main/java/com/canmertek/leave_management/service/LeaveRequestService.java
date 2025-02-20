package com.canmertek.leave_management.service;

import com.canmertek.leave_management.model.Employee;
import com.canmertek.leave_management.model.LeaveRequest;
import com.canmertek.leave_management.repository.EmployeeRepository;
import com.canmertek.leave_management.repository.LeaveRequestRepository;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeaveRequestService {

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;
    @Autowired
    private EmployeeRepository employeeRepository;

    // Tüm izin taleplerini getir
    public List<LeaveRequest> getAllLeaveRequests() {
        return leaveRequestRepository.findAll();
    }

    // Belirli bir çalışanın izin taleplerini getir
    public List<LeaveRequest> getLeaveRequestsByEmployee(Long employeeId) {
        return leaveRequestRepository.findByEmployeeId(employeeId);
    }

    // Yeni izin talebi oluştur
    public ResponseEntity<?> createLeaveRequest(@Valid LeaveRequest leaveRequest) {
        Employee employee = employeeRepository.findByEmail(leaveRequest.getEmployee().getEmail())
                .orElseThrow(() -> new RuntimeException("Çalışan bulunamadı!"));

        if (leaveRequestRepository.existsByEmployeeAndStatus(employee, "PENDING")) {
            return ResponseEntity.badRequest().body("Zaten bekleyen bir izin talebiniz var!");
        }

        leaveRequest.setEmployee(employee);
        leaveRequest.setStatus("PENDING");
        leaveRequestRepository.save(leaveRequest);

        return ResponseEntity.ok("İzin talebi başarıyla oluşturuldu ve onay bekliyor.");
    }



    public void deleteLeaveRequest(Long id) {
        if (!leaveRequestRepository.existsById(id)) {
            throw new RuntimeException("İzin talebi bulunamadı.");
        }
        leaveRequestRepository.deleteById(id);
    }
    
    public ResponseEntity<?> approveLeaveRequest(Long id) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("İzin talebi bulunamadı."));

        Employee employee = leaveRequest.getEmployee();

        if (leaveRequest.getStatus().equals("APPROVED")) {
            return ResponseEntity.badRequest().body("Bu izin zaten onaylanmış!");
        }

        if (employee.getLeaveDays() < leaveRequest.getLeaveDaysRequested()) {
            return ResponseEntity.badRequest().body("Çalışanın yeterli izin günü yok!");
        }

        // Onay verildiğinde çalışanın izin günlerini düş
        employee.setLeaveDays(employee.getLeaveDays() - leaveRequest.getLeaveDaysRequested());
        employeeRepository.save(employee);

        leaveRequest.setStatus("APPROVED");
        leaveRequestRepository.save(leaveRequest);

        return ResponseEntity.ok("İzin talebi onaylandı ve çalışanın izin günleri güncellendi.");
    }


    public ResponseEntity<?> rejectLeaveRequest(Long id) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("İzin talebi bulunamadı."));

        if (leaveRequest.getStatus().equals("APPROVED")) {
            return ResponseEntity.badRequest().body("Bu izin zaten onaylanmış, iptal edilemez!");
        }

        leaveRequestRepository.deleteById(id);
        return ResponseEntity.ok("İzin talebi reddedildi.");
    }





}
