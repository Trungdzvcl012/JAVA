package com.nhakhoa.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.nhakhoa.model.DichVu;
import com.nhakhoa.model.HoaDon;
import com.nhakhoa.model.LichHen;
import com.nhakhoa.model.NguoiDung;
import com.nhakhoa.model.TrangThaiHoaDon;
import com.nhakhoa.model.TrangThaiLichHen;
import com.nhakhoa.model.VaiTro;
import com.nhakhoa.service.DichVuService;
import com.nhakhoa.service.HoaDonService;
import com.nhakhoa.service.LichHenService;
import com.nhakhoa.service.NguoiDungService;

@Controller
@RequestMapping("/nhan-vien")
@PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
public class NhanVienController {
    
    @Autowired
    private LichHenService lichHenService;
    
    @Autowired
    private HoaDonService hoaDonService;
    
    @Autowired
    private NguoiDungService nguoiDungService;
    
    @Autowired
    private DichVuService dichVuService;
    
    @GetMapping("/lich-hen")
    public String quanLyLichHen(Model model,
                               @RequestParam(required = false) String trangThai,
                               @RequestParam(required = false) Long bacSiId,
                               @RequestParam(required = false) String tuNgay,
                               @RequestParam(required = false) String denNgay) {
        
        List<LichHen> lichHens = lichHenService.findAll();
        List<NguoiDung> bacSiList = nguoiDungService.findByVaiTro(VaiTro.DOCTOR);
        
        // Lọc theo trạng thái nếu có
        if (trangThai != null && !trangThai.isEmpty()) {
            try {
                TrangThaiLichHen trangThaiEnum = TrangThaiLichHen.valueOf(trangThai);
                lichHens = lichHens.stream()
                        .filter(lh -> lh.getTrangThai() == trangThaiEnum)
                        .toList();
            } catch (IllegalArgumentException e) {
                // Nếu trạng thái không hợp lệ, giữ nguyên danh sách
            }
        }
        
        // Lọc theo bác sĩ nếu có
        if (bacSiId != null) {
            lichHens = lichHens.stream()
                    .filter(lh -> lh.getBacSi().getId().equals(bacSiId))
                    .toList();
        }
        
        // Tính toán thống kê
        long tongSoLichHen = lichHens.size();
        long lichHenChoXacNhan = lichHens.stream()
                .filter(lh -> lh.getTrangThai() == TrangThaiLichHen.CHO_XAC_NHAN)
                .count();
        long lichHenDaXacNhan = lichHens.stream()
                .filter(lh -> lh.getTrangThai() == TrangThaiLichHen.DA_XAC_NHAN)
                .count();
        long lichHenHomNay = lichHens.stream()
                .filter(lh -> lh.getThoiGianHen().toLocalDate().equals(LocalDate.now()))
                .count();
        
        model.addAttribute("lichHens", lichHens);
        model.addAttribute("bacSiList", bacSiList);
        model.addAttribute("tongSoLichHen", tongSoLichHen);
        model.addAttribute("lichHenChoXacNhan", lichHenChoXacNhan);
        model.addAttribute("lichHenDaXacNhan", lichHenDaXacNhan);
        model.addAttribute("lichHenHomNay", lichHenHomNay);
        model.addAttribute("selectedTrangThai", trangThai);
        model.addAttribute("selectedBacSiId", bacSiId);
        model.addAttribute("selectedTuNgay", tuNgay);
        model.addAttribute("selectedDenNgay", denNgay);
        
        return "nhan-vien/lich-hen";
    }
    
    @GetMapping("/lich-hen/{id}")
    public String chiTietLichHen(@PathVariable Long id, Model model) {
        Optional<LichHen> lichHenOpt = lichHenService.findById(id);
        
        if (lichHenOpt.isEmpty()) {
            return "redirect:/nhan-vien/lich-hen?error=notfound";
        }
        
        LichHen lichHen = lichHenOpt.get();
        model.addAttribute("lichHen", lichHen);
        
        return "nhan-vien/chi-tiet-lich-hen";
    }
    
