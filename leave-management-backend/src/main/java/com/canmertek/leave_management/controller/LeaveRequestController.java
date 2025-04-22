package com.canmertek.leave_management.controller;

import com.canmertek.leave_management.dto.LeaveRequestDTO;
import com.canmertek.leave_management.model.Employee;
import com.canmertek.leave_management.model.LeaveRequest;
import com.canmertek.leave_management.model.LeaveType;
import com.canmertek.leave_management.repository.EmployeeRepository;
import com.canmertek.leave_management.repository.LeaveRequestRepository;
import com.canmertek.leave_management.service.LeaveRequestService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/leave-requests")
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

    @GetMapping
    public ResponseEntity<List<LeaveRequest>> getAllLeaveRequests() {
        return ResponseEntity.ok(leaveRequestService.getAllLeaveRequests());
    }

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

        LeaveRequest leaveRequest = new LeaveRequest(
                employee,
                leaveRequestDTO.getLeaveDaysRequested(),
                leaveRequestDTO.getLeaveType() != null ? leaveRequestDTO.getLeaveType() : LeaveType.ANNUAL
        );

        leaveRequestRepository.save(leaveRequest);

        return ResponseEntity.ok("İzin talebi başarıyla oluşturuldu!");
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approveLeaveRequest(@PathVariable UUID id) {
        try {
            return leaveRequestService.approveLeaveRequest(id);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

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
