package com.canmertek.leave_management.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

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
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "UUID DEFAULT gen_random_uuid()")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    private int leaveDaysRequested;
    private LocalDate requestDate;
    private String status;

    public LeaveRequest(Employee employee, int leaveDaysRequested) {
        this.employee = employee;
        this.leaveDaysRequested = leaveDaysRequested;
        this.requestDate = LocalDate.now();
        this.status = "PENDING";
    }
}
