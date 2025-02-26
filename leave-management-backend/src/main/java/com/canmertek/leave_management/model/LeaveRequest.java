package com.canmertek.leave_management.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "leave_requests")
public class LeaveRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    private int leaveDaysRequested;

    @Column(name = "status")
    private String status; 

    private LocalDate requestDate;

    
    public LeaveRequest() {
        this.status = "PENDING";
        this.requestDate = LocalDate.now();
    }

    
    public LeaveRequest(Employee employee, int leaveDaysRequested) {
        this.employee = employee;
        this.leaveDaysRequested = leaveDaysRequested;
        this.status = "PENDING";
        this.requestDate = LocalDate.now();
    }

   
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public int getLeaveDaysRequested() {
        return leaveDaysRequested;
    }

    public void setLeaveDaysRequested(int leaveDaysRequested) {
        this.leaveDaysRequested = leaveDaysRequested;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDate requestDate) {
        this.requestDate = requestDate;
    }
}
