package com.nhakhoa.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.nhakhoa.model.DichVu;
import com.nhakhoa.model.HoaDon;
import com.nhakhoa.model.LichHen;
import com.nhakhoa.model.NguoiDung;
import com.nhakhoa.model.TrangThaiHoaDon;
import com.nhakhoa.model.TrangThaiLichHen;
import com.nhakhoa.model.VaiTro;
import com.nhakhoa.service.DichVuService;
import com.nhakhoa.service.ExcelExportService;
import com.nhakhoa.service.HoaDonService;
import com.nhakhoa.service.LichHenService;
import com.nhakhoa.service.NguoiDungService;

import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/quan-tri")
public class AdminController {

    @Autowired
    private NguoiDungService nguoiDungService;

    @Autowired
    private DichVuService dichVuService;

    @Autowired
    private LichHenService lichHenService;

    @Autowired
    private HoaDonService hoaDonService;

    @Autowired
    private ExcelExportService excelExportService;

    // ==================== DASHBOARD ====================
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Lấy dữ liệu thống kê từ database
        long soBacSi = nguoiDungService.countByVaiTro(VaiTro.DOCTOR);
        long soBenhNhan = nguoiDungService.countByVaiTro(VaiTro.PATIENT);
        long soNhanVien = nguoiDungService.countByVaiTro(VaiTro.STAFF);
        
        model.addAttribute("soBacSi", soBacSi);
        model.addAttribute("soBenhNhan", soBenhNhan);
        model.addAttribute("soNhanVien", soNhanVien);
        
