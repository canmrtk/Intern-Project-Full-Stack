package com.canmertek.leave_management.model;

import jakarta.persistence.*;

@Entity
@Table(name = "employees")
public class Employee {
	
	

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String surname;
    private String email;
    private String password;
    @Column(nullable = false)
    private String department;

    private int leaveDays = 15; // Varsayılan olarak 15 gün izin ver
    @Enumerated(EnumType.STRING)
    private Role role;

    

	public Employee() {}
    
    public enum Role {
        EMPLOYEE, // Çalışan
        MANAGER   // Yönetici
    }

    public Employee(String name, String surname, String email, String department) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.department = department;
        this.leaveDays = 15;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public int getLeaveDays() { 
        return leaveDays;
    }

    public void setLeaveDays(int leaveDays) { 
        this.leaveDays = leaveDays;
    }
    
    public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}
}
