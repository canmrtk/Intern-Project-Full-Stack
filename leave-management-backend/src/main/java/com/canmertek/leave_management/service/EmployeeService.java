package com.canmertek.leave_management.service;

import com.canmertek.leave_management.model.Employee;
import com.canmertek.leave_management.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Employee addEmployee(Employee employee) {
        if (employee.getName() == null || employee.getSurname() == null || employee.getEmail() == null) {
            throw new RuntimeException("Çalışan bilgileri eksik!");
        }
        return employeeRepository.save(employee);
    }
}
