package com.canmertek.leave_management.controller;

import com.canmertek.leave_management.dto.UserProfileDTO;
import com.canmertek.leave_management.model.Employee;
import com.canmertek.leave_management.repository.EmployeeRepository;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @GetMapping("/profile")
    public ResponseEntity<Employee> getUserProfile(@RequestParam String email) {
        Optional<Employee> employee = employeeRepository.findByEmail(email);
        return employee.map(ResponseEntity::ok)
                       .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

}
