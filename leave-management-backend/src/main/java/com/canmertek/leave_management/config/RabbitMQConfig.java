package com.canmertek.leave_management.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue leaveRequestQueue() {
        return new Queue("leaveRequestsQueue", true);
    }
}
