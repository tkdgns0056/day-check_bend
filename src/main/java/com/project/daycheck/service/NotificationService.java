package com.project.daycheck.service;

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
import java.util.stream.Collectors;

/**
 * 알림을 관리하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    /**
     * 읽지 않은 알림 목록을 조회한다.
     */
    @Transactional(readOnly = true)  // 읽기 전용 작업에서 사용됨. 성능 최적화
    public List<NotificationDTO> getUnreadNotifications(){
        return notificationRepository.findByIsReadFalseOrderByNotificationTimeDesc()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 모든 알림 목록을 조회한다.
     */
    @Transactional(readOnly = true)
    public List<NotificationDTO> getAllNotifications() {
        return notificationRepository.findAllByOrderByNotificationTimeDesc()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 특정 알림을 읽음 상태로 변경한다.
     */
    @Transactional // 데이터 변경이 필요한 부분에서는 readOnly 뺴고 사용.
    public NotificationDTO markAsRead(Long id){
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("알림을 찾을 수 없습니다" + id));

        notification.setRead(true);
        Notification updatedNotification = notificationRepository.save(notification);

        return mapToDTO(updatedNotification);
    }

    /**
     * 모든 읽지 않은 알림을 읽음 상태로 변경한다.
     */
    @Transactional
    public void markAllAsRead() {
        List<Notification> unreadNotifications = notificationRepository.findByIsReadFalseOrderByNotificationTimeDesc();

        for(Notification notification : unreadNotifications) {
            notification.setRead(true);
            notificationRepository.save(notification);
        }
    }

    /**
     * 읽지 않은 알림의 개수를 조회한다.
     */
    @Transactional(readOnly = true)
    public int getUnreadCount() {
        return notificationRepository.findByIsReadFalseOrderByNotificationTimeDesc().size();
    }

    /**
     * Notification 엔티티를 NotificationDTO로 변환한다.
     */
    private NotificationDTO mapToDTO(Notification notification) {
        return NotificationDTO.builder()
                .id(notification.getId())
                .scheduleId(notification.getSchedules().getId())
                .scheduleContent(notification.getSchedules().getContent())
                .message(notification.getMessage())
                .notificationTime(notification.getNotificationTime())
                .type(notification.getType())
                .isRead(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }


    /**
     * 스케줄려 이용하여 새 알림 설정
     */
    @Transactional
    public NotificationDTO createNotification(Schedules schedules,
                                              String message,
                                              LocalDateTime notificationTime,
                                              Notification.NotificationType type) {

        // 동일한 일정과 타입의 알림이 있는지 확인
        List<Notification> existingNotifications = notificationRepository.findByScheduleIdAndType (schedules.getId(), type);

        if(!existingNotifications.isEmpty()) {
            // 이미 알림이 존재하면 생성하지 않음.
            return mapToDTO(existingNotifications.get(0));
        }

        Notification notification = Notification.builder()
                .schedules(schedules)
                .message(message)
                .notificationTime(notificationTime)
                .type(type)
                .isRead(false)
                .build();

        Notification savedNotification = notificationRepository.save(notification);

        return mapToDTO(savedNotification);
    }

    /**
     * 일정에 대한 알림을 자동으로 생성한다.
     * 일정 생성/수정 시 호출된다.
     */
    @Transactional
    public void createScheduleNotifications(Schedules schedules) {
        try {
            // 일정 시작 시간
            LocalDateTime startDate =  schedules.getStartDate();

            // 알림 시간 계산 (기본값 : 15분 전)
            // 알림 기본 값 (15분) 이 비어있지 않으면, 그대로 값 넣어주고, 비어있으면 15분으로 설정
            int minuteBefore = schedules.getNotificationBefore() != null ? schedules.getNotificationBefore() : 15;

            LocalDateTime notificationTime = startDate.minusMinutes(minuteBefore);

            // 현재 시간
            LocalDateTime now = LocalDateTime.now();

            // 알림 시간이 현재 이후인 경우에만 알림 생성
            // 이전꺼는 필요가 없음 현재까지는...
            if(notificationTime.isAfter(now)) {
                // 시작 전 알림 생성
                String beforeMessage = String.format("'%s' 일정이 %d분 후에 시작됩니다.", schedules.getContent(), minuteBefore);

                // 스케줄러 통해 알림 생성
                createNotification(
                        schedules,
                        beforeMessage,
                        notificationTime,
                        Notification.NotificationType.BEFORE_START
                );
            }

            // 시작 시간 알림 생성 ( 시작 시간이 현재 이후인 경우 ) - 딱 그 시간이 됐을 때 실행 하면됨.
            if(startDate.isAfter(now)) {
                String atStartMessage = String.format("'%s' 일정이 지금 시작됩니다.", schedules.getContent());

                createNotification(
                        schedules,
                        atStartMessage,
                        startDate,
                        Notification.NotificationType.AT_START
                );
            }
        } catch (Exception e) {
            // 알림 생성 중 오류 발생 시 로그 기록
            e.printStackTrace();
        }
    }
}
