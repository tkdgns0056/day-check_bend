package com.project.daycheck.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 일정 생성/수정 요청 DTO
 */
@Data
public class ScheduleRequest {

    @NotBlank(message = "일정 내용은 필수 입력값입니다.")
    private String content;

    @NotNull(message = "시작 날짜/ 시간은 필수 입력값입니다.")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startDate;

    @NotNull(message = "종료 날짜/시간은 필수 입력값입니다.")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime endDate;

    private String priority;
    private String description;
    private Boolean completed;
}
