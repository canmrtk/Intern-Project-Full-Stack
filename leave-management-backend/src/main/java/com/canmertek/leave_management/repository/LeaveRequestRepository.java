package com.canmertek.leave_management.repository;

import com.canmertek.leave_management.model.Employee;
import com.canmertek.leave_management.model.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, UUID> {
    
    List<LeaveRequest> findByEmployee(Employee employee);
    
    boolean existsByEmployeeAndStatus(Employee employee, String status);
    List<LeaveRequest> findByEmployeeId(UUID employeeId);

}
