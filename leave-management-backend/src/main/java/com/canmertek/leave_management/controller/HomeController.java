package com.canmertek.leave_management.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class HomeController {

    @GetMapping
    public String home() {
        return "Backend Çalışıyor API'yi test etmek için /api/employees endpoint'ini kullan.";
    }
}
