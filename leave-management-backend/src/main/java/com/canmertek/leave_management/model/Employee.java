package com.canmertek.leave_management.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Table(name = "employees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(updatable = false, nullable = false)
    private UUID id;

    private String name;
    private String surname;
    private String email;
    private String password;

    @Column(nullable = false)
    private String department;

    private int leaveDays = 15;

    @Enumerated(EnumType.STRING)
    private Role role;

    public enum Role {
        EMPLOYEE,
        MANAGER
    }
}
