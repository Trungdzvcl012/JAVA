package com.nhakhoa.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpMethod; 
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/chat")
public class ChatAPIController {

    @Value("${gemini.api.key}") 
    private String geminiApiKey;
    private final List<Map<String, Object>> chatHistory = new ArrayList<>();

    private static final String SYSTEM_INSTRUCTION_TEXT = 
        "Bạn là trợ lý ảo tên An của Phòng khám Nha khoa Sunshine. " +
        "Nhiệm vụ của bạn là trả lời các câu hỏi về dịch vụ (niềng răng, tẩy trắng, trồng implant...), lịch làm việc, địa chỉ và đặt lịch hẹn. " +
        "Hãy sử dụng giọng điệu chuyên nghiệp, thân thiện. **Không** đưa ra lời khuyên y tế chuyên sâu hoặc chẩn đoán thay cho bác sĩ. " +
        "Luôn khuyến khích khách hàng đặt lịch hẹn để được thăm khám trực tiếp.";
    
    private static final Map<String, Object> SYSTEM_INSTRUCTION;
    static {
        SYSTEM_INSTRUCTION = new HashMap<>();
        Map<String, Object> part = new HashMap<>();
        part.put("text", SYSTEM_INSTRUCTION_TEXT);
        SYSTEM_INSTRUCTION.put("parts", List.of(part));
    }
    
    @PostMapping("/ask")
    public ResponseEntity<Map<String, String>> askAI(@RequestBody Map<String, String> payload) {
        String userMessage = payload.getOrDefault("message", "");
        
        if (userMessage.isBlank()) {
             Map<String, String> response = new HashMap<>();
             response.put("reply", "Vui lòng nhập câu hỏi của bạn.");
             return ResponseEntity.badRequest().body(response);
        }

        String aiReply = callGeminiAI(userMessage);

        Map<String, String> response = new HashMap<>();
        response.put("reply", aiReply);
        return ResponseEntity.ok(response);
    }

    private String callGeminiAI(String userQuestion) {
        try {
            final String apiUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + geminiApiKey;

            Map<String, Object> userPart = new HashMap<>();
            userPart.put("text", userQuestion);

            Map<String, Object> userContent = new HashMap<>();
            userContent.put("role", "user");
            userContent.put("parts", new ArrayList<>(List.of(userPart)));
            chatHistory.add(userContent); 

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> generationConfig = new HashMap<>();
           
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("contents", chatHistory);
            
            requestBody.put("systemInstruction", SYSTEM_INSTRUCTION); 
            
            if (!generationConfig.isEmpty()) {
                requestBody.put("generationConfig", generationConfig); 
            }

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Map> response = restTemplate.exchange(
                apiUrl, 
                HttpMethod.POST, 
                entity, 
                Map.class
            );

     
            Map<String, Object> body = response.getBody();
            if (body != null && body.containsKey("candidates")) {
                List<Map<String, Object>> candidates = (List<Map<String, Object>>) body.get("candidates");
                if (!candidates.isEmpty()) {
                    Map<String, Object> contentRes = (Map<String, Object>) candidates.get(0).get("content");
                    List<Map<String, Object>> parts = (List<Map<String, Object>>) contentRes.get("parts");
                    String aiReply = (String) parts.get(0).get("text");

                
                    Map<String, Object> modelPart = new HashMap<>();
                    modelPart.put("text", aiReply);
                    Map<String, Object> modelContent = new HashMap<>();
                    modelContent.put("role", "model");
                    modelContent.put("parts", new ArrayList<>(List.of(modelPart)));
                    chatHistory.add(modelContent); 

                    return aiReply;
                }
            }
            return "Xin lỗi, tôi chưa rõ câu hỏi.";

        } catch (HttpClientErrorException e) {
           
            String errorBody = e.getResponseBodyAsString();
            System.err.println("Gemini API Error (Status " + e.getRawStatusCode() + "): " + errorBody);
         
            return "Lỗi API: (" + e.getRawStatusCode() + ") Vui lòng kiểm tra lại API Key. Thông báo chi tiết: " + errorBody;
        } catch (Exception e) {
            e.printStackTrace(); 
            return "Lỗi Server: Lỗi không xác định. Vui lòng kiểm tra log."; 
        }
    }
}
