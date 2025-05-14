package com.canmertek.leave_management.repository;

import com.canmertek.leave_management.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List; 
import java.util.Optional;
import java.util.UUID;

public interface EmployeeRepository extends JpaRepository<Employee, UUID>  {

    Optional<Employee> findByEmail(String email);

    boolean existsByEmail(String email);
  
    List<Employee> findByRole(Employee.Role role);
}