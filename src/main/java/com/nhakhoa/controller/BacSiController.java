package com.nhakhoa.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.nhakhoa.model.BenhAn;
import com.nhakhoa.model.LichHen;
import com.nhakhoa.model.NguoiDung;
import com.nhakhoa.model.TrangThaiLichHen;
import com.nhakhoa.service.BenhAnService;
import com.nhakhoa.service.LichHenService;
import com.nhakhoa.service.NguoiDungService;

@Controller
@RequestMapping("/bac-si")
@PreAuthorize("hasRole('DOCTOR') or hasRole('ADMIN')")
public class BacSiController {
    
    @Autowired
    private LichHenService lichHenService;
    
    @Autowired
    private BenhAnService benhAnService;
    
    @Autowired
    private NguoiDungService nguoiDungService;
    
    // ==================== DASHBOARD BÁC SĨ ====================
    @GetMapping("/dashboard")
    public String dashboard(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        Optional<NguoiDung> bacSiOpt = nguoiDungService.findByEmail(email);
        
        if (bacSiOpt.isEmpty()) {
            return "redirect:/dang-nhap";
        }
        
        NguoiDung bacSi = bacSiOpt.get();
        List<LichHen> lichHens = lichHenService.findByBacSi(bacSi);
        
        // Thống kê
        long tongSoLichHen = lichHens.size();
        long lichHenDaXacNhan = lichHens.stream()
                .filter(lh -> lh.getTrangThai() == TrangThaiLichHen.DA_XAC_NHAN)
                .count();
        long lichHenChoXacNhan = lichHens.stream()
                .filter(lh -> lh.getTrangThai() == TrangThaiLichHen.CHO_XAC_NHAN)
                .count();
        long tongBenhAn = benhAnService.demBenhAnTheoBacSi(bacSi);
        
        // Lịch hẹn hôm nay
        List<LichHen> lichHenHomNay = lichHens.stream()
                .filter(lh -> lh.getThoiGianHen().toLocalDate().equals(LocalDate.now()))
                .collect(Collectors.toList());
        
        model.addAttribute("bacSi", bacSi);
        model.addAttribute("tongSoLichHen", tongSoLichHen);
        model.addAttribute("lichHenDaXacNhan", lichHenDaXacNhan);
        model.addAttribute("lichHenChoXacNhan", lichHenChoXacNhan);
        model.addAttribute("tongBenhAn", tongBenhAn);
        model.addAttribute("lichHenHomNay", lichHenHomNay);
        model.addAttribute("lichHenHomNayCount", lichHenHomNay.size());
        model.addAttribute("daKhamHomNay", lichHenHomNay.stream()
                .filter(lh -> lh.getTrangThai() == TrangThaiLichHen.DA_HOAN_THANH)
                .count());
        model.addAttribute("choXacNhanHomNay", lichHenHomNay.stream()
                .filter(lh -> lh.getTrangThai() == TrangThaiLichHen.CHO_XAC_NHAN)
                .count());
        
        return "bac-si/dashboard";
    }
    
    // ==================== QUẢN LÝ LỊCH HẸN ====================
    @GetMapping("/lich-hen")
    public String danhSachLichHen(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        Optional<NguoiDung> bacSiOpt = nguoiDungService.findByEmail(email);
        
        if (bacSiOpt.isEmpty()) {
            return "redirect:/dang-nhap";
        }
        
        NguoiDung bacSi = bacSiOpt.get();
        List<LichHen> lichHens = lichHenService.findByBacSi(bacSi);
        
        // Thống kê
        long totalAppointments = lichHens.size();
        long confirmedCount = lichHens.stream()
                .filter(lh -> lh.getTrangThai() == TrangThaiLichHen.DA_XAC_NHAN)
                .count();
        long pendingCount = lichHens.stream()
                .filter(lh -> lh.getTrangThai() == TrangThaiLichHen.CHO_XAC_NHAN)
                .count();
        long todayCount = lichHens.stream()
                .filter(lh -> lh.getThoiGianHen().toLocalDate().equals(LocalDate.now()))
                .count();
        
        model.addAttribute("bacSi", bacSi);
        model.addAttribute("lichHens", lichHens);
        model.addAttribute("totalAppointments", totalAppointments);
        model.addAttribute("confirmedCount", confirmedCount);
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("todayCount", todayCount);
        
        return "bac-si/lich-hen";
    }
    
