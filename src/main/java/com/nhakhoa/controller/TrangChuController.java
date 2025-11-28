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
    
    // SỬA LẠI: Endpoint chính - kiểm tra đăng nhập
    @GetMapping("/")
    public String trangChu(Model model, Principal principal) {
        System.out.println("=== TRANG CHỦ (/): Principal = " + (principal != null ? principal.getName() : "null") + " ===");
        
        // Nếu đã đăng nhập, chuyển hướng đến trang đã đăng nhập
        if (principal != null) {
            System.out.println("=== ĐÃ ĐĂNG NHẬP - Chuyển hướng đến /trang-chu ===");
            return "redirect:/trang-chu";
        }
        
        // Nếu chưa đăng nhập, hiển thị trang chủ bình thường
        List<DichVu> dichVus = dichVuService.findAll();
        List<NguoiDung> bacSis = nguoiDungService.findAllBacSiDangHoatDong();
        
        model.addAttribute("dichVus", dichVus);
        model.addAttribute("bacSis", bacSis);
        return "trang-chu";
    }
    
    // SỬA LẠI: Trang chủ đã đăng nhập - xử lý đầy đủ
    @GetMapping("/trang-chu")
    public String trangChuDaDangNhap(Model model, Principal principal) {
        System.out.println("=== TRANG CHỦ ĐÃ ĐĂNG NHẬP (/trang-chu) ===");
        
        // Kiểm tra đăng nhập
        if (principal == null) {
            System.out.println("=== CHƯA ĐĂNG NHẬP - Chuyển hướng về / ===");
            return "redirect:/";
        }
        
        String email = principal.getName();
        System.out.println("=== Email từ Principal: " + email + " ===");
        
        // Lấy thông tin người dùng từ database
        NguoiDung nguoiDung = nguoiDungService.findByEmail(email).orElse(null);
        if (nguoiDung != null) {
            System.out.println("=== Tìm thấy người dùng: " + nguoiDung.getHoTen() + " - Vai trò: " + nguoiDung.getVaiTro() + " ===");
            model.addAttribute("nguoiDung", nguoiDung);
        } else {
            System.out.println("=== KHÔNG tìm thấy người dùng trong database ===");
            // Nếu không tìm thấy, chuyển về trang chủ chưa đăng nhập
            return "redirect:/";
        }
        
        // Lấy dữ liệu dịch vụ và bác sĩ
        List<DichVu> dichVus = dichVuService.findAll();
        List<NguoiDung> bacSis = nguoiDungService.findAllBacSiDangHoatDong();
        
        model.addAttribute("dichVus", dichVus);
        model.addAttribute("bacSis", bacSis);
        return "trang-chu-da-dang-nhap";
    }
    
    // SỬA LẠI: Các trang khác cũng cần xử lý đăng nhập
    @GetMapping("/gioi-thieu")
    public String gioiThieu(Model model, Principal principal) {
        // Xử lý principal cho tất cả các trang
        if (principal != null) {
            String email = principal.getName();
            NguoiDung nguoiDung = nguoiDungService.findByEmail(email).orElse(null);
            model.addAttribute("nguoiDung", nguoiDung);
        }
        return "gioi-thieu";
    }
    
    @GetMapping("/dich-vu")
    public String dichVu(Model model, Principal principal) {
        // Xử lý principal
        if (principal != null) {
            String email = principal.getName();
            NguoiDung nguoiDung = nguoiDungService.findByEmail(email).orElse(null);
            model.addAttribute("nguoiDung", nguoiDung);
        }
        
        List<DichVu> dichVus = dichVuService.findAll();
        model.addAttribute("dichVus", dichVus);
        return "dich-vu";
    }
    
    @GetMapping("/bac-si")
    public String bacSi(Model model, Principal principal) {
        // Xử lý principal
        if (principal != null) {
            String email = principal.getName();
            NguoiDung nguoiDung = nguoiDungService.findByEmail(email).orElse(null);
            model.addAttribute("nguoiDung", nguoiDung);
        }
        
        List<NguoiDung> bacSis = nguoiDungService.findAllBacSiDangHoatDong();
        model.addAttribute("bacSis", bacSis);
        return "bac-si";
    }
}
