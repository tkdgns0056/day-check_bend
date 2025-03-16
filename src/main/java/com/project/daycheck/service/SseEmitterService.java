package com.project.daycheck.service;

import com.project.daycheck.dto.NotificationDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class SseEmitterService {

    // 클라이언트의 SseEmitter 저장할 맵임 ( 사용자 ID -> SseEmitter)
    //현재는 로그인 기능 없음. 임시로 세션 id를 키로 사용
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    // 타임아웃 시간 설정 ( 3시간 ? )
    private static final long TIMEOUT = 3 * 60 * 60 * 1000L;

    // 클라이언트 연결 시 SseEmitter 생성
    public SseEmitter createEmitter(String clientId){
        SseEmitter emitter = new SseEmitter(TIMEOUT); // 3시간 타임아웃 담아줌.

        // SseEmitter 완료되거나 타임아웃되면 맵에서 제거
        emitter.onCompletion(() -> {
            log.info("SSE 연결 완료 : {}", clientId);
            emitters.remove(clientId);
        });

        // 연결 확인용 더미 데이터
        try {
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("연결되었습니다."));
        } catch (IOException e) {
            log.error("SSE 초기 이벤트 전송 오류: {}", e.getMessage());
            emitter.complete();
            return  null;
        }

        // 맵에 저장
        emitters.put(clientId, emitter);
        log.info("SSE 연결 생성: {}, 현재 연결 수: {}", clientId, emitters.size());

        return emitter;
    }

    // 모든 클라이언트에게 알림 전송(브로드캐스트?)
    public void sendToAll(NotificationDTO notificationDTO) {
        emitters.forEach((clientId, emitter) -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("notification")
                        .data(notificationDTO));
                log.info("브로드캐스트 알림 전송: {}, 알림 ID: {}", clientId, notificationDTO.getId());
            } catch (IOException e){
                log.error("브로드캐스트 알림 전송 오류: {}, 오류: {}", clientId, e.getMessage());
                emitters.remove(clientId);
            }
        });
    }

    // 클라이언트 연결 종료
    public void removeEmitter(String clientId) {
        SseEmitter emitter = emitters.get(clientId);
        if(emitter != null){
            emitter.complete();
            emitters.remove(clientId);
            log.info("SSE 연결 제거: {}, 현재 연결 수: {}", clientId, emitters.size());
        }
    }

    // 현재 연결된 클라이언트 조회
    public int getActiveEmitterCount() {
        return emitters.size();
    }
}
