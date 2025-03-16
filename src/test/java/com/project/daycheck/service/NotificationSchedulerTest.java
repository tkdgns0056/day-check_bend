package com.project.daycheck.service;

import com.project.daycheck.config.NotificationScheduler;
import com.project.daycheck.entity.Notification;
import com.project.daycheck.entity.Schedules;
import com.project.daycheck.repository.NotificationRepository;
import com.project.daycheck.repository.ScheduleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
public class NotificationSchedulerTest {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationScheduler notificationScheduler;

    @Test
    @Transactional
    public void testSchedulerAutomaticExecution(){
        // 테스트 데이터 준비
        LocalDateTime now = LocalDateTime.now();

        // 지금 시작하는 일정(AT_START 테스트)
        Schedules schedules1 = Schedules.builder()
                .content("통합 테스트 일정1")
                .description("스케줄러 테스트")
                .startDate(now)
                .endDate(now.plusHours(1))
                .notificationBefore(15)
                .priority("medium")
                .build();


        // 15분 후에 시작하는 일정 (BEFORE_START 테스트)
        Schedules schedules2 = Schedules.builder()
                .content("통합 테스트 일정2")
                .description("스케줄러 테스트")
                .startDate(now) // 테스트에서는 isWithinOneMinute 메서드가 true 반환하도록 현재 시간으로 설정
                .endDate(now.plusHours(1))
                .notificationBefore(0) // 일정 시작 시간과 알림 시간이 같음
                .priority("medium")
                .build();

        // 데이터베이스 저장
        scheduleRepository.save(schedules1);
        scheduleRepository.save(schedules2);

        // 테스트 전 알림 개수 확인
        long notificationCountBefore = notificationRepository.count();

        //스케줄러 수동 실행
        notificationScheduler.checkSchedulesForNotification();

        // 알림이 생성되었는지 확인
        long notificationCountAfter = notificationRepository.count();

        assertEquals(notificationCountBefore + 2, notificationCountAfter, "두 개의 알림이 생성되어야 합니다.");

        // schedules1 알림 확인
        List<Notification> schedulesNotifications = notificationRepository.findByScheduleIdAndType(schedules1.getId(), Notification.NotificationType.AT_START);
        assertEquals(1, schedulesNotifications.size(), "schedules에 대한 AT_START 알림이 있어야 합니다.");
        assertEquals("'통합테스트일정1' 일정이 지금 시작됩니다.", schedulesNotifications.get(0).getMessage());

        // schedules2 알림 확인
        List<Notification> schedules2Notifications  = notificationRepository.findByScheduleIdAndType(schedules2.getId(), Notification.NotificationType.BEFORE_START);
        assertEquals(1, schedules2Notifications.size(), "schedule2에 대한 BEFORE_START 알림이 있어야 합니다");
        assertEquals("'통합 테스트 일정 2' 일정이 0분 후에 시작됩니다.", schedules2Notifications.get(0).getMessage());
    }
}
