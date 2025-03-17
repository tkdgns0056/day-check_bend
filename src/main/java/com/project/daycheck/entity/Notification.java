package com.project.daycheck.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String clientId; // 로그인 대신 사용할 클라이언트ID

    @Column(length = 1000)
    private String content;

    // 스케줄 Id 필드 추가
    private Long scheduleId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Column(nullable = false)
    private boolean read = false;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;


    // 알림 타입 정의
    public enum NotificationType {
       MESSAGE, SCHEDULE, SYSTEM, MENTION
    }

    // 생성사 수정(빌더 패턴)
    @Builder
    public Notification(String clientId, String content, NotificationType type, Long scheduleId) {
        this.clientId = clientId;
        this.content = content;
        this.type = type;
        this.scheduleId = scheduleId;
        this.createdAt = LocalDateTime.now();
        this.read= false;
    }

    // 읽음 처리 메서드
    public void markAsRead() {
        this.read = true;
    }
}
