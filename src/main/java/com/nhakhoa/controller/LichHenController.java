package com.nhakhoa.controller;

import java.math.BigDecimal;
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
@RequestMapping("/lich-hen")
public class LichHenController {

    @Autowired
    private LichHenService lichHenService;

    @Autowired
    private NguoiDungService nguoiDungService;

    @Autowired
    private DichVuService dichVuService;

    @Autowired
    private HoaDonService hoaDonService;

    // Trang danh sách lịch hẹn của người dùng
    @GetMapping("/cua-toi")
    public String lichHenCuaToi(Model model, Principal principal) {
        if (principal == null) return "redirect:/dang-nhap";

        Optional<NguoiDung> nguoiDungOpt = nguoiDungService.findByEmail(principal.getName());
        if (nguoiDungOpt.isEmpty()) {
            model.addAttribute("error", "Không tìm thấy thông tin người dùng!");
            return "lich-hen-cua-toi";
        }

        NguoiDung nguoiDung = nguoiDungOpt.get();
        List<LichHen> lichHens = lichHenService.findByNguoiDung(nguoiDung);

        long tongLichHen = lichHens.size();
        long choXacNhan = lichHens.stream()
                .filter(lh -> lh.getTrangThai() == TrangThaiLichHen.CHO_XAC_NHAN).count();
        long daXacNhan = lichHens.stream()
                .filter(lh -> lh.getTrangThai() == TrangThaiLichHen.DA_XAC_NHAN).count();
        long daHoanThanh = lichHens.stream()
                .filter(lh -> lh.getTrangThai() == TrangThaiLichHen.DA_HOAN_THANH).count();

        model.addAttribute("lichHens", lichHens);
        model.addAttribute("nguoiDung", nguoiDung);
        model.addAttribute("tongLichHen", tongLichHen);
        model.addAttribute("lichHenChoXacNhan", choXacNhan);
        model.addAttribute("lichHenDaXacNhan", daXacNhan);
        model.addAttribute("lichHenHoanThanh", daHoanThanh);

        return "lich-hen-cua-toi";
    }

    // Trang đặt lịch
    @GetMapping("/dat-lich")
    public String hienThiTrangDatLich(Model model, Principal principal) {
        if (principal == null) return "redirect:/dang-nhap";

        Optional<NguoiDung> nguoiDungOpt = nguoiDungService.findByEmail(principal.getName());
        if (nguoiDungOpt.isEmpty()) {
            model.addAttribute("error", "Không tìm thấy thông tin người dùng!");
            return "dat-lich";
        }

        model.addAttribute("dichVus", dichVuService.findAll());
        model.addAttribute("bacSis", nguoiDungService.findByVaiTro(VaiTro.DOCTOR));
        model.addAttribute("nguoiDung", nguoiDungOpt.get());
        model.addAttribute("lichHen", new LichHen());

        return "dat-lich";
    }

    // Xử lý đặt lịch
    @PostMapping("/dat-lich")
    public String datLich(@ModelAttribute LichHen lichHen,
                          Principal principal,
                          Model model) {

        if (principal == null) return "redirect:/dang-nhap";

        Optional<NguoiDung> nguoiDungOpt = nguoiDungService.findByEmail(principal.getName());
        if (nguoiDungOpt.isEmpty()) {
            model.addAttribute("error", "Không tìm thấy thông tin người dùng!");
            return reloadDatLichForm(model);
        }

        NguoiDung nguoiDung = nguoiDungOpt.get();

        if (lichHen.getDichVu() == null || lichHen.getBacSi() == null) {
            model.addAttribute("error", "Vui lòng chọn dịch vụ và bác sĩ!");
            return reloadDatLichForm(model);
        }

        lichHen.setNguoiDung(nguoiDung);
        lichHen.setTrangThai(TrangThaiLichHen.CHO_XAC_NHAN);

        try {
            LichHen savedLichHen = lichHenService.save(lichHen);

            // Tạo hóa đơn
            BigDecimal gia = savedLichHen.getDichVu().isDangKhuyenMai()
                    ? savedLichHen.getDichVu().getGiaKhuyenMai()
                    : savedLichHen.getDichVu().getGiaDichVu();

            HoaDon hoaDon = new HoaDon();
            hoaDon.setLichHen(savedLichHen);
            hoaDon.setTongTien(gia);
            hoaDon.setTrangThai(TrangThaiHoaDon.CHUA_THANH_TOAN);

            HoaDon savedHoaDon = hoaDonService.save(hoaDon);

            // Redirect sang trang thanh toán
            return "redirect:/thanh-toan/" + savedHoaDon.getId();
        } catch (Exception e) {
            model.addAttribute("error", "Có lỗi xảy ra khi đặt lịch: " + e.getMessage());
            return reloadDatLichForm(model);
        }
    }

    // Hủy lịch hẹn
    @PostMapping("/huy/{id}")
    public String huyLichHen(@PathVariable Long id, Principal principal) {
        if (principal == null) return "redirect:/dang-nhap";

        boolean result = lichHenService.huyLichHen(id, principal.getName());
        return result ? "redirect:/lich-hen/cua-toi?success=huy" : "redirect:/lich-hen/cua-toi?error=huy";
    }

    // Xem chi tiết lịch hẹn
    @GetMapping("/{id}")
    public String chiTietLichHen(@PathVariable Long id, Model model, Principal principal) {
        if (principal == null) return "redirect:/dang-nhap";

        Optional<LichHen> lichHenOpt = lichHenService.findById(id);
        if (lichHenOpt.isEmpty()) return "redirect:/lich-hen/cua-toi?error=notfound";

        LichHen lichHen = lichHenOpt.get();
        if (!lichHen.getNguoiDung().getEmail().equals(principal.getName()))
            return "redirect:/lich-hen/cua-toi?error=permission";

        model.addAttribute("lichHen", lichHen);
        return "chi-tiet-lich-hen";
    }

    // Helper reload form khi có lỗi
    private String reloadDatLichForm(Model model) {
        model.addAttribute("dichVus", dichVuService.findAll());
        model.addAttribute("bacSis", nguoiDungService.findByVaiTro(VaiTro.DOCTOR));
        model.addAttribute("lichHen", new LichHen());
        return "dat-lich";
    }
}
