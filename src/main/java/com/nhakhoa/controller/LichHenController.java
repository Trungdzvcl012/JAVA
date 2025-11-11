package com.nhakhoa.controller;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.nhakhoa.model.DichVu;
import com.nhakhoa.model.LichHen;
import com.nhakhoa.model.NguoiDung;
import com.nhakhoa.model.TrangThaiLichHen;
import com.nhakhoa.model.VaiTro;
import com.nhakhoa.service.DichVuService;
import com.nhakhoa.service.LichHenService;
import com.nhakhoa.service.NguoiDungService;

@Controller
@RequestMapping("/lich-hen")
public class LichHenController {
    
    @Autowired
    private LichHenService lichHenService;
    
    @Autowired
    private NguoiDungService nguoiDungService;
    
    @Autowired
    private DichVuService dichVuService;
    
    @GetMapping("/cua-toi")
    public String lichHenCuaToi(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/dang-nhap";
        }
        
        String email = principal.getName();
        Optional<NguoiDung> nguoiDungOpt = nguoiDungService.findByEmail(email);
        
        if (nguoiDungOpt.isEmpty()) {
            model.addAttribute("error", "Không tìm thấy thông tin người dùng!");
            return "lich-hen-cua-toi";
        }
        
        NguoiDung nguoiDung = nguoiDungOpt.get();
        List<LichHen> lichHens = lichHenService.findByNguoiDung(nguoiDung);
        
        // Tính toán thống kê
        long tongLichHen = lichHens.size();
        long lichHenChoXacNhan = lichHens.stream()
                .filter(lh -> lh.getTrangThai() == TrangThaiLichHen.CHO_XAC_NHAN)
                .count();
        long lichHenDaXacNhan = lichHens.stream()
                .filter(lh -> lh.getTrangThai() == TrangThaiLichHen.DA_XAC_NHAN)
                .count();
        long lichHenHoanThanh = lichHens.stream()
                .filter(lh -> lh.getTrangThai() == TrangThaiLichHen.DA_HOAN_THANH)
                .count();
        
        model.addAttribute("lichHens", lichHens);
        model.addAttribute("nguoiDung", nguoiDung);
        model.addAttribute("tongLichHen", tongLichHen);
        model.addAttribute("lichHenChoXacNhan", lichHenChoXacNhan);
        model.addAttribute("lichHenDaXacNhan", lichHenDaXacNhan);
        model.addAttribute("lichHenHoanThanh", lichHenHoanThanh);
        
        return "lich-hen-cua-toi";
    }
    
    @GetMapping("/dat-lich")
    public String hienThiTrangDatLich(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/dang-nhap";
        }
        
        String email = principal.getName();
        Optional<NguoiDung> nguoiDungOpt = nguoiDungService.findByEmail(email);
        
        if (nguoiDungOpt.isEmpty()) {
            model.addAttribute("error", "Không tìm thấy thông tin người dùng!");
            return "dat-lich";
        }
        
        List<DichVu> dichVus = dichVuService.findAll();
        List<NguoiDung> bacSis = nguoiDungService.findByVaiTro(VaiTro.DOCTOR);
        
        model.addAttribute("dichVus", dichVus);
        model.addAttribute("bacSis", bacSis);
        model.addAttribute("lichHen", new LichHen());
        model.addAttribute("nguoiDung", nguoiDungOpt.get());
        
        return "dat-lich";
    }
    
    @PostMapping("/dat-lich")
    public String datLich(@ModelAttribute LichHen lichHen, 
                        Principal principal, 
                        Model model) {
        
        if (principal == null) {
            return "redirect:/dang-nhap";
        }
        
        String email = principal.getName();
        Optional<NguoiDung> nguoiDungOpt = nguoiDungService.findByEmail(email);
        
        if (nguoiDungOpt.isEmpty()) {
            model.addAttribute("error", "Không tìm thấy thông tin người dùng!");
            return reloadDatLichForm(model);
        }
        
        NguoiDung nguoiDung = nguoiDungOpt.get();
        
        // Kiểm tra dịch vụ và bác sĩ đã được chọn chưa
        if (lichHen.getDichVu() == null || lichHen.getBacSi() == null) {
            model.addAttribute("error", "Vui lòng chọn dịch vụ và bác sĩ!");
            return reloadDatLichForm(model);
        }
        
        // Set relationships
        lichHen.setNguoiDung(nguoiDung);
        lichHen.setTrangThai(TrangThaiLichHen.CHO_XAC_NHAN);
        
        try {
            lichHenService.save(lichHen);
            return "redirect:/lich-hen/cua-toi?success=true";
        } catch (Exception e) {
            model.addAttribute("error", "Có lỗi xảy ra khi đặt lịch: " + e.getMessage());
            return reloadDatLichForm(model);
        }
    }

    // Helper method để reload form data
    private String reloadDatLichForm(Model model) {
        List<DichVu> dichVus = dichVuService.findAll();
        List<NguoiDung> bacSis = nguoiDungService.findByVaiTro(VaiTro.DOCTOR);
        model.addAttribute("dichVus", dichVus);
        model.addAttribute("bacSis", bacSis);
        model.addAttribute("lichHen", new LichHen());
        return "dat-lich";
    }
    
    @PostMapping("/huy/{id}")
    public String huyLichHen(@PathVariable Long id, Principal principal) {
        if (principal == null) {
            return "redirect:/dang-nhap";
        }
        
        String email = principal.getName();
        boolean result = lichHenService.huyLichHen(id, email);
        
        if (result) {
            return "redirect:/lich-hen/cua-toi?success=huy";
        } else {
            return "redirect:/lich-hen/cua-toi?error=huy";
        }
    }
    
    // Thêm method để xem chi tiết lịch hẹn
    @GetMapping("/{id}")
    public String chiTietLichHen(@PathVariable Long id, Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/dang-nhap";
        }
        
        Optional<LichHen> lichHenOpt = lichHenService.findById(id);
        if (lichHenOpt.isEmpty()) {
            return "redirect:/lich-hen/cua-toi?error=notfound";
        }
        
        LichHen lichHen = lichHenOpt.get();
        String email = principal.getName();
        
        // Kiểm tra quyền truy cập
        if (!lichHen.getNguoiDung().getEmail().equals(email)) {
            return "redirect:/lich-hen/cua-toi?error=permission";
        }
        
        model.addAttribute("lichHen", lichHen);
        return "chi-tiet-lich-hen";
    }
}