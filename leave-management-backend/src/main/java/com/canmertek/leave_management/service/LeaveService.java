package com.canmertek.leave_management.service;

import com.canmertek.leave_management.model.Employee;
import com.canmertek.leave_management.model.LeaveRequest;
import com.canmertek.leave_management.repository.EmployeeRepository;
import com.canmertek.leave_management.repository.LeaveRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LeaveService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private LeaveRepository leaveRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public String requestLeave(Long employeeId, int leaveDays) {
        Optional<Employee> employeeOpt = employeeRepository.findById(employeeId);
        if (employeeOpt.isPresent()) {
            Employee employee = employeeOpt.get();
            
            if (employee.getLeaveDays() >= leaveDays) {
                employee.setLeaveDays(employee.getLeaveDays() - leaveDays);
                employeeRepository.save(employee);
                
                LeaveRequest leaveRequest = new LeaveRequest(employee, leaveDays);
                leaveRepository.save(leaveRequest);

                // RabbitMQ'ya mesaj gönder
                rabbitTemplate.convertAndSend("leaveRequestsQueue", "İzin talebi işlendi: " + employee.getName());

                return "İzin talebi başarıyla işlendi. Kalan izin günleri: " + employee.getLeaveDays();
            } else {
                return "Yetersiz izin gününüz var!";
            }
        } else {
            return "Çalışan bulunamadı!";
        }
    }
}