    @GetMapping("/lich-hen/tao-moi")
    public String hienThiTaoLichHen(Model model) {
        List<DichVu> dichVus = dichVuService.findAll();
        List<NguoiDung> bacSis = nguoiDungService.findByVaiTro(VaiTro.DOCTOR);
        List<NguoiDung> benhNhans = nguoiDungService.findByVaiTro(VaiTro.PATIENT);
        
        model.addAttribute("lichHen", new LichHen());
        model.addAttribute("dichVus", dichVus);
        model.addAttribute("bacSis", bacSis);
        model.addAttribute("benhNhans", benhNhans);
        
        return "nhan-vien/tao-lich-hen";
    }
    
    @PostMapping("/lich-hen/tao-moi")
    public String taoLichHen(LichHen lichHen) {
        lichHen.setTrangThai(TrangThaiLichHen.DA_XAC_NHAN); // Tự động xác nhận khi nhân viên tạo
        lichHenService.save(lichHen);
        
        return "redirect:/nhan-vien/lich-hen?success=create";
    }
    
    @GetMapping("/lich-hen/{id}/chinh-sua")
    public String hienThiChinhSuaLichHen(@PathVariable Long id, Model model) {
        Optional<LichHen> lichHenOpt = lichHenService.findById(id);
        
        if (lichHenOpt.isEmpty()) {
            return "redirect:/nhan-vien/lich-hen?error=notfound";
        }
        
        List<DichVu> dichVus = dichVuService.findAll();
        List<NguoiDung> bacSis = nguoiDungService.findByVaiTro(VaiTro.DOCTOR);
        List<NguoiDung> benhNhans = nguoiDungService.findByVaiTro(VaiTro.PATIENT);
        
        model.addAttribute("lichHen", lichHenOpt.get());
        model.addAttribute("dichVus", dichVus);
        model.addAttribute("bacSis", bacSis);
        model.addAttribute("benhNhans", benhNhans);
        
        return "nhan-vien/chinh-sua-lich-hen";
    }
    
    @PostMapping("/lich-hen/{id}/chinh-sua")
    public String chinhSuaLichHen(@PathVariable Long id, LichHen lichHenCapNhat) {
        Optional<LichHen> lichHenOpt = lichHenService.findById(id);
        
        if (lichHenOpt.isEmpty()) {
            return "redirect:/nhan-vien/lich-hen?error=notfound";
        }
        
        LichHen lichHen = lichHenOpt.get();
        
        // Cập nhật thông tin - SỬA LỖI Ở ĐÂY
        // Thay vì dùng getBenhNhan(), sử dụng getNguoiDung() vì trong model LichHen có quan hệ với NguoiDung
        lichHen.setNguoiDung(lichHenCapNhat.getNguoiDung()); // Sửa từ setBenhNhan() thành setNguoiDung()
        lichHen.setBacSi(lichHenCapNhat.getBacSi());
        lichHen.setDichVu(lichHenCapNhat.getDichVu());
        lichHen.setThoiGianHen(lichHenCapNhat.getThoiGianHen());
        lichHen.setLyDoKham(lichHenCapNhat.getLyDoKham());
        lichHen.setGhiChu(lichHenCapNhat.getGhiChu());
        
        lichHenService.save(lichHen);
        
        return "redirect:/nhan-vien/lich-hen/" + id + "?success=update";
    }
    
    @PostMapping("/lich-hen/{id}/xac-nhan")
    public String xacNhanLichHen(@PathVariable Long id) {
        boolean result = lichHenService.xacNhanLichHen(id, "nhanvien");
        
        if (result) {
            return "redirect:/nhan-vien/lich-hen?success=xacnhan";
        } else {
            return "redirect:/nhan-vien/lich-hen?error=xacnhan";
        }
    }
    
