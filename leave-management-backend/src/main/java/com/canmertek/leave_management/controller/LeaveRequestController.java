package com.canmertek.leave_management.controller;

import com.canmertek.leave_management.dto.LeaveRequestDTO;
import com.canmertek.leave_management.model.Employee;
import com.canmertek.leave_management.model.LeaveRequest;
import com.canmertek.leave_management.repository.EmployeeRepository;
import com.canmertek.leave_management.repository.LeaveRequestRepository;
import com.canmertek.leave_management.service.LeaveRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
    
    
    /*@PostMapping("/request")
    public ResponseEntity<?> requestLeave(@RequestBody LeaveRequest leaveRequest) {
        return leaveRequestService.createLeaveRequest(leaveRequest);}*/
    
    @PostMapping("/request")
    public ResponseEntity<String> requestLeave(@RequestBody LeaveRequestDTO leaveRequestDTO) {
        Optional<Employee> employeeOpt = employeeRepository.findByEmail(leaveRequestDTO.getEmployeeEmail());

        if (employeeOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Çalışan bulunamadı!");
        }

        Employee employee = employeeOpt.get(); // Optional içindeki Employee nesnesini alıyoruz.

        if (employee.getLeaveDays() < leaveRequestDTO.getLeaveDaysRequested()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Yetersiz izin gününüz var!");
        }

        LeaveRequest leaveRequest = new LeaveRequest(employee, leaveRequestDTO.getLeaveDaysRequested());
        leaveRequestRepository.save(leaveRequest);

        return ResponseEntity.ok("İzin talebi başarıyla oluşturuldu!");
    }


    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approveLeaveRequest(@PathVariable Long id) {
        return leaveRequestService.approveLeaveRequest(id);
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<?> rejectLeaveRequest(@PathVariable Long id) {
        return leaveRequestService.rejectLeaveRequest(id);
    }
    @GetMapping("/{employeeId}")
    public List<LeaveRequest> getEmployeeLeaveRequests(@PathVariable Long employeeId) {
        return leaveRequestRepository.findByEmployeeId(employeeId);
    }
    





}

