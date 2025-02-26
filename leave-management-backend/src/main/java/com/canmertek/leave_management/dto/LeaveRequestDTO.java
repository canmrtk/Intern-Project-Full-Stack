package com.canmertek.leave_management.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class LeaveRequestDTO {

    @NotBlank(message = "E-posta adresi boş olamaz!")
    @Email(message = "Geçerli bir e-posta adresi giriniz!")
    private String employeeEmail; 

    @NotNull(message = "İzin gün sayısı boş olamaz!")
    @Min(value = 1, message = "En az 1 gün izin almalısınız!")
    private int leaveDaysRequested; 

    public LeaveRequestDTO() {}

    public String getEmployeeEmail() {
        return employeeEmail;
    }

    public void setEmployeeEmail(String employeeEmail) {
        this.employeeEmail = employeeEmail;
    }

    public int getLeaveDaysRequested() {
        return leaveDaysRequested;
    }

    public void setLeaveDaysRequested(int leaveDaysRequested) {
        this.leaveDaysRequested = leaveDaysRequested;
    }
}
