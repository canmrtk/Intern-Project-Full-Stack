package com.canmertek.leave_management.controller;

import com.canmertek.leave_management.dto.LeaveRequestDTO;
import com.canmertek.leave_management.model.LeaveType;
import com.canmertek.leave_management.service.LeaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/leave")
@CrossOrigin(origins = "http://localhost:3000")
public class LeaveController {

    @Autowired
    private LeaveService leaveService;

    @PostMapping("/request")
    public ResponseEntity<String> requestLeave(@RequestBody LeaveRequestDTO leaveRequestDTO) {
        // LeaveType null gelirse varsayılan olarak ANNUAL kullanılır
        LeaveType leaveType = leaveRequestDTO.getLeaveType() != null
                ? leaveRequestDTO.getLeaveType()
                : LeaveType.ANNUAL;

        String result = leaveService.requestLeaveByEmail(
                leaveRequestDTO.getEmployeeEmail(),
                leaveRequestDTO.getLeaveDaysRequested(),
                leaveType
        );

        return ResponseEntity.ok(result);
    }
}
