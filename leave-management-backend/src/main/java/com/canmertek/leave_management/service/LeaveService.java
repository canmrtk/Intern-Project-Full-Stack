package com.canmertek.leave_management.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.canmertek.leave_management.exception.ResourceNotFoundException;
import com.canmertek.leave_management.model.Employee;
import com.canmertek.leave_management.model.LeaveRequest;
import com.canmertek.leave_management.model.LeaveType;
import com.canmertek.leave_management.repository.EmployeeRepository;
import com.canmertek.leave_management.repository.LeaveRepository;

@Service
public class LeaveService {
    
    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private LeaveRepository leaveRepository;

    /**
     * Belirli bir çalışanın e-posta adresi üzerinden izin talebini işler.
     * @param email Çalışanın e-posta adresi
     * @param leaveDays Talep edilen izin gün sayısı
     * @param leaveType İzin türü (LeaveType enum)
     * @return Mesaj
     */
    public String requestLeaveByEmail(String email, int leaveDays, LeaveType leaveType) {
        Optional<Employee> employeeOpt = employeeRepository.findByEmail(email);

        if (employeeOpt.isEmpty()) {
            throw new ResourceNotFoundException("E-posta ile eşleşen çalışan bulunamadı!");
        }

        Employee employee = employeeOpt.get();

        if (employee.getLeaveDays() < leaveDays) {
            throw new IllegalArgumentException("Yetersiz izin gününüz var!");
        }

        // Kalan izin gününden düş
        employee.setLeaveDays(employee.getLeaveDays() - leaveDays);
        employeeRepository.save(employee);

        // Yeni izin talebi oluştur ve leaveType'ı da set et
        LeaveRequest leaveRequest = new LeaveRequest(employee, leaveDays, leaveType);
        leaveRepository.save(leaveRequest);

        return "İzin talebi başarıyla işlendi. Kalan izin günleri: " + employee.getLeaveDays();
    }
}
