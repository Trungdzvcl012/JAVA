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
public class ZaloPayService {

    // TODO: Thay các giá trị bằng thông tin thật từ ZaloPay
    private final String appId = "YOUR_APP_ID";
    private final String key1 = "YOUR_KEY1";
    private final String key2 = "YOUR_KEY2";
    private final String endpoint = "https://sandbox.zalopay.com.vn/v001/tpe/createorder";
    private final String callbackUrl = "http://localhost:8080/thanh-toan/zalopay/return";

    public String createPaymentUrl(HoaDon hoaDon) {
        try {
            String amount = hoaDon.getTongTien().toString();
            String orderId = hoaDon.getId().toString();
            String orderInfo = "Thanh toán hóa đơn #" + orderId;

            Map<String, String> params = new HashMap<>();
            params.put("app_id", appId);
            params.put("amount", amount);
            params.put("app_trans_id", orderId + System.currentTimeMillis() / 1000); // unique
            params.put("app_time", String.valueOf(System.currentTimeMillis()));
            params.put("item", "[{\"name\":\"Dịch vụ\",\"quantity\":1,\"price\":" + amount + "}]");
            params.put("description", orderInfo);
            params.put("callback_url", callbackUrl);

            // Tạo mac (chữ ký) theo HMAC SHA256 với key1
            String rawData = params.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .map(e -> e.getKey() + "=" + e.getValue())
                    .collect(Collectors.joining("&"));

            String mac = HmacSHA256.hash(key1, rawData);
            params.put("mac", mac);

            // Tạo URL để quét QR (trong sandbox ZaloPay trả về orderurl)
            String url = endpoint + "?" + params.entrySet().stream()
                    .map(e -> e.getKey() + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                    .collect(Collectors.joining("&"));

            return url;

        } catch (Exception e) {
            throw new RuntimeException("Lỗi tạo URL thanh toán ZaloPay", e);
        }
    }
}
