package com.canmertek.leave_management.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "leave_requests")
public class LeaveRequest {
	
	@Column(name = "status")
	private String status = "PENDING"; 


    public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    private int leaveDaysRequested;

    private LocalDate requestDate;

    public LeaveRequest() {
        this.requestDate = LocalDate.now(); // İzin talebi oluşturulduğunda tarihi kaydeder
    }

    public LeaveRequest(Employee employee, int leaveDaysRequested) {
        this.employee = employee;
        this.leaveDaysRequested = leaveDaysRequested;
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

    public LocalDate getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDate requestDate) {
        this.requestDate = requestDate;
    }
}