    @GetMapping("/lich-hen/{id}")
    public String chiTietLichHen(@PathVariable Long id, Model model,
                                @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        Optional<NguoiDung> bacSiOpt = nguoiDungService.findByEmail(email);
        
        if (bacSiOpt.isEmpty()) {
            return "redirect:/dang-nhap";
        }
        
        Optional<LichHen> lichHenOpt = lichHenService.findById(id);
        
        if (lichHenOpt.isEmpty()) {
            return "redirect:/bac-si/lich-hen?error=notfound";
        }
        
        LichHen lichHen = lichHenOpt.get();
        
        // Kiểm tra quyền truy cập - chỉ bác sĩ được phân công mới xem được
        if (!lichHen.getBacSi().getEmail().equals(email)) {
            return "redirect:/bac-si/lich-hen?error=permission";
        }
        
        // Lấy lịch sử lịch hẹn của bệnh nhân (trừ lịch hẹn hiện tại)
        List<LichHen> lichHenLienQuan = lichHenService.findByBacSi(bacSiOpt.get()).stream()
                .filter(lh -> lh.getNguoiDung().getId().equals(lichHen.getNguoiDung().getId()))
                .filter(lh -> !lh.getId().equals(lichHen.getId()))
                .sorted((lh1, lh2) -> lh2.getThoiGianHen().compareTo(lh1.getThoiGianHen()))
                .collect(Collectors.toList());
        
        model.addAttribute("bacSi", bacSiOpt.get());
        model.addAttribute("lichHen", lichHen);
        model.addAttribute("lichHenLienQuan", lichHenLienQuan);
        
        return "bac-si/chi-tiet-lich-hen";
    }
    
    @PostMapping("/lich-hen/{id}/xac-nhan")
    public String xacNhanLichHen(@PathVariable Long id,
                                @AuthenticationPrincipal UserDetails userDetails,
                                RedirectAttributes redirectAttributes) {
        String email = userDetails.getUsername();
        boolean result = lichHenService.xacNhanLichHen(id, email);
        
        if (result) {
            redirectAttributes.addFlashAttribute("success", "Xác nhận lịch hẹn thành công!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Xác nhận lịch hẹn thất bại!");
        }
        return "redirect:/bac-si/lich-hen";
    }
    
    // ==================== QUẢN LÝ BỆNH ÁN ====================
    @GetMapping("/benh-an")
    public String danhSachBenhAn(Model model,
                                @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        Optional<NguoiDung> bacSiOpt = nguoiDungService.findByEmail(email);
        
        if (bacSiOpt.isEmpty()) {
            return "redirect:/dang-nhap";
        }
        
        NguoiDung bacSi = bacSiOpt.get();
        List<BenhAn> benhAns = benhAnService.findByBacSi(bacSi);
        
        model.addAttribute("bacSi", bacSi);
        model.addAttribute("benhAns", benhAns);
      
        
        return "bac-si/benh-an";
    }
    
    @GetMapping("/benh-an/tao-moi")
    public String hienThiTaoBenhAn(@RequestParam(required = false) Long lichHenId,
                                  Model model,
                                  @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        Optional<NguoiDung> bacSiOpt = nguoiDungService.findByEmail(email);
        
        if (bacSiOpt.isEmpty()) {
            return "redirect:/dang-nhap";
        }
        
        NguoiDung bacSi = bacSiOpt.get();
        BenhAn benhAn = new BenhAn();
        
        // Nếu có lichHenId, tự động điền thông tin từ lịch hẹn
        if (lichHenId != null) {
            Optional<LichHen> lichHenOpt = lichHenService.findById(lichHenId);
            if (lichHenOpt.isPresent()) {
                LichHen lichHen = lichHenOpt.get();
                
                // Kiểm tra quyền truy cập
                if (!lichHen.getBacSi().getEmail().equals(email)) {
                    return "redirect:/bac-si/benh-an?error=permission";
                }
                
                model.addAttribute("lichHen", lichHen);
                benhAn.setLichHen(lichHen);
                benhAn.setBacSi(lichHen.getBacSi());
            }
        }
        
        model.addAttribute("bacSi", bacSi);
        model.addAttribute("benhAn", benhAn);
        model.addAttribute("lichHenId", lichHenId);
        
        return "bac-si/tao-benh-an";
    }
    
