package com.project.daycheck.dto;

import com.project.daycheck.entity.Notification;
import lombok.*;

import java.time.LocalDateTime;


public class NotificationDTO {

    // 알림 생성 요청 DTO
    @Getter
    @Builder
    public static class CreateRequest {
        private String clientId;
        private String content;
        private Notification.NotificationType type;
        private Long scheduleId; // 스케줄 ID 필드

        // DTO -> Entity 변환
        public Notification toEntity () {
            return Notification.builder()
                    .clientId(clientId)
                    .content(content)
                    .type(type)
                    .scheduleId(scheduleId)
                    .build();
        }
    }

    // 알림 응답 DTO
    @Getter
    @Builder
    public static class Response {
        private Long id;
        private String clientId;
        private String content;
        private Notification.NotificationType type;
        private Long scheduleId;
        private boolean read;
        private LocalDateTime createdAt;

        // Entity -> DTO 변환
        public static Response fromEntity(Notification notification) {
            return Response.builder()
                    .id(notification.getId())
                    .clientId(notification.getClientId())
                    .content(notification.getContent())
                    .type(notification.getType())
                    .scheduleId(notification.getScheduleId())
                    .read(notification.isRead())
                    .createdAt(notification.getCreatedAt())
                    .build();
        }
    }
}
