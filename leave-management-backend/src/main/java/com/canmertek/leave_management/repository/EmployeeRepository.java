package com.canmertek.leave_management.repository;

import com.canmertek.leave_management.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

public interface EmployeeRepository extends JpaRepository<Employee, UUID>  {

    Optional<Employee> findByEmail(String email);

    boolean existsByEmail(String email);
    Optional<Employee> findById(UUID id);
    

}