    @PostMapping("/lich-hen/{id}/huy")
    public String huyLichHen(@PathVariable Long id, 
                           @RequestParam(required = false) String lyDo) {
        boolean result = lichHenService.huyLichHen(id, "nhanvien");
        
        if (result) {
            return "redirect:/nhan-vien/lich-hen?success=huy";
        } else {
            return "redirect:/nhan-vien/lich-hen?error=huy";
        }
    }
    
    @GetMapping("/hoa-don")
    public String quanLyHoaDon(Model model,
                              @RequestParam(required = false) String trangThai) {
        
        List<HoaDon> hoaDons = hoaDonService.findAll();
        
        // Lọc theo trạng thái nếu có
        if (trangThai != null && !trangThai.isEmpty()) {
            try {
                TrangThaiHoaDon trangThaiEnum = TrangThaiHoaDon.valueOf(trangThai);
                hoaDons = hoaDons.stream()
                        .filter(hd -> hd.getTrangThai() == trangThaiEnum)
                        .toList();
            } catch (IllegalArgumentException e) {
                // Nếu trạng thái không hợp lệ, giữ nguyên danh sách
            }
        }
        
        // Tính toán thống kê - SỬA LỖI Ở ĐÂY
        long tongSoHoaDon = hoaDons.size();
        long hoaDonChuaThanhToan = hoaDons.stream()
                .filter(hd -> hd.getTrangThai() == TrangThaiHoaDon.CHUA_THANH_TOAN)
                .count();
        long hoaDonDaThanhToan = hoaDons.stream()
                .filter(hd -> hd.getTrangThai() == TrangThaiHoaDon.DA_THANH_TOAN)
                .count();
        
        // Sửa lỗi BigDecimal -> double: convert BigDecimal to double
        double tongDoanhThu = hoaDons.stream()
                .filter(hd -> hd.getTrangThai() == TrangThaiHoaDon.DA_THANH_TOAN)
                .mapToDouble(hd -> hd.getTongTien().doubleValue()) // Sửa ở đây
                .sum();
        
        model.addAttribute("hoaDons", hoaDons);
        model.addAttribute("tongSoHoaDon", tongSoHoaDon);
        model.addAttribute("hoaDonChuaThanhToan", hoaDonChuaThanhToan);
        model.addAttribute("hoaDonDaThanhToan", hoaDonDaThanhToan);
        model.addAttribute("tongDoanhThu", tongDoanhThu);
        model.addAttribute("selectedTrangThai", trangThai);
        
        return "nhan-vien/hoa-don";
    }
    
    @GetMapping("/hoa-don/{id}")
    public String chiTietHoaDon(@PathVariable Long id, Model model) {
        // Sửa: dùng trực tiếp HoaDon thay vì Optional
        HoaDon hoaDon = hoaDonService.findById(id);
        
        if (hoaDon == null) {
            return "redirect:/nhan-vien/hoa-don?error=notfound";
        }
        

        model.addAttribute("hoaDon", hoaDon);
        
        return "nhan-vien/chi-tiet-hoa-don";
    }

    @PostMapping("/hoa-don/{id}/thanh-toan")
    public String thanhToanHoaDon(@PathVariable Long id,
                                @RequestParam String phuongThucThanhToan) {
        // Sửa: dùng phương thức thanh toán có sẵn trong service
        boolean result = hoaDonService.thanhToanHoaDon(id, phuongThucThanhToan);
        
        if (result) {
            return "redirect:/nhan-vien/hoa-don?success=thanhtoan";
        } else {
            return "redirect:/nhan-vien/hoa-don?error=thanhtoan";
        }
    }
    
    @GetMapping("/bac-si")
    public String quanLyBacSi(Model model) {
        List<NguoiDung> bacSis = nguoiDungService.findByVaiTro(VaiTro.DOCTOR);
        model.addAttribute("bacSis", bacSis);
        
        return "nhan-vien/bac-si";
    }
    
    @GetMapping("/dich-vu")
    public String quanLyDichVu(Model model) {
        List<DichVu> dichVus = dichVuService.findAll();
        model.addAttribute("dichVus", dichVus);
        
        return "nhan-vien/dich-vu";
    }
}