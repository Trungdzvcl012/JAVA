package com.nhakhoa.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.nhakhoa.model.HoaDon;
import com.nhakhoa.util.HmacSHA256;

@Service
public class ZalopayService {

    private final String appId = "YOUR_APP_ID";
    private final String key1 = "YOUR_KEY1";
    private final String endpoint = "https://sandbox.zalopay.com.vn/v001/tpe/createorder";
    private final String returnUrl = "http://localhost:8080/thanh-toan/zalopay/return";

    public String createPaymentUrl(HoaDon hoaDon) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("app_id", appId);
            params.put("app_trans_id", hoaDon.getId().toString());
            params.put("app_time", String.valueOf(System.currentTimeMillis()));
            params.put("amount", hoaDon.getTongTien().toBigInteger().toString());
            params.put("description", "Thanh toán hóa đơn #" + hoaDon.getId());
            params.put("return_url", returnUrl);

            String rawData = params.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .map(e -> e.getKey() + "=" + e.getValue())
                    .collect(Collectors.joining("&"));

            String signature = HmacSHA256.hash(key1, rawData);
            params.put("mac", signature);

            return endpoint + "?" + params.entrySet().stream()
                    .map(e -> e.getKey() + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                    .collect(Collectors.joining("&"));
        } catch (Exception e) {
            throw new RuntimeException("Lỗi tạo URL Zalopay", e);
        }
    }
}
