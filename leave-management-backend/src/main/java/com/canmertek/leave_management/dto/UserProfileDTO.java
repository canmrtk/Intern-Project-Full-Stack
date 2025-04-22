package com.canmertek.leave_management.dto;

import com.canmertek.leave_management.model.Employee;

public class UserProfileDTO {
    private String name;
    private String surname;
    private String email;
    private String department;
    private String role;
    private int leaveDays;

    public UserProfileDTO(Employee employee) {
        this.name = employee.getName();
        this.surname = employee.getSurname();
        this.email = employee.getEmail();
        this.department = employee.getDepartment();
        this.role = employee.getRole().name();
        this.leaveDays = employee.getLeaveDays();
    }

    // Getter ve setter'lar (Lombok varsa @Getter/@Setter yeterli)
}
