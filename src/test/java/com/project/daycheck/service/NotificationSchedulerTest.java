package com.project.daycheck.service;

import com.project.daycheck.entity.Schedules;
import com.project.daycheck.repository.NotificationRepository;
import com.project.daycheck.repository.ScheduleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
public class NotificationSchedulerTest {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Test
    public void testSchedulerAutomaticExecution() throws InterruptedException {
        // 테스트 전 알림 개수
        long initialCount = notificationRepository.count();

        // 2초 후에 시작하는 일정 생성
        LocalDateTime startTime = LocalDateTime.now().plusSeconds(2);
        Schedules schedule = Schedules.builder()
                .content("자동 실행 테스트")
                .description("스케줄러 자동 실행 테스트")
                .startDate(startTime)
                .endDate(startTime.plusMinutes(30))
                .notificationBefore(0)  // 시작 시간에 알림
                .build();

        scheduleRepository.save(schedule);

        // 스케줄러가 실행될 시간을 기다림 (최소 3초)
        TimeUnit.SECONDS.sleep(3);

        // 알림이 생성되었는지 확인
        long finalCount = notificationRepository.count();
        assertTrue(finalCount > initialCount, "스케줄러가 자동으로 실행되어 알림이 생성되어야 합니다");
    }

}
