package com.canmertek.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication
@ComponentScan(basePackages = "com.canmertek.leave_management") // Tüm paketleri tara
@EnableJpaRepositories(basePackages = "com.canmertek.leave_management.repository") // Repository'leri tara
@EntityScan(basePackages = "com.canmertek.leave_management.model") // Model paketini tara
public class LeaveManagementBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(LeaveManagementBackendApplication.class, args);
    }
}
