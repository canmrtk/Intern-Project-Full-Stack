package com.canmertek.leave_management.controller;

import com.canmertek.leave_management.dto.UserProfileDTO;
import com.canmertek.leave_management.model.Employee;
import com.canmertek.leave_management.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @GetMapping("/profile")
    public UserProfileDTO getUserProfile(@RequestParam String email) {
        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı!"));
        return new UserProfileDTO(employee);
    }
}
