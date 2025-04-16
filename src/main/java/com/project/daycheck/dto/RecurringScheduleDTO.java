package com.project.daycheck.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.daycheck.entity.RecurringSchedule;
import com.project.daycheck.entity.Schedules;
import lombok.*;

import java.time.LocalDateTime;

/**
 *  반복 일정 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecurringScheduleDTO {

    private Long id;
    private String content;
    private String patternType; // DAILY, WEEKLY, MONTHLY, YEARLY, CUSTOM
    private Integer interval;
    private String dayOfWeek;
    private Integer dayOfMonth;
    private Integer weekOfMonth;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startDate;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime endDate;

    private String startTime;
    private String endTime;
    private String priority;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    // 추가 표시 필드(클라이언트 요청에 따라 설정)
    private Integer exceptionCount; // 예외 개수

    // Entity 변환 매소드
    public RecurringSchedule toEntity(Long memberId){
        return RecurringSchedule.builder()
                .id(id)
                .memberId(memberId)
                .content(content)
                .patternType(patternType)
                .interval(interval != null ? interval : 1)
                .dayOfWeek(dayOfWeek)
                .dayOfMonth(dayOfMonth)
                .weekOfMonth(weekOfMonth)
                .startDate(startDate)
                .endDate(endDate)
                .startTime(startTime)
                .endTime(endTime)
                .priority(priority)
                .description(description)
                .build();
    }

    // Entity에서 DTO 변환 정적 메소드
    public static RecurringScheduleDTO fromEntity(RecurringSchedule recurringSchedule) {
        return RecurringScheduleDTO.builder()
                .id(recurringSchedule.getId())
                .content(recurringSchedule.getContent())
                .patternType(recurringSchedule.getPatternType())
                .interval(recurringSchedule.getInterval())
                .dayOfWeek(recurringSchedule.getDayOfWeek())
                .dayOfMonth(recurringSchedule.getDayOfMonth())
                .weekOfMonth(recurringSchedule.getWeekOfMonth())
                .startDate(recurringSchedule.getStartDate())
                .endDate(recurringSchedule.getEndDate())
                .startTime(recurringSchedule.getStartTime())
                .endTime(recurringSchedule.getEndTime())
                .priority(recurringSchedule.getPriority())
                .description(recurringSchedule.getDescription())
                .exceptionCount(recurringSchedule.getExceptions() != null ? recurringSchedule.getExceptions().size() : 0)
                .createdAt(recurringSchedule.getCreateAt())
                .updatedAt(recurringSchedule.getUpdateAt())
                .build();
    }
}
