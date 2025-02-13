package com.canmertek.leave_management.model;

import jakarta.persistence.*;

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

    public LeaveRequest() {}

    public LeaveRequest(Employee employee, int leaveDaysRequested) {
        this.employee = employee;
        this.leaveDaysRequested = leaveDaysRequested;
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
}
