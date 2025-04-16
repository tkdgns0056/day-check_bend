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
//    private String recurrencePattern; // 반복 패턴
//    private Long parentScheduleId; // 부모일정(개별일정) - 반복 일정 설정 시 필요
    private String priority;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updateAt;


    // Entity 변환 메소드
    public Schedules toEntity(Long memberId){
        return Schedules.builder()
                .id(id)
                .memberId(memberId)
                .content(content)
                .startDate(startDate)
                .endDate(endDate)
                .completed(completed != null ? completed : false)
//                .recurrencePattern(recurrencePattern)
//                .parentScheduleId(parentScheduleId)
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
//                .recurrencePattern(schedules.getRecurrencePattern())
//                .parentScheduleId(schedules.getParentScheduleId())
                .priority(schedules.getPriority())
                .description(schedules.getDescription())
                .createdAt(schedules.getCreatedAt())
                .updateAt(schedules.getUpdatedAt())
                .build();
    }

}