        return "quan-tri/dashboard";
    }

    // ==================== QUẢN LÝ NGƯỜI DÙNG ====================
    @GetMapping("/nguoi-dung")
    public String quanLyNguoiDung(Model model) {
        List<NguoiDung> danhSachNguoiDung = nguoiDungService.findAll();
        model.addAttribute("danhSachNguoiDung", danhSachNguoiDung);
        return "quan-tri/nguoi-dung";
    }

    @GetMapping("/nguoi-dung/them")
    public String themNguoiDungForm(Model model) {
        model.addAttribute("nguoiDung", new NguoiDung());
        model.addAttribute("cacVaiTro", VaiTro.values());
        return "quan-tri/them-nguoi-dung";
    }

    @PostMapping("/nguoi-dung/luu")
    public String luuNguoiDung(@ModelAttribute NguoiDung nguoiDung, 
                              RedirectAttributes redirectAttributes) {
        try {
            nguoiDungService.save(nguoiDung);
            redirectAttributes.addFlashAttribute("success", "Thêm người dùng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi thêm người dùng: " + e.getMessage());
        }
        return "redirect:/quan-tri/nguoi-dung";
    }

    @GetMapping("/nguoi-dung/sua/{id}")
    public String suaNguoiDungForm(@PathVariable Long id, Model model) {
        NguoiDung nguoiDung = nguoiDungService.findById(id)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));
        model.addAttribute("nguoiDung", nguoiDung);
        model.addAttribute("cacVaiTro", VaiTro.values());
        return "quan-tri/sua-nguoi-dung";
    }

    @PostMapping("/nguoi-dung/cap-nhat/{id}")
    public String capNhatNguoiDung(@PathVariable Long id, 
                                  @ModelAttribute NguoiDung nguoiDung,
                                  RedirectAttributes redirectAttributes) {
        try {
            nguoiDung.setId(id);
            nguoiDungService.save(nguoiDung);
            redirectAttributes.addFlashAttribute("success", "Cập nhật người dùng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi cập nhật người dùng: " + e.getMessage());
        }
        return "redirect:/quan-tri/nguoi-dung";
    }

    @PostMapping("/nguoi-dung/toggle-status/{id}")
    public String toggleTrangThaiNguoiDung(@PathVariable Long id, 
                                          RedirectAttributes redirectAttributes) {
        try {
            nguoiDungService.toggleTrangThai(id);
            redirectAttributes.addFlashAttribute("success", "Thay đổi trạng thái thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi thay đổi trạng thái: " + e.getMessage());
        }
        return "redirect:/quan-tri/nguoi-dung";
    }

    @PostMapping("/nguoi-dung/xoa/{id}")
    public String xoaNguoiDung(@PathVariable Long id, 
                              RedirectAttributes redirectAttributes) {
        try {
            nguoiDungService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Xóa người dùng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa người dùng: " + e.getMessage());
        }
        return "redirect:/quan-tri/nguoi-dung";
    }

    // ==================== QUẢN LÝ DỊCH VỤ ====================
    @GetMapping("/dich-vu")
    public String qlDichVu(Model model) {
        try {
            System.out.println("=== DEBUG: Bắt đầu tải danh sách dịch vụ ===");
            
            List<DichVu> dichVus = dichVuService.findAll();
            
            System.out.println("=== DEBUG: Số lượng dịch vụ: " + (dichVus != null ? dichVus.size() : "NULL") + " ===");
            
            if (dichVus != null) {
                for (DichVu dv : dichVus) {
                    System.out.println("Dịch vụ: " + dv.getTenDichVu() + " - Giá: " + dv.getGiaDichVu());
                }
            }
            
            model.addAttribute("dichVus", dichVus != null ? dichVus : new ArrayList<>());
            return "quan-tri/qldich-vu";
        } catch (Exception e) {
            System.out.println("=== DEBUG: Lỗi khi tải dịch vụ: " + e.getMessage() + " ===");
            e.printStackTrace();
            model.addAttribute("error", "Lỗi khi tải danh sách dịch vụ: " + e.getMessage());
            model.addAttribute("dichVus", new ArrayList<>());
            return "quan-tri/qldich-vu";
        }
    }
    
    @GetMapping("/dich-vu/them")
    public String themDichVuForm(Model model) {
        model.addAttribute("dichVu", new DichVu());
        return "quan-tri/them-dich-vu";
    }
    
    @PostMapping("/dich-vu/luu")
    public String luuDichVu(@ModelAttribute DichVu dichVu, 
                           BindingResult result, 
                           RedirectAttributes redirectAttributes) {
        try {
            if (result.hasErrors()) {
                redirectAttributes.addFlashAttribute("error", "Dữ liệu không hợp lệ");
                return "redirect:/quan-tri/dich-vu/them";
            }
            
            dichVuService.save(dichVu);
            redirectAttributes.addFlashAttribute("success", "Thêm dịch vụ thành công!");
            return "redirect:/quan-tri/dich-vu";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi thêm dịch vụ: " + e.getMessage());
            return "redirect:/quan-tri/dich-vu/them";
        }
    }
    
    @GetMapping("/dich-vu/sua/{id}")
    public String suaDichVuForm(@PathVariable Long id, Model model) {
        try {
            DichVu dichVu = dichVuService.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy dịch vụ với ID: " + id));
            model.addAttribute("dichVu", dichVu);
            return "quan-tri/sua-dich-vu";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/quan-tri/dich-vu";
        }
    }
    
    @PostMapping("/dich-vu/cap-nhat")
    public String capNhatDichVu(@ModelAttribute DichVu dichVu, 
                               BindingResult result, 
                               RedirectAttributes redirectAttributes) {
        try {
            if (result.hasErrors()) {
                redirectAttributes.addFlashAttribute("error", "Dữ liệu không hợp lệ");
                return "redirect:/quan-tri/dich-vu/sua/" + dichVu.getId();
            }
            
            dichVuService.save(dichVu);
            redirectAttributes.addFlashAttribute("success", "Cập nhật dịch vụ thành công!");
            return "redirect:/quan-tri/dich-vu";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi cập nhật dịch vụ: " + e.getMessage());
            return "redirect:/quan-tri/dich-vu/sua/" + dichVu.getId();
        }
    }
    
    @PostMapping("/dich-vu/xoa/{id}")
    public String xoaDichVu(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            dichVuService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Xóa dịch vụ thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa dịch vụ: " + e.getMessage());
        }
        return "redirect:/quan-tri/dich-vu";
    }

    // ==================== QUẢN LÝ LỊCH HẸN ====================
    @GetMapping("/lich-hen")
    public String quanLyLichHen(Model model) {
        return "redirect:/quan-tri/qllich-hen";
    }

    @GetMapping("/qllich-hen")
    public String qlLichHen(Model model) {
        List<LichHen> lichHens = lichHenService.findAll();
        
        if (lichHens == null) {
            lichHens = new ArrayList<>();
        }
        
        if (lichHens.isEmpty()) {
            lichHens = taoDuLieuLichHenMau();
        }
        
        long totalAppointments = lichHens.size();
        long confirmedCount = lichHens.stream()
            .filter(lh -> lh.getTrangThai() == TrangThaiLichHen.DA_XAC_NHAN)
            .count();
        long pendingCount = lichHens.stream()
            .filter(lh -> lh.getTrangThai() == TrangThaiLichHen.CHO_XAC_NHAN)
            .count();
        long cancelledCount = lichHens.stream()
            .filter(lh -> lh.getTrangThai() == TrangThaiLichHen.DA_HUY)
            .count();
        
        model.addAttribute("lichHens", lichHens);
        model.addAttribute("totalAppointments", totalAppointments);
        model.addAttribute("confirmedCount", confirmedCount);
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("cancelledCount", cancelledCount);
        
        return "quan-tri/qllich-hen";
    }

    @GetMapping("/them-lich-hen")
    public String themLichHenForm(Model model) {
        model.addAttribute("lichHen", new LichHen());
        model.addAttribute("nguoiDungService", nguoiDungService);
        model.addAttribute("dichVus", dichVuService.findAll());
        model.addAttribute("bacSis", nguoiDungService.findByVaiTro(VaiTro.DOCTOR));
        model.addAttribute("cacTrangThai", TrangThaiLichHen.values());
        return "quan-tri/them-lich-hen";
    }

    @PostMapping("/lich-hen/luu")
    public String luuLichHen(@ModelAttribute LichHen lichHen, 
                           BindingResult result,
                           RedirectAttributes redirectAttributes) {
        try {
            if (result.hasErrors()) {
                redirectAttributes.addFlashAttribute("error", "Dữ liệu không hợp lệ");
                return "redirect:/quan-tri/them-lich-hen";
            }
            
            lichHenService.save(lichHen);
            redirectAttributes.addFlashAttribute("success", "Thêm lịch hẹn thành công!");
            return "redirect:/quan-tri/qllich-hen";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi thêm lịch hẹn: " + e.getMessage());
            return "redirect:/quan-tri/them-lich-hen";
        }
    }

    @GetMapping("/sua-lich-hen/{id}")
    public String suaLichHenForm(@PathVariable Long id, Model model) {
        try {
            LichHen lichHen = lichHenService.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch hẹn với ID: " + id));
            
            model.addAttribute("lichHen", lichHen);
            model.addAttribute("nguoiDungService", nguoiDungService);
            model.addAttribute("dichVus", dichVuService.findAll());
            model.addAttribute("bacSis", nguoiDungService.findByVaiTro(VaiTro.DOCTOR));
            model.addAttribute("cacTrangThai", TrangThaiLichHen.values());
            
            return "quan-tri/sua-lich-hen";
        } catch (Exception e) {
            return "redirect:/quan-tri/qllich-hen?error=notfound";
        }
    }

    @PostMapping("/lich-hen/cap-nhat")
    public String capNhatLichHen(@ModelAttribute LichHen lichHen,
                               BindingResult result,
                               RedirectAttributes redirectAttributes) {
        try {
            if (result.hasErrors()) {
                redirectAttributes.addFlashAttribute("error", "Dữ liệu không hợp lệ");
                return "redirect:/quan-tri/sua-lich-hen/" + lichHen.getId();
            }
            
            lichHenService.save(lichHen);
            redirectAttributes.addFlashAttribute("success", "Cập nhật lịch hẹn thành công!");
            return "redirect:/quan-tri/qllich-hen";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi cập nhật lịch hẹn: " + e.getMessage());
            return "redirect:/quan-tri/sua-lich-hen/" + lichHen.getId();
        }
    }

    @PostMapping("/lich-hen/xoa/{id}")
    public String xoaLichHen(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            lichHenService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Xóa lịch hẹn thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa lịch hẹn: " + e.getMessage());
        }
        return "redirect:/quan-tri/qllich-hen";
    }

    // ==================== QUẢN LÝ HÓA ĐƠN ====================
    @GetMapping("/hoa-don")
    public String quanLyHoaDon(Model model) {
        return "redirect:/quan-tri/qlhoa-don";
    }

    @GetMapping("/qlhoa-don")
    public String qlHoaDon(Model model) {
        List<HoaDon> hoaDons = hoaDonService.findAll();
        
        if (hoaDons == null) {
            hoaDons = new ArrayList<>();
        }
        
        if (hoaDons.isEmpty()) {
            hoaDons = taoDuLieuHoaDonMau();
        }
        
        double totalRevenue = hoaDons.stream()
            .filter(hd -> hd.getTrangThai() == TrangThaiHoaDon.DA_THANH_TOAN)
            .mapToDouble(hd -> hd.getTongTien() != null ? hd.getTongTien().doubleValue() : 0)
            .sum();
        
        long paidCount = hoaDons.stream()
            .filter(hd -> hd.getTrangThai() == TrangThaiHoaDon.DA_THANH_TOAN)
            .count();
        
        long pendingCount = hoaDons.stream()
            .filter(hd -> hd.getTrangThai() == TrangThaiHoaDon.CHUA_THANH_TOAN)
            .count();
        
        long cancelledCount = hoaDons.stream()
            .filter(hd -> hd.getTrangThai() == TrangThaiHoaDon.DA_HUY)
            .count();
        
        model.addAttribute("hoaDons", hoaDons);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("paidCount", paidCount);
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("cancelledCount", cancelledCount);
        
        return "quan-tri/qlhoa-don";
    }

    // ==================== BÁO CÁO THỐNG KÊ ====================
    @GetMapping("/bao-cao")
    public String baoCaoThongKe(Model model) {
        return "redirect:/quan-tri/qlbao-cao";
    }

    @GetMapping("/qlbao-cao")
    public String qlBaoCao(Model model) {
        // Giả lập dữ liệu thống kê
        model.addAttribute("doanhThuThang", 15000000);
        model.addAttribute("soLichHenThang", 45);
        model.addAttribute("soBenhNhanMoi", 12);
        model.addAttribute("soBacSi", nguoiDungService.countByVaiTro(VaiTro.DOCTOR));
        model.addAttribute("soBenhNhan", nguoiDungService.countByVaiTro(VaiTro.PATIENT));
        model.addAttribute("soNhanVien", nguoiDungService.countByVaiTro(VaiTro.STAFF));
        
        return "quan-tri/qlbao-cao";
    }

    // ==================== ALIAS ENDPOINTS ====================
    @GetMapping("/qldich-vu")
    public String redirectDichVu() {
        return "redirect:/quan-tri/dich-vu";
    }

    // ==================== XUẤT EXCEL ====================
    @GetMapping("/xuat-excel")
    public void xuatBaoCaoExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String fileName = "bao-cao-nha-khoa-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy-HH-mm")) + ".xlsx";
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        
        if (excelExportService != null) {
            byte[] excelData = excelExportService.exportBaoCaoTongHop();
            response.getOutputStream().write(excelData);
            response.getOutputStream().flush();
        } else {
            throw new RuntimeException("ExcelExportService chưa được khởi tạo");
        }
    }

    // ==================== PHƯƠNG THỨC HỖ TRỢ ====================
    private List<LichHen> taoDuLieuLichHenMau() {
        List<LichHen> lichHens = new ArrayList<>();
        
        for (int i = 1; i <= 10; i++) {
            LichHen lichHen = new LichHen();
            lichHen.setId((long) i);
            lichHen.setThoiGianHen(java.time.LocalDateTime.now().plusDays(i));
            
            TrangThaiLichHen[] trangThai = TrangThaiLichHen.values();
            lichHen.setTrangThai(trangThai[i % trangThai.length]);
            
            lichHens.add(lichHen);
        }
        
        return lichHens;
    }
    
    private List<HoaDon> taoDuLieuHoaDonMau() {
        List<HoaDon> hoaDons = new ArrayList<>();
        
        for (int i = 1; i <= 10; i++) {
            HoaDon hoaDon = new HoaDon();
            hoaDon.setId((long) i);
            hoaDon.setTongTien(BigDecimal.valueOf(i * 500000));
            hoaDon.setThoiGianTao(java.time.LocalDateTime.now().minusDays(i));
            
            TrangThaiHoaDon[] trangThai = TrangThaiHoaDon.values();
            hoaDon.setTrangThai(trangThai[i % trangThai.length]);
            
            hoaDons.add(hoaDon);
        }
        
        return hoaDons;
    }
}
