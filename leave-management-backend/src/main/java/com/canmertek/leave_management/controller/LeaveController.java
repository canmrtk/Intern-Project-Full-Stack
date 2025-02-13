package com.canmertek.leave_management.controller;

import com.canmertek.leave_management.dto.LeaveRequestDTO;
import com.canmertek.leave_management.service.LeaveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/leave")
@Tag(name = "Leave Management", description = "Çalışan izin yönetimi ile ilgili API'ler")
public class LeaveController {

    @Autowired
    private LeaveService leaveService;

    @Operation(summary = "İzin talebi oluştur", description = "Çalışanın izin talebini oluşturur ve işler.")
    @PostMapping("/request")
    public String requestLeave(@Valid @RequestBody LeaveRequestDTO leaveRequestDTO) {
        return leaveService.requestLeave(leaveRequestDTO.getEmployeeId(), leaveRequestDTO.getLeaveDays());
    }
}
