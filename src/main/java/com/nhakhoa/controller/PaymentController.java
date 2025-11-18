package com.nhakhoa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.nhakhoa.model.HoaDon;
import com.nhakhoa.model.TrangThaiHoaDon;
import com.nhakhoa.service.HoaDonService;
import com.nhakhoa.service.MomoService;
import com.nhakhoa.service.VnpayService;
import com.nhakhoa.service.ZalopayService;

@Controller
@RequestMapping("/thanh-toan")
public class PaymentController {

    @Autowired
    private HoaDonService hoaDonService;

    @Autowired
    private MomoService momoService;

    @Autowired
    private VnpayService vnpayService;

    @Autowired
    private ZalopayService zalopayService;

    @GetMapping("/{id}")
    public String hienThiTrangThanhToan(@PathVariable Long id, Model model) {
        HoaDon hoaDon = hoaDonService.findById(id);
        if (hoaDon == null) {
            model.addAttribute("error", "Không tìm thấy hóa đơn!");
            return "error";
        }
        model.addAttribute("hoaDon", hoaDon);
        return "thanh-toan";
    }

    @PostMapping("/thuc-hien")
    public String thucHienThanhToan(@RequestParam("hoaDonId") Long hoaDonId,
                                    @RequestParam("phuongThuc") String phuongThuc,
                                    Model model) {
        HoaDon hoaDon = hoaDonService.findById(hoaDonId);
        if (hoaDon == null) {
            model.addAttribute("error", "Không tìm thấy hóa đơn!");
            return "error";
        }

        hoaDon.setPhuongThucThanhToan(phuongThuc);
        hoaDonService.save(hoaDon);

        // Truyền URL thanh toán tương ứng vào model
        switch (phuongThuc.toLowerCase()) {
            case "momo":
                model.addAttribute("momoUrl", momoService.createPaymentUrl(hoaDon));
                break;
            case "vnpay":
                model.addAttribute("vnpUrl", vnpayService.createPaymentUrl(hoaDon));
                break;
            case "zalopay":
                model.addAttribute("zalopayUrl", zalopayService.createPaymentUrl(hoaDon));
                break;
            default:
                model.addAttribute("success", "Thanh toán thành công bằng phương thức: " + phuongThuc.toUpperCase());
                break;
        }

        model.addAttribute("hoaDon", hoaDon);
        return "ket-qua-thanh-toan";
    }

    // Callback Momo
    @GetMapping("/momo/return")
    public String momoReturn(@RequestParam("orderId") Long hoaDonId,
                             @RequestParam("resultCode") int resultCode,
                             Model model) {
        HoaDon hoaDon = hoaDonService.findById(hoaDonId);
        if (hoaDon == null) { 
            model.addAttribute("error", "Không tìm thấy hóa đơn!"); 
            return "error"; 
        }
        if (resultCode == 0) {
            hoaDon.setTrangThai(TrangThaiHoaDon.DA_THANH_TOAN);
            hoaDonService.save(hoaDon);
            model.addAttribute("success", "Thanh toán Momo thành công!");
        } else {
            model.addAttribute("error", "Thanh toán Momo thất bại. Mã lỗi: " + resultCode);
        }
        model.addAttribute("hoaDon", hoaDon);
        return "ket-qua-thanh-toan";
    }

    // Callback VNPay
    @GetMapping("/vnpay/return")
    public String vnpayReturn(@RequestParam("vnp_ResponseCode") String responseCode,
                              @RequestParam("vnp_TxnRef") Long hoaDonId,
                              Model model) {
        HoaDon hoaDon = hoaDonService.findById(hoaDonId);
        if (hoaDon == null) { 
            model.addAttribute("error", "Không tìm thấy hóa đơn!"); 
            return "error"; 
        }
        if ("00".equals(responseCode)) {
            hoaDon.setTrangThai(TrangThaiHoaDon.DA_THANH_TOAN);
            hoaDonService.save(hoaDon);
            model.addAttribute("success", "Thanh toán VNPay thành công!");
        } else {
            model.addAttribute("error", "Thanh toán VNPay thất bại. Mã lỗi: " + responseCode);
        }
        model.addAttribute("hoaDon", hoaDon);
        return "ket-qua-thanh-toan";
    }

    // Callback ZaloPay
    @GetMapping("/zalopay/return")
    public String zalopayReturn(@RequestParam("transactionId") String txnId,
                                @RequestParam("status") int status,
                                @RequestParam("orderId") Long hoaDonId,
                                Model model) {
        HoaDon hoaDon = hoaDonService.findById(hoaDonId);
        if (hoaDon == null) { 
            model.addAttribute("error", "Không tìm thấy hóa đơn!"); 
            return "error"; 
        }
        if (status == 1) {
            hoaDon.setTrangThai(TrangThaiHoaDon.DA_THANH_TOAN);
            hoaDonService.save(hoaDon);
            model.addAttribute("success", "Thanh toán ZaloPay thành công!");
        } else {
            model.addAttribute("error", "Thanh toán ZaloPay thất bại. Mã trạng thái: " + status);
        }
        model.addAttribute("hoaDon", hoaDon);
        return "ket-qua-thanh-toan";
    }
}
