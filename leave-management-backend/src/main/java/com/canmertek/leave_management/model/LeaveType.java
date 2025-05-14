// model/LeaveType.java
package com.canmertek.leave_management.model;

public enum LeaveType {
    ANNUAL("Yıllık İzin"),
    SICK("Hastalık İzni"),
    UNPAID("Ücretsiz İzin"),
    MATERNITY("Doğum İzni"), // Anne için
    PATERNITY("Babalık İzni"); // Baba için

    private final String displayName;

    LeaveType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}