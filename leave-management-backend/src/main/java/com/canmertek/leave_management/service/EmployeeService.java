package com.canmertek.leave_management.service;

import com.canmertek.leave_management.exception.ResourceNotFoundException;
import java.util.UUID;
import com.canmertek.leave_management.model.Employee;
import com.canmertek.leave_management.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    // Çalışanları getir
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    // ID ile çalışan getir
    public Employee getEmployeeById(UUID id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ID " + id + " ile çalışan bulunamadı!"));
    }

    // E-posta ile çalışan getir
    public Employee getEmployeeByEmail(String email) {
        return employeeRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("E-posta " + email + " ile çalışan bulunamadı!"));
    }

    // Çalışan ekle
    public Employee addEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    // Çalışan güncelle
 
        public Employee updateEmployee(UUID id, Employee updatedEmployeeData) {
          
            Employee existingEmployee = getEmployeeById(id); 

       
            existingEmployee.setName(updatedEmployeeData.getName());
            existingEmployee.setSurname(updatedEmployeeData.getSurname());
            existingEmployee.setEmail(updatedEmployeeData.getEmail()); 
            existingEmployee.setDepartment(updatedEmployeeData.getDepartment());
          

            return employeeRepository.save(existingEmployee);
        }

        // Çalışan sil (Refactor edilmiş)
        public void deleteEmployee(UUID id) {
            
            if (!employeeRepository.existsById(id)) {
                 throw new ResourceNotFoundException("ID " + id + " ile çalışan bulunamadı!");
            }
            employeeRepository.deleteById(id); // Doğrudan ID ile sil
        }
    }

  

