package com.project.daycheck.config.component;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class SseEmitters {
    //클라이언트 ID별 SSE 연결 저장 맵
    private final Map<String, List<SseEmitter>> emittersMap = new ConcurrentHashMap<>();

    // 클라이언트별 SSE 연결 추가
    public SseEmitter add(String clientId, SseEmitter emitter) {
        // 해당 클라이언트의 이미터 목록 가져오거나 새로 생성
        emittersMap.computeIfAbsent(clientId, k -> new CopyOnWriteArrayList<>()).add(emitter);

        // 연결 완료 시 제거 콜백 등록
        emitter.onCompletion(() -> {
            remove(clientId, emitter);
        });

        // 연결 타임아웃 시 제거 콜백 등록
        emitter.onTimeout(() -> {
            emitter.complete();;
            remove(clientId, emitter);
        });

        // 연결 오류 시 제거 콜백 등록
        emitter.onError(e -> {
            remove(clientId, emitter);
        });

        // 연결 확인용 이벤트 전송
        try{
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("연결되었습니다(SSE)"));
        } catch (IOException e){
            remove(clientId, emitter);
        }

        return emitter;
    }

    // emitter 제거
    public void remove(String clientId, SseEmitter emitter) {
        List<SseEmitter> clientEmitters = emittersMap.get(clientId);
        if(clientEmitters != null) {
            clientEmitters.remove(emitter);
            // 목록이 비어있으면 맵에서 클라이언트 키 자체를 제거
            if(clientEmitters.isEmpty()){
                emittersMap.remove(clientId);
            }
        }
    }

    // 특정 클라이언트에게 알림 전송
    public void sendToClient(String clientId, Object data) {
        List<SseEmitter> clientEmitters = emittersMap.get(clientId);
        if(clientEmitters != null) {
            List<SseEmitter> deadEmitters = new ArrayList<>();

            clientEmitters.forEach(emitter -> {
                try{
                    emitter.send(SseEmitter.event()
                            .name("notification")
                            .data(data));
                } catch (IOException e){
                    deadEmitters.add(emitter);
                }
            });
                    // 실패한 emitter 제거
            if(!deadEmitters.isEmpty()){
                deadEmitters.forEach(emitter -> remove(clientId, emitter));
            }
        }
    }

    // 전체 SSE 연결 수 조회
    public int getConnectionCount() {
        return emittersMap.values().stream()
                .mapToInt(List::size)
                .sum();
    }
}
