package com.project.daycheck.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.daycheck.entity.RecurringException;
import com.project.daycheck.entity.RecurringSchedule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 반복 일정 예외 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecurringExceptionDTO {
    private Long id;
    private Long recurringScheduleId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate exceptionDate;

    private String exceptionType; // SKIP, MODIFY
    private String modifiedTitle;
    private String modifiedStartTime;
    private String modifiedEndTime;
    private String modifiedPriority;
    private String modifiedDescription;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 추가 정보 (응답용)
    private String originalTitle;
    private String originalStartTime;
    private String originalEndTime;

    // Entity 변환 메소드
    public RecurringException toEntity(RecurringSchedule recurringSchedule) {
        return RecurringException.builder()
                .id(id)
                .recurringSchedule(recurringSchedule)
                .recurringScheduleId(recurringSchedule.getId())
                .exceptionDate(exceptionDate)
                .exceptionType(exceptionType)
                .modifiedTitle(modifiedTitle)
                .modifiedStartTime(modifiedStartTime)
                .modifiedEndTime(modifiedEndTime)
                .modifiedPriority(modifiedPriority)
                .modifiedDescription(modifiedDescription)
                .build();
    }

    // Entity에서 DTO 변환 정적 메소드
    public static RecurringExceptionDTO fromEntity(RecurringException exception) {
        return RecurringExceptionDTO.builder()
                .id(exception.getId())
                .recurringScheduleId(exception.getRecurringSchedule().getId())
                .exceptionDate(exception.getExceptionDate())
                .exceptionType(exception.getExceptionType())
                .modifiedTitle(exception.getModifiedTitle())
                .modifiedStartTime(exception.getModifiedStartTime())
                .modifiedEndTime(exception.getModifiedEndTime())
                .modifiedPriority(exception.getModifiedPriority())
                .modifiedDescription(exception.getModifiedDescription())
                .originalTitle(exception.getRecurringSchedule().getContent())
                .originalStartTime(exception.getRecurringSchedule().getStartTime())
                .originalEndTime(exception.getRecurringSchedule().getEndTime())
                .createdAt(exception.getCreatedAt())
                .updatedAt(exception.getUpdatedAt())
                .build();
    }
}