package com.nhakhoa.controller;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import com.nhakhoa.model.HoaDon;
import com.nhakhoa.model.LichHen;
import com.nhakhoa.model.TrangThaiHoaDon;
import com.nhakhoa.service.HoaDonService;
import com.nhakhoa.service.LichHenService;

@Controller
@RequestMapping("/thanh-toan")
public class PaymentController {

    @Autowired
    private HoaDonService hoaDonService;

    @Autowired
    private LichHenService lichHenService;

    // Hiển thị trang thanh toán (QR hoặc redirect)
    @GetMapping("/{hoaDonId}")
    public String hienThiThanhToan(@PathVariable Long hoaDonId, Model model, Principal principal) {
        HoaDon hoaDon = hoaDonService.findById(hoaDonId);
        if (hoaDon == null) {
            model.addAttribute("error", "Không tìm thấy hóa đơn!");
            return "error";
        }

        // Lấy thông tin giá
        LichHen lichHen = hoaDon.getLichHen();
        BigDecimal tongTien = hoaDon.getTongTien();

        // Tạo URL thanh toán cho Momo
        String momoUrl = createMomoPaymentUrl(hoaDon);
        // Tạo URL thanh toán cho VNPay
        String vnpayUrl = createVnpayPaymentUrl(hoaDon);
        // Tạo URL thanh toán cho ZaloPay
        String zalopayUrl = createZalopayPaymentUrl(hoaDon);

        model.addAttribute("hoaDon", hoaDon);
        model.addAttribute("momoUrl", momoUrl);
        model.addAttribute("vnpayUrl", vnpayUrl);
        model.addAttribute("zalopayUrl", zalopayUrl);
        model.addAttribute("tongTien", tongTien);

        return "thanh-toan-qr"; // trang HTML có thể hiển thị QR hoặc redirect
    }

    // --- Các phương thức tạo URL thanh toán ---
    private String createMomoPaymentUrl(HoaDon hoaDon) {
        try {
            String endpoint = "https://test-payment.momo.vn/v2/gateway/api/create";
            Map<String, Object> params = new HashMap<>();
            params.put("partnerCode", "YOUR_PARTNER_CODE");
            params.put("accessKey", "YOUR_ACCESS_KEY");
            params.put("amount", hoaDon.getTongTien().toString());
            params.put("orderId", hoaDon.getId().toString());
            params.put("orderInfo", "Thanh toán dịch vụ nha khoa");
            params.put("returnUrl", "http://localhost:8080/thanh-toan/momo/return");
            params.put("notifyUrl", "http://localhost:8080/webhook/momo");

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String,Object>> request = new HttpEntity<>(params, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(endpoint, request, Map.class);
            return response.getBody().get("payUrl").toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String createVnpayPaymentUrl(HoaDon hoaDon) {
        // Đây chỉ là ví dụ tạo URL giả lập, bạn cần làm theo doc VNPay sandbox
        return "https://sandbox.vnpayment.vn/payment?amount=" + hoaDon.getTongTien() + "&orderId=" + hoaDon.getId();
    }

    private String createZalopayPaymentUrl(HoaDon hoaDon) {
        // Ví dụ ZaloPay sandbox
        return "https://sandbox.zalopay.vn/pay?amount=" + hoaDon.getTongTien() + "&orderId=" + hoaDon.getId();
    }

    // --- Callback các cổng thanh toán ---
    // Momo
    @GetMapping("/momo/return")
    public String momoReturn(Long orderId, int resultCode, Model model) {
        HoaDon hoaDon = hoaDonService.findById(orderId);
        if (hoaDon == null) return "error";
        if (resultCode == 0) hoaDon.setTrangThai(TrangThaiHoaDon.DA_THANH_TOAN);
        hoaDonService.save(hoaDon);
        return "redirect:/lich-hen/cua-toi?success=true";
    }

    // VNPay
    @GetMapping("/vnpay/return")
    public String vnpayReturn(String vnp_ResponseCode, Long vnp_TxnRef) {
        HoaDon hoaDon = hoaDonService.findById(vnp_TxnRef);
        if (hoaDon == null) return "error";
        if ("00".equals(vnp_ResponseCode)) hoaDon.setTrangThai(TrangThaiHoaDon.DA_THANH_TOAN);
        hoaDonService.save(hoaDon);
        return "redirect:/lich-hen/cua-toi?success=true";
    }

    // ZaloPay
    @GetMapping("/zalopay/return")
    public String zalopayReturn(String transactionId, int status, Long orderId) {
        HoaDon hoaDon = hoaDonService.findById(orderId);
        if (hoaDon == null) return "error";
        if (status == 1) hoaDon.setTrangThai(TrangThaiHoaDon.DA_THANH_TOAN);
        hoaDonService.save(hoaDon);
        return "redirect:/lich-hen/cua-toi?success=true";
    }
}
