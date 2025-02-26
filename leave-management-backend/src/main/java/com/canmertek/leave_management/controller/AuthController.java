package com.canmertek.leave_management.controller;

import com.canmertek.leave_management.model.Employee;
import com.canmertek.leave_management.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private EmployeeRepository employeeRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Employee loginRequest) {
        Optional<Employee> employeeOpt = employeeRepository.findByEmail(loginRequest.getEmail());

        if (employeeOpt.isPresent()) {
            Employee employee = employeeOpt.get();
            
            if (employee.getPassword().equals(loginRequest.getPassword())) {
                return ResponseEntity.ok(employee); // Başarılı girişte çalışan bilgilerini dön
            } else {
                return ResponseEntity.status(401).body("Şifre yanlış!");
            }
        } else {
            return ResponseEntity.status(404).body("Kullanıcı bulunamadı!");
        }
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Employee newUser) {
        if (employeeRepository.findByEmail(newUser.getEmail()).isPresent()) {
            return ResponseEntity.status(400).body("Bu e-posta adresi zaten kullanılıyor!");
        }

        newUser.setPassword(newUser.getPassword()); // Şifreyi direkt kaydediyoruz 
        employeeRepository.save(newUser);
        
        return ResponseEntity.ok("Kullanıcı başarıyla oluşturuldu!");
    }
    
    @PutMapping("/update-password")
    public ResponseEntity<?> updatePassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String oldPassword = request.get("oldPassword");
        String newPassword = request.get("newPassword");

        Optional<Employee> employeeOpt = employeeRepository.findByEmail(email);
        
        if (employeeOpt.isPresent()) {
            Employee employee = employeeOpt.get();

            if (!employee.getPassword().equals(oldPassword)) {
                return ResponseEntity.status(401).body("Mevcut şifre yanlış!");
            }

            employee.setPassword(newPassword);
            employeeRepository.save(employee);
            return ResponseEntity.ok("Şifre başarıyla güncellendi.");
        } else {
            return ResponseEntity.status(404).body("Kullanıcı bulunamadı!");
        }
    }


}
