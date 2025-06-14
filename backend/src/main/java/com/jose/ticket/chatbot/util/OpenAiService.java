package com.jose.ticket.chatbot.util;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OpenAiService {

    @Value("${openai.api-key}")
    private String apiKey;

    @Value("${openai.api-url}")
    private String apiUrl;

    @Value("${openai.model}")
    private String model;

    public String ask(String userPrompt) {
        // 요청 본문 구성
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("messages", List.of(
                Map.of("role", "system", "content", "당신은 공연 필터 추출기입니다. 사용자 질문을 JSON으로 분석하세요."),
                Map.of("role", "user", "content", userPrompt)
        ));
        requestBody.put("temperature", 0.2);

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        // HTTP 요청
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        RestTemplate restTemplate = new RestTemplate();

        try {
            // ✅ 로그 출력
            System.out.println("🧠 GPT 요청 프롬프트: " + userPrompt);
            System.out.println("🌐 API 요청 URL: " + apiUrl);
            System.out.println("📤 요청 바디: " + requestBody);

            ResponseEntity<Map> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, Map.class);

            // ✅ 응답 null 및 구조 확인
            if (response.getBody() == null || !response.getBody().containsKey("choices")) {
                System.out.println("❌ GPT 응답이 비정상입니다: " + response);
                return "{}";
            }

            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
            if (choices == null || choices.isEmpty()) {
                System.out.println("❌ GPT 응답 choices 비어 있음");
                return "{}";
            }

            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            if (message == null || !message.containsKey("content")) {
                System.out.println("❌ GPT message 비어 있음 또는 content 없음");
                return "{}";
            }

            String content = (String) message.get("content");
            System.out.println("✅ GPT 응답 내용:\n" + content);
            return content;

        } catch (Exception e) {
            System.out.println("❌ GPT API 호출 실패: " + e.getMessage());
            return "{}";
        }
    }
}
