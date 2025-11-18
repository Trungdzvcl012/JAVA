package com.nhakhoa.service;

import org.springframework.stereotype.Service;
import com.nhakhoa.model.HoaDon;
import com.nhakhoa.util.HmacSHA256;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class VnpayService {

    private final String vnp_TmnCode = "YOUR_TMN_CODE";
    private final String vnp_HashSecret = "YOUR_HASH_SECRET";
    private final String vnp_Url = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    private final String returnUrl = "http://localhost:8080/thanh-toan/vnpay/return";

    public String createPaymentUrl(HoaDon hoaDon) {
        try {
            String amount = hoaDon.getTongTien().multiply(new java.math.BigDecimal(100)).toString();
            String orderId = hoaDon.getId().toString();
            String orderInfo = "Thanh toán hóa đơn #" + orderId;

            Map<String, String> vnp_Params = new HashMap<>();
            vnp_Params.put("vnp_Version", "2.1.0");
            vnp_Params.put("vnp_Command", "pay");
            vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
            vnp_Params.put("vnp_Amount", amount);
            vnp_Params.put("vnp_CreateDate", java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
            vnp_Params.put("vnp_CurrCode", "VND");
            vnp_Params.put("vnp_TxnRef", orderId);
            vnp_Params.put("vnp_OrderInfo", orderInfo);
            vnp_Params.put("vnp_ReturnUrl", returnUrl);

            // Tạo chữ ký
            String rawData = vnp_Params.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .map(e -> e.getKey() + "=" + e.getValue())
                    .collect(Collectors.joining("&"));

            String vnp_SecureHash = HmacSHA256.hash(vnp_HashSecret, rawData);
            vnp_Params.put("vnp_SecureHash", vnp_SecureHash);

            String url = vnp_Url + "?" + vnp_Params.entrySet().stream()
                    .map(e -> e.getKey() + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                    .collect(Collectors.joining("&"));

            return url;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi tạo URL thanh toán VNPay", e);
        }
    }
}
