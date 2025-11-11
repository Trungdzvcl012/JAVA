package com.nhakhoa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.nhakhoa.model.NguoiDung;
import com.nhakhoa.model.VaiTro;
import com.nhakhoa.service.NguoiDungService;

import jakarta.validation.Valid;

@Controller
public class XacThucController {
    
    @Autowired
    private NguoiDungService nguoiDungService;
    
    @GetMapping("/dang-ky")
    public String hienThiTrangDangKy(Model model) {
        model.addAttribute("nguoiDung", new NguoiDung());
        return "dang-ky";
    }
    
    @PostMapping("/dang-ky")
    public String dangKy(@Valid @ModelAttribute("nguoiDung") NguoiDung nguoiDung, 
                        BindingResult result, Model model) {
        
        if (result.hasErrors()) {
            return "dang-ky";
        }
        
        if (nguoiDungService.existsByEmail(nguoiDung.getEmail())) {
            model.addAttribute("error", "Email đã được sử dụng!");
            return "dang-ky";
        }
        
        nguoiDung.setVaiTro(VaiTro.PATIENT);
        nguoiDungService.save(nguoiDung);
        
        model.addAttribute("success", "Đăng ký thành công! Vui lòng đăng nhập.");
        return "dang-nhap";
    }
    
    @GetMapping("/dang-nhap")
    public String hienThiTrangDangNhap() {
        return "dang-nhap";
    }
}
