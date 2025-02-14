package com.canmertek.leave_management.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class LeaveRequestDTO {

    @NotBlank(message = "E-posta adresi boş olamaz!")
    @Email(message = "Geçerli bir e-posta adresi giriniz!")
    private String email;

    @NotNull(message = "İzin gün sayısı boş olamaz!")
    @Min(value = 1, message = "En az 1 gün izin almalısınız!")
    private int leaveDays;

    public LeaveRequestDTO() {}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getLeaveDays() {
        return leaveDays;
    }

    public void setLeaveDays(int leaveDays) {
        this.leaveDays = leaveDays;
    }
}
