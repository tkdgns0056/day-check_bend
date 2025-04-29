package com.project.daycheck.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 반복 일정 생성/수정 요청 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecurringScheduleRequest {

    @NotBlank(message = "반복 일정 내용은 필수 입력값입니다.")
    private String content;

    @NotBlank(message = "반복 패턴 유형은 필수 입력값입니다.")
    private String patternType; // DAILY, WEEKLY, MONTHLY, YEARLY, CUSTOM

    @Min(value = 1, message = "반복 간격은 최소 1 이상이어야 합니다.")
    private Integer interval;

    private List<DayOfWeek> daysOfWeek; // 변경됨: String에서 List<DayOfWeek>로

    private Integer dayOfMonth; // 매월 n일 (MONTHLY 타입)

    private Integer weekOfMonth; // 매월 n주차 (MONTHLY 타입)

    @NotNull(message = "시작 날짜/시간은 필수 입력값입니다.")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startDate;

    @NotNull(message = "종료 날짜/시간은 필수 입력값입니다.")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime endDate;

    private String startTime; // 옵션 - 시작 시간 (HH:mm)
    private String endTime; // 옵션 - 종료 시간(HH:mm)
    private String priority;
    private String description;
}