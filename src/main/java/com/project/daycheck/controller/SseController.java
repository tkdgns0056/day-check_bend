package com.project.daycheck.controller;

import com.project.daycheck.service.SseEmitterService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/sse")
@RequiredArgsConstructor
public class SseController {

    private final SseEmitterService sseEmitterService;

    // SSE 연결 엔드포인트
    @GetMapping(value = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect(HttpServletRequest request) {
        // 세션 ID 또는 클라이언트 IP 주소를 클라이언트 ID로 사용
        // 로그인 기능이 있다면 사용자 ID를 사용하는 것이 바람직.
        String clientId = request.getSession().getId();

        log.info("SSE 연결 요청: {}", clientId);
        return sseEmitterService.createEmitter(clientId);
    }

    // 연결 상태 확인 엔드포인트
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> status(){
        Map<String, Object> status = new HashMap<>();
        status.put("activeConnections", sseEmitterService.getActiveEmitterCount());
        status.put("status", "running");

        return ResponseEntity.ok(status);
    }

    // 테스트용 알림 전송 엔드포인트
    @PostMapping("/test")
    public ResponseEntity<Map<String, String>> test(@RequestParam(required = false) String clientId){
        Map<String, String> response = new HashMap<>();

        // 테스트 알림 생성 로직은 실제 구현 시 필요

        response.put("message", "테스트 알림이 전송");
        return ResponseEntity.ok(response);
    }
}
