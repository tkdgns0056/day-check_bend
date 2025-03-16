package com.project.daycheck.dto;

import com.project.daycheck.entity.Notification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {

    private Long id;
    private Long scheduleId;
    private String content; // 일정 제목
    private String message;
    private LocalDateTime notificationTime;
    private Notification.NotificationType type;
    private boolean isRead;
    private LocalDateTime createdAt;
}
