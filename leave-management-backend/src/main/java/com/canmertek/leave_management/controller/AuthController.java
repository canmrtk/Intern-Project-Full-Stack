package com.canmertek.leave_management.controller;

import com.canmertek.leave_management.model.Employee;

import com.canmertek.leave_management.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.Map; 

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    
    public ResponseEntity<?> login(@RequestBody Employee loginRequest) { 
        try {
           
             Employee user = authService.login(loginRequest.getEmail(), loginRequest.getPassword()); 
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) { // ResourceNotFound veya LoginFailed gibi özel hatalar yakalanabilir
            
            if (e.getMessage().contains("Kullanıcı bulunamadı")) {
                 return ResponseEntity.status(404).body(e.getMessage());
            } else if (e.getMessage().contains("Şifre yanlış")) {
                 return ResponseEntity.status(401).body(e.getMessage());
            }
             return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Employee newUser) {
        try {
            Employee registeredUser = authService.register(newUser);
            
            return ResponseEntity.ok("Kullanıcı başarıyla oluşturuldu!");
        } catch (RuntimeException e) {
           
             return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @PutMapping("/update-password")
  
    public ResponseEntity<?> updatePassword(@RequestBody Map<String, String> request) {
         try {
           
            authService.updatePassword(request.get("email"), request.get("oldPassword"), request.get("newPassword")); 
            return ResponseEntity.ok("Şifre başarıyla güncellendi.");
         } catch (RuntimeException e) {
        
             if (e.getMessage().contains("Kullanıcı bulunamadı")) {
                 return ResponseEntity.status(404).body(e.getMessage());
            } else if (e.getMessage().contains("Mevcut şifre yanlış")) {
                 return ResponseEntity.status(401).body(e.getMessage());
            }
             return ResponseEntity.badRequest().body(e.getMessage());
         }
    }
}