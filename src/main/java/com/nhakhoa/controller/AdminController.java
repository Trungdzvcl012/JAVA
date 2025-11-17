package com.nhakhoa.controller;

import com.nhakhoa.model.NguoiDung;
import com.nhakhoa.model.VaiTro;
import com.nhakhoa.service.NguoiDungService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/quan-tri")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private NguoiDungService nguoiDungService;

    // Dashboard tổng quan
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        long soBacSi = nguoiDungService.countByVaiTro(VaiTro.DOCTOR);
        long soBenhNhan = nguoiDungService.countByVaiTro(VaiTro.PATIENT);
        long soNhanVien = nguoiDungService.countByVaiTro(VaiTro.STAFF);
        
        model.addAttribute("soBacSi", soBacSi);
        model.addAttribute("soBenhNhan", soBenhNhan);
        model.addAttribute("soNhanVien", soNhanVien);
        
        return "quan-tri/dashboard";
    }

    // Quản lý người dùng
    @GetMapping("/nguoi-dung")
    public String quanLyNguoiDung(Model model) {
        List<NguoiDung> danhSachNguoiDung = nguoiDungService.findAll();
        model.addAttribute("danhSachNguoiDung", danhSachNguoiDung);
        return "quan-tri/nguoi-dung";
    }

    // Form thêm/sửa người dùng
    @GetMapping("/nguoi-dung/them")
    public String formThemNguoiDung(Model model) {
        model.addAttribute("nguoiDung", new NguoiDung());
        model.addAttribute("cacVaiTro", VaiTro.values());
        return "quan-tri/them-nguoi-dung";
    }

    @GetMapping("/nguoi-dung/sua/{id}")
    public String formSuaNguoiDung(@PathVariable Long id, Model model) {
        NguoiDung nguoiDung = nguoiDungService.findById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        model.addAttribute("nguoiDung", nguoiDung);
        model.addAttribute("cacVaiTro", VaiTro.values());
        return "quan-tri/sua-nguoi-dung";
    }

    // Xử lý thêm/sửa người dùng
    @PostMapping("/nguoi-dung/luu")
    public String luuNguoiDung(@ModelAttribute NguoiDung nguoiDung) {
        nguoiDungService.save(nguoiDung);
        return "redirect:/quan-tri/nguoi-dung?success=true";
    }

    // Xóa người dùng
    @PostMapping("/nguoi-dung/xoa/{id}")
    public String xoaNguoiDung(@PathVariable Long id) {
        nguoiDungService.deleteById(id);
        return "redirect:/quan-tri/nguoi-dung?deleteSuccess=true";
    }

    // Kích hoạt/vô hiệu hóa người dùng
    @PostMapping("/nguoi-dung/toggle-status/{id}")
    public String toggleTrangThai(@PathVariable Long id) {
        NguoiDung nguoiDung = nguoiDungService.findById(id)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        nguoiDung.setDaKichHoat(!nguoiDung.isDaKichHoat());
        nguoiDungService.save(nguoiDung);
        return "redirect:/quan-tri/nguoi-dung?toggleSuccess=true";
    }

    // Các trang quản lý mới - THÊM VÀO TỪ CODE CŨ
    @GetMapping("/dich-vu")
    public String quanLyDichVu(Model model) {
        return "quan-tri/qldich-vu";
    }

    @GetMapping("/lich-hen")
    public String quanLyLichHen(Model model) {
        return "quan-tri/qllich-hen";
    }

    @GetMapping("/hoa-don")
    public String quanLyHoaDon(Model model) {
        return "quan-tri/qlhoa-don";
    }

    @GetMapping("/bao-cao")
    public String quanLyBaoCao(Model model) {
        return "quan-tri/qlbao-cao";
    }
}