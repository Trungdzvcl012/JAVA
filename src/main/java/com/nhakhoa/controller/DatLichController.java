package com.nhakhoa.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dat-lich")
public class DatLichController {
    
    // Redirect đến controller lịch hẹn để tránh trùng lặp
    @GetMapping
    public String datLich() {
        return "redirect:/lich-hen/dat-lich";
    }
}