package com.nhakhoa.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.nhakhoa.model.DichVu;
import com.nhakhoa.model.NguoiDung;
import com.nhakhoa.service.DichVuService;
import com.nhakhoa.service.NguoiDungService;

@Controller
public class TrangChuController {
    
    @Autowired
    private DichVuService dichVuService;
    
    @Autowired
    private NguoiDungService nguoiDungService;
    
    @GetMapping("/")
    public String trangChu(Model model) {
        List<DichVu> dichVus = dichVuService.findAll();
        List<NguoiDung> bacSis = nguoiDungService.findAllBacSiDangHoatDong();
        
        model.addAttribute("dichVus", dichVus);
        model.addAttribute("bacSis", bacSis);
        return "trang-chu";
    }
    
    @GetMapping("/trang-chu")
    public String trangChuDaDangNhap(Model model, Principal principal) {
        if (principal != null) {
            String email = principal.getName();
            NguoiDung nguoiDung = nguoiDungService.findByEmail(email).orElse(null);
            model.addAttribute("nguoiDung", nguoiDung);
        }
        
        List<DichVu> dichVus = dichVuService.findAll();
        List<NguoiDung> bacSis = nguoiDungService.findAllBacSiDangHoatDong();
        
        model.addAttribute("dichVus", dichVus);
        model.addAttribute("bacSis", bacSis);
        return "trang-chu-da-dang-nhap";
    }
    
    @GetMapping("/gioi-thieu")
    public String gioiThieu() {
        return "gioi-thieu";
    }
    
    @GetMapping("/dich-vu")
    public String dichVu(Model model) {
        List<DichVu> dichVus = dichVuService.findAll();
        model.addAttribute("dichVus", dichVus);
        return "dich-vu";
    }
    
    @GetMapping("/bac-si")
    public String bacSi(Model model) {
        List<NguoiDung> bacSis = nguoiDungService.findAllBacSiDangHoatDong();
        model.addAttribute("bacSis", bacSis);
        return "bac-si";
    }
}