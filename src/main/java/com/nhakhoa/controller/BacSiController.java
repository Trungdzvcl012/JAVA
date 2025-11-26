package com.nhakhoa.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    
    @GetMapping("/lich-hen")
    public String danhSachLichHen(Model model, 
                                 @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        Optional<NguoiDung> bacSiOpt = nguoiDungService.findByEmail(email);
        
        if (bacSiOpt.isEmpty()) {
            model.addAttribute("error", "Không tìm thấy thông tin bác sĩ!");
            return "bac-si/lich-hen";
        }
        
        NguoiDung bacSi = bacSiOpt.get();
        List<LichHen> lichHens = lichHenService.findByBacSi(bacSi);
        
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
        model.addAttribute("bacSi", bacSi);
        model.addAttribute("tongSoLichHen", tongSoLichHen);
        model.addAttribute("lichHenChoXacNhan", lichHenChoXacNhan);
        model.addAttribute("lichHenDaXacNhan", lichHenDaXacNhan);
        model.addAttribute("lichHenHomNay", lichHenHomNay);
        
        return "bac-si/lich-hen";
    }
    
    @GetMapping("/lich-hen/{id}")
    public String chiTietLichHen(@PathVariable Long id, Model model,
                                @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        Optional<LichHen> lichHenOpt = lichHenService.findById(id);
        
        if (lichHenOpt.isEmpty()) {
            return "redirect:/bac-si/lich-hen?error=notfound";
        }
        
        LichHen lichHen = lichHenOpt.get();
        
        // Kiểm tra quyền truy cập - chỉ bác sĩ được phân công mới xem được
        if (!lichHen.getBacSi().getEmail().equals(email)) {
            return "redirect:/bac-si/lich-hen?error=permission";
        }
        
        model.addAttribute("lichHen", lichHen);
        return "bac-si/chi-tiet-lich-hen";
    }
    
    @PostMapping("/lich-hen/{id}/xac-nhan")
    public String xacNhanLichHen(@PathVariable Long id,
                                @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        boolean result = lichHenService.xacNhanLichHen(id, email);
        
        if (result) {
            return "redirect:/bac-si/lich-hen?success=xacnhan";
        } else {
            return "redirect:/bac-si/lich-hen?error=xacnhan";
        }
    }
    
    @GetMapping("/benh-an")
    public String danhSachBenhAn(Model model,
                                @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        Optional<NguoiDung> bacSiOpt = nguoiDungService.findByEmail(email);
        
        if (bacSiOpt.isEmpty()) {
            model.addAttribute("error", "Không tìm thấy thông tin bác sĩ!");
            return "bac-si/benh-an";
        }
        
        NguoiDung bacSi = bacSiOpt.get();
        List<BenhAn> benhAns = benhAnService.findByBacSi(bacSi);
        
        model.addAttribute("benhAns", benhAns);
        model.addAttribute("bacSi", bacSi);
        
        return "bac-si/benh-an";
    }
    
    @GetMapping("/benh-an/tao-moi")
    public String hienThiTaoBenhAn(@RequestParam(required = false) Long lichHenId,
                                  Model model,
                                  @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        
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
                
                benhAn.setLichHen(lichHen);
                benhAn.setBacSi(lichHen.getBacSi());
            }
        }
        
        model.addAttribute("benhAn", benhAn);
        return "bac-si/tao-benh-an";
    }
    
    @PostMapping("/benh-an/tao-moi")
    public String taoBenhAn(@RequestParam Long lichHenId,
                           BenhAn benhAn,
                           @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        
        Optional<LichHen> lichHenOpt = lichHenService.findById(lichHenId);
        if (lichHenOpt.isEmpty()) {
            return "redirect:/bac-si/benh-an?error=lichhennotfound";
        }
        
        LichHen lichHen = lichHenOpt.get();
        
        // Kiểm tra quyền truy cập
        if (!lichHen.getBacSi().getEmail().equals(email)) {
            return "redirect:/bac-si/benh-an?error=permission";
        }
        
        benhAn.setLichHen(lichHen);
        benhAn.setBacSi(lichHen.getBacSi());
        
        benhAnService.save(benhAn);
        
        // Cập nhật trạng thái lịch hẹn thành đã hoàn thành
        lichHenService.capNhatTrangThai(lichHenId, TrangThaiLichHen.DA_HOAN_THANH);
        
        return "redirect:/bac-si/benh-an?success=create";
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
        
        model.addAttribute("benhAn", benhAn);
        return "bac-si/chinh-sua-benh-an";
    }
    
    @PostMapping("/benh-an/{id}/chinh-sua")
    public String chinhSuaBenhAn(@PathVariable Long id,
                                BenhAn benhAnCapNhat,
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
        
        // Cập nhật thông tin
        benhAn.setChanDoan(benhAnCapNhat.getChanDoan());
        benhAn.setDonThuoc(benhAnCapNhat.getDonThuoc());
        benhAn.setPhacDoDieuTri(benhAnCapNhat.getPhacDoDieuTri());
        benhAn.setGhiChu(benhAnCapNhat.getGhiChu());
        benhAn.setHinhAnhXQuang(benhAnCapNhat.getHinhAnhXQuang());
        benhAn.setHinhAnhKhac(benhAnCapNhat.getHinhAnhKhac());
        
        benhAnService.save(benhAn);
        
        return "redirect:/bac-si/benh-an/" + id + "?success=update";
    }
    
    @GetMapping("/lich-lam-viec")
    public String lichLamViec(Model model,
                             @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        Optional<NguoiDung> bacSiOpt = nguoiDungService.findByEmail(email);
        
        if (bacSiOpt.isEmpty()) {
            model.addAttribute("error", "Không tìm thấy thông tin bác sĩ!");
            return "bac-si/lich-lam-viec";
        }
        
        NguoiDung bacSi = bacSiOpt.get();
        model.addAttribute("bacSi", bacSi);
        
        return "bac-si/lich-lam-viec";
    }
}