package com.jose.ticket.chatbot.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jose.ticket.chatbot.dto.ChatbotFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class ChatbotFilterExtractor {

    private final OpenAiService openAiService;
    private final ObjectMapper objectMapper;

    @Value("${openai.prompt}")
    private String systemPrompt;

    public ChatbotFilter extract(String message) {
        try {
            // ✅ 오늘 날짜를 기준으로 명시
            String today = LocalDate.now().toString();

            String prompt = """
                오늘은 %s입니다. 날짜를 해석할 때 이 날짜를 기준으로 해 주세요.

                %s

                반드시 JSON 형식으로 아래 키를 포함해줘. 없는 값은 null로 반환해.

                🎭 categoryId 매핑:
                - 콘서트 = 1
                - 전시 = 2
                - 연극 = 3
                - 뮤지컬 = 4

                💰 priceMax는 정수 숫자로 추출 (예: "3만원 이하" → 30000)

                📅 날짜 표현 ("7월", "이번 주말")은 YYYY-MM-DD 형식의 startDate, endDate로 변환 (예: 2025-07-01 ~ 2025-07-31)

                🎟️ title, venue, ageLimit, bookingProvider도 가능하면 추출

                예시 질문: "%s"
            """.formatted(today, systemPrompt, message);

            // GPT 호출
            String response = openAiService.ask(prompt);

            // 🧠 GPT 응답 로그 출력
            System.out.println("🧠 GPT 응답 내용:\n" + response);

            // ✅ "null" 문자열을 실제 null 값으로 치환
            String sanitizedResponse = response.replace("\"null\"", "null");

            // JSON 파싱
            return objectMapper.readValue(sanitizedResponse, ChatbotFilter.class);

        } catch (Exception e) {
            System.out.println("❌ GPT 필터 파싱 실패: " + e.getMessage());
            return new ChatbotFilter(); // fallback (빈 필터)
        }
    }
}
