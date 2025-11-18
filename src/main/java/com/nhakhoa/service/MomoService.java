package com.nhakhoa.service;

import org.springframework.stereotype.Service;
import com.nhakhoa.model.HoaDon;
import com.nhakhoa.util.HmacSHA256;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MomoService {

    private final String partnerCode = "YOUR_PARTNER_CODE";
    private final String accessKey = "YOUR_ACCESS_KEY";
    private final String secretKey = "YOUR_SECRET_KEY";
    private final String requestType = "captureWallet";
    private final String endpoint = "https://test-payment.momo.vn/v2/gateway/api/create";
    private final String returnUrl = "http://localhost:8080/thanh-toan/momo/return";

    public String createPaymentUrl(HoaDon hoaDon) {
        try {
            String orderId = hoaDon.getId().toString();
            String amount = hoaDon.getTongTien().toString();
            String orderInfo = "Thanh toán hóa đơn #" + orderId;

            Map<String, String> params = new HashMap<>();
            params.put("partnerCode", partnerCode);
            params.put("accessKey", accessKey);
            params.put("requestId", orderId);
            params.put("amount", amount);
            params.put("orderId", orderId);
            params.put("orderInfo", orderInfo);
            params.put("returnUrl", returnUrl);
            params.put("notifyUrl", returnUrl);
            params.put("requestType", requestType);

            // Tạo rawSignature
            String rawData = params.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .map(e -> e.getKey() + "=" + e.getValue())
                    .collect(Collectors.joining("&"));

            String signature = HmacSHA256.hash(secretKey, rawData);
            params.put("signature", signature);

            // Tạo URL để quét QR
            String url = endpoint + "?" + params.entrySet().stream()
                    .map(e -> e.getKey() + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                    .collect(Collectors.joining("&"));

            return url;

        } catch (Exception e) {
            throw new RuntimeException("Lỗi tạo URL thanh toán Momo", e);
        }
    }
}
