package com.project.daycheck.config.component;

import com.project.daycheck.entity.Notification;
import com.project.daycheck.entity.Schedules;
import com.project.daycheck.repository.ScheduleRepository;
import com.project.daycheck.repository.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationScheduler {

    private final ScheduleRepository scheduleRepository;
    private final NotificationService notificationService;

    // 매 분마다 실행되는 스케줄러
    @Scheduled(cron = "0 * * * * *")
    public void checkUpcomingSchedules() {
        LocalDateTime now = LocalDateTime.now();
        log.info("일정 알림 스케줄러 실행: {}", now);

        // 15분 후 시작 예쩡인 일정 확인( 14분 30초 ~ 15분 30초 사이)
        LocalDateTime fifteenMinFrom = now.plusMinutes(14).plusSeconds(30);
        LocalDateTime fifteenMinTo = now.plusMinutes(15).plusSeconds(30);

    }
}
