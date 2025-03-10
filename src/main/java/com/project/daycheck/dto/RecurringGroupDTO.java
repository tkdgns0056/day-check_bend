package com.project.daycheck.dto;

import com.project.daycheck.entity.Schedules;
import lombok.AllArgsConstructor;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecurringGroupDTO {
    private Schedules parentSchedule;  // 부모 일정 정보
    private String startDate;          // 시작일 (YYYY-MM-DD 형식)
    private String endDate;            // 종료일 (YYYY-MM-DD 형식)
    private int scheduleCount;         // 일정 개수
}
