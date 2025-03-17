package com.project.daycheck.repository.service;

import com.project.daycheck.config.component.SseEmitters;
import com.project.daycheck.dto.NotificationDTO;
import com.project.daycheck.entity.Notification;
import com.project.daycheck.entity.Schedules;
import com.project.daycheck.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 알림을 관리하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SseEmitters sseEmitters;
//    private final SseEmitterService sseEmitterService;

    /**
     * 클라이언트의 알림 목록 조회
     */
    @Transactional(readOnly = true)
    public List<NotificationDTO.Response> getNotifications(String clientId) {
        return notificationRepository.findByClientIdOrderByCreatedAtDesc(clientId)
                .stream()
                .map(NotificationDTO.Response::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 알림 읽음 처리
     */
    @Transactional
    public Optional<NotificationDTO.Response> markAsRead(Long notificationId, String clientId) {
        return notificationRepository.findByIdAndClientId(notificationId, clientId)
                .map(notification -> {
                    notification.markAsRead();
                    return NotificationDTO.Response.fromEntity(notification);
                });
    }

    /**
     * 알림 생성
     */
    @Transactional
    public NotificationDTO.Response createNotification(NotificationDTO.CreateRequest request){
        // DTO -> Entity 변환 및 저장
        Notification notification = request.toEntity();
        Notification savedNotification = notificationRepository.save(notification);

        // Entity -> DTO 변환
        NotificationDTO.Response response = NotificationDTO.Response.fromEntity(savedNotification );

        // SSE를 통해 클라이언트에 전송
        sseEmitters.sendToClient(notification.getClientId(), response);

        return response;
    }

    /**
     * 특정 스케줄과 관련된 알림 조회
     */
    @Transactional(readOnly = true)
    public List<NotificationDTO.Response> getNotificationsByScheduleId(Long scheduleId) {
        return notificationRepository.findByScheduleId(scheduleId)
                .stream()
                .map(NotificationDTO.Response::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * 스케줄 삭제 시 관련 알림 모두 삭제
     */
    @Transactional
    public void deleteNotificationByScheduleId(Long scheduleId) {
        notificationRepository.deleteByScheduleId(scheduleId);
    }

}