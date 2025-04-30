package com.canmertek.leave_management.controller;

import com.canmertek.leave_management.model.Employee;
import com.canmertek.leave_management.service.EmployeeService; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    @Autowired
    private EmployeeService employeeService; 
    @GetMapping("/profile")
    public ResponseEntity<Employee> getUserProfile(@RequestParam String email) {
   
         Employee employee = employeeService.getEmployeeByEmail(email);
        
         return ResponseEntity.ok(employee); 
    }
}