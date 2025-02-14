package com.canmertek.leave_management.controller;

import com.canmertek.leave_management.dto.LeaveRequestDTO;
import com.canmertek.leave_management.service.LeaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/leave") 
public class LeaveController {

    @Autowired
    private LeaveService leaveService;

    @PostMapping("/request") 
    public ResponseEntity<String> requestLeave(@RequestBody LeaveRequestDTO leaveRequestDTO) {
        String result = leaveService.requestLeaveByEmail(leaveRequestDTO.getEmail(), leaveRequestDTO.getLeaveDays());
        return ResponseEntity.ok(result);
    }
}
