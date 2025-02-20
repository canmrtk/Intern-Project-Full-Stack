package com.canmertek.leave_management.controller;

import com.canmertek.leave_management.model.Employee;
import com.canmertek.leave_management.model.LeaveRequest;
import com.canmertek.leave_management.repository.EmployeeRepository;
import com.canmertek.leave_management.repository.LeaveRequestRepository;
import com.canmertek.leave_management.service.LeaveRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leave-requests")  //  http://localhost:9090/api/leave-requests
@CrossOrigin(origins = "http://localhost:3000")
public class LeaveRequestController {

    @Autowired
    private LeaveRequestService leaveRequestService;

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    //Tüm izin taleplerini listeleme
    @GetMapping
    public ResponseEntity<List<LeaveRequest>> getAllLeaveRequests() {
        return ResponseEntity.ok(leaveRequestService.getAllLeaveRequests());
    }

    //Belirli bir çalışanın izin taleplerini listeleme
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<LeaveRequest>> getLeaveRequestsByEmployee(@PathVariable Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Çalışan bulunamadı!"));

        List<LeaveRequest> leaveRequests = leaveRequestRepository.findByEmployee(employee);
        return ResponseEntity.ok(leaveRequests);
    }
    
    
    @PostMapping("/request")
    public ResponseEntity<?> requestLeave(@RequestBody LeaveRequest leaveRequest) {
        return leaveRequestService.createLeaveRequest(leaveRequest);
    }
    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approveLeaveRequest(@PathVariable Long id) {
        return leaveRequestService.approveLeaveRequest(id);
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<?> rejectLeaveRequest(@PathVariable Long id) {
        return leaveRequestService.rejectLeaveRequest(id);
    }
    





}