    @PostMapping("/benh-an/tao-moi")
    public String taoBenhAn(@RequestParam Long lichHenId,
                           @ModelAttribute BenhAn benhAn,
                           @AuthenticationPrincipal UserDetails userDetails,
                           RedirectAttributes redirectAttributes) {
        String email = userDetails.getUsername();
        
        Optional<LichHen> lichHenOpt = lichHenService.findById(lichHenId);
        if (lichHenOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy lịch hẹn!");
            return "redirect:/bac-si/benh-an";
        }
        
        LichHen lichHen = lichHenOpt.get();
        
        // Kiểm tra quyền truy cập
        if (!lichHen.getBacSi().getEmail().equals(email)) {
            redirectAttributes.addFlashAttribute("error", "Bạn không có quyền truy cập lịch hẹn này!");
            return "redirect:/bac-si/benh-an";
        }
        
        benhAn.setLichHen(lichHen);
        benhAn.setBacSi(lichHen.getBacSi());
        
        benhAnService.save(benhAn);
        
        // Cập nhật trạng thái lịch hẹn thành đã hoàn thành
        lichHenService.capNhatTrangThai(lichHenId, TrangThaiLichHen.DA_HOAN_THANH);
        
        redirectAttributes.addFlashAttribute("success", "Tạo bệnh án thành công!");
        return "redirect:/bac-si/benh-an";
    }
    
    @GetMapping("/benh-an/{id}")
    public String chiTietBenhAn(@PathVariable Long id, Model model,
                               @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        Optional<BenhAn> benhAnOpt = benhAnService.findById(id);
        
        if (benhAnOpt.isEmpty()) {
            return "redirect:/bac-si/benh-an?error=notfound";
        }
        
        BenhAn benhAn = benhAnOpt.get();
        
        // Kiểm tra quyền truy cập
        if (!benhAn.getBacSi().getEmail().equals(email)) {
            return "redirect:/bac-si/benh-an?error=permission";
        }
        
        Optional<NguoiDung> bacSiOpt = nguoiDungService.findByEmail(email);
        bacSiOpt.ifPresent(bacSi -> model.addAttribute("bacSi", bacSi));
        
        model.addAttribute("benhAn", benhAn);
        return "bac-si/chi-tiet-benh-an";
    }
    
    @GetMapping("/benh-an/{id}/chinh-sua")
    public String hienThiChinhSuaBenhAn(@PathVariable Long id, Model model,
                                       @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        Optional<BenhAn> benhAnOpt = benhAnService.findById(id);
        
        if (benhAnOpt.isEmpty()) {
            return "redirect:/bac-si/benh-an?error=notfound";
        }
        
        BenhAn benhAn = benhAnOpt.get();
        
        // Kiểm tra quyền truy cập
        if (!benhAn.getBacSi().getEmail().equals(email)) {
            return "redirect:/bac-si/benh-an?error=permission";
        }
        
        Optional<NguoiDung> bacSiOpt = nguoiDungService.findByEmail(email);
        bacSiOpt.ifPresent(bacSi -> model.addAttribute("bacSi", bacSi));
        
        model.addAttribute("benhAn", benhAn);
        return "bac-si/chinh-sua-benh-an";
    }
    
    @PostMapping("/benh-an/{id}/chinh-sua")
    public String chinhSuaBenhAn(@PathVariable Long id,
                                @ModelAttribute BenhAn benhAnCapNhat,
                                @AuthenticationPrincipal UserDetails userDetails,
                                RedirectAttributes redirectAttributes) {
        String email = userDetails.getUsername();
        Optional<BenhAn> benhAnOpt = benhAnService.findById(id);
        
        if (benhAnOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy bệnh án!");
            return "redirect:/bac-si/benh-an";
        }
        
        BenhAn benhAn = benhAnOpt.get();
        
        // Kiểm tra quyền truy cập
        if (!benhAn.getBacSi().getEmail().equals(email)) {
            redirectAttributes.addFlashAttribute("error", "Bạn không có quyền sửa bệnh án này!");
            return "redirect:/bac-si/benh-an";
        }
        
        // Cập nhật thông tin
        benhAn.setChanDoan(benhAnCapNhat.getChanDoan());
        benhAn.setDonThuoc(benhAnCapNhat.getDonThuoc());
        benhAn.setPhacDoDieuTri(benhAnCapNhat.getPhacDoDieuTri());
        benhAn.setGhiChu(benhAnCapNhat.getGhiChu());
        benhAn.setHinhAnhXQuang(benhAnCapNhat.getHinhAnhXQuang());
        benhAn.setHinhAnhKhac(benhAnCapNhat.getHinhAnhKhac());
        benhAn.setThoiGianCapNhat(LocalDateTime.now());
        
        benhAnService.save(benhAn);
        
        redirectAttributes.addFlashAttribute("success", "Cập nhật bệnh án thành công!");
        return "redirect:/bac-si/benh-an/" + id;
    }
    
