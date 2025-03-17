package com.project.daycheck.controller;

import com.project.daycheck.config.component.SseEmitters;
import com.project.daycheck.dto.NotificationDTO;
import com.project.daycheck.repository.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;
    private final SseEmitters sseEmitters;

    // SSE 연결 엔드포인트
    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@RequestParam String clientId){
        // 기본 타임아웃을 30분으로 설정
        SseEmitter emitter = new SseEmitter(1800000L);
        return sseEmitters.add(clientId, emitter);
    }

    /**
     * 알림 목록 조회
     */
    @GetMapping
    public ResponseEntity<List<NotificationDTO.Response>> getNotifications(@RequestParam String clientId){
        return ResponseEntity.ok(notificationService.getNotifications(clientId));
    }


    /**
     * 알림 읽음 처리
     */
    @PatchMapping("/{id}/read")
    public ResponseEntity<NotificationDTO.Response> markAsRead(@PathVariable Long id, @RequestParam String clientId) {
        return notificationService.markAsRead(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 테스트용 알림 생성 엔드포인트
    @PostMapping("/test")
    public ResponseEntity<NotificationDTO.Response> createTestNotification(@RequestBody NotificationDTO.CreateRequest request){

        NotificationDTO.Response response = notificationService.createNotification(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 특정 스케줄의 알림 목록 조회
     */
    @GetMapping("/schedule/{scheduleId")
    public ResponseEntity<List<NotificationDTO.Response>> getNotificationsByScheduleId(@PathVariable Long scheduleId) {
        return ResponseEntity.ok(notificationService.getNotificationsByScheduleId(scheduleId));
    }
}
