package com.example.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class LambdaHandler implements RequestHandler<Object, String> {

    @Override
    public String handleRequest(Object input, Context context) {
        context.getLogger().log("🚀 Lambda 시작됨\n");

        try (Connection conn = DBUtil.getConnection()) {
            context.getLogger().log("✅ DB 연결 성공\n");

            // 시크릿 이름 인자 없이 호출
            Map<String, String> secret = SecretUtil.getSecret();
            String mailUsername = secret.get("MAIL_USERNAME");
            String mailPassword = secret.get("MAIL_PASSWORD");

            String sql = """
                SELECT s.alert_id, u.user_email, t.ticket_title, t.ticket_event_start_datetime, s.alert_minutes
                FROM user_alert_setting s
                JOIN user u ON s.user_id = u.user_id
                JOIN ticket t ON s.ticket_id = t.ticket_id
                WHERE s.email_enabled = 1
                  AND s.alert_sent = 0
                  AND TIMESTAMPDIFF(MINUTE, NOW(), t.ticket_event_start_datetime) = s.alert_minutes
            """;

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    Long alertId = rs.getLong("alert_id");
                    String email = rs.getString("user_email");
                    String title = rs.getString("ticket_title");
                    LocalDateTime eventDate = rs.getTimestamp("ticket_event_start_datetime").toLocalDateTime();
                    int alertMinutes = rs.getInt("alert_minutes");

                    String formattedDate = eventDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH:mm"));

                    context.getLogger().log("📤 이메일 대상: " + email + ", 공연: " + title + " (" + alertMinutes + "분 전)\n");

                    try {
                        EmailUtil.sendMail(
                                email,
                                "[티켓플래닛] 공연 알림",
                                "<h3>" + title + " 공연이 곧 시작됩니다!</h3><p>공연 시작 시간: " + formattedDate + "</p>",
                                mailUsername,
                                mailPassword
                        );

                        context.getLogger().log("✅ 이메일 전송 성공: " + email + "\n");

                        // 발송 성공 시 alert_sent = 1 로 업데이트
                        try (PreparedStatement updateStmt = conn.prepareStatement(
                                "UPDATE user_alert_setting SET alert_sent = 1 WHERE alert_id = ?")) {
                            updateStmt.setLong(1, alertId);
                            updateStmt.executeUpdate();
                        }

                    } catch (Exception e) {
                        context.getLogger().log("❌ 이메일 전송 실패 (" + email + "): " + e.getMessage() + "\n");
                    }
                }
            }

            return "이메일 알림 처리 완료";
        } catch (Exception e) {
            context.getLogger().log("❌ Lambda 처리 중 오류: " + e.getMessage() + "\n");
            return "오류 발생: " + e.getMessage();
        }
    }
}