    // ==================== LỊCH LÀM VIỆC ====================
    @GetMapping("/lich-lam-viec")
    public String lichLamViec(Model model,
                             @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        Optional<NguoiDung> bacSiOpt = nguoiDungService.findByEmail(email);
        
        if (bacSiOpt.isEmpty()) {
            return "redirect:/dang-nhap";
        }
        
        NguoiDung bacSi = bacSiOpt.get();
        
        // Lấy dữ liệu thống kê cho lịch làm việc
        List<LichHen> lichHens = lichHenService.findByBacSi(bacSi);
        long totalAppointmentsThisWeek = lichHens.stream()
                .filter(lh -> lh.getThoiGianHen().isAfter(LocalDateTime.now().minusDays(7)))
                .count();
        long completedAppointmentsThisWeek = lichHens.stream()
                .filter(lh -> lh.getThoiGianHen().isAfter(LocalDateTime.now().minusDays(7)))
                .filter(lh -> lh.getTrangThai() == TrangThaiLichHen.DA_HOAN_THANH)
                .count();
        long pendingAppointmentsThisWeek = lichHens.stream()
                .filter(lh -> lh.getThoiGianHen().isAfter(LocalDateTime.now().minusDays(7)))
                .filter(lh -> lh.getTrangThai() == TrangThaiLichHen.CHO_XAC_NHAN)
                .count();
        
        model.addAttribute("bacSi", bacSi);
        model.addAttribute("totalAppointmentsThisWeek", totalAppointmentsThisWeek);
        model.addAttribute("completedAppointmentsThisWeek", completedAppointmentsThisWeek);
        model.addAttribute("pendingAppointmentsThisWeek", pendingAppointmentsThisWeek);
        
        return "bac-si/lich-lam-viec";
    }
    
    // ==================== THỐNG KÊ CÁ NHÂN ====================
    @GetMapping("/thong-ke")
    public String thongKeCaNhan(Model model,
                               @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        Optional<NguoiDung> bacSiOpt = nguoiDungService.findByEmail(email);
        
        if (bacSiOpt.isEmpty()) {
            return "redirect:/dang-nhap";
        }
        
        NguoiDung bacSi = bacSiOpt.get();
        
        // Lấy dữ liệu thống kê
        List<LichHen> lichHens = lichHenService.findByBacSi(bacSi);
        List<BenhAn> benhAns = benhAnService.findByBacSi(bacSi);
        
        long totalAppointments = lichHens.size();
        long completedAppointments = lichHens.stream()
                .filter(lh -> lh.getTrangThai() == TrangThaiLichHen.DA_HOAN_THANH)
                .count();
        long totalMedicalRecords = benhAns.size();
        
        // Tính toán các chỉ số khác
        double satisfactionRate = 95.5; // Giả định
        double avgAppointmentsPerDay = totalAppointments > 0 ? 
                (double) totalAppointments / 30 : 0; // Giả định 30 ngày
        double completionRate = totalAppointments > 0 ? 
                (double) completedAppointments / totalAppointments * 100 : 0;
        double avgRating = 4.8; // Giả định
        
        model.addAttribute("bacSi", bacSi);
        model.addAttribute("totalAppointments", totalAppointments);
        model.addAttribute("completedAppointments", completedAppointments);
        model.addAttribute("totalMedicalRecords", totalMedicalRecords);
        model.addAttribute("satisfactionRate", satisfactionRate);
        model.addAttribute("avgAppointmentsPerDay", String.format("%.1f", avgAppointmentsPerDay));
        model.addAttribute("completionRate", String.format("%.0f", completionRate));
        model.addAttribute("avgRating", avgRating);
        model.addAttribute("bestMonth", "Tháng 6");
        model.addAttribute("mostPopularService", "Khám tổng quát");
        model.addAttribute("avgWorkingHours", "8h/ngày");
        model.addAttribute("mostProductiveDay", "Thứ 4");
        
        return "bac-si/thong-ke";
    }
    
    // ==================== API CHO AJAX/XỬ LÝ BẤT ĐỒNG BỘ ====================
    @GetMapping("/api/lich-hen/ngay")
    public String getLichHenTheoNgay(@RequestParam String ngay,
                                    Model model,
                                    @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        Optional<NguoiDung> bacSiOpt = nguoiDungService.findByEmail(email);
        
        if (bacSiOpt.isEmpty()) {
            return "redirect:/dang-nhap";
        }
        
        NguoiDung bacSi = bacSiOpt.get();
        LocalDate localDate = LocalDate.parse(ngay);
        
        List<LichHen> lichHens = lichHenService.findByBacSi(bacSi).stream()
                .filter(lh -> lh.getThoiGianHen().toLocalDate().equals(localDate))
                .collect(Collectors.toList());
        
        model.addAttribute("lichHens", lichHens);
        return "bac-si/fragments/lich-hen-list :: lichHenList";
    }
    
    // ==================== XỬ LÝ LỖI VÀ REDIRECT ====================
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "error/access-denied";
    }
}
