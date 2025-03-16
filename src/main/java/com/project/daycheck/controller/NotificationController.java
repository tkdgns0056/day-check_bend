package com.project.daycheck.controller;

import com.project.daycheck.dto.NotificationDTO;
import com.project.daycheck.service.NotificationService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * 읽지 않은 알림 목록을 조회합니다.
     */
    @GetMapping("/unread")
    public ResponseEntity<List<NotificationDTO>> getUnreadNotifications() {
        List<NotificationDTO> notifications = notificationService.getUnreadNotifications();
        log.info("읽지 않은 알림 조회: {} 개", notifications.size());
        return ResponseEntity.ok(notifications);
    }

    /**
     * 모든 알림 목록을 조회합니다.
     */
    @GetMapping
    public ResponseEntity<List<NotificationDTO>> getAllNotifications() {
        List<NotificationDTO> notifications = notificationService.getAllNotifications();
        log.info("모든 알림 조회: {} 개", notifications.size());
        return ResponseEntity.ok(notifications);
    }

    /**
     * 특정 알림을 읽음 상태로 변경합니다.
     */
    @PatchMapping("/{id}/read")
    public ResponseEntity<NotificationDTO> markAsRead(@PathVariable Long id) {
        NotificationDTO notification = notificationService.markAsRead(id);
        log.info("알림 읽음 처리: {}", id);
        return ResponseEntity.ok(notification);
    }

    /**
     * 모든 읽지 않은 알림을 읽음 상태로 변경합니다.
     */
    @PatchMapping("/read-all")
    public ResponseEntity<Map<String, String>> markAllAsRead() {
        notificationService.markAllAsRead();

        Map<String, String> response = new HashMap<>();
        response.put("message", "모든 알림을 읽음 처리했습니다.");
        log.info("모든 알림 읽음 처리 완료");

        return ResponseEntity.ok(response);
    }

    /**
     * 알림 상태 정보를 조회합니다.
     * 프론트엔드에서 폴링 방식으로 새 알림을 확인하기 위한 용도입니다.
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getNotificationStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("active", true);
        status.put("lastCheck", LocalDateTime.now().toString());

        // 읽지 않은 알림 개수
        int unreadCount = notificationService.getUnreadCount();
        status.put("unreadCount", unreadCount);

        return ResponseEntity.ok(status);
    }

}
