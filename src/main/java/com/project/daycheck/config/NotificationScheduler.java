package com.project.daycheck.config;

import com.project.daycheck.entity.Notification;
import com.project.daycheck.entity.Schedules;
import com.project.daycheck.repository.NotificationRepository;
import com.project.daycheck.repository.ScheduleRepository;
import com.project.daycheck.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class NotificationScheduler {

    private final ScheduleRepository scheduleRepository;
    private final NotificationService notificationService;

    /**
     * 1분마다 실행되어 알림이 필요한 일정을 확인한다.
     */
    @Scheduled(fixedRate = 60000) //1분 마다 실행
    @Transactional
    public void checkSchedulesForNotification() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime thirtyMinutesLater = now.plusMinutes(30);

        try {
            // 현재시간부터 30분 이내에 시작하는 알정이 있는지 체크 조회
            List<com.project.daycheck.entity.Schedules> upcomingSchedules = scheduleRepository.findSchedulesBetween(now, thirtyMinutesLater);

            for(Schedules schedules : upcomingSchedules) {
               LocalDateTime startDate = schedules.getStartDate();

               // 알림 시간 계산(기본값 : 15분전)
                int minutesBefore = schedules.getNotificationBefore() != null ? schedules.getNotificationBefore() : 15;

                // 디비에 저장된 startDate 즉, 일정 시간  -15분전 데이터 만들어둬서 알림 뿌려줄거임.
                LocalDateTime notificationTime =startDate.minusNanos(minutesBefore);

                // 알림 시간이 현재 시간과 비교하여 ±1분 이내인 경우 (알림 시간 도달)
                if(isWithinOneMinute(notificationTime, now)) {
                    createBeforeStartNotification(schedules, notificationTime, minutesBefore);
                }

                // 일정 시작 시간이 현재 시간과 비교하여 ±1분 이내인 경우 (일정 시작)
                if(isWithinOneMinute(startDate, now)) {
                    createAtStartNotification(schedules,startDate);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 특정 시간이 현재 시간으로부터 ±1분 이내인지 확인하는 헬퍼 메서드
     * 정확한 시간에 알림 생성하기 어렵기 때문에 1분 오차 허용(추후 변경해야할듯)
     */
    private boolean isWithinOneMinute(LocalDateTime time, LocalDateTime now) {
        LocalDateTime oneMinuteBefore = now.minusMinutes(1);
        LocalDateTime oneMinuteAfter = now.plusMinutes(1);
        return !time.isBefore(oneMinuteBefore) && !time.isAfter(oneMinuteAfter);
    }


    /**
     * 일정 시작 전 알림을 생성
     */
    private void createBeforeStartNotification(Schedules schedules, LocalDateTime notificationTime, int minutesBefore) {
        String message = String.format("'%s' 일정이 %d분 후에 시작됩니다.",
                schedules.getContent(), minutesBefore);

        notificationService.createNotification(
                schedules,
                message,
                notificationTime,
                Notification.NotificationType.BEFORE_START
        );
    }

        /**
         * 일정 시작 시간 알림을 생성.
         */
        private void createAtStartNotification(Schedules schedules, LocalDateTime startDate) {
            String message = String.format("'%s' 일정이 지금 시작됩니다.", schedules.getContent());

            notificationService.createNotification(
                    schedules,
                    message,
                    startDate,
                    Notification.NotificationType.AT_START
            );
    }
}
