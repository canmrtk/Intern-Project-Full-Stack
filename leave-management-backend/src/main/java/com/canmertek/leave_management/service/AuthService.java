package com.canmertek.leave_management.service;

import com.canmertek.leave_management.model.Employee;
import com.canmertek.leave_management.repository.EmployeeRepository;
import com.canmertek.leave_management.exception.ResourceNotFoundException; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private EmployeeRepository employeeRepository;

   

    public Employee login(String email, String password) {
        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı: " + email));

  
        if (!employee.getPassword().equals(password)) {
             throw new RuntimeException("Şifre yanlış!"); 
        }

        return employee;
    }

    public Employee register(Employee newUser) {
         if (employeeRepository.existsByEmail(newUser.getEmail())) {
             throw new RuntimeException("Bu e-posta adresi zaten kullanılıyor!"); 
         }

         newUser.setPassword(newUser.getPassword());

        return employeeRepository.save(newUser);
    }

     public void updatePassword(String email, String oldPassword, String newPassword) {
         Employee employee = employeeRepository.findByEmail(email)
                 .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı: " + email));

    

         if (!employee.getPassword().equals(oldPassword)) {
             throw new RuntimeException("Mevcut şifre yanlış!");
         }
         employee.setPassword(newPassword); 
         employeeRepository.save(employee);
     }
}