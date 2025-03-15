package com.project.daycheck.repository;

import com.project.daycheck.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // 읽지 않은 알림을 시간 역순으로 조회
    List<Notification> findByIsReadFalseOrderByNotificationTimeDesc();

    // 모든 알림을 시간 역순으로 조회
    List<Notification> findAllByOrderByNotificationTimeDesc();

    // 일정 ID와 알림 타입으로 알림 찾기
    @Query("SELECT n FROM Notification  n where n.schedules.id = :scheduleId AND n.type = :type")
    List<Notification> findByScheduleIdAndType(@Param("schedulesId") Long schedulesId,
                                               @Param("type") Notification.NotificationType type);

    // 현재 시간 이전 & 읽지 않은 알림 찾기 (알림 감지용)
    @Query("SELECT n FROM Notification n WHERE n.isRead = false AND  n.notificationTime <= :now ")
    List<Notification> findPendingNotifications(@Param("now") LocalDateTime now);
}
