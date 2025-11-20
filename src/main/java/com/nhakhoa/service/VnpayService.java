package com.nhakhoa.service;

import com.nhakhoa.model.HoaDon;
import com.nhakhoa.util.HmacSHA256;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
public class VnpayService {

    private final String vnp_TmnCode = "YOUR_TMN_CODE";
    private final String vnp_HashSecret = "YOUR_HASH_SECRET";
    private final String vnp_Url = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
    private final String returnUrl = "http://localhost:8080/thanh-toan/vnpay/return";

    public String createPaymentUrl(HoaDon hoaDon) {
        try {
            Map<String, String> vnp_Params = new TreeMap<>();
            vnp_Params.put("vnp_Version", "2.1.0");
            vnp_Params.put("vnp_Command", "pay");
            vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
            vnp_Params.put("vnp_Amount", hoaDon.getTongTien().multiply(java.math.BigDecimal.valueOf(100)).toBigInteger().toString());
            vnp_Params.put("vnp_CurrCode", "VND");
            vnp_Params.put("vnp_TxnRef", hoaDon.getId().toString());
            vnp_Params.put("vnp_OrderInfo", "Thanh toán hóa đơn #" + hoaDon.getId());
            vnp_Params.put("vnp_ReturnUrl", returnUrl);
            vnp_Params.put("vnp_CreateDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));

            String hashData = vnp_Params.entrySet().stream()
                    .map(e -> e.getKey() + "=" + e.getValue())
                    .collect(Collectors.joining("&"));

            String vnp_SecureHash = HmacSHA256.hash(vnp_HashSecret, hashData);
            vnp_Params.put("vnp_SecureHash", vnp_SecureHash);

            return vnp_Url + "?" + vnp_Params.entrySet().stream()
                    .map(e -> e.getKey() + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                    .collect(Collectors.joining("&"));
        } catch (Exception e) {
            throw new RuntimeException("Lỗi tạo URL VNPAY", e);
        }
    }
}
