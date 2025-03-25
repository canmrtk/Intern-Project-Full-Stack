package com.canmertek.leave_management.controller;

import com.canmertek.leave_management.exception.ResourceNotFoundException;
import com.canmertek.leave_management.model.Employee;
import com.canmertek.leave_management.repository.EmployeeRepository;
import com.canmertek.leave_management.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private EmployeeRepository employeeRepository;

   
    @GetMapping
    public ResponseEntity<List<Employee>> getEmployees() {
        List<Employee> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(employees);
    }

   
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable UUID id) {
        Employee employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(employee);
    }

    
    @PostMapping
    public ResponseEntity<Employee> addEmployee(@Valid @RequestBody Employee employee) {
        Employee savedEmployee = employeeService.addEmployee(employee);
        return ResponseEntity.ok(savedEmployee);
    }

    
  

    
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployee(@PathVariable UUID id) {
        Employee employee = employeeService.getEmployeeById(id);
        if (employee == null) {
            throw new ResourceNotFoundException("ID " + id + " ile çalışan bulunamadı!");
        }
        employeeService.deleteEmployee(id);
        return ResponseEntity.ok("Çalışan başarıyla silindi!");
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEmployee(@PathVariable UUID id, @RequestBody Employee updatedEmployee) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Çalışan bulunamadı!"));

        employee.setName(updatedEmployee.getName());
        employee.setSurname(updatedEmployee.getSurname());
        employee.setEmail(updatedEmployee.getEmail());
        employee.setDepartment(updatedEmployee.getDepartment());
        

        employeeRepository.save(employee);
        return ResponseEntity.ok("Çalışan bilgileri başarıyla güncellendi.");
    }

}
