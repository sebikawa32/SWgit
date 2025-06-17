package com.jose.ticket.domain.notification.controller;

import com.jose.ticket.domain.notification.dto.AlertRequest;
import com.jose.ticket.domain.notification.entity.UserAlertSetting;
import com.jose.ticket.domain.notification.service.UserAlertSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class UserAlertSettingController {

    private final UserAlertSettingService alertService;

    /** 1) 새 알림 설정
     *   POST /api/alerts?userId=2
     *   Body: { "ticketId":…, "alertMinutes":…, "emailEnabled":… }
     */
    @PostMapping
    public ResponseEntity<String> createAlert(
            @RequestParam Long userId,
            @RequestBody AlertRequest req
    ) {
        alertService.saveAlertSetting(
                userId,
                req.getTicketId(),
                req.getAlertMinutes(),
                req.isEmailEnabled()
        );
        return ResponseEntity.ok("알림 설정 완료!");
    }

    /** 2) 특정 티켓 조회
     *   GET /api/alerts/check?userId=2&ticketId=42
     */
    @GetMapping("/check")
    public ResponseEntity<?> checkAlertSetting(
            @RequestParam Long userId,
            @RequestParam Long ticketId
    ) {
        return alertService.getAlertSetting(userId, ticketId)
                .<ResponseEntity<?>>map(s -> ResponseEntity.ok(Map.of(
                        "alertId",      s.getAlertId(),
                        "ticketId",     s.getTicketId(),
                        "alertMinutes", s.getAlertMinutes(),
                        "emailEnabled", s.isEmailEnabled()
                )))
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    /** 3) 전체 목록 조회
     *   GET /api/alerts?userId=2
     */
    @GetMapping
    public ResponseEntity<List<UserAlertSetting>> listAlerts(
            @RequestParam Long userId
    ) {
        return ResponseEntity.ok(alertService.getAllByUserId(userId));
    }

    /** 4) 수정
     *   PUT /api/alerts/{alertId}?userId=2
     *   Body: { "ticketId":…, "alertMinutes":…, "emailEnabled":… }
     */
    @PutMapping("/{alertId}")
    public ResponseEntity<String> updateAlert(
            @PathVariable Long alertId,
            @RequestParam Long userId,
            @RequestBody AlertRequest req
    ) {
        alertService.updateAlertSetting(
                userId,
                alertId,
                req.getTicketId(),
                req.getAlertMinutes(),
                req.isEmailEnabled()
        );
        return ResponseEntity.ok("알림 수정 완료!");
    }

    /** 5) 삭제
     *   DELETE /api/alerts/{alertId}?userId=2
     */
    @DeleteMapping("/{alertId}")
    public ResponseEntity<String> deleteAlert(
            @PathVariable Long alertId,
            @RequestParam Long userId
    ) {
        alertService.deleteAlertSetting(userId, alertId);
        return ResponseEntity.ok("알림 삭제 완료!");
    }
}
