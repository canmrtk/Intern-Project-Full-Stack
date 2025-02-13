package com.canmertek.leave_management.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class LeaveRequestDTO {

    @NotNull(message = "Çalışan ID boş olamaz!")
    private Long employeeId;

    @Min(value = 1, message = "En az 1 gün izin almalısınız!")
    private int leaveDays;

    public LeaveRequestDTO() {}

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public int getLeaveDays() {
        return leaveDays;
    }

    public void setLeaveDays(int leaveDays) {
        this.leaveDays = leaveDays;
    }
}
