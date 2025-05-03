
package com.project.daycheck.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.daycheck.entity.Schedules;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleDTO {

    private Long id;
    private String content;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startDate;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime endDate;

    private Boolean completed; // 일정 완료/미완료
    private String priority;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updateAt;

    // 반복 일정 관련 필드 추가
    private Boolean isRecurring; // 반복 일정 여부
    private String patternType;  // 반복 패턴 유형 (DAILY, WEEKLY, MONTHLY, YEARLY)


    // Entity 변환 메소드
    public Schedules toEntity(Long memberId){
        return Schedules.builder()
                .id(id)
                .memberId(memberId)
                .content(content)
                .startDate(startDate)
                .endDate(endDate)
                .completed(completed != null ? completed : false)
                .priority(priority)
                .description(description)
                .build();
    }

    // Entity에서 DTO 변환 정적 메소드
    public static ScheduleDTO fromEntity(Schedules schedules){
        return ScheduleDTO.builder()
                .id(schedules.getId())
                .content(schedules.getContent())
                .startDate(schedules.getStartDate())
                .endDate(schedules.getEndDate())
                .completed(schedules.getCompleted())
                .priority(schedules.getPriority())
                .description(schedules.getDescription())
                .createdAt(schedules.getCreatedAt())
                .updateAt(schedules.getUpdatedAt())
                .isRecurring(false) // 일반 일정은 기본적으로 false
                .build();
    }

}