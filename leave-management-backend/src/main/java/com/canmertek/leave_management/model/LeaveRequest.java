package com.canmertek.leave_management.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "leave_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LeaveRequest {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    private int leaveDaysRequested;
    private LocalDate requestDate;
    private String status;

    // Özel Constructor
    public LeaveRequest(Employee employee, int leaveDaysRequested) {
        this.id = UUID.randomUUID(); // UUID'yi elle oluşturuyoruz.
        this.employee = employee;
        this.leaveDaysRequested = leaveDaysRequested;
        this.requestDate = LocalDate.now();
        this.status = "PENDING";
    }
}
