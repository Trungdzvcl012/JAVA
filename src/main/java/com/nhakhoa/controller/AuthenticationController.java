package com.nhakhoa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.nhakhoa.model.NguoiDung;
import com.nhakhoa.model.VaiTro;
import com.nhakhoa.service.NguoiDungService;

import jakarta.validation.Valid;

@Controller
public class AuthenticationController {
    
    @Autowired
    private NguoiDungService nguoiDungService;
    
    // ========== ĐĂNG KÝ ==========
    
    @GetMapping("/dang-ky")
    public String hienThiTrangDangKy(Model model) {
        System.out.println("=== TRUY CẬP TRANG ĐĂNG KÝ ===");
        model.addAttribute("nguoiDung", new NguoiDung());
        return "dang-ky";
    }
    
    @PostMapping("/dang-ky")
    public String processDangKy(@Valid @ModelAttribute("nguoiDung") NguoiDung nguoiDung, 
                               BindingResult result, 
                               @RequestParam(value = "vaiTro", defaultValue = "PATIENT") String vaiTro,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        
        System.out.println("=== XỬ LÝ ĐĂNG KÝ: " + nguoiDung.getEmail() + " - Vai trò: " + vaiTro + " ===");
        
        if (result.hasErrors()) {
            System.out.println("=== LỖI VALIDATION ===");
            return "dang-ky";
        }
        
        if (nguoiDungService.existsByEmail(nguoiDung.getEmail())) {
            System.out.println("=== EMAIL ĐÃ TỒN TẠI: " + nguoiDung.getEmail() + " ===");
            model.addAttribute("error", "Email đã được sử dụng!");
            return "dang-ky";
        }
        
        try {
            // Set vai trò từ parameter
            VaiTro selectedVaiTro = VaiTro.valueOf(vaiTro);
            nguoiDung.setVaiTro(selectedVaiTro);
            
            // XỬ LÝ TRẠNG THÁI KÍCH HOẠT THEO YÊU CẦU
            if (selectedVaiTro == VaiTro.PATIENT) {
                nguoiDung.setDaKichHoat(true); // Bệnh nhân: kích hoạt ngay
                System.out.println("=== BỆNH NHÂN - KÍCH HOẠT NGAY ===");
            } else {
                nguoiDung.setDaKichHoat(false); // Bác sĩ/Nhân viên: chờ duyệt
                System.out.println("=== " + selectedVaiTro + " - CHỜ DUYỆT ===");
            }
            
            nguoiDungService.dangKyNguoiDung(nguoiDung);
            
            // THÔNG BÁO PHÙ HỢP
            if (selectedVaiTro == VaiTro.PATIENT) {
                redirectAttributes.addFlashAttribute("success", "Đăng ký thành công! Tài khoản đã được kích hoạt. Vui lòng đăng nhập.");
                return "redirect:/dang-nhap?dangky=success&vaitro=patient";
            } else {
                redirectAttributes.addFlashAttribute("success", 
                    "Đăng ký thành công! Tài khoản của bạn đang chờ xét duyệt. " +
                    "Vui lòng chờ admin kích hoạt tài khoản. Thời gian chờ thường từ 5-10 phút.");
                return "redirect:/dang-nhap?dangky=success&vaitro=" + selectedVaiTro.name().toLowerCase();
            }
            
        } catch (IllegalArgumentException e) {
            System.out.println("=== VAI TRÒ KHÔNG HỢP LỆ: " + vaiTro + " ===");
            model.addAttribute("error", "Vai trò không hợp lệ!");
            return "dang-ky";
        } catch (Exception e) {
            System.out.println("=== LỖI HỆ THỐNG: " + e.getMessage() + " ===");
            model.addAttribute("error", "Lỗi hệ thống: " + e.getMessage());
            return "dang-ky";
        }
    }
    
    // ========== ĐĂNG NHẬP ==========
    
    @GetMapping("/dang-nhap")
    public String hienThiTrangDangNhap(@RequestParam(value = "role", defaultValue = "PATIENT") String role, 
                                      @RequestParam(value = "error", required = false) String error,
                                      @RequestParam(value = "dangky", required = false) String dangky,
                                      @RequestParam(value = "vaitro", required = false) String vaitro,
                                      Model model) {
        System.out.println("=== TRUY CẬP TRANG ĐĂNG NHẬP ===");
        
        model.addAttribute("defaultRole", role);
        
        // Xử lý thông báo lỗi đăng nhập
        if (error != null) {
            if (error.equals("true")) {
                model.addAttribute("error", "Email hoặc mật khẩu không đúng!");
            } else if (error.equals("role_error")) {
                model.addAttribute("error", "Tài khoản của bạn không có quyền truy cập với vai trò này!");
            }
        }
        
        // Xử lý thông báo đăng ký thành công
        if (dangky != null && dangky.equals("success")) {
            if ("patient".equals(vaitro)) {
                model.addAttribute("success", "Đăng ký thành công! Tài khoản đã được kích hoạt. Vui lòng đăng nhập.");
            } else if ("doctor".equals(vaitro) || "staff".equals(vaitro)) {
                model.addAttribute("success", 
                    "Đăng ký thành công! Tài khoản của bạn đang chờ xét duyệt. " +
                    "Vui lòng chờ admin kích hoạt tài khoản. Thời gian chờ thường từ 5-10 phút.");
            } else {
                model.addAttribute("success", "Đăng ký thành công! Vui lòng đăng nhập.");
            }
        }
        
        return "dang-nhap";
    }
    
    // ========== XỬ LÝ ĐĂNG NHẬP THÀNH CÔNG ==========
    
    @GetMapping("/xu-ly-dang-nhap-thanh-cong")
    public String xuLyDangNhapThanhCong(Authentication authentication,
                                       @AuthenticationPrincipal UserDetails userDetails,
                                       @RequestParam(value = "role", required = false) String requestedRole) {
        System.out.println("=== XỬ LÝ ĐĂNG NHẬP THÀNH CÔNG ===");
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/dang-nhap?error=true";
        }

        // Ưu tiên sử dụng requestedRole nếu có
        if (requestedRole != null && !requestedRole.isEmpty()) {
            boolean hasRequestedRole = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .anyMatch(authority -> authority.equals("ROLE_" + requestedRole));
            
            if (hasRequestedRole) {
                return "redirect:" + getDefaultTargetUrlForRole(requestedRole);
            } else {
                return "redirect:/dang-nhap?error=role_error&role=" + requestedRole;
            }
        }

        // Fallback: xác định role dựa trên authorities
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (authority.getAuthority().equals("ROLE_ADMIN")) {
                return "redirect:/quan-tri/dashboard";
            } else if (authority.getAuthority().equals("ROLE_DOCTOR")) {
                return "redirect:/bac-si/dashboard";
            } else if (authority.getAuthority().equals("ROLE_STAFF")) {
                return "redirect:/nhan-vien/dashboard";
            }
        }

        // Mặc định cho bệnh nhân
        return "redirect:/trang-chu";
    }
    
    private String getDefaultTargetUrlForRole(String role) {
        switch (role.toUpperCase()) {
            case "ADMIN":
                return "/quan-tri/dashboard";
            case "DOCTOR":
                return "/bac-si/dashboard";
            case "STAFF":
                return "/nhan-vien/dashboard";
            case "PATIENT":
            default:
                return "/trang-chu";
        }
    }
    
    // ========== ACCESS DENIED ==========
    
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";
    }
}