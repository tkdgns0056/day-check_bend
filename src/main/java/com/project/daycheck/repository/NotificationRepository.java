package com.project.daycheck.repository;

import com.project.daycheck.entity.Notification;
import com.project.daycheck.entity.Schedules;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // 읽지않은 알림만 조회하여 최신순으로 정렬
    List<Notification> findByIsReadFalseOrderByNotificationTimeDesc();

    // 모든 알림을 최신순 조회. 알림 히스토리 볼 떄 사용
    List<Notification> findAllByOrderByNotificationTimeDesc();

    @Query("SELECT n FROM Notification n WHERE n.schedule.id = :scheduleId AND n.type = :type")
    List<Notification> findByScheduleIdAndType(
            @Param("scheduleId") Long scheduleId,
            @Param("type") Notification.NotificationType type);

    @Query("SELECT n FROM Notification n WHERE n.isRead = false AND n.notificationTime <= :now")
    List<Notification> findPendingNotifications(@Param("now") LocalDateTime now);

}
