package com.jose.ticket.chatbot.util;

import com.jose.ticket.global.security.AwsSecretsManager;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OpenAiService {

    private final AwsSecretsManager secretsManager;

    private String apiKey;
    private String apiUrl;
    private String model;

    @PostConstruct
    public void init() {
        Map<String, String> secrets = secretsManager.getSecretMap("ticketplanet/credentials");
        this.apiKey = secrets.get("OPENAI_API_KEY");

        // 아래 두 값은 application.yml 그대로 사용해도 문제없음
        this.apiUrl = "https://api.openai.com/v1/chat/completions";
        this.model = "gpt-3.5-turbo";

        System.out.println("✅ [OpenAiService] GPT 키 로드 완료");
    }

    public String ask(String userPrompt) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("messages", List.of(
                Map.of("role", "system", "content", "당신은 공연 필터 추출기입니다. 사용자 질문을 JSON으로 분석하세요."),
                Map.of("role", "user", "content", userPrompt)
        ));
        requestBody.put("temperature", 0.2);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        RestTemplate restTemplate = new RestTemplate();

        try {
            System.out.println("🧠 GPT 요청 프롬프트: " + userPrompt);
            System.out.println("🌐 API 요청 URL: " + apiUrl);

            ResponseEntity<Map> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, Map.class);

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

        } catch (HttpClientErrorException e) {
            System.out.println("❌ GPT API 호출 실패 (상태): " + e.getStatusCode());
            System.out.println("📩 OpenAI 응답 본문: " + e.getResponseBodyAsString());
            return "{}";
        } catch (Exception e) {
            System.out.println("❌ GPT 호출 일반 예외: " + e.getMessage());
            e.printStackTrace();
            return "{}";
        }
    }
}
